package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
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
    public final static String DATA_TYPE_BATTERY = "byte[]";

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
