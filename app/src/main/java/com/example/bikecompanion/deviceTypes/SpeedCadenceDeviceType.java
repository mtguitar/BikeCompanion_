package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.R;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SpeedCadenceDeviceType {

    private static ArrayList<Characteristic> characteristicList;
    public final static String DEVICE_TYPE = "Speed/Cadence";
    public final static int icon = R.drawable.ic_device_type_csc;


    //Advertised service UUIDs
    public final static String STRING_SERVICE_ADVERTISED_1 = "00001816-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE_ADVERTISED_1 = UUID.fromString(STRING_SERVICE_ADVERTISED_1);

    // Device Feature
    public final static String UUID_SERVICE_CSC_FEATURE_STRING = "00001816-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_CSC_FEATURE_STRING = "00002a5c-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE_CSC_FEATURE = UUID.fromString(UUID_SERVICE_CSC_FEATURE_STRING);
    public final static UUID UUID_CHARACTERISTIC_CSC_FEATURE = UUID.fromString(UUID_CHARACTERISTIC_CSC_FEATURE_STRING);
    public final static String DATA_TYPE_DEVICE_CSC_FEATURE = "int";

    public final static HashMap<Integer, String> getCSCFeatureMap(){
        HashMap<Integer, String> cSCFeatureMap = new HashMap<>();

        cSCFeatureMap.put(1, "Speed Sensor");
        cSCFeatureMap.put(2, "Cadence Sensor");
        cSCFeatureMap.put(3, "Speed and Cadence Sensor");

        return cSCFeatureMap;
    }

    //Device Location
    public final static String UUID_SERVICE_CSC_LOCATION_STRING = "00001816-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_CSC_LOCATION_STRING = "00002a5d-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE_CSC_LOCATION = UUID.fromString(UUID_SERVICE_CSC_LOCATION_STRING);
    public final static UUID UUID_CHARACTERISTIC_CSC_LOCATION = UUID.fromString(UUID_CHARACTERISTIC_CSC_LOCATION_STRING);
    public final static String DATA_TYPE_DEVICE_CSC_LOCATION = "int";

    public final static HashMap<Integer, String> getCSCSensorLocationMap(){
        HashMap<Integer, String> cSCSensorLocationMap = new HashMap<>();

        cSCSensorLocationMap.put(0, "Other");
        cSCSensorLocationMap.put(1, "Top of shoe");
        cSCSensorLocationMap.put(2, "In shoe");
        cSCSensorLocationMap.put(3, "Hip");
        cSCSensorLocationMap.put(4, "Front wheel");
        cSCSensorLocationMap.put(5, "Left crank");
        cSCSensorLocationMap.put(6, "Right crank");
        cSCSensorLocationMap.put(7, "Left pedal");
        cSCSensorLocationMap.put(8, "Right pedal");
        cSCSensorLocationMap.put(9, "Front hub");
        cSCSensorLocationMap.put(10, "Rear dropout");
        cSCSensorLocationMap.put(11, "Chainstay");
        cSCSensorLocationMap.put(12, "Rear wheel");
        cSCSensorLocationMap.put(13, "Rear hub");
        cSCSensorLocationMap.put(14, "Chest");
        cSCSensorLocationMap.put(15, "Spider");
        cSCSensorLocationMap.put(16, "Chain ring");

        return cSCSensorLocationMap;
    }

    public static Characteristic sensorLocation = new Characteristic("csc sensor location", UUID_SERVICE_CSC_LOCATION, UUID_CHARACTERISTIC_CSC_LOCATION, true, false, true);
    public static Characteristic sensorFeature = new Characteristic("csc sensor feature", UUID_SERVICE_CSC_FEATURE, UUID_CHARACTERISTIC_CSC_FEATURE, true, false, false);


    public static ArrayList<Characteristic> getCharacteristicList() {
        if (characteristicList == null){
            characteristicList = new ArrayList<>();
            characteristicList.add(sensorLocation);
            characteristicList.add(sensorFeature);

        }
        return characteristicList;
    }



}
