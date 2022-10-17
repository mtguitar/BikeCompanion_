package com.example.bikecompanion.constants;

import java.util.UUID;

public class Constants {
    public final static String ACTION_BLE_SCANNING_STARTED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STARTED";
    public final static String ACTION_BLE_SCANNING_STOPPED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STOPPED";

    public final static String ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE";
    public final static String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    public final static String TAG = "FlareLog";


    //Foreground service notification constants
    public final static String NOTIFICATION_CHANNEL_ID = "scanning_channel";
    public final static String CHANNEL_NAME = "scanning";
    public final static int NOTIFICATION_ID = 1;

    public final static String AVENTON_FLARE_MAC_ADDRESS =
            "F8:EF:93:1C:EC:DB";

    //Gatt intent actions
    public static final String ACTION_GATT_STATE_CHANGE =
            "com.example.bikeCompanion.ACTION_GATT_STATE_CHANGE";
    public final static String ACTION_CHARACTERISTIC_CHANGE =
            "com.example.bikeCompanion.ACTION_CHARACTERISTIC_CHANGE";
    public static final String ACTION_DESCRIPTOR_CHANGE =
            "com.example.bikeCompanion.ACTION_DESCRIPTOR_CHANGE";
    public static final String ACTION_CHARACTERISTIC_CHANGE_BYTE =
            "com.example.bikeCompanion.ACTION_CHARACTERISTIC_CHANGE";
    public static final String ACTION_GATT_ERROR =
            "com.example.bikeCompanion.ACTION_GATT_ERROR";
    public final static String EXTRA_DATA =
            "com.example.bikeCompanion.EXTRA_DATA";


    public static final String GATT_MAC_ADDRESS = "GATT_MAC_ADDRESS";
    public static final String CHARACTERISTIC_UUID = "CHARACTERISTIC_UUID";
    public static final String CHARACTERISTIC_VALUE_BYTE = "CHARACTERISTIC_VALUE_BYTE";
    public static final String CHARACTERISTIC_VALUE_STRING = "CHARACTERISTIC_VALUE_STRING";
    public static final String CHARACTERISTIC_VALUE_INT = "CHARACTERISTIC_VALUE_INT";
    public static final String GATT_OPERATION_TYPE = "GATT_OPERATION_TYPE";
    public static final String DESCRIPTOR_UUID = "DESCRIPTOR_UUID";

    //Gatt operation types
    public static final int OPERATION_CONNECT_DEVICE = 0;
    public static final int OPERATION_DISCONNECT_DEVICE = 1;
    public static final int OPERATION_DISCOVER_SERVICES = 2;

    public static final int OPERATION_CHARACTERISTIC_CHANGED = 3;
    public static final int OPERATION_CHARACTERISTIC_READ = 4;
    public static final int OPERATION_CHARACTERISTIC_WRITE = 5;
    public static final int OPERATION_DESCRIPTOR_WRITE = 6;
    public static final int OPERATION_UNKNOWN = 9;

    //Gatt status
    public static final String GATT_STATUS = "GATT_STATUS";
    public static final int GATT_SUCCESS = 0;
    public static final int GATT_ERROR = 1;

    //Gatt connection states
    public static final String CONNECTION_STATE = "CONNECTION_STATE";

    public static final int CONNECTION_STATE_DISCONNECTED_INT = 0;
    public static final int CONNECTION_STATE_CONNECTING_INT = 1;
    public static final int CONNECTION_STATE_CONNECTED_INT = 2;
    public static final int CONNECTION_STATE_DISCONNECTING_INT = 3;
    public static final int CONNECTION_STATE_SERVICES_DISCOVERED_INT = 4;
    public static final int CONNECTION_STATE_SERVICES_UNKNOWN_INT = 5;
    public static final int CONNECTION_STATE_UNKNOWN_INT = 9;

    public static final String CONNECTION_STATE_DISCONNECTED = String.valueOf(CONNECTION_STATE_DISCONNECTED_INT);
    public static final String CONNECTION_STATE_CONNECTING = String.valueOf(CONNECTION_STATE_CONNECTING_INT);
    public static final String CONNECTION_STATE_CONNECTED = String.valueOf(CONNECTION_STATE_CONNECTED_INT);
    public static final String CONNECTION_STATE_DISCONNECTING = String.valueOf(CONNECTION_STATE_DISCONNECTING_INT);
    public static final String CONNECTION_STATE_SERVICES_DISCOVERED = String.valueOf(CONNECTION_STATE_SERVICES_DISCOVERED_INT);
    public static final String CONNECTION_STATE_SERVICES_UNKNOWN = String.valueOf(CONNECTION_STATE_SERVICES_UNKNOWN_INT);
    public static final String CONNECTION_STATE_UNKNOWN = String.valueOf(CONNECTION_STATE_UNKNOWN_INT);

    public static final String CONNECTION_STATE_DISCONNECTED_NAME = "Disconnected";
    public static final String CONNECTION_STATE_CONNECTING_NAME = "Connecting";
    public static final String CONNECTION_STATE_CONNECTED_NAME = "Connected";
    public static final String CONNECTION_STATE_DISCONNECTING_NAME = "Disconnecting";
    public static final String CONNECTION_STATE_SERVICES_DISCOVERED_NAME = "Connected";
    public static final String CONNECTION_STATE_UNKNOWN_NAME = "Unknown state";







    //Device types
    public static final CharSequence DEVICE_TYPE_LIGHT = "light";
    public static final CharSequence DEVICE_TYPE_SPEED = "speed";

    //CCCD UUID - used for subscribing to notifications
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Button text
    public static final String BUTTON_TEXT_DISCONNECTING = "Disconnecting";
    public static final String BUTTON_TEXT_CONNECTING = "Connecting";
    public static final String BUTTON_TEXT_DISCONNECT = "Disconnect";
    public static final String BUTTON_TEXT_CONNECT = "Connect";




    //Table row formatting
    public static final int TEXT_VIEW_NAME = 0;
    public static final int TEXT_VIEW_VALUE = 1;
    public static final String BATTERY = "Battery";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String MODEL = "Model";
    public static final String LIGHT_MODE = "Light Mode";
    public static final String UNKNOWN = "Unknown";



}


