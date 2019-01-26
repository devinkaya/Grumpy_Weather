package com.amkdomaini.android.amkhavasi101.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.amkdomaini.android.amkhavasi101.Controllers.ApplicationContextProvider;

public class PreferencesActivity extends AppCompatActivity {


    SharedPreferences.OnSharedPreferenceChangeListener Preference_Change = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    if (key.equals("Vulgar") || key.equals("Format") || key.equals("Unit") || key.equals("ShowNotification") || key.equals("ShowLocation")) {
                        sharedPreferences.edit().putBoolean("Change", true).apply();
                    }
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext()).unregisterOnSharedPreferenceChangeListener(Preference_Change);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext()).registerOnSharedPreferenceChangeListener(Preference_Change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
                pref.edit().putBoolean("FromPreference", true).apply();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


        }
    }

}