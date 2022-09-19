package com.example.bikecompanion.ui.scanner;

public class ScannerListenerInterface {
    private int imageResource;
    private String deviceName;
    private String deviceMacAddress;
    private String deviceType;

    public ScannerListenerInterface(int imageResource, String deviceName, String deviceMacAddress, String deviceType){
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
