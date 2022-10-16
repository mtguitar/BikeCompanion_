package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class AbstractDeviceType {

    /**
     * Characteristic UUIDS
     */

    // Battery UUIDs
    public final static UUID UUID_SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public final static String UUID_CHARACTERISTIC_BATTERY_STRING = "00002a19-0000-1000-8000-00805f9b34fb";
    public final static String DATA_TYPE_BATTERY = "int";

    // Device Name UUIDs
    public final static UUID UUID_SERVICE_DEVICE_NAME = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public final static String UUID_CHARACTERISTIC_DEVICE_NAME_STRING = "00002a00-0000-1000-8000-00805f9b34fb";
    public final static String DATA_TYPE_DEVICE_NAME = "String";

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_DEVICE_MANUFACTURER = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_MANUFACTURER = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    public final static String UUID_CHARACTERISTIC_DEVICE_MANUFACTURER_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public final static String DATA_TYPE_DEVICE_MANUFACTURER = "String";

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_DEVICE_MODEL = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_MODEL = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public final static String UUID_CHARACTERISTIC_DEVICE_MODEL_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public final static String DATA_TYPE_DEVICE_MODEL = "String";

    public static Characteristic battery = new Characteristic("battery",UUID_SERVICE_BATTERY, UUID_CHARACTERISTIC_BATTERY, true, true, false);
    public static Characteristic name = new Characteristic("name", UUID_SERVICE_DEVICE_NAME, UUID_CHARACTERISTIC_DEVICE_NAME, true, false, true);
    public static Characteristic manufacturer = new Characteristic("manufacturer", UUID_SERVICE_DEVICE_MANUFACTURER, UUID_CHARACTERISTIC_DEVICE_MANUFACTURER, true, false, false);
    public static Characteristic model = new Characteristic("model", UUID_SERVICE_DEVICE_MODEL, UUID_CHARACTERISTIC_DEVICE_MODEL, true, false, false );

    /**
     * Getters
     *
     */

    public static ArrayList<Characteristic> getCharacteristicList() {
        ArrayList<Characteristic> characteristicList = new ArrayList<>();

        characteristicList.add(battery);
        characteristicList.add(name);
        characteristicList.add(manufacturer);
        characteristicList.add(model);

        return characteristicList;
    }

    public static HashMap<String, String> getConnectionStateNameHashMap(){
        HashMap<String, String> connectionStateNameHashMap = new HashMap<>();

        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_DISCONNECTED, Constants.CONNECTION_STATE_DISCONNECTED_NAME);
        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_CONNECTING, Constants.CONNECTION_STATE_CONNECTING_NAME);
        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_CONNECTED, Constants.CONNECTION_STATE_CONNECTED_NAME);
        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_DISCONNECTING, Constants.CONNECTION_STATE_DISCONNECTING_NAME);
        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_SERVICES_DISCOVERED, Constants.CONNECTION_STATE_SERVICES_DISCOVERED_NAME);
        connectionStateNameHashMap.put(Constants.CONNECTION_STATE_UNKNOWN, Constants.CONNECTION_STATE_UNKNOWN_NAME);

        return connectionStateNameHashMap;
    }

    public abstract ArrayList<UUID> getAdvertisedServiceList();

    public abstract DeviceType getDeviceType();


}
