//package com.example.bikecompanion.deviceTypes;
//
//import com.example.bikecompanion.sharedClasses.Characteristic;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class Device {
//    private String name;
//    private String macAddress;
//    private boolean isConnected;
//    private DeviceTypeEnum deviceType;
//    private int icon;
//    private ArrayList<Characteristic> characteristicList;
//    private ArrayList<UUID> advertisedServiceList;
//
//    public Device(String name, String macAddress, boolean isConnected, DeviceTypeEnum deviceType) {
//        this.name = name;
//        this.macAddress = macAddress;
//        this.isConnected = isConnected;
//        this.deviceType = deviceType;
//        this.icon = deviceType.getIcon();
//        this.characteristicList = deviceType.getCharacteristicList();
//        this.advertisedServiceList = deviceType.getAdvertisedServiceList();
//    }
//
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getMacAddress() {
//        return macAddress;
//    }
//
//    public void setMacAddress(String macAddress) {
//        this.macAddress = macAddress;
//    }
//
//    public boolean isConnected() {
//        return isConnected;
//    }
//
//    public void setIsConnected(boolean isConnected) {
//        this.isConnected = isConnected;
//    }
//
//    public DeviceTypeEnum getDeviceType() {
//        return deviceType;
//    }
//
//    public void setDeviceType(DeviceTypeEnum deviceType) {
//        this.deviceType = deviceType;
//    }
//
//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }
//
//    public ArrayList<Characteristic> getCharacteristicList() {
//        return characteristicList;
//    }
//
//    public void setCharacteristicList(ArrayList<Characteristic> characteristicList) {
//        this.characteristicList = characteristicList;
//    }
//
//    public ArrayList<UUID> getAdvertisedServiceList() {
//        return advertisedServiceList;
//    }
//
//    public void setAdvertisedServiceList(ArrayList<UUID> advertisedServiceList) {
//        this.advertisedServiceList = advertisedServiceList;
//    }
//}
