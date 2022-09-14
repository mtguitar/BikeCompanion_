package com.example.bikecompanion.databases.bike;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.bikecompanion.databases.devices.MyDevice;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "bike_table")
public class MyBike {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    //public ArrayList<MyDevice> deviceList;


    public MyBike(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    /*

    public ArrayList getDeviceList(){ return deviceList;}

    public void addDeviceToList(MyDevice device){
        if(deviceList == null){
            deviceList = new ArrayList<MyDevice>();
        }
        deviceList.add(device);
    }



     */
}
