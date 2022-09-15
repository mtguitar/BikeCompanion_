package com.example.bikecompanion.databases.bike;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bikecompanion.databases.entities.Bike;

import java.util.List;

@Dao
public interface MyBikesDao {

    @Insert
    void insert(Bike bike);

    @Update
    void update(Bike bike);

    @Delete
    void delete(Bike bike);

    @Query("DELETE FROM bike_table")
    void deleteAllBikes();

    @Query("SELECT * FROM bike_table ORDER BY bikeName DESC")
    LiveData<List<Bike>> getAllBikes();

}
