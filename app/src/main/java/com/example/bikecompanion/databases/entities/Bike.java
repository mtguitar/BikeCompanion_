package com.example.bikecompanion.databases.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.bikecompanion.constants.Constants;

import java.util.ArrayList;

@Entity(tableName = "bike_table")
public class Bike {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String bikeName = "ignore";

    private int bikeId;
    private String bikeMake;
    private String bikeModel;
    private ArrayList<Device> deviceList;



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

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }




}
