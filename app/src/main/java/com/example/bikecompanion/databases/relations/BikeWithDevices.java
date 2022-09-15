package com.example.bikecompanion.databases.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.List;

public class BikeWithDevices {

    @Embedded public Bike bike;
    @Relation(
            parentColumn = "bikeName",
            entityColumn = "deviceMacAddress",
            associateBy = @Junction(BikeDeviceCrossRef.class)

    )
    public List<Device> devices;

}
