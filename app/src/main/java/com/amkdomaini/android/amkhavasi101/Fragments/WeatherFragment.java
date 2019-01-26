package com.amkdomaini.android.amkhavasi101.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amkdomaini.android.amkhavasi101.Adapters.ForecastRecyclerViewAdapter;
import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;
import com.amkdomaini.android.amkhavasi101.Controllers.Utils;
import com.amkdomaini.android.amkhavasi101.Modules.City;


public class WeatherFragment extends Fragment implements ForecastRecyclerViewAdapter.ItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Degree";
    private static final String ARG_PARAM2 = "City";

    private int pos;
    private ForecastRecyclerViewAdapter mAdapter;
    private OnFragmentInteractionListener mListener;

    public WeatherFragment() {
        // Required empty public constructor
    }


    public static WeatherFragment newInstance(Bundle passed_bundle, String city_string, boolean first) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, passed_bundle);
        args.putString(ARG_PARAM2, city_string);
        args.putBoolean("First", first);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        TextView QuoteView = view.findViewById(R.id.QuoteView);
        TextView Degree = view.findViewById(R.id.DegreeView);
        ImageView BackgroundImageView = view.findViewById(R.id.Night_sky);
        TextView CityView = view.findViewById(R.id.CityNameView);
        TextView IconView = view.findViewById(R.id.IconView);
        TextView TempUnit = view.findViewById(R.id.TempUnitView);
        RecyclerView recyclerView = view.findViewById(R.id.forecast_recyclerview);

        if (getArguments() != null) {
            CityView.setText(getArguments().getString(ARG_PARAM2));
        }

        boolean IsItNight;


        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
        //Check for Nullpointerexception
        if (getArguments() != null) {
            if (getArguments().getBundle(ARG_PARAM1) == null) {
                //If no city is accessed this blank screen should appear.
                Degree.setText(getResources().getString(R.string.Dash));
                QuoteView.setText(getResources().getString(R.string.Nothing));
            } else {
                Bundle mBundle = getArguments().getBundle(ARG_PARAM1);
                if (mBundle != null) { //Nullcheck

                    final City Current = (City) mBundle.getSerializable("Current_City");

                    if (Current != null) {
                        int current_condition = Current.getCurrentWeatherInfo().getCondition();

                        switch (Integer.parseInt(pref.getString("Unit", "0"))) {
                            case 0:
                                Degree.setText(String.valueOf(Math.round(Current.getCurrentWeatherInfo().getTemperature_Celcius() * 10) / 10));
                                TempUnit.setText(" l");//Celcius sign in font
                                break;
                            case 1:
                                Degree.setText(String.valueOf(Math.round(Current.getCurrentWeatherInfo().getTemperature_Fahrenheit() * 10) / 10));
                                TempUnit.setText(" m");//Fahrenheit sign in font
                                break;
                            case 2:
                                Degree.setText(String.valueOf(Math.round(Current.getCurrentWeatherInfo().getTemperature_Kelvin() * 10) / 10));
                                TempUnit.setText("");//Kelvin doesn't need a sign
                                break;
                            default:
                                Degree.setText(String.valueOf(Math.round(Current.getCurrentWeatherInfo().getTemperature_Celcius() * 10) / 10));
                                TempUnit.setText(" l");
                                break;
                        }
                        Degree.setTextSize(80);
                        TempUnit.setTextSize(60);
                        CityView.setTextSize(25);
                        IsItNight = false;

                        switch (Utils.IsTheSunUp(Current.getCurrentWeatherInfo().getSunrise_Time(), Current.getCurrentWeatherInfo().getSunset_Time())) {
                            case 0:
                                Degree.setTextColor(Color.WHITE);
                                QuoteView.setTextColor(Color.WHITE);
                                CityView.setTextColor(Color.WHITE);
                                IconView.setTextColor(Color.WHITE);
                                TempUnit.setTextColor(Color.WHITE);
                                BackgroundImageView.setImageResource(Utils.FindImageForBackground(current_condition, false));
                                break;
                            case 1:
                                BackgroundImageView.setImageResource(Utils.FindImageForBackground(current_condition, true));
                                break;
                            case 2:
                                BackgroundImageView.setImageResource(R.drawable.sunrise); //Sunrise and Sunset images are different
                                break;
                            case 3:
                                BackgroundImageView.setImageResource(R.drawable.sunset);
                                break;
                        }
                        //In our case, all conditions that start with 2,3 and 5
                        switch ((int) Math.floor(current_condition / 100)) {
                            case 2:
                                Degree.setTextColor(Color.WHITE);
                                QuoteView.setTextColor(Color.WHITE);
                                CityView.setTextColor(Color.WHITE);
                                IconView.setTextColor(Color.WHITE);
                                TempUnit.setTextColor(Color.WHITE);
                                break;
                            case 3:
                                Degree.setTextColor(Color.WHITE);
                                QuoteView.setTextColor(Color.WHITE);
                                CityView.setTextColor(Color.WHITE);
                                IconView.setTextColor(Color.WHITE);
                                TempUnit.setTextColor(Color.WHITE);
                                break;
                            case 5:
                                Degree.setTextColor(Color.WHITE);
                                QuoteView.setTextColor(Color.WHITE);
                                CityView.setTextColor(Color.WHITE);
                                IconView.setTextColor(Color.WHITE);
                                TempUnit.setTextColor(Color.WHITE);
                                break;
                        }
                        //Find the conditions position for quote
                        pos = Utils.Find_Condition_Position(current_condition);

                        if (pref.getString("Vulgar", "1").equals("1")) {
                            if (pref.getString("Unit", "0").equals("2")) {
                                //Bring up Quotes for Kelvin if Kelvin is used
                                QuoteView.setText(Utils.get_Kelvin_Quote());
                            } else {
                                //Or get the regular quotes
                                QuoteView.setText(Utils.Get_Quote(Current.getCurrentWeatherInfo().getCondition(), Current.getCurrentWeatherInfo().getTemperature_Celcius()));
                            }
                        } else {
                            //Offensive language is off
                            QuoteView.setText(getResources().getStringArray(R.array.conditions)[pos]);
                        }
                        //Assign Icons, dependant on day or night
                        if (Utils.IsTheSunUp_Definate(Current.getCurrentWeatherInfo().getSunrise_Time(), Current.getCurrentWeatherInfo().getSunset_Time(), Current.getCurrentWeatherInfo().getmTime())) {
                            IconView.setText(Utils.FindWeatherIconLetterForDay(Current.getCurrentWeatherInfo().getCondition()));
                        } else {
                            IconView.setText(Utils.FindWeatherIconLetterForNight(Current.getCurrentWeatherInfo().getCondition()));
                        }

                        IconView.setVisibility(View.VISIBLE);
                        IconView.setTextSize(100);

                        //User can press on Icon to see what it is
                        if (pos != -1) {
                            IconView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ApplicationContextProvider.getContext(), getResources().getStringArray(R.array.conditions)[pos], Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        recyclerView.setHasFixedSize(true);
                        //Make it more readable with shadow background
                        int rad = 2;
                        IconView.setShadowLayer(rad, 2, 2, R.color.text_shadow_white);
                        Degree.setShadowLayer(rad, 2, 2, R.color.text_shadow_white);
                        TempUnit.setShadowLayer(rad, 2, 2, R.color.text_shadow_white);
                        CityView.setShadowLayer(rad, 2, 2, R.color.text_shadow_white);


                        LinearLayoutManager horizontalLayoutManager
                                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(horizontalLayoutManager);


                        switch (pref.getString("Format", "0")) {
                            case "1"://Daily Forecast
                                Current.setForecastInfo(Utils.Average_Out_For_Daily(Current.getForecastInfo()));
                                mAdapter = new ForecastRecyclerViewAdapter(ApplicationContextProvider.getContext(), Current.getForecastInfo(), IsItNight, Current.getCurrentWeatherInfo().getSunrise_Time(), Current.getCurrentWeatherInfo().getSunset_Time());
                                recyclerView.setAdapter(mAdapter);
                                break;
                            case "0": // Hourly Forecast
                                mAdapter = new ForecastRecyclerViewAdapter(ApplicationContextProvider.getContext(), Current.getForecastInfo(), IsItNight, Current.getCurrentWeatherInfo().getSunrise_Time(), Current.getCurrentWeatherInfo().getSunset_Time());
                                recyclerView.setAdapter(mAdapter);
                                break;
                        }
                    }
                }
            }
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        int p = Utils.Find_Condition_Position(mAdapter.getItem(position).getCondition());
        Toast.makeText(ApplicationContextProvider.getContext(), getResources().getStringArray(R.array.conditions)[p], Toast.LENGTH_SHORT).show();
    }


    public interface OnFragmentInteractionListener {
        //Required if Interraction will be implemented to Fragment
        void onFragmentInteraction(Uri uri);
    }

}
