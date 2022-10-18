package com.example.bikecompanion.deviceTypes;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.UUID;

public enum DeviceType {
    FRONT_LIGHT {
        @Override
        public ArrayList<Characteristic> getCharacteristicList() {
            return FlareRTDeviceType.getCharacteristicList();
        }
        @Override
        public ArrayList<UUID> getAdvertisedServiceList(){
            return FlareRTDeviceType.getAdvertisedServiceList();
        }
        @Override
        public int getIcon(){
            return FlareRTDeviceType.getIcon();
        }

        @Override
        public DeviceType getDeviceType() {
            return FRONT_LIGHT;
        }

    },
    REAR_LIGHT{
        @Override
        public ArrayList<Characteristic> getCharacteristicList() {
            return FlareRTDeviceType.getCharacteristicList();
        }
        @Override
        public ArrayList<UUID> getAdvertisedServiceList(){
            return FlareRTDeviceType.getAdvertisedServiceList();
        }
        @Override
        public int getIcon(){
            return FlareRTDeviceType.getIcon();
        }

        @Override
        public DeviceType getDeviceType() {
            return REAR_LIGHT;
        }
    },
    SPEED_CADENCE{
        @Override
        public ArrayList<Characteristic> getCharacteristicList() {
            return SpeedCadenceDeviceType.getCharacteristicList();
        }
        @Override
        public ArrayList<UUID> getAdvertisedServiceList(){
            return SpeedCadenceDeviceType.getAdvertisedServiceList();
        }
        @Override
        public int getIcon(){
            return SpeedCadenceDeviceType.getIcon();
        }

        @Override
        public DeviceType getDeviceType() {
            return SPEED_CADENCE;
        }
    },
    GENERIC{
        @Override
        public ArrayList<Characteristic> getCharacteristicList() {
            return GenericDeviceType.getCharacteristicList();
        }

        @Override
        public ArrayList<UUID> getAdvertisedServiceList() {
            return null;
        }

        @Override
        public int getIcon(){
            return GenericDeviceType.getIcon();
        }

        @Override
        public DeviceType getDeviceType() {
            return GENERIC;
        }
    };



//    private ArrayList<Characteristic>characteristicsList = AbstractDeviceType.getCharacteristicList();
//    private ArrayList<UUID> advertisedServiceList;
//    private int icon;

    public abstract ArrayList<Characteristic> getCharacteristicList();
    public abstract ArrayList<UUID> getAdvertisedServiceList();
    public abstract int getIcon();
    public abstract DeviceType getDeviceType();

}
