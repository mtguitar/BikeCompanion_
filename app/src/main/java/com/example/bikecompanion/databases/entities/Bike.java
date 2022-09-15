package com.example.bikecompanion.databases.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.bikecompanion.constants.Constants;

import java.util.ArrayList;

@Entity(tableName = "bike_table")
public class Bike {

    @PrimaryKey(autoGenerate = true)
    private int bikeId;

    private String bikeName = Constants.PLACEHOLDER;


    public Bike(String bikeName) {
        this.bikeName = bikeName;
    }


    public String getBikeName() {
        return bikeName;
    }

    public int getBikeId() {
        return bikeId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }



}
