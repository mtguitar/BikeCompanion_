package com.example.bikecomputerfirstdraft.ui.scanner;

public class ScanResults {
    private int imageResource;
    private String deviceName;
    private String deviceMacAddress;
    private String deviceType;

    public ScanResults(int imageResource, String deviceName, String deviceMacAddress, String deviceType){
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

    public String getDeviceType(){
        return deviceType;
    }

}
