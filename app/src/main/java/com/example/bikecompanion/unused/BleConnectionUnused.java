package com.example.bikecompanion.unused;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.ui.scanner.ScannerListenerInterface;

import java.util.ArrayList;
import java.util.UUID;

import static com.example.bikecompanion.constants.Constants.ACTION_DATA_AVAILABLE;
import static com.example.bikecompanion.constants.Constants.ACTION_GATT_CONNECTED;
import static com.example.bikecompanion.constants.Constants.ACTION_GATT_DISCONNECTED;
import static com.example.bikecompanion.constants.Constants.ACTION_GATT_SERVICES_DISCOVERED;
import static com.example.bikecompanion.constants.Constants.EXTRA_DATA;

@SuppressLint("MissingPermission")

public class BleConnectionUnused {

    private final static String TAG = "FlareLog";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    //testing vars
    public static UUID LIGHT_MODE_SERVICE_UUID = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public static UUID LIGHT_MODE_CHARACTERISTIC_UUID = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");
    public UUID SERVICE_UUID = LIGHT_MODE_SERVICE_UUID;
    public UUID CHARACTERISTIC_UUID = LIGHT_MODE_CHARACTERISTIC_UUID;

    //private byte[] payloadToWrite;
    private static String stringDaySolid = "1";
    private static String stringDayBlink = "7";
    private static String stringNightSolid = "5";
    private static String stringNightBlink = "3F";
    private static String off = "0";



    //bluetooth vars
    private BleConnectionServiceOLD bleService;
    private BluetoothAdapter bluetoothAdapter;
    private Intent gattServiceIntent;
    String name;
    String macAddress;
    ParcelUuid serviceUuids;
    private Context mContext;


    //connection state booleans
    private boolean deviceConnected = false;
    private boolean boundToService = false;
    private boolean servicesDiscovered = false;
    private boolean subscribeToNotification = false;


    //recyclerView vars
    private View view;
    private RecyclerView mRecyclerView;


    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    private ArrayList<ScannerListenerInterface> scannerList;


    public BleConnectionUnused(Context mContext, View view, String name, String macAddress, ParcelUuid serviceUuids) {
        this.mContext = mContext;
        this.view = view;
        this.name = name;
        this.macAddress = macAddress;
        this.serviceUuids = serviceUuids;


        scannerList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerViewScanner);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    // Initialize bluetooth
    public void initializeBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        logMessages("initialized ble");
    }

    // Checks if BluetoothLeService is bound
    // If not bound, binds to BluetoothLeService and calls serviceConnection
    // If bound, call BluetoothLeService's connected method directly, passing deviceMacAddress
    private void connectDevice(String deviceMacAddress){
        if (!boundToService) {
            gattServiceIntent = new Intent(mContext, BleConnectionServiceOLD.class);
            mContext.bindService(gattServiceIntent, serviceConnection, mContext.BIND_AUTO_CREATE);
        }
        else {
            bleService.connectDevice(deviceMacAddress);
            logMessages("Trying to connect to " + deviceName);
        }

    }

    //subscribe to characteristic notification
    private void subscribeToNotification(){
        bleService.setCharacteristicNotification(SERVICE_UUID, CHARACTERISTIC_UUID, true);
        subscribeToNotification = false;
    }

    //write to characteristic notification
    private void writeCharacteristic(String payload){
        FormatBleData formatBleData = new FormatBleData();
        byte[] payloadToWrite = formatBleData.convertStingtoByte(payload);
        bleService.writeCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID, payloadToWrite);
    }

    // serviceConnection object to connect to BluetoothLeService
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BleConnectionServiceOLD.LocalBinder) service).getService();
            // If bluetoothLeService is initialized, connect to device
            if (!bleService.initialize()) {
                logMessages("Failed to initialize BluetoothLeService");
            }
            boundToService = true;

            bleService.connectDevice("F8:EF:93:1C:EC:DB");
            logMessages("Trying to connect to " + deviceName);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleService = null;
        }
    };

    // Creates gatt intent filters to receive intents from BluetoothLeService
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the BluetoothLeService.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MainActivity:  broadcast received");
            final String action = intent.getAction();
            final String characteristic = intent.getStringExtra(EXTRA_DATA);
            if (ACTION_GATT_CONNECTED.equals(action)){
                deviceConnected = true;
                logMessages(deviceName + " connected");
                //textViewConnectedDevices.append(deviceName + "\n");

            }
            else if (ACTION_GATT_DISCONNECTED.equals(action)){
                deviceConnected = false;
                logMessages(deviceName + " disconnected");
            }
            else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                logMessages(deviceName + " services discovered");
                servicesDiscovered = true;
                if(subscribeToNotification){
                    subscribeToNotification();
                }
            }
            else if (ACTION_DATA_AVAILABLE.equals(action)){
                logMessages(deviceName + " characteristic: " + characteristic);

            }


        }
    };

    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
    }

}
