package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SpeedCadenceDeviceType {

    private ArrayList<Characteristic> characteristicsList;

    public final static String DEVICE_TYPE = "Speed/Cadence";

    //Advertised service UUIDs
    public final static String STRING_SERVICE_ADVERTISED_1 = "00001816-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_SERVICE_ADVERTISED_1 = UUID.fromString(STRING_SERVICE_ADVERTISED_1);




    // Battery UUIDs
    public final static UUID UUID_SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


    // Device Name UUIDs
    public final static UUID UUID_SERVICE_DEVICE_NAME = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_NAME = "String";

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_DEVICE_MANUFACTURER = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_MANUFACTURER = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_MANUFACTURER = "String";

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_DEVICE_MODEL = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_MODEL = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_MODEL = "String";

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_CSC_FEATURE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_CSC_FEATURE = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_CSC_FEATURE = "int";
    public final static String CSC_FEATURE_SPEED = "Speed Sensor";
    public final static String CSC_FEATURE_CADENCE = "Cadence Sensor";
    public final static String CSC_FEATURE_BOTH = "Speed and Cadence Sensor";

    public final static HashMap<Integer, String> getCSCFeatureMap(){
        HashMap<Integer, String> cSCFeatureMap = new HashMap<>();

        cSCFeatureMap.put(1, "Speed Sensor");
        cSCFeatureMap.put(2, "Cadence Sensor");
        cSCFeatureMap.put(3, "Speed and Cadence Sensor");

        return cSCFeatureMap;
    }

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





    Characteristic battery = new Characteristic("battery",UUID_SERVICE_BATTERY, UUID_CHARACTERISTIC_BATTERY, true, true, false);

    Characteristic name = new Characteristic("name", UUID_SERVICE_DEVICE_NAME, UUID_CHARACTERISTIC_DEVICE_NAME, true, false, true);

    Characteristic manufacturer = new Characteristic("manufacturer", UUID_SERVICE_DEVICE_MANUFACTURER, UUID_CHARACTERISTIC_DEVICE_MANUFACTURER, true, false, false);

    Characteristic model = new Characteristic("model", UUID_SERVICE_DEVICE_MODEL, UUID_CHARACTERISTIC_DEVICE_MODEL, true, false, false );


    public ArrayList<Characteristic> getCharacteristicsList() {
        if (characteristicsList == null){
            characteristicsList = new ArrayList<>();
            characteristicsList.add(battery);
            characteristicsList.add(name);
            characteristicsList.add(manufacturer);
            characteristicsList.add(model);
        }
        return characteristicsList;
    }



}
