package com.example.bikecompanion.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bikecompanion.ui.myDevices.MyDevice;

import java.util.List;

@Dao
public interface MyDevicesDao {

    @Insert
    void insert(MyDevice device);

    @Update
    void update(MyDevice device);

    @Delete
    void delete(MyDevice device);

    @Query("DELETE FROM device_table")
    void deleteAllDevices();

    @Query("SELECT * FROM device_table ORDER BY id DESC")
    LiveData<List<MyDevice>> getAllDevices();

}
