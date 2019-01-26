package com.amkdomaini.android.amkhavasi101.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.amkdomaini.android.amkhavasi101.Activities.MainActivity;
import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(new ApplicationContextProvider().getContext());

        setContentView(R.layout.splash_screen);

        Button mButton_off = findViewById(R.id.splash_button_off);
        Button mButton_on = findViewById(R.id.splash_button_on);

        mButton_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putBoolean("FirstTime", false).apply();
                pref.edit().putBoolean("AddLondon", true).apply();
                pref.edit().putString("Vulgar", "0").apply();
                pref.edit().putString("Unit", "0").apply();
                Intent mintent = new Intent(new ApplicationContextProvider().getContext(), MainActivity.class);
                startActivity(mintent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        mButton_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putBoolean("FirstTime", false).apply();
                pref.edit().putBoolean("AddLondon", true).apply();
                pref.edit().putString("Vulgar", "1").apply();
                pref.edit().putString("Unit", "0").apply();
                Intent mintent = new Intent(ApplicationContextProvider.getContext(), MainActivity.class);
                startActivity(mintent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

}
