package com.example.bikecompanion.deviceTypes;

import android.os.ParcelUuid;

import com.example.bikecompanion.R;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FlareRTDeviceType{


    private ArrayList<Characteristic> characteristicsList;

    public final static String DEVICE_TYPE = "Light";
    public static final String MODEL = "553852";

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

    // Device Manufacturer UUIDs
    public final static UUID UUID_SERVICE_DEVICE_MODEL = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_DEVICE_MODEL = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public final static String DATA_TYPE_DEVICE_MODEL = "String";


    Characteristic battery = new Characteristic("battery",UUID_SERVICE_BATTERY, UUID_CHARACTERISTIC_BATTERY, true, true, false);

    Characteristic lightMode = new Characteristic("light mode", UUID_SERVICE_LIGHT_MODE, UUID_CHARACTERISTIC_LIGHT_MODE, true, true, true);

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
            characteristicsList.add(lightMode);
        }
        return characteristicsList;
    }






    public static ParcelUuid getServiceUUID(){
        return PARCELUUID_ADVERTISED_SERVICE_1;
    }



    public final static String DAY_SOLID_MODE_INT = "1";
    public final static String DAY_BLINK_MODE_INT = "7";
    public final static String DAY_BLINK_MODE_2_INT = "8";
    public final static String NIGHT_SOLID_MODE_INT = "5";
    public final static String NIGHT_BLINK_MODE_INT = "63";
    public final static String OFF_MODE_INT = "0";

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
