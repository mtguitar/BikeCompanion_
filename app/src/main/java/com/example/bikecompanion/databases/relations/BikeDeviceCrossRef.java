package com.example.bikecompanion.databases.relations;


import androidx.room.Entity;

@Entity(primaryKeys = {"deviceId", "bikeId"})
public class BikeDeviceCrossRef {

    public int deviceId;
    public int bikeId;


}
