package com.example.bikecompanion.databases.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"deviceMacAddress", "bikeName"},
        foreignKeys = {
        @ForeignKey(entity = Bike.class,
                parentColumns = "bikeName",
                childColumns = "bikeName",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        @ForeignKey(entity = Device.class,
                parentColumns = "deviceMacAddress",
                childColumns = "deviceMacAddress",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        }



        )
public class BikeDeviceCrossRef {

    @NonNull
    public String bikeName;
    @NonNull
    public String deviceMacAddress;



    public BikeDeviceCrossRef(String deviceMacAddress, String bikeName) {
        this.deviceMacAddress = deviceMacAddress;
        this.bikeName = bikeName;
    }
}
