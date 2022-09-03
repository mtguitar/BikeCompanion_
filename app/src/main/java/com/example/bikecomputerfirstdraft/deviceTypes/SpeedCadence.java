package com.example.bikecomputerfirstdraft.deviceTypes;

import android.os.ParcelUuid;

import java.util.UUID;

public class SpeedCadence {

    public final static String DEVICE_TYPE = "Speed/Cadence";

    //Advertised service UUIDs
    public final static String STRING_SERVICE_ADVERTISED_1 = "00001816-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_SERVICE_ADVERTISED_1 = UUID.fromString(STRING_SERVICE_ADVERTISED_1);

    public final static ParcelUuid PARCELUUID_ADVERTISED_SERVICE_1 = new ParcelUuid(UUID_SERVICE_ADVERTISED_1);

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

    public static ParcelUuid getServiceUUID(){
        return PARCELUUID_ADVERTISED_SERVICE_1;
    }


}
