package com.amkdomaini.android.amkhavasi101.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amkdomaini.android.amkhavasi101.Controllers.WeatherInfoLoader;
import com.amkdomaini.android.amkhavasi101.Modules.City;
import com.amkdomaini.android.amkhavasi101.Fragments.WeatherFragment;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private static int NUMBER_OF_PAGES;
    private static ArrayList<City> Active_Cities_List = new ArrayList<City>();
    private WeatherInfoLoader loader;

    @SuppressWarnings("unchecked")
    public PagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        if (bundle != null) {
            Active_Cities_List = (ArrayList<City>) bundle.getSerializable("City_List");
        }
        if (Active_Cities_List != null) {
            NUMBER_OF_PAGES = Active_Cities_List.size();
            notifyDataSetChanged();
        }
    }


    private void Change_Page_Number(int new_page_number) {
        if (new_page_number > 0 && new_page_number < 10) {
            NUMBER_OF_PAGES = new_page_number;
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {

        if (Active_Cities_List == null) {
            return WeatherFragment.newInstance(null, "City,Country", position == 0);
        }

        if (true /*Active_Cities_List != null*/) {
            if (Active_Cities_List.size() != NUMBER_OF_PAGES) {
                Change_Page_Number(Active_Cities_List.size());
            }
            if (position >= 0 && position < Active_Cities_List.size()) {
                if (Active_Cities_List.get(position).getCurrentWeatherInfo() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Current_City", Active_Cities_List.get(position));
                    return WeatherFragment.newInstance(bundle, Active_Cities_List.get(position).getFullName(), position == 0);
                } else {
                    return WeatherFragment.newInstance(null, Active_Cities_List.get(position).getFullName(), position == 0);
                }
            } else
                return null;
        } else
            return null;
    }

    @Override
    public int getCount() {
        if (Active_Cities_List != null) {
            return Active_Cities_List.size();
        } else
            return -1;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}

