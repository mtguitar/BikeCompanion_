package com.example.bikecomputerfirstdraft.deviceTypes;

import android.os.ParcelUuid;

import com.example.bikecomputerfirstdraft.R;

import java.util.UUID;

public class GenericDevice {

    public final static String DATA_TYPE_BYTE_ARRAY = "DATA_TYPE_BYTE_ARRAY";
    public final static String DATA_TYPE_STRING = "DATA_TYPE_STRING";

    public final static String DEVICE_TYPE = "Unknown";



    // Battery UUIDs
    public final static String STRING_SERVICE_BATTERY = "0000180f-0000-1000-8000-00805f9b34fb";
    public final static String STRING_CHARACTERISTIC_BATTERY = "00002a19-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE_BATTERY = UUID.fromString(STRING_SERVICE_BATTERY);
    public final static UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString(STRING_CHARACTERISTIC_BATTERY);
    public final static String DATA_TYPE_BATTERY = DATA_TYPE_BYTE_ARRAY;
    public final static String[] ARRAY_CHARACTERISTIC_BATTERY = {STRING_SERVICE_BATTERY, STRING_CHARACTERISTIC_BATTERY};


    // Device Name UUIDs
    public final static UUID UUID_SERVICE_DEVICE_NAME = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_NAME = DATA_TYPE_STRING;

    // Device Manufacturer UUIDs

    public final static String STRING_SERVICE_MANUFACTURER= "0000180a-0000-1000-8000-00805f9b34fb";
    public final static String STRING_CHARACTERISTIC_MANUFACTURER = "00002a29-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_SERVICE_MANUFACTURER = UUID.fromString(STRING_SERVICE_MANUFACTURER);
    public final static UUID UUID_CHARACTERISTIC_MANUFACTURER = UUID.fromString(STRING_SERVICE_MANUFACTURER);
    public final static String DATA_TYPE_MANUFACTURER = DATA_TYPE_STRING;
    public final static String[] ARRAY_CHARACTERISTIC_MANUFACTURER = {STRING_SERVICE_MANUFACTURER, STRING_CHARACTERISTIC_MANUFACTURER};




}
