package com.example.bikecomputerfirstdraft.other;

public class Constant {
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

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static String ACTION_SHOW_SCANNING_FRAGMENT = "ACTION_SHOW_SCANNING_FRAGMENT";
    public final static String ACTION_SHOW_CONNECTION_FRAGMENT = "ACTION_SHOW_CONNECTION_FRAGMENT";


    public final static String NOTIFICATION_CHANNEL_ID = "scanning_channel";
    public final static String CHANNEL_NAME = "scanning";
    public final static int NOTIFICATION_ID = 1;


    public final static String AVENTON_FLARE_MAC_ADDRESS = "F8:EF:93:1C:EC:DB";



}
