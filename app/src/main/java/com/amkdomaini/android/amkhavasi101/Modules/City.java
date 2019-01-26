package com.amkdomaini.android.amkhavasi101.Modules;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;


@Entity
public class City implements Serializable {

    @NonNull
    private int mQueue_number = -1;
    private double mLatitude = 200;
    private double mLongitude = 200;
    private String mCityName = "";
    private String mCountry = "";
    //After seeing the Google Places API, a full name string seperate from City and country was needed.
    private String mFullName = "";
    @PrimaryKey
    private int mCityID = -1;
    //Key is same ID as the one from OpenWeatherMap.org

    //The following can be made into one if the premium account is purchased
    @Ignore //FORECAST ArrayList
    private ArrayList<Weather> mWeatherForecastInfo;
    @Ignore //CURRENT Object
    private Weather mCurrentWeatherInfo;

    public City(int mQueue_number, double mLatitude, double mLongitude, String mCityName, String mCountry, String mFullName, int mCityID, ArrayList<Weather> mWeatherForecastInfo, Weather mCurrentWeatherInfo) {
        this.mQueue_number = mQueue_number;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mCityName = mCityName;
        this.mCountry = mCountry;
        this.mFullName = mFullName;
        this.mCityID = mCityID;
        this.mWeatherForecastInfo = mWeatherForecastInfo;
        this.mCurrentWeatherInfo = mCurrentWeatherInfo;
    }

    public City(int mQueue_number, double mLatitude, double mLongitude, int mCityID, ArrayList<Weather> mWeatherForecastInfo, Weather mCurrentWeatherInfo) {
        this.mQueue_number = mQueue_number;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mCityID = mCityID;
        this.mWeatherForecastInfo = mWeatherForecastInfo;
        this.mCurrentWeatherInfo = mCurrentWeatherInfo;
    }

    public City(int mCityID, double mLatitude, double mLongitude) {

        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mCityID = mCityID;
    }

    public City() {
        this.mQueue_number = -1;
        this.mLatitude = 200;
        this.mLongitude = 200;
        this.mCityName = "";
        this.mCountry = "";
        this.mFullName = "";
        this.mCityID = 0;
        this.mWeatherForecastInfo = null;
        this.mCurrentWeatherInfo = null;
    }

    public City(int ID) {
        mCityID = ID;
    }


    public City(double Lat, double Lon) {
        mLatitude = Lat;
        mLongitude = Lon;
    }

    public City(double Lat, double Lon, int ID) {
        mLatitude = Lat;
        mLongitude = Lon;
        mCityID = ID;

    }

    public City(int ID, String CityName, String Country, double Lat, double Lon) {
        mCityID = ID;
        mCityName = CityName;
        mCountry = Country;
        mLatitude = Lat;
        mLongitude = Lon;
    }


    public City(int ID, String CityFullName, double Lat, double Lon) {
        mCityID = ID;
        mFullName = CityFullName;
        mLatitude = Lat;
        mLongitude = Lon;
        mCurrentWeatherInfo = new Weather();
    }

    public void Fill_Full_Name() //Useful for current location. Forms full name from city and country.
    {
        if (mCityName != null && mCountry != null) {
            mFullName = mCityName + ", " + mCountry;
        } else {
            mWeatherForecastInfo = null;
        }
    }

    public int getQueue_number() {
        return mQueue_number;
    }

    public void setQueue_number(int mQueue_number) {
        this.mQueue_number = mQueue_number;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void set_Queue_number(int Number) {
        this.mQueue_number = Number;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public int getCityID() {
        return mCityID;
    }

    public void setCityID(int mCityID) {
        this.mCityID = mCityID;
    }

    public ArrayList<Weather> getForecastInfo() {
        return mWeatherForecastInfo;
    }

    public void setForecastInfo(ArrayList<Weather> new_list) {
        this.mWeatherForecastInfo = new_list;
    }

    public Weather getCurrentWeatherInfo() {
        return this.mCurrentWeatherInfo;
    }

    public double getLatitude() {

        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void UpdateWeatherForecastData(ArrayList<Weather> List) {
        this.mWeatherForecastInfo = List;
    }

    public void UpdateCurrentWeatherData(Weather data) {
        this.mCurrentWeatherInfo = data;
    }

//    boolean CityInfoComplete() {
//        if (mLongitude != 200 && mLatitude != 200 && mCityName != null && mFullName != null && mCountry != null && mFullName != null && mCityID != -1) {
//            return true;
//        } else {
//            return false;
//        }
//    }

}

