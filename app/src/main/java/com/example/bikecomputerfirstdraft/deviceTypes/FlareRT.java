package com.example.bikecomputerfirstdraft.deviceTypes;

import android.os.ParcelUuid;


import com.example.bikecomputerfirstdraft.R;

import java.util.UUID;

public class FlareRT{

    public final static String DEVICE_TYPE = "light";

    public final static int icon = R.drawable.ic_device_type_light;

    //Advertised service UUIDs
    public final static String STRING_SERVICE_ADVERTISED_1 = "0000180a-0000-1000-8000-00805f9b34fb";
    public final static String STRING_SERVICE_ADVERTISED_2 = "0000180f-0000-1000-8000-00805f9b34fb";
    public final static String STRING_SERVICE_ADVERTISED_3 = "71262000-3692-ae93-e711-472ba41689c9";

    public final static UUID UUID_SERVICE_ADVERTISED_1 = UUID.fromString(STRING_SERVICE_ADVERTISED_1);
    public final static UUID UUID_SERVICE_ADVERTISED_2 = UUID.fromString(STRING_SERVICE_ADVERTISED_2);
    public final static UUID UUID_SERVICE_ADVERTISED_3 = UUID.fromString(STRING_SERVICE_ADVERTISED_3);

    public final static ParcelUuid PARCELUUID_ADVERTISED_SERVICE_1 = new ParcelUuid(UUID_SERVICE_ADVERTISED_1);
    public final static ParcelUuid PARCELUUID_ADVERTISED_SERVICE_2 = new ParcelUuid(UUID_SERVICE_ADVERTISED_2);
    public final static ParcelUuid PARCELUUID_ADVERTISED_SERVICE_3 = new ParcelUuid(UUID_SERVICE_ADVERTISED_3);

    // Light mode UUIDs
    public final static UUID UUID_SERVICE_LIGHT_MODE = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public final static UUID UUID_CHARACTERISTIC_LIGHT_MODE = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");
    public final static String DATA_TYPE_LIGHT_MODE = "byte[]";

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
