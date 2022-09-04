package com.example.bikecomputerfirstdraft.ui.myDevices;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_table")
public class MyDevice {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String assignedName;
    private String bleName;
    private String macAddress;
    private String deviceType;


    public MyDevice(String assignedName, String bleName, String macAddress, String deviceType) {
        this.assignedName = assignedName;
        this.bleName = bleName;
        this.macAddress = macAddress;
        this.deviceType = deviceType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public String getBleName() {
        return bleName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
