package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.R;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FlareRTDeviceType{


    private static ArrayList<Characteristic> characteristicsList;

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

    // Light mode UUIDs
    public final static UUID UUID_SERVICE_LIGHT_MODE = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public final static UUID UUID_CHARACTERISTIC_LIGHT_MODE = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");
    public final static String UUID_CHARACTERISTIC_LIGHT_MODE_STRING = "71261001-3692-ae93-e711-472ba41689c9";
    public final static String DATA_TYPE_LIGHT_MODE = "byte[]";

    static Characteristic lightMode = new Characteristic("light mode", UUID_SERVICE_LIGHT_MODE, UUID_CHARACTERISTIC_LIGHT_MODE, true, true, true);

    public final static String DAY_SOLID_MODE_INT = "1";
    public final static String DAY_BLINK_MODE_INT = "7";
    public final static String DAY_BLINK_MODE_2_INT = "8";
    public final static String NIGHT_SOLID_MODE_INT = "5";
    public final static String NIGHT_BLINK_MODE_INT = "63";
    public final static String OFF_MODE_INT = "0";

    public final static String DAY_SOLID_MODE_NAME = "Day Solid";
    public final static String DAY_BLINK_MODE_NAME = "Day Blink";
    public final static String DAY_BLINK_MODE_2_NAME = "Day Blink";
    public final static String NIGHT_SOLID_MODE_NAME = "Night Solid";
    public final static String NIGHT_BLINK_MODE_NAME = "Night Blink";
    public final static String OFF_MODE_NAME = "Off";

    public static HashMap<Integer, String> getLightModeHashMap(){
        HashMap<Integer, String> lightModeHashMap= new HashMap<>();

        lightModeHashMap.put(1, DAY_SOLID_MODE_NAME);
        lightModeHashMap.put(7, DAY_BLINK_MODE_NAME);
        lightModeHashMap.put(8, DAY_BLINK_MODE_2_NAME);
        lightModeHashMap.put(5, NIGHT_SOLID_MODE_NAME);
        lightModeHashMap.put(63, NIGHT_BLINK_MODE_NAME);
        lightModeHashMap.put(0, OFF_MODE_NAME);

        return lightModeHashMap;
    }

    public static ArrayList<Characteristic> getCharacteristicList() {
        if (characteristicsList == null){
            characteristicsList = new ArrayList<>();
            characteristicsList.add(lightMode);
        }
        return characteristicsList;
    }



}
