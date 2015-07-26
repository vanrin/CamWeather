package com.gracejvc.khmerweatherforecast;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Chhea Vanrin on 7/18/2015.
 */
public class ForecastAdapter extends CursorAdapter {
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    public static Typeface battambong;
    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }
    @Override
    public int getItemViewType(int position) {
        return (position==0 && mUseTodayLayout)?VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        battambong= Typeface.createFromAsset(context.getAssets(),"fonts/Battambang.ttf");
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        int visibleId = View.VISIBLE;
        if (viewType==VIEW_TYPE_TODAY){
            layoutId= R.layout.list_item_forecast_today;
        }
        else if (viewType==VIEW_TYPE_FUTURE_DAY){
            layoutId = R.layout.list_item_forecast;
            visibleId = View.GONE;
        }
        View view= LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);
        viewHolder.locationView.setVisibility(visibleId);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder)view.getTag();
        int viewType =getItemViewType(cursor.getPosition());
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
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
                .into(viewHolder.iconView);
        //  Get city name
        String cityName = cursor.getString(ForecastFragment.COL_CITY_NAME);
        viewHolder.locationView.setText(cityName);
        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);

        // Find TextView and set formatted date on it

        viewHolder.dayView.setText(Utility.getFriendlyDayString(context,dateString));
        // Read weather forecast from cursor
        String description = Utility.getDescriptionForWeatherCondition(context,weatherId);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }
    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView dayView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView locationView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dayView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            locationView = (TextView) view.findViewById(R.id.list_item_location_textview);

            dayView.setTypeface(battambong);
            descriptionView.setTypeface(battambong);

        }
    }
}
