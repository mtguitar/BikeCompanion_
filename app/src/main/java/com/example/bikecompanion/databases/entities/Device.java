package com.example.bikecompanion.databases.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.bikecompanion.deviceTypes.DeviceType;

@Entity(tableName = "device_table", indices = {@Index(value = {"deviceMacAddress"}, unique = true)})
public class Device {


    @PrimaryKey(autoGenerate = true)
    private int deviceId;

    private String deviceMacAddress;
    private String deviceAssignedName;
    private String deviceBleName;
    private DeviceType deviceType;
    private DeviceType deviceTypeEnum;
    private String connectionState;


    public Device(String deviceAssignedName, String deviceBleName, String deviceMacAddress, DeviceType deviceType) {
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

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceTypeEnum(DeviceType deviceTypeEnum) {
        this.deviceTypeEnum = deviceTypeEnum;
    }

    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }

    public DeviceType getDeviceTypeEnum() {
        return deviceTypeEnum;
    }

    public String getConnectionState() {
        return connectionState;
    }
}
