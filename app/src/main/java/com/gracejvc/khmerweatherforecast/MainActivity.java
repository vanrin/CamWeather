package com.gracejvc.khmerweatherforecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gracejvc.khmerweatherforecast.sync.CamWeatherSyncAdapter;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    public void onItemSelected(String date) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
