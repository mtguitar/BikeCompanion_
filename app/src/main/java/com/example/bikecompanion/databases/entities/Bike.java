package com.example.bikecompanion.databases.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.bikecompanion.constants.Constants;

import java.util.ArrayList;

@Entity(tableName = "bike_table", indices = {@Index(value = {"bikeName"}, unique = true)})

public class Bike {

    @PrimaryKey(autoGenerate = true)
    private int bikeId;


    private String bikeName;
    private String bikeMake;
    private String bikeModel;



    //Constructor
    public Bike(String bikeName, String bikeMake, String bikeModel) {
        this.bikeName = bikeName;
        this.bikeMake = bikeMake;
        this.bikeModel = bikeModel;
    }

    //Getters and setters

    public void setBikeName(String bikeName) {
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

    public String getBikeMake() {
        return bikeMake;
    }

    public void setBikeMake(String bikeMake) {
        this.bikeMake = bikeMake;
    }

    public String getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(String bikeModel) {
        this.bikeModel = bikeModel;
    }



}
