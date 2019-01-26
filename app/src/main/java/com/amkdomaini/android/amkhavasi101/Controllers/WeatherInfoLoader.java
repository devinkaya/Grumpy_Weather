package com.amkdomaini.android.amkhavasi101.Controllers;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.amkdomaini.android.amkhavasi101.Modules.CityDatabase;
import com.amkdomaini.android.amkhavasi101.Modules.City;
import com.amkdomaini.android.amkhavasi101.Modules.Weather;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class WeatherInfoLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<City>> {
    private static final String DATABASE_NAME = "City_Database";
    private Bundle bundle;


    public WeatherInfoLoader(Context context, Bundle passed_bundle) {
        super(context);
        bundle = passed_bundle;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //In this loader we will fill our City List with its weather data.
    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<City> loadInBackground() {
        try {
            ArrayList<City> Active_City_List;

            City New_Loc = (City) bundle.getSerializable("New_City");

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

            CityDatabase Citydb = Room.databaseBuilder(ApplicationContextProvider.getContext(),
                    CityDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

            //Check Database for existing list
            Active_City_List = (ArrayList<City>) Citydb.getCityDao().getAllCities();
            //Look for an updated list
            ArrayList<City> Possible_Array = (ArrayList<City>) bundle.getSerializable("S");

            if (Possible_Array != null) { //If there is an update list, switch
                Active_City_List = new ArrayList<City>(Possible_Array);
            }

            if (New_Loc != null) {
                Active_City_List.add(New_Loc);
            }

            Active_City_List = Utils.Assign_Queue_Numbers(Active_City_List); //Assign Queue number on list

            if (Active_City_List == null) { //If all fails,create a new one
                Active_City_List = new ArrayList<City>();
            }


            if (pref.getBoolean("ShowLocation", false)) { //If current location is needed, it should be added

                Active_City_List.add(0, new City());

                Active_City_List.get(0).setLatitude(pref.getFloat("Latitude", 50));
                Active_City_List.get(0).setLongitude(pref.getFloat("Longitude", 30));
                //If current location becomes Fastiv,UA. An Error occured
            }

            //London is added as a first time usage.
            if (pref.getBoolean("AddLondon", false)) {
                Active_City_List.add(Active_City_List.size(), new City(51.51, -0.13));
                pref.edit().putBoolean("AddLondon", false).apply();
            }

            //FILL IN THE CITY ID's
            for (int i = 0; i < Active_City_List.size(); i++) {
                if (Active_City_List.get(i) != null) {
                    if ((Active_City_List.get(i).getCityID() == -1) || (Active_City_List.get(i).getCityName() == "" || Active_City_List.get(i).getCountry() == "")) {

                        URL url_for_reverse_geocoding = Utils.Create_URL_For_Reverse_Geocoding(Active_City_List.get(i).getLatitude(), Active_City_List.get(i).getLongitude());
                        String jsonResponse = Utils.makeHttpRequest(url_for_reverse_geocoding);
                        ArrayList<City> c = Utils.extractCityInfoFromJson(jsonResponse, 1);
                        if (c != null) {
                            if (Active_City_List.get(i).getFullName() == "") {
                                c.get(0).Fill_Full_Name();
                            } else {
                                c.get(0).setFullName(Active_City_List.get(i).getFullName());
                            }
                            Active_City_List.set(i, c.get(0));
                        }

                    }
                }
            }

            //Use City ID's to get weather info
            URL Request_url = Utils.Create_URL_For_Current_Weather_Request(Active_City_List);
            String jsonResponse = Utils.makeHttpRequest(Request_url);
            //if (Active_City_List != null) {
                if ((jsonResponse.equals("Banana") || jsonResponse.equals("")) && (Active_City_List.size() > 0)) {
                    //banana shows that an error occured.
                    return null;
                }
           // }

            //Get the current weather info from json
            ArrayList<Weather> Weather_Results = Utils.extractWeatherInfoFromJson(jsonResponse, false);
            //Assign Weather data to the current cities
            Utils.Assign_Current_Weather_to_City_List(Weather_Results, Active_City_List);
            //If all is well so far, lets clean the database and add them again.
            //This is needed for when the user only switches array locations
            Citydb.getCityDao().nukeTable();

            //Now it is time to get forecast
            //NOTE: THIS WONT BE NEEDED IF PREMIUM ACCOUNT IS BOUGHT FOR WEATHER
            for (int i = 0; i < Active_City_List.size(); i++) {

                Request_url = Utils.Create_URL_For_Forecast(Active_City_List.get(i).getLatitude(), Active_City_List.get(i).getLongitude());
                jsonResponse = Utils.makeHttpRequest(Request_url);
                Weather_Results = Utils.extractWeatherInfoFromJson(jsonResponse, true);


                if (pref.getString("Format", "0").equals("1")) {
                    if(Weather_Results != null){
                    Weather_Results = Utils.Average_Out_For_Daily(Weather_Results);}
                }

                Active_City_List.get(i).setForecastInfo(Weather_Results);
                if (!pref.getBoolean("ShowLocation", false) || i != 0) {
                    Citydb.getCityDao().insert(Active_City_List.get(i));
                }
            }


            //if (Active_City_List != null) {
            if (Active_City_List.size() > 0) {
                if (Active_City_List.get(0).getLatitude() == 50 && Active_City_List.get(0).getLongitude() == 30 && pref.getBoolean("ShowLocation", true)) {
                    Active_City_List.remove(0);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                                     public void run() {
                                         Toast.makeText(ApplicationContextProvider.getContext(), "Connection Error. Failed To Get Location", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                    );
                }
              //  }
            }


            deliverResult(Active_City_List);
            return Active_City_List;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    public void deliverResult(ArrayList<City> data) {
        if (isStarted()) {
            super.deliverResult(data);
        }
    }


}
