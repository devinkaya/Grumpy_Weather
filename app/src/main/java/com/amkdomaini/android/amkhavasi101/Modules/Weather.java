package com.amkdomaini.android.amkhavasi101.Modules;

import java.io.Serializable;


public class Weather implements Serializable {
    private double mTemperature;
    private double mTemperature_Max;
    private double mTemperature_Min;
    private float mWind_Speed;
    private float mWind_Angle;
    private int mHumidity;
    private int mPressure;
    private int mCondition;
    private long mSunrise_Time;
    private long mSunset_Time;
    private long mTime;
    public Weather() {
        mTemperature = 0;
        mTemperature_Max = 0;
        mTemperature_Min = 0;
        mWind_Angle = -1;
        mWind_Speed = 0;
        mHumidity = 0;
        mPressure = 0;
        mCondition = 0;
        mSunrise_Time = 0;
        mSunset_Time = 0;
        mTime = 0;
    }
    public Weather(double Temperature, double Max_Temp, double Min_Temp, float Wind_Speed, float Wind_Angle, int Humidity, int Pressure, int Condition, long Sunrise_Time, long Sunset_Time, long Time) {
        mTemperature = Temperature;
        mTemperature_Max = Max_Temp;
        mTemperature_Min = Min_Temp;
        mWind_Speed = Wind_Speed;
        mWind_Angle = Wind_Angle;
        mHumidity = Humidity;
        mPressure = Pressure;
        mCondition = Condition;
        mSunrise_Time = Sunrise_Time;
        mSunset_Time = Sunset_Time;
        mTime = Time;
    }

    public long getSunrise_Time() {
        return mSunrise_Time;
    }

    public long getSunset_Time() {
        return mSunset_Time;
    }

    public long getmTime() {
        return mTime;
    }

    public double getTemperature_Kelvin() {
        return mTemperature;
    }

    public double getTemperature_Celcius() {
        return mTemperature - 273.15;
    }

    public double getTemperature_Fahrenheit() {
        return ((mTemperature - 273.15) * 1.8) + 32;
    }

    public double getMaxTemperature_Kelvin() {
        return mTemperature_Max;
    }

    public double getMaxTemperature_Celcius() {
        return mTemperature_Max - 273.15;
    }

    public double getMaxTemperature_Fahrenheit() {
        return ((mTemperature_Max - 273.15) * 1.8) + 32;
    }

    public double getMinTemperature_Kelvin() {
        return mTemperature_Min;
    }

    public double getMinTemperature_Celcius() {
        return mTemperature_Min - 273.15;
    }

    public double getMinTemperature_Fahrenheit() {
        return ((mTemperature_Min - 273.15) * 1.8) + 32;
    }

    //The following were not used in the app, however could be added for later releases.
    public int getHumidity() {
        return mHumidity;
    }

    public int getPressure() {
        return mPressure;
    }

    public float getWind_Angle() {
        return mWind_Angle;
    }

    public float getWind_Speed() {
        return mWind_Speed;
    }

    public int getCondition() {
        return mCondition;
    }

    public void add(Weather toAdd) { //Adds two weather objects together
        mTemperature += toAdd.getTemperature_Kelvin();
        if (toAdd.getMaxTemperature_Kelvin() > mTemperature_Max) {
            mTemperature_Max = toAdd.getMaxTemperature_Kelvin();
        }
        if (toAdd.getMinTemperature_Kelvin() < mTemperature_Min) {
            mTemperature_Min = toAdd.getMinTemperature_Kelvin();
        }
        if (toAdd.getCondition() < mCondition) {
            mCondition = toAdd.getCondition();
        }
    }

    public void divide(int a) {
        mTemperature = mTemperature / a;
    } //Only divides mTemperature;


}