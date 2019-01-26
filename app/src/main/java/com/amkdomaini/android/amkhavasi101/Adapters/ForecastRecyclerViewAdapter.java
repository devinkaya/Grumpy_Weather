package com.amkdomaini.android.amkhavasi101.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;
import com.amkdomaini.android.amkhavasi101.Controllers.Utils;
import com.amkdomaini.android.amkhavasi101.Modules.Weather;

import java.util.ArrayList;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.ViewHolder> {

    //USE AS RECYCLERVIEW
    private ArrayList<Weather> mForecast_Array;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean IsItNight;
    private long sunrise; //time
    private long sunset;

    ForecastRecyclerViewAdapter(Context context, ArrayList<Weather> Forecasts, boolean IsNight, long Sunrise, long Sunset) {
        this.mInflater = LayoutInflater.from(context);
        this.mForecast_Array = Forecasts;
        this.IsItNight = IsNight;
        this.sunrise = Sunrise;
        this.sunset = Sunset;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.forecast_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather mCurrentWeather = mForecast_Array.get(position);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
        java.util.Date mDate = new java.util.Date(mCurrentWeather.getmTime() * 1000L);

        //Switch color if it is dark background
        if (IsItNight) {
            holder.myDayView.setTextColor(Color.WHITE);
            holder.myTimeView.setTextColor(Color.WHITE);
            holder.myIconView.setTextColor(Color.WHITE);
            holder.myMaxTempView.setTextColor(Color.WHITE);
        } else {
            holder.myDayView.setTextColor(Color.BLACK);
            holder.myTimeView.setTextColor(Color.BLACK);
            holder.myIconView.setTextColor(Color.BLACK);
            holder.myMaxTempView.setTextColor(Color.BLACK);
        }
        holder.myDayView.setText(DateFormat.format("EEEE", mDate).subSequence(0, 3));

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

        switch (pref.getString("Format", "0")) { //Forecast format
            case "0":
                holder.myTimeView.setVisibility(View.VISIBLE);
                holder.myTimeView.setText(DateFormat.format("HH:mm", mDate));
                break;
            case "1":
                holder.myTimeView.setVisibility(View.INVISIBLE);
                break;
        }
        //Weather Icon must match with the time of the day
        if (Utils.IsTheSunUp_Definate(sunrise % (24 * 60 * 60), sunset % (24 * 60 * 60), mCurrentWeather.getmTime() % (24 * 60 * 60)) || (pref.getString("Format", "0").equals("1"))) {
            holder.myIconView.setText(Utils.FindWeatherIconLetterForDay(mCurrentWeather.getCondition()));
        } else {
            holder.myIconView.setText(Utils.FindWeatherIconLetterForNight(mCurrentWeather.getCondition()));
        }


        //Pull string for temperature
        String temp;
        switch (Integer.parseInt(prefs.getString("Unit", "0"))) {
            case 0:
                temp = String.valueOf(Math.round(mCurrentWeather.getMaxTemperature_Celcius() * 10) / 10) + "/" + String.valueOf(Math.round(mCurrentWeather.getMinTemperature_Celcius() * 10) / 10);
                break;
            case 1:
                temp = String.valueOf(Math.round(mCurrentWeather.getMaxTemperature_Fahrenheit() * 10) / 10) + "/" + String.valueOf(Math.round(mCurrentWeather.getMinTemperature_Fahrenheit() * 10) / 10);
                break;
            case 2:
                temp = String.valueOf(Math.round(mCurrentWeather.getMaxTemperature_Kelvin() * 10) / 10) + "/" + String.valueOf(Math.round(mCurrentWeather.getMinTemperature_Kelvin() * 10) / 10);
                break;
            default:
                temp = String.valueOf(Math.round(mCurrentWeather.getMaxTemperature_Celcius() * 10) / 10) + "/" + String.valueOf(Math.round(mCurrentWeather.getMinTemperature_Celcius() * 10) / 10);
                break;
        }
        if (pref.getString("Format", "0").equals("0")) { //Dont show max,min when its hourly. They are rarely different.
            int i = temp.indexOf("/");
            temp = temp.substring(0, i);
        }

        holder.myMaxTempView.setText(temp);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mForecast_Array == null) {
            return 0;
        } else {
            return mForecast_Array.size();
        }
    }

    // convenience method for getting data at click position
    public Weather getItem(int id) {
        return mForecast_Array.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myDayView;
        TextView myTimeView;
        TextView myIconView;
        TextView myMaxTempView;

        ViewHolder(View itemView) {
            super(itemView);
            myDayView = itemView.findViewById(R.id.day_view);
            myTimeView = itemView.findViewById(R.id.time_view);
            myIconView = itemView.findViewById(R.id.forecast_iconview);
            myMaxTempView = itemView.findViewById(R.id.max_temp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


}