package com.example.bikecompanion.deviceTypes;

import android.os.ParcelUuid;


import com.example.bikecompanion.R;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class FlareRTDeviceType {

    public final static String DEVICE_TYPE = "Light";

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
    public final static String STRING_SERVICE_LIGHT_MODE = "71261000-3692-ae93-e711-472ba41689c9";
    public final static String STRING_CHARACTERISTIC_LIGHT_MODE = "71261001-3692-ae93-e711-472ba41689c9";


    public final static UUID UUID_SERVICE_LIGHT_MODE = UUID.fromString(STRING_SERVICE_LIGHT_MODE);
    public final static UUID UUID_CHARACTERISTIC_LIGHT_MODE = UUID.fromString(STRING_CHARACTERISTIC_LIGHT_MODE);
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


    public final static ParcelUuid getServiceUUID(){
        return PARCELUUID_ADVERTISED_SERVICE_1;
    }



    public final static byte DAY_SOLID_MODE_STRING = 1;
    public final static byte DAY_BLINK_MODE_STRING = 7;
    public final static byte DAY_BLINK_MODE_2_STRING = 8;
    public final static byte NIGHT_SOLID_MODE_STRING = 5;
    public final static byte NIGHT_BLINK_MODE_STRING = 63;
    public final static byte OFF_MODE_STRING = 0;

    public final static byte[] DAY_SOLID_MODE_BYTE = {1};
    public final static byte[] DAY_BLINK_MODE_BYTE = {7};
    public final static byte[] DAY_BLINK_MODE_2_BYTE = {8};
    public final static byte[] NIGHT_SOLID_MODE_BYTE = {5};
    public final static byte[] NIGHT_BLINK_MODE_BYTE = {63};
    public final static byte[] OFF_MODE_BYTE = {0};

    public final static String DAY_SOLID_MODE_NAME = "Day Solid";
    public final static String DAY_BLINK_MODE_NAME = "Day Blink";
    public final static String DAY_BLINK_MODE_2_NAME = "Day Blink";
    public final static String NIGHT_SOLID_MODE_NAME = "Night Solid";
    public final static String NIGHT_BLINK_MODE_NAME = "Night Blink";
    public final static String OFF_MODE_NAME = "Off";

    public HashMap<String, byte[]> lightModeHashMap;

    public HashMap<String, byte[]> getLightModeHashMap(){
        if (lightModeHashMap == null) {
            lightModeHashMap = new HashMap<>();
        }
        lightModeHashMap.put(DAY_SOLID_MODE_NAME, DAY_SOLID_MODE_BYTE);
        lightModeHashMap.put(DAY_BLINK_MODE_NAME, DAY_BLINK_MODE_BYTE);
        lightModeHashMap.put(DAY_BLINK_MODE_2_NAME, DAY_BLINK_MODE_2_BYTE);
        lightModeHashMap.put(NIGHT_SOLID_MODE_NAME, NIGHT_SOLID_MODE_BYTE);
        lightModeHashMap.put(NIGHT_BLINK_MODE_NAME, NIGHT_BLINK_MODE_BYTE);
        lightModeHashMap.put(OFF_MODE_NAME, OFF_MODE_BYTE);

        return lightModeHashMap;

    }




}
