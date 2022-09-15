package com.example.bikecompanion.databases.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.List;

public class DeviceWithBikes {

    @Embedded
    public Device device;
    @Relation(
            parentColumn = "deviceMacAddress",
            entityColumn = "bikeId",
            associateBy = @Junction(BikeDeviceCrossRef.class)
    )

    public List<Bike> bikes;


}
