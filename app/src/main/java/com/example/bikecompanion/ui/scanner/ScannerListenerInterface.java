package com.example.bikecompanion.ui.scanner;

import com.example.bikecompanion.deviceTypes.DeviceType;

public class ScannerListenerInterface {
    private int imageResource;
    private String deviceName;
    private String deviceMacAddress;
    private DeviceType deviceType;

    public ScannerListenerInterface(int imageResource, String deviceName, String deviceMacAddress, DeviceType deviceType){
        this.imageResource = imageResource;
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.deviceType = deviceType;
    }

    public int getImageResource(){
        return imageResource;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceMacAddress(){
        return deviceMacAddress;
    }

    public DeviceType getDeviceType(){
        return deviceType;
    }

}
