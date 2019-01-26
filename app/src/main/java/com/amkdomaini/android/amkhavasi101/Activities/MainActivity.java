package com.amkdomaini.android.amkhavasi101.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;
import com.amkdomaini.android.amkhavasi101.Modules.CityDatabase;
import com.amkdomaini.android.amkhavasi101.Adapters.ForecastRecyclerViewAdapter;
import com.amkdomaini.android.amkhavasi101.Modules.City;
import com.amkdomaini.android.amkhavasi101.Adapters.PagerAdapter;
import com.amkdomaini.android.amkhavasi101.Fragments.WeatherFragment;
import com.amkdomaini.android.amkhavasi101.Controllers.WeatherInfoLoader;
import com.amkdomaini.android.amkhavasi101.Controllers.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements WeatherFragment.OnFragmentInteractionListener {

    public static final String Connection_error_message = "Failed to Sync. Check your Internet Connection.";
    private static final String DATABASE_NAME = "City_Database";
    private static ArrayList<City> Active_Cities_List = new ArrayList<City>();
    private static Bundle bundle = new Bundle();
    private LinearLayout ProgressBarView;
    private FragmentStatePagerAdapter myAdapter;
    private CityDatabase Citydb;
    private ViewPager WeatherPager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView NoCityText;
    private LocationRequest mLocationRequest;
    private ForecastRecyclerViewAdapter Forecast_adapter;
    private long UPDATE_INTERVAL = 30*60 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 10000; /* 10 sec */


    private LoaderManager.LoaderCallbacks<ArrayList<City>> loaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<City>>() {
        @NonNull
        @Override
        public android.support.v4.content.Loader<ArrayList<City>> onCreateLoader(int id, @Nullable Bundle args) {
            //The text which states no cities
            NoCityText.setVisibility(View.GONE);
            return new WeatherInfoLoader(ApplicationContextProvider.getContext(), args);
        }

        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<City>> loader, ArrayList<City> data) {

            if (data == null) { //Something went wrong
                Intent serviceIntent = new Intent(getApplicationContext(), WeatherService.class);
                stopService(serviceIntent);
                Toast.makeText(getApplicationContext(), Connection_error_message, Toast.LENGTH_SHORT).show();
                ProgressBarView.setVisibility(View.GONE);
                WeatherPager.setVisibility(View.GONE);
                NoCityText.setVisibility(View.VISIBLE);
            } else {
                Active_Cities_List = data;
                WeatherPager = (ViewPager) findViewById(R.id.Pager);

                if (data.size() == 0) {
                    WeatherPager.setVisibility(View.GONE);
                    NoCityText.setVisibility(View.VISIBLE);
                } else {
                    bundle.clear();
                    bundle.putSerializable("City_List", Active_Cities_List);
                    myAdapter = new PagerAdapter(getSupportFragmentManager(), bundle);

                    WeatherPager.setAdapter(myAdapter);
                    NoCityText.setVisibility(View.GONE);
                    WeatherPager.setOffscreenPageLimit(Active_Cities_List.size() - 1);
                    myAdapter.notifyDataSetChanged();
                    WeatherPager.setVisibility(View.VISIBLE);

                }

                ProgressBarView.setVisibility(View.GONE);
                Intent serviceIntent = new Intent(getApplicationContext(), WeatherService.class);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
                pref.edit().putLong("LastUpdate", Calendar.getInstance().getTimeInMillis()).apply();

                if (Active_Cities_List != null) {
                    if (Active_Cities_List.size() > 0 && pref.getBoolean("ShowNotification", false)) {

                        serviceIntent.putExtra("inputExtra", Active_Cities_List.get(0));
                        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                    } else {
                        stopService(serviceIntent);
                    }
                } else {
                    stopService(serviceIntent);
                }
            }


        }

        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<City>> loader) {
            ProgressBarView.setVisibility(View.VISIBLE);
            NoCityText.setVisibility(View.GONE);
            loader.reset();

        }

    };

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager = (LocationManager) getSystemService(ApplicationContextProvider.getContext().LOCATION_SERVICE);
                    if (locationListener != null && locationManager != null) {
                        locationManager.requestLocationUpdates("gps", 300000, 500, locationListener);
                    }
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        long Now = Calendar.getInstance().getTimeInMillis();
        long diff = Now - pref.getLong("LastUpdate", 0);
        if (pref.getBoolean("Change", true) || (diff > 1000 * 60 * 60 * 3)) {
            bundle.clear();
            pref.edit().putBoolean("Change", false).apply();
            bundle.putSerializable("City_List", Active_Cities_List);

            new CitydbAsyncTask().execute(bundle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NoCityText = findViewById(R.id.nothingtextview);
        NoCityText.setVisibility(View.GONE);
        ProgressBarView = findViewById(R.id.ProgressBar_LinearLayout);


        startLocationUpdates();


        WeatherPager = findViewById(R.id.Pager);
        bundle.clear();
        bundle.putSerializable("City_List", Active_Cities_List);

        if ((prefs.getBoolean("FromPreference", false))) {
            prefs.edit().putBoolean("FromPreference", false).apply();
            if (prefs.getBoolean("Change", true)) {
                Active_Cities_List.clear();
                new CitydbAsyncTask().execute(bundle);
                prefs.edit().putBoolean("Change", false).apply();
            }
        } else {
            Active_Cities_List.clear();
            new CitydbAsyncTask().execute(bundle);
        }


        if (Active_Cities_List != null) {
            if (Active_Cities_List.size() == 0) {
                if (WeatherPager != null && NoCityText != null)
                    WeatherPager.setVisibility(View.GONE);
                NoCityText.setVisibility(View.VISIBLE);


            } else {
                if (WeatherPager != null) {
                    WeatherPager.setVisibility(View.VISIBLE);
                    NoCityText.setVisibility(View.GONE);
                    myAdapter = new PagerAdapter(getSupportFragmentManager(), bundle);
                    WeatherPager.setAdapter(myAdapter);

                }
            }
        }
        if (WeatherPager != null && Active_Cities_List != null) {
            if (Active_Cities_List.size() > 1) {
                WeatherPager.setOffscreenPageLimit(Active_Cities_List.size());
            }
        }

        WeatherPager.setPageTransformer(true, new ParallaxPageTransformer());
        WeatherPager.setPageMargin(4);
        WeatherPager.setOffscreenPageLimit(20);
        WeatherPager.setPageMarginDrawable(R.color.seperatorgray);

        ImageButton button =  findViewById(R.id.Add_City_Button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddCityIntent = new Intent(getApplicationContext(), AddCityActivity.class);
                AddCityIntent.putExtra("A", Active_Cities_List);
                AddCityIntent.putExtra("Last_Page", WeatherPager.getCurrentItem());
                startActivityForResult(AddCityIntent, 0);
            }
        });

        ImageButton pref_button = findViewById(R.id.Preference_Button);


        pref_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PrefIntent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivityForResult(PrefIntent, 1);
            }
        });


        if (prefs.getBoolean("FirstTime", true)) {
            Intent mFirstTime = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(mFirstTime);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(R.id.add_city_loader_id);
        getSupportLoaderManager().destroyLoader(R.id.Activity_Intent_id);
        getSupportLoaderManager().destroyLoader(R.id.weather_info_loader_id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Active_Cities_List = (ArrayList<City>) data.getSerializableExtra("Possible");
            //WeatherPager.setCurrentItem(data.getIntExtra("Last_Page",0));
            if (data.getBooleanExtra("Change", true)) {
                final Bundle bundle2 = new Bundle();
                bundle2.putSerializable("S", Active_Cities_List);
                if (Active_Cities_List != null) {
                    if (Active_Cities_List.size() == 0) {
                        bundle2.putBoolean("EmptyList", true);
                    } else {
                        bundle2.putBoolean("EmptyList", false);
                    }
                }
                //getSupportLoaderManager().restartLoader(R.id.Intent_Return_loader_id,bundle2,loaderCallbacks);
                new CitydbAsyncTask().execute(bundle2);
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            // Nothing
        }
    }

    public final void Update(int Unique_key, Bundle bundle) {
        getSupportLoaderManager().initLoader(Unique_key, bundle, loaderCallbacks);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        checkPermission();
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

        prefs.edit().putFloat("Latitude", (float) location.getLatitude()).apply();
        prefs.edit().putFloat("Longitude", (float) location.getLongitude()).apply();

        Log.d("TAG", "PROBLEM COMMITING");
    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        checkPermission();
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}
                        , 10);

            }
        }
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1.0) {
            configuration.fontScale = (float) 1.0;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(metrics);
                metrics.scaledDensity = configuration.fontScale * metrics.density;
                getBaseContext().getResources().updateConfiguration(configuration, metrics);
            }
        }
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, WeatherService.class);
        stopService(serviceIntent);
    }

    public class ParallaxPageTransformer implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {

            int pageWidth = view.getWidth();


            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(1);

            } else if (position <= 1) { // [-1,1]
                ImageView Night = (ImageView) view.findViewById(R.id.Night_sky);
                Night.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(1);
            }


        }
    }
    //Makes sure The Database is ready and complete
    //A user might call the list of cities while a previous version is still being saved to the DB
    //This asynctask method prevents this bug.
    private class CitydbAsyncTask extends AsyncTask<Bundle, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarView.setVisibility(View.VISIBLE);
            NoCityText.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(Bundle... Bundles) {
            Citydb = Room.databaseBuilder(getApplicationContext(),
                    CityDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
            int count = Citydb.getCityDao().getDataCount();
            bundle = Bundles[0];

            ArrayList<City> lol = (ArrayList<City>) bundle.getSerializable("S");

            if (lol != null) {
                Citydb.getCityDao().nukeTable();
                for (int i = 0; i < lol.size(); i++) {
                    Citydb.getCityDao().insert(lol.get(i));
                }
            }
            if (bundle.getBoolean("EmptyList")) {
                Citydb.getCityDao().nukeTable();
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            bundle.putInt("DB_size", count);
            getSupportLoaderManager().destroyLoader(R.id.weather_info_loader_id);
            getSupportLoaderManager().initLoader(R.id.weather_info_loader_id, bundle, loaderCallbacks);
        }
    }

}
