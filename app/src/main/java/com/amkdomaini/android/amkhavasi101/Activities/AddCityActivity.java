package com.amkdomaini.android.amkhavasi101.Activities;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;
import com.amkdomaini.android.amkhavasi101.Controllers.CityCardsViewTouchHelperCallbacks;
import com.amkdomaini.android.amkhavasi101.Modules.CityDatabase;
import com.amkdomaini.android.amkhavasi101.Adapters.CityRecycleViewAdapter;
import com.amkdomaini.android.amkhavasi101.Modules.City;
import com.amkdomaini.android.amkhavasi101.Interfaces.OnStartDragListener;
import com.amkdomaini.android.amkhavasi101.Interfaces.RecyclerViewCityClick;
import com.amkdomaini.android.amkhavasi101.Controllers.Utils;
import com.amkdomaini.android.amkhavasi101.Controllers.WeatherInfoLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

public class AddCityActivity extends AppCompatActivity implements OnStartDragListener, RecyclerViewCityClick {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static ArrayList<City> Active_Cities_List = null;
    private ArrayList<City> Shown_List=null;
    private ItemTouchHelper mItemTouchHelper;
    private Bundle citybundle = new Bundle();
    private static final String DATABASE_NAME ="City_Database";
    private static LinearLayout ProgressBarView;
    private CityDatabase Citydb;
    CityRecycleViewAdapter dataRecyclerViewAdapter;
    private boolean Only_Once = false;
    public static final String TAG = "LOL";
    private static ArrayList<City>Cache;

    private LoaderManager.LoaderCallbacks<ArrayList<City>> cityloaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<City>>() {
        @NonNull
        @Override
        public android.support.v4.content.Loader<ArrayList<City>> onCreateLoader(int id, @Nullable Bundle args) {

            return new WeatherInfoLoader(ApplicationContextProvider.getContext(), args);
        }

        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<City>> loader, ArrayList<City> data) {

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
            Active_Cities_List = data;
            if(Only_Once && prefs.getBoolean("ShowLocation",false) ){Active_Cities_List.remove(0);
            Only_Once = false;}
            initViews(false);
            ProgressBarView.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<City>> loader) {
            loader.reset();
        }
    };
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcity_layout);

        ProgressBarView = findViewById(R.id.Second_Progressbar_LinearLayout);

        Active_Cities_List =(ArrayList<City>) this.getIntent().getSerializableExtra("A");


        Citydb = Room.databaseBuilder(getApplicationContext(),
                CityDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Cache = (ArrayList<City>) Citydb.getCityDao().getAllCities();
            }
        });
        initViews(true);
        Button button = (Button) findViewById(R.id.Return_Button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OnButtonClicked(v);
            }
        });

        Button search =(Button) findViewById(R.id.place_autocomplete_search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Active_Cities_List != null){

                    if(Active_Cities_List.size() > 20){
                        Toast.makeText(getApplicationContext(),"WOAH THERE!Not more than 20 Cities",Toast.LENGTH_LONG).show();
                    }
                    else{
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                            .build();

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                                    .build(AddCityActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }}
            }}
        });


    }
    public void OnButtonClicked(View v){
        Intent ReturnIntent = new Intent();
        Active_Cities_List = dataRecyclerViewAdapter.getDataModelArrayList();
        ReturnIntent.putExtra("Possible",Active_Cities_List);
        ReturnIntent.putExtra( "Last_Page",this.getIntent().getIntExtra("Last_Page",0));
        if(Utils.Same_City_List(Active_Cities_List,Cache)){
            ReturnIntent.putExtra("Change",false);
        }else {
            ReturnIntent.putExtra("Change",true);
        }
        ReturnIntent.putExtra("Possible",Active_Cities_List);
        ReturnIntent.putExtra( "Last_Page",this.getIntent().getIntExtra("Last_Page",0));
        setResult(Activity.RESULT_OK,ReturnIntent);
        finish();
    }
    private void initViews(boolean first) {

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
        Shown_List = Active_Cities_List;
        if(first && Shown_List.size()!=0 && prefs.getBoolean("ShowLocation",false)){Shown_List.remove(0);}
        dataRecyclerViewAdapter = new CityRecycleViewAdapter(getApplicationContext(), Shown_List,this);
        ItemTouchHelper.Callback callback =
                new CityCardsViewTouchHelperCallbacks(dataRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        dataRecyclerViewAdapter.delegate=this;
        mRecyclerView.setAdapter(dataRecyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent ReturnIntent = new Intent();
        Active_Cities_List = dataRecyclerViewAdapter.getDataModelArrayList();
        ReturnIntent.putExtra("Possible",Active_Cities_List);
        ReturnIntent.putExtra( "Last_Page",this.getIntent().getIntExtra("Last_Page",0));
        if(Utils.Same_City_List(Active_Cities_List,Cache)){
            ReturnIntent.putExtra("Change",false);
        }else {
            ReturnIntent.putExtra("Change",true);
        }
        ReturnIntent.putExtra("Possible",Active_Cities_List);
        ReturnIntent.putExtra( "Last_Page",this.getIntent().getIntExtra("Last_Page",0));
        setResult(Activity.RESULT_OK,ReturnIntent);
        finish();
        return;
    }



    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (mItemTouchHelper!=null)
            mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClicked(String name) {
        Toast.makeText(getApplicationContext(),String.valueOf(name),Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                City NEW_City = new City(place.getLatLng().latitude,place.getLatLng().longitude);
                NEW_City.setFullName(place.getName().toString());

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

                citybundle.clear();
                citybundle.putSerializable("S", dataRecyclerViewAdapter.getDataModelArrayList());
                citybundle.putSerializable("New_City",NEW_City);
                citybundle.putFloat("Latitude",prefs.getFloat("Latitude",50));
                citybundle.putFloat("Longitude",prefs.getFloat("Longitude",30));
                Only_Once = true;

                ProgressBarView.setVisibility(View.VISIBLE);
                getSupportLoaderManager().destroyLoader(R.id.add_city_loader_id);
                getSupportLoaderManager().initLoader(R.id.add_city_loader_id, citybundle,cityloaderCallbacks);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(getApplicationContext(),"Connection Error. Check your Internet Connection.",Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
               // The user canceled the operation
            }
        }
    }
}