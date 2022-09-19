package com.example.bikecompanion.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databases.relations.DeviceWithBikes;

import java.util.List;

@Dao
public interface EntitiesDao {

    //devices
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(Device device);

    @Update
    void updateDevice(Device device);

    @Delete
    void deleteDevice(Device device);

    @Query("DELETE FROM device_table")
    void deleteAllDevices();

    @Query("SELECT * FROM device_table ORDER BY deviceMacAddress DESC")
    LiveData<List<Device>> getAllDevices();


    //bikes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBike(Bike bike);

    @Update
    void updateBike(Bike bike);

    @Delete
    void deleteBike(Bike bike);

    @Query("DELETE FROM bike_table")
    void deleteAllBikes();

    @Query("SELECT * FROM bike_table ORDER BY bikeName ASC")
    LiveData<List<Bike>> getAllBikes();


    //Joint
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBikeDeviceCrossRef(BikeDeviceCrossRef bikeDeviceCrossRef);

    @Transaction
    @Query("SELECT * FROM bike_table WHERE bikeName = bikeName ")
    LiveData<List<BikeWithDevices>> getBikesWithDevices();

    @Transaction

    @Query("SELECT * FROM device_table WHERE deviceMacAddress = deviceMacAddress")
    LiveData<List<DeviceWithBikes>> getDevicesWithBikes();


}
