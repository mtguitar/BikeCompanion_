package com.example.bikecompanion.deviceTypes;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.UUID;

public enum DeviceType {
    FRONT_LIGHT {
        @Override
        ArrayList<Characteristic> getCharacteristicList() {
            return FlareRTDeviceType.getCharacteristicList();
        }
        @Override
        ArrayList<UUID> getAdvertisedServiceList(){
            return FlareRTDeviceType.getAdvertisedServiceList();
        }
        @Override
        int getIcon(){
            return FlareRTDeviceType.getIcon();
        }

    },
    REAR_LIGHT{
        @Override
        ArrayList<Characteristic> getCharacteristicList() {
            return FlareRTDeviceType.getCharacteristicList();
        }
        @Override
        ArrayList<UUID> getAdvertisedServiceList(){
            return FlareRTDeviceType.getAdvertisedServiceList();
        }
        @Override
        int getIcon(){
            return FlareRTDeviceType.getIcon();
        }
    },
    SPEED_CADENCE{
        @Override
        ArrayList<Characteristic> getCharacteristicList() {
            return SpeedCadenceDeviceType.getCharacteristicList();
        }
        @Override
        ArrayList<UUID> getAdvertisedServiceList(){
            return SpeedCadenceDeviceType.getAdvertisedServiceList();
        }
        @Override
        int getIcon(){
            return SpeedCadenceDeviceType.getIcon();
        }
    },
    GENERIC{
        @Override
        ArrayList<Characteristic> getCharacteristicList() {
            return GenericDeviceType.getCharacteristicList();
        }

        @Override
        ArrayList<UUID> getAdvertisedServiceList() {
            return null;
        }

        @Override
        int getIcon(){
            return GenericDeviceType.getIcon();
        }
    };



//    private ArrayList<Characteristic>characteristicsList = AbstractDeviceType.getCharacteristicList();
//    private ArrayList<UUID> advertisedServiceList;
//    private int icon;

    abstract ArrayList<Characteristic> getCharacteristicList();
    abstract ArrayList<UUID> getAdvertisedServiceList();
    abstract int getIcon();

}
