package com.amkdomaini.android.amkhavasi101.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.amkdomaini.android.amkhavasi101.Modules.City;

import java.util.List;

@Dao
public interface CityDao {

    @Query("SELECT * FROM city ORDER BY mQueue_number ASC")
    List<City> getAllCities();

    @Insert(onConflict =  OnConflictStrategy.REPLACE )
    void insert(City... cities);

    @Query("SELECT COUNT(mCityID) FROM city")
    int getDataCount();

    @Query("SELECT * FROM city WHERE mCityID == :queryid")
    City FindCitybyId(int queryid);

    @Update
    void update(City... cities);

    @Delete
    void delete(City... cities);

    @Query("DELETE FROM city")
    public void nukeTable();

}
