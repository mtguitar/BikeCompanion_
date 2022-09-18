package com.example.bikecompanion.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "device_table", indices = {@Index(value = {"deviceMacAddress"}, unique = true)})
public class Device {


    @PrimaryKey(autoGenerate = true)
    private int deviceId;

    private String deviceMacAddress = "Ignore";
    private String deviceAssignedName;
    private String deviceBleName;
    private String deviceType;



    public Device(String deviceAssignedName, String deviceBleName, String deviceMacAddress, String deviceType) {
        this.deviceAssignedName = deviceAssignedName;
        this.deviceBleName = deviceBleName;
        this.deviceMacAddress = deviceMacAddress;
        this.deviceType = deviceType;
    }


    public String getDeviceAssignedName() {
        return deviceAssignedName;
    }

    public String getDeviceBleName() {
        return deviceBleName;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
