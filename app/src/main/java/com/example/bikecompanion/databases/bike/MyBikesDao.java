package com.example.bikecompanion.databases.bike;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bikecompanion.databases.devices.MyDevice;

import java.util.List;

@Dao
public interface MyBikesDao {

    @Insert
    void insert(MyBike bike);

    @Update
    void update(MyBike bike);

    @Delete
    void delete(MyBike bike);

    @Query("DELETE FROM bike_table")
    void deleteAllBikes();

    @Query("SELECT * FROM bike_table ORDER BY id DESC")
    LiveData<List<MyBike>> getAllBikes();

}
