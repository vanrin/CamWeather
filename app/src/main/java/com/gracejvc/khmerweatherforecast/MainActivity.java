package com.gracejvc.khmerweatherforecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.gracejvc.khmerweatherforecast.sync.CamWeatherSyncAdapter;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback,AppBarLayout.OnOffsetChangedListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private Context context = this;
    boolean isTablet;
    boolean isLand;
   // int mutedColor = R.attr.colorPrimary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        isLand = getResources().getBoolean(R.bool.isLand);
        if (!isLand && !isTablet){
            ForecastFragment.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    CamWeatherSyncAdapter.syncImmediately(context);
                    ForecastFragment.mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
            collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle("CamWeather");
            ImageView header = (ImageView) findViewById(R.id.header);
            header.setImageResource(R.drawable.header);
            Bitmap bitmap = ((BitmapDrawable)header.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int primaryDark = getResources().getColor(R.color.primary_dark);
                    int primary = getResources().getColor(R.color.primary);
                    collapsingToolbar.setContentScrimColor(palette.getMutedColor(primary));
                    collapsingToolbar.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                }
            });}
        if (findViewById(R.id.weather_detail_container)!=null){
            mTwoPane =true;
            if (savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container,new DetailFragment())
                        .commit();
            }
        }
        else {
            mTwoPane=false;
        }
        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        CamWeatherSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (!isLand && !isTablet) {
            if (i==0){
                ForecastFragment.mSwipeRefreshLayout.setEnabled(true);
            }
            else {
                ForecastFragment.mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isLand && !isTablet) {
            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLand && !isTablet) {
            appBarLayout.removeOnOffsetChangedListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        else if (id==R.id.action_map){
            openPreferredLocationInMap();
        }

        return super.onOptionsItemSelected(item);
    }
    private void openPreferredLocationInMap(){
        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharePrefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG,"couldn't call"+ location + "no receiving apps installed!");
        }
    }

    @Override
    public void onItemSelected(Uri contentUri,ForecastAdapter.ForecastAdapterViewHolder vh) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,new Pair<View, String>(vh.iconView,getString(R.string.detail_icon_transition)));
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }
}
