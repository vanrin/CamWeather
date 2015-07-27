package com.gracejvc.khmerweatherforecast;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Chhea Vanrin on 7/18/2015.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    private Cursor mCursor;
    final private Context mContext;
    public static Typeface battambong;
    final private ForecastAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;



    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0 && mUseTodayLayout)?VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    public static interface ForecastAdapterOnClickHandler {
        void onClick(String date, ForecastAdapterViewHolder vh);
    }

    @Override
    public int getItemCount() {
        if (mCursor==null){
            return 0;
        }
        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }
    public ForecastAdapter(Context context,ForecastAdapterOnClickHandler dh,View emptyView) {
        mContext = context;
        battambong= Typeface.createFromAsset(context.getAssets(),"fonts/Battambang.ttf");
        mClickHandler = dh;
        mEmptyView = emptyView;
    }
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ForecastAdapter.ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder,int positoin) {
        mCursor.moveToPosition(positoin);
        int viewType =getItemViewType(positoin);
        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int fallBackIconId;
        switch (viewType){
            case VIEW_TYPE_TODAY:{
                fallBackIconId = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            }
            default:{
                fallBackIconId =Utility.getIconResourceForWeatherCondition(weatherId);
                break;
            }
        }

        Glide.with(mContext)
                .load(Utility.getArtUrlForWeatherCondition(mContext,weatherId))
                .error(fallBackIconId)
                .crossFade()
                .into(forecastAdapterViewHolder.iconView);

        //  Get city name
        String cityName = mCursor.getString(ForecastFragment.COL_CITY_NAME);
        forecastAdapterViewHolder.locationView.setText(cityName);

        // Read date from cursor
        String dateString = mCursor.getString(ForecastFragment.COL_WEATHER_DATE);

        // Find TextView and set formatted date on it

        forecastAdapterViewHolder.dayView.setText(Utility.getFriendlyDayString(mContext,dateString));
        // Read weather forecast from cursor
        String description = Utility.getDescriptionForWeatherCondition(mContext,weatherId);
        // Find TextView and set weather forecast on it
        forecastAdapterViewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(mContext);

        // Read high temperature from cursor
        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        forecastAdapterViewHolder.highTempView.setText(Utility.formatTemperature(mContext, high, isMetric));

        // Read low temperature from cursor
        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        forecastAdapterViewHolder.lowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView iconView;
        public final TextView dayView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView locationView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dayView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            locationView = (TextView) view.findViewById(R.id.list_item_location_textview);
            dayView.setTypeface(battambong);
            descriptionView.setTypeface(battambong);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String fareCastDate = mCursor.getString(ForecastFragment.COL_WEATHER_DATE);
            mClickHandler.onClick(fareCastDate, this);
        }
    }
}
