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

    //Gatt operations
    public static final String OPERATION_CHARACTERISTIC_CHANGED = "OPERATION_CHARACTERISTIC_CHANGED";
    public static final String OPERATION_CHARACTERISTIC_READ = "OPERATION_CHARACTERISTIC_READ";
    public static final String OPERATION_CHARACTERISTIC_WRITE = "OPERATION_CHARACTERISTIC_WRITE";
    public static final String OPERATION_DESCRIPTOR_WRITE = "OPERATION_DESCRIPTOR_WRITE";
    public static final String OPERATION_CONNECT_DEVICE = "OPERATION_CONNECT_DEVICE";
    public static final String OPERATION_DISCONNECT_DEVICE = "OPERATION_DISCONNECT_DEVICE";
    public static final String OPERATION_DISCOVER_SERVICES = "OPERATION_DISCOVER_SERVICES";
    public static final String CONNECTION_SUCCESS = "CONNECTION_SUCCESS";
    public static final String CONNECTION_ERROR = "CONNECTION_ERROR";
    public static final String GATT_STATUS = "GATT_STATUS";

    //Gatt connection states
    public final static String CONNECTION_STATE_CONNECTED = "Connected";
    public final static String CONNECTION_STATE_DISCONNECTED = "Disconnected";
    public static final String CONNECTION_STATE_SERVICES_DISCOVERED = "Ready to Read/Write";
    public static final String CONNECTION_STATE = "CONNECTION_STATE";

    //Device types
    public static final CharSequence DEVICE_TYPE_LIGHT = "light";
    public static final CharSequence DEVICE_TYPE_SPEED = "speed";

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Button text
    public static final String BUTTON_TEXT_DISCONNECTING = "Disconnecting";
    public static final String BUTTON_TEXT_CONNECTING = "Connecting";



}


