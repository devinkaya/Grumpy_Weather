package com.amkdomaini.android.amkhavasi101.Controllers;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.amkdomaini.android.amkhavasi101.Modules.City;
import com.amkdomaini.android.amkhavasi101.Modules.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public final class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static Random rand;

    private static final String Connection_Error = "Banana";

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static String makeHttpRequest (URL url) throws IOException{
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/*millisecond*/);
            urlConnection.setConnectTimeout(15000 /*millisecond*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                jsonResponse = Connection_Error;}
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the Weather JSON Response", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }

        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }
    public static ArrayList<City> extractCityInfoFromJson(String weatherJSON,int counter) {


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }
        boolean isOneCity = false;
        ArrayList<City> Complete_City_List = new ArrayList<City>();
        if(counter == 1){ isOneCity = true;}

        try {
            JSONObject baseJsonResponse = new JSONObject(weatherJSON);
            if(!isOneCity){
            JSONArray featureArray = baseJsonResponse.getJSONArray("list");


            while (featureArray.length() > 0 && counter > 0) {

                JSONObject firstFeature = featureArray.getJSONObject(0);

                int CityID = firstFeature.getInt("id");
                String CityName = firstFeature.getString("name");

                JSONObject Coordinates = firstFeature.getJSONObject("coord");
                JSONObject SysObject = firstFeature.getJSONObject("sys");

                String CountryName = SysObject.getString("country");
                double City_Lat = Coordinates.getDouble("lat");
                double City_Lon = Coordinates.getDouble("lon");

                Complete_City_List.add(new City(CityID,CityName,CountryName,City_Lat,City_Lon));

                counter--;
            }}
            else{
                int CityID = baseJsonResponse.getInt("id");
                String CityName = baseJsonResponse.getString("name");

                JSONObject Coordinates = baseJsonResponse.getJSONObject("coord");
                JSONObject SysObject = baseJsonResponse.getJSONObject("sys");

                String CountryName = SysObject.getString("country");
                double City_Lat = Coordinates.getDouble("lat");
                double City_Lon = Coordinates.getDouble("lon");

                Complete_City_List.add(new City(CityID,CityName,CountryName,City_Lat,City_Lon));
            }

            return Complete_City_List;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

    public static ArrayList<Weather> extractWeatherInfoFromJson(String weatherJSON, boolean forecast) {



        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }

        ArrayList<Weather> Results = new ArrayList<Weather>();

        try {
            JSONObject baseJsonResponse = new JSONObject(weatherJSON);
            JSONArray List_o_Weathers = baseJsonResponse.getJSONArray("list");
            float wind_angle;
            for(int i =0;List_o_Weathers.length() > i;i++) {
                JSONObject WeatherObject = List_o_Weathers.getJSONObject(i);
                JSONObject mainObject = WeatherObject.getJSONObject("main");
                JSONObject windObject = WeatherObject.getJSONObject("wind");
                JSONArray weatherArray = WeatherObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                JSONObject sysObject = WeatherObject.getJSONObject("sys");

                double temp = mainObject.getDouble("temp");
                double temp_max = mainObject.getDouble("temp_max");
                double temp_min = mainObject.getDouble("temp_min");
                float wind_speed = windObject.getLong("speed");

                if(windObject.has("deg"))
                { wind_angle = windObject.getLong("deg");}
                else{  wind_angle = 0;}

                int hum = mainObject.getInt("humidity");
                int pres = mainObject.getInt("pressure");
                int Cond = weatherObject.getInt("id");
                long sunrise;
                long sunset;
                if(!forecast){
                 sunrise = sysObject.getLong("sunrise");
                 sunset = sysObject.getLong("sunset");}
                else{
                     sunrise = 0;
                     sunset = 0;
                }
                long current_time;
                if (WeatherObject.has("dt")) {
                    current_time = WeatherObject.getLong("dt");
                } else {
                    current_time = System.currentTimeMillis() / 1000;
                }

                Results.add(new Weather(temp, temp_max, temp_min, wind_speed, wind_angle, hum, pres, Cond, sunrise, sunset, current_time));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the weather JSON results", e);
            return null;
        }

        return Results;
    }


    public static URL Create_URL_For_Current_Weather_Request(ArrayList<City> Main_List){

        String ID_List = "";

        if(Main_List == null || Main_List.size() == 0){
            return createUrl("");
        }

        for(int i = 0;Main_List.size()>i;i++){

            if(Main_List.get(i).getCityID() > 0){
                if(i > 0){
                    ID_List += ",";
                }
                ID_List += String.valueOf(Main_List.get(i).getCityID());
            }


        }

        String Request_URL = "http://api.openweathermap.org/data/2.5/group?id=" + ID_List + "&appid=" ;
        Request_URL += "2dfd61501b25b0d5f7c4fb86b3c493de";

        return createUrl(Request_URL);
    }


    public static void  Assign_Current_Weather_to_City_List(ArrayList<Weather> Weathers, ArrayList<City> Cities){
        if(Weathers == null || Cities == null){
            return;
        }
        if(Weathers.size() == Cities.size()){
            for(int i = 0;i<Weathers.size();i++){
                Cities.get(i).UpdateCurrentWeatherData(Weathers.get(i));
            }
        }
    }

    public static ArrayList<City> Create_Dummy_Initial_City_List(ArrayList<City> Active_Cities_List){
        if(Active_Cities_List != null){
            Active_Cities_List.clear();
        }
        if(Active_Cities_List == null){
            Active_Cities_List = new ArrayList<City>(4);
        }
        Active_Cities_List.add(new City(745044,"Istanbul,TR",1,1));
        Active_Cities_List.add(new City(5368361,"Los Angeles,US",1,1));
        Active_Cities_List.add(new City(2673730,"Stockholm,SE",1,1));
        Active_Cities_List.add(new City(323777,"Antalya,TR",1,1));
        Active_Cities_List.add(new City(2673730,"Moscow,RU",1,1));
        Active_Cities_List.add(new City(323777,"Kiev, UR",1,1));
        Active_Cities_List.add(new City(2673730,"London,UK",1,1));
        Active_Cities_List.add(new City(2673730,"Moscow,RU",1,1));
        Active_Cities_List.add(new City(323777,"Kiev, UR",1,1));
        Active_Cities_List.add(new City(2673730,"London,UK",1,1));
        return Active_Cities_List;
    }

    public static URL Create_URL_For_Reverse_Geocoding(double lat,double  lon){

        String Request_URL = "http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lat) +"&lon="+ String.valueOf(lon) + "&appid=" ;
        Request_URL += "2dfd61501b25b0d5f7c4fb86b3c493de";

        return createUrl(Request_URL);
    }
    public static URL Create_URL_For_Forecast(double lat,double  lon){

        String Request_URL = "http://api.openweathermap.org/data/2.5/forecast?lat=" + String.valueOf(lat) +"&lon="+ String.valueOf(lon) + "&appid=" ;
        Request_URL += "2dfd61501b25b0d5f7c4fb86b3c493de";

        return createUrl(Request_URL);
    }

    public static ArrayList<City> Assign_Queue_Numbers(ArrayList<City> queuelist) {
        if (queuelist == null) {
            return null;
        } else if(queuelist.size()>0) {
            for(int i = 0; i<queuelist.size();i++){
                queuelist.get(i).set_Queue_number(i);
            }
            return queuelist;
        }
        else
            return null;
    }

    public static String Create_Name_For_String(int id,int max,int min){
        Random r = new Random();
        int i = r.nextInt(max-min)+min;
        return "Quote_" + String.valueOf(id) + "_" + String.format("%03d",i);

    }

    public static int IsTheSunUp(long sunrise,long sunset){
        //0 Dark
        //1 Light
        //2 Sunrise
        //3 Sunset
        long current = System.currentTimeMillis()/1000;
        if(Math.abs(current - sunset)<(30*60))
        { return 3;}
        else if(Math.abs(current - sunrise)<(30*60))
        {return 2;}
        else if((current > sunrise) && (current < sunset))
        {return 1;}
        else {return 0;}
    }
    public static boolean IsTheSunUp_Definate(long sunrise,long sunset,long current){
        //True sun is up
        //False it is night
        return ((current > sunrise) && (current < sunset));

    }


    public static String FindWeatherIconLetterForDay(int id){
        switch(id/100) {
            case 2: //Thunderstorm
                return "U";
            case 3: //Drizzle
                return "S";
            case 5: //rain
                switch (id/10){
                    case 50: //Sunny Rain
                        return "F";
                    case 51: //Freezing Rain
                        return "W";
                    case 52: //Rainy Clouds
                        return "S";
                    default:
                        return "F";

                }
            case 6: // Snow
                return "W";
            case 7: // atmosphere
                switch (id){
                    case 781: //Tornado
                        return "e";
                    default:
                        return "N";

                }
            case 8:
                switch (id-((id/100)*100)){
                    case 0: //clear
                        return "A";
                    case 1: //few clouds
                        return "C";
                    case 2: //scattered
                        return "P";
                    case 3: //broken
                        return "G";
                    case 4: //overcast
                        return "G";
                    default:
                        return "A";

                }
            default:
                return "A";


        }

    }
    public static String FindWeatherIconLetterForNight(int id){
        switch(id/100) {
            case 2: //Thunderstorm
                return "U";
            case 3: //Drizzle
                return "S";
            case 5: //rain
                switch (id/10){
                    case 50: //Sunny Rain
                        return "K";
                    case 51: //Freezing Rain
                        return "W";
                    case 52: //Rainy Clouds
                        return "S";
                    default:
                        return "F";

                }
            case 6: // Snow
                return "W";
            case 7: // atmosphere
                switch (id){
                    case 781: //Tornado
                        return "e";
                    default:
                        return "N";

                }
            case 8:
                switch (id-((id/100)*100)){
                    case 0: //clear
                        return "I";
                    case 1: //few clouds
                        return "J";
                    case 2: //scattered
                        return "P";
                    case 3: //broken
                        return "P";
                    case 4: //overcast
                        return "P";
                    default:
                        return "I";

                }
            default:
                return "A";




        }

    }
    public static int FindImageForBackground(int id,boolean isitDay){
        switch(id/100) {
            case 2: //Thunderstorm
                return R.drawable.thunderstorm;
            case 3: //Drizzle
                return R.drawable.rain_sky;
            case 5: //rain
                switch (id/10){
                    case 50: //Sunny Rain
                        return R.drawable.sunny_rain;
                    default:
                        return R.drawable.rain_sky;

                }
            case 6: // Snow
                if(isitDay){
                return R.drawable.snowy_day;}
                else{return R.drawable.snowy_night;}
            case 7: // atmosphere
                switch (id){
                    case 781: //Tornado
                        return R.drawable.rain_sky;
                    default:
                        if(isitDay){
                            return R.drawable.mist_sky;}
                        return R.drawable.misty_night;

                }
            case 8:
                switch (id-((id/100)*100)){
                    case 0: //clear
                        if(isitDay){
                            return R.drawable.clear_background;}
                        else{return R.drawable.clear_night_sky;}
                    case 1: //few clouds
                        if(isitDay){
                            return R.drawable.scattered_clouds;}
                        else{return R.drawable.cloudy_night_sky;}
                    case 2: //scattered
                        if(isitDay){
                            return R.drawable.cloudy_sky;}
                        else{return R.drawable.cloudy_night_sky;}
                    case 3: //broken
                        if(isitDay){
                            return R.drawable.cloudy_sky;}
                        else{return R.drawable.cloudy_night_sky;}
                    case 4: //overcast
                        if(isitDay){
                            return R.drawable.overcast_sky;}
                        else{return R.drawable.cloudy_night_sky;}
                    default:
                        return R.drawable.clear_background;

                }
            default:
                return R.drawable.clear_background;




        }
    }
    public static Weather Average_Out(@NonNull ArrayList<Weather> WeatherList){
        Weather Product = WeatherList.get(0);
        for (int i = 1;i<WeatherList.size();i++){
            Product.add(WeatherList.get(i));
        }
        Product.divide(WeatherList.size());
        return Product;
    }

    public static ArrayList<Weather> Average_Out_For_Daily(ArrayList<Weather> WeatherList){
        ArrayList<Weather> temp = new ArrayList<Weather>();
        ArrayList<Weather> Final_List = new ArrayList<Weather>();
        temp.add(WeatherList.get(0));

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();


        for(int i = 1;i<WeatherList.size();i++){
            cal1.setTime(new java.util.Date(WeatherList.get(i).getmTime()*1000L));
            cal2.setTime(new java.util.Date(WeatherList.get(i - 1).getmTime()*1000L));

            if( cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)){
                Final_List.add(Average_Out(temp));
                temp.clear();
            }
            temp.add(WeatherList.get(i));
            cal1.clear();
            cal2.clear();
        }

        if(temp.size()!= 0){Final_List.add(Average_Out(temp));}

        return Final_List;
    }
    public static int Find_Condition_Position(int id){
       String[] temp =  ApplicationContextProvider.getContext().getResources().getStringArray(R.array.condition_ids);
       for(int i = 0;i<temp.length;i++){
           if(temp[i].contains(String.valueOf(id))){
               return i;
           }
       }

       return -1;

    }

    public static int Get_Temp_Index_For_Quote(double temp){
        if(temp >30){
            return 4;
        }else if(temp > 20){
            return 3;
        }else if(temp > 7){
            return 2;
        }else if(temp > -15){
            return 1;
        }else{return 0;}
    }

    public static String Get_Quote(int id,double temp){
        int heat_pos;
        String[] Quote_array;
        Resources Res = ApplicationContextProvider.getContext().getResources();
        TypedArray Results;
        switch(id){
            case 800:
                Results = Res.obtainTypedArray(R.array.Quote_800);
                break;
            case 802:
            case 801:
                Results = Res.obtainTypedArray(R.array.Quote_801_802);
                break;
            case 771:
                Results = Res.obtainTypedArray(R.array.Quote_771);
                break;
            case 781:
                Results = Res.obtainTypedArray(R.array.Quote_781);
                break;
            case 511:
                Results = Res.obtainTypedArray(R.array.Quote_511);
                break;
            case 501:
            case 502:
                Results = Res.obtainTypedArray(R.array.Quote_501_502);
                break;
            default:
                switch ((int) Math.floor(id/10)){
                    case 50:
                        Results = Res.obtainTypedArray(R.array.Quote_50x);
                        break;
                    case 52:
                        Results = Res.obtainTypedArray(R.array.Quote_52x);
                        break;
                    case 80:
                        Results = Res.obtainTypedArray(R.array.Quote_80x);
                        break;
                    default:
                        switch ((int) Math.floor(id/100)){
                            case 2:
                                Results = Res.obtainTypedArray(R.array.Quote_2xx);
                                break;
                            case 3:
                                Results = Res.obtainTypedArray(R.array.Quote_3xx);
                                break;
                            case 6:
                                Results = Res.obtainTypedArray(R.array.Quote_6xx);
                                break;
                            case 7:
                                Results = Res.obtainTypedArray(R.array.Quote_7xx);
                                break;
                            default:
                                Results = Res.obtainTypedArray(R.array.Quote_800);
                        }
                        break;
                }
                break;
        }

        int l = Results.length();
        if(l == 1){
           heat_pos = Results.getResourceId(0,0);
        }else {
            heat_pos = Results.getResourceId(Utils.Get_Temp_Index_For_Quote(temp),0);
        }
        Quote_array = Res.getStringArray(heat_pos);
        Results.recycle();
        if(Quote_array.length >0){
            rand = new Random();
            l = rand.nextInt(Quote_array.length);
            return Quote_array[l];
        }else{
            return "";
        }

    }

    public static String get_Kelvin_Quote(){
        Resources Res = ApplicationContextProvider.getContext().getResources();
        String[] Quote_array = Res.getStringArray(R.array.Kelvin_Quotes);
        if(Quote_array.length >0){
            rand = new Random();
            int l = rand.nextInt(Quote_array.length);
            return Quote_array[l];
        }else{
            return "";
        }
    }
    public static boolean Same_City_List(ArrayList<City> List1,ArrayList<City> List2){
        if(List1 == null || List2 == null){
            return false;
        }
        if(List1.size() != List2.size()){
            return false;
        }else if(List1.size() == 0){
            return true;
        }

        for(int i = 0;i<List1.size();i++){
            if(List1.get(i).getCityID() != List2.get(i).getCityID()){
                return false;
            }
        }
        return true;

    }

}
