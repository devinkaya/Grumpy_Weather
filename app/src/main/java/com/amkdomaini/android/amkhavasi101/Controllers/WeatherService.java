package com.amkdomaini.android.amkhavasi101.Controllers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.amkdomaini.android.amkhavasi101.Activities.MainActivity;
import com.amkdomaini.android.amkhavasi101.Modules.City;

import static com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider.CHANNEL_ID;

public class WeatherService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopSelf();
            return START_STICKY;
        }

        City input = (City) intent.getSerializableExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        String[] condition_array = ApplicationContextProvider.getContext().getResources().getStringArray(R.array.conditions);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

        String output = "";
        output += input.getFullName();
        output += " - ";

        if (input.getCurrentWeatherInfo() != null) {
            //Add Current Temperature for notification
            switch (prefs.getString("Unit", "0")) {
                case "0":
                    output += String.valueOf(Math.round(input.getCurrentWeatherInfo().getTemperature_Celcius()));
                    output += "\u00b0" + "C";
                    break;
                case "1":
                    output += String.valueOf(Math.round(input.getCurrentWeatherInfo().getTemperature_Fahrenheit()));
                    output += "\u00b0" + "F";
                    break;
                case "2":
                    output += String.valueOf(Math.round(input.getCurrentWeatherInfo().getTemperature_Kelvin()));
                    output += "K";
                    break;
                default:
                    output += String.valueOf(Math.round(input.getCurrentWeatherInfo().getTemperature_Celcius()));
                    output += "\u00b0" + "C";
                    break;
            }
        }
        String desc = "";
        //If Offensive language is on, show a quote. Else, show the condition as description
        if (prefs.getString("Vulgar", "1").equals("1")) {
            desc = Utils.Get_Quote(input.getCurrentWeatherInfo().getCondition(), input.getCurrentWeatherInfo().getTemperature_Celcius());
        } else {
            desc = condition_array[Utils.Find_Condition_Position(input.getCurrentWeatherInfo().getCondition())];
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(output)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                .setContentText(desc)
                .setLargeIcon(textAsBitmap(Utils.FindWeatherIconLetterForDay(input.getCurrentWeatherInfo().getCondition()), 70, Color.BLACK))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //stopSelf();

        return START_STICKY;
    }
    //Turn the font into bitmap for notification
    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Typeface mfont = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(), "weathertime.ttf");
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(mfont);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
