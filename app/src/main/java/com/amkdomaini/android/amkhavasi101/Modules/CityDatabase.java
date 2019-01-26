package com.amkdomaini.android.amkhavasi101.Modules;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.amkdomaini.android.amkhavasi101.Interfaces.CityDao;

@Database(entities = {City.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {

    private static final String DB_NAME = "CityDatabase.db";
    //   private static volatile CityDatabase instance;

//    static synchronized CityDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = create(context);
//        }
//        return instance;
//    }

    private static CityDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                CityDatabase.class,
                DB_NAME).build();
    }

    public abstract CityDao getCityDao();
}