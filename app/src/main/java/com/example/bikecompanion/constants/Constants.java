package com.example.bikecompanion.constants;

public class Constants {
    public final static String ACTION_BLE_SCANNING_STARTED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STARTED";
    public final static String ACTION_BLE_SCANNING_STOPPED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STOPPED";

    public final static String ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE";
    public final static String ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE";
    public final static String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    public final static String TAG = "FlareLog";


    public final static int STATE_DISCONNECTED = 0;
    public final static int STATE_CONNECTING = 1;
    public final static int STATE_CONNECTED = 2;

    public final static String STATE_DISCONNECTED_STRING = "Disconnected";
    public final static String STATE_CONNECTING_STRING = "Connecting";
    public final static String STATE_CONNECTED_STRING = "Connected";


    public final static String ACTION_SHOW_SCANNING_FRAGMENT = "ACTION_SHOW_SCANNING_FRAGMENT";
    public final static String ACTION_SHOW_CONNECTION_FRAGMENT = "ACTION_SHOW_CONNECTION_FRAGMENT";


    public final static String NOTIFICATION_CHANNEL_ID = "scanning_channel";
    public final static String CHANNEL_NAME = "scanning";
    public final static int NOTIFICATION_ID = 1;


    public final static String AVENTON_FLARE_MAC_ADDRESS =
            "F8:EF:93:1C:EC:DB";

    public final static String SHARED_PREFERENCES_MY_DEVICES_KEY =
            "com.example.bikecompanion.PREFERENCE_FILE_KEY";


    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_STATE_CHANGE =
            "com.example.bluetooth.le.ACTION_GATT_STATE_CHANGED";

    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_CONNECT_TO_DEVICE =
            "com.example.bluetooth.le.CONNECT_TO_DEVICE";
    public static final String ACTION_DISCONNECT_DEVICE = "com.example.bluetooth.le.ACTION_DISCONNECT_DEVICE";;


    public static final String ACTION_READ_CHARACTERISTIC =
            "com.example.bluetooth.le.ACTION_READ_CHARACTERISTIC";
    public static final String ACTION_WRITE_CHARACTERISTIC =
            "com.example.bluetooth.le.ACTION_WRITE_CHARACTERISTIC";
    public static final String ACTION_SUBSCRIBE_TO_CHARACTERISTIC =
            "com.example.bluetooth.le.ACTION_SUBSCRIBE_TO_CHARACTERISTIC";

    public static final String ACTION_READ_CHARACTERISTIC_BATTERY =
            "com.example.bluetooth.le.ACTION_READ_CHARACTERISTIC_BATTERY";
    public static final String ACTION_READ_CHARACTERISTIC_MANUFACTURER =
            "com.example.bluetooth.le.ACTION_READ_CHARACTERISTIC_MANUFACTURER";


    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public static final String EXTRA_DATA_BATTERY =
            "com.example.bluetooth.le.EXTRA_DATA_BATTERY";
    public static final String EXTRA_DATA_MANUFACTURER =
            "com.example.bluetooth.le.EXTRA_DATA_MANUFACTURER";





    public static final String GATT_MAC_ADDRESS = "GATT_MAC_ADDRESS";
    public static final String CHARACTERISTIC_UUID = "CHARACTERISTIC_UUID";
    public static final String CHARACTERISTIC_VALUE_BYTE = "CHARACTERISTIC_VALUE_BYTE";
    public static final String CHARACTERISTIC_VALUE_STRING = "CHARACTERISTIC_VALUE_STRING";


    public final static String GATT_CONNECTED = "Connected";
    public final static String GATT_DISCONNECTED = "Disconnected";
    public static final String GATT_SERVICES_DISCOVERED = "Ready to Read/Write";
    public static final String GATT_CONNECTION_STATE = "GATT_CONNECTION_STATE";
}
