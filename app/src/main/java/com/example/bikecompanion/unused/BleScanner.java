package com.example.bikecompanion.unused;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.ui.scanner.ScannerFragment;
import com.example.bikecompanion.ui.scanner.ScanResults;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("MissingPermission")

public class BleScanner {

    private final static String TAG = "FlareLog";


    //vars
    public boolean scanning = false;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Context mContextApplication;
    private Context mContextActivity;
    private Handler handler;

    //filter and scan settings vars
    private ScanFilter scanFilter;
    String name;
    String macAddress;
    ParcelUuid serviceUuids;
    private ScanSettings scanSettings;
    private static final long SCAN_PERIOD =3000;


    //recyclerView vars
    private View view;
    private RecyclerView mRecyclerView;

    private String deviceType = null;

    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    private ArrayList<ScanResults> scannerList;

    //broadcast vars
    Intent scanningStatusIntent;
    public final static String ACTION_BLE_SCANNING_STARTED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STARTED";
    public final static String ACTION_BLE_SCANNING_STOPPED =
            "com.example.bluetooth.le.ACTION_BLE_SCANNING_STOPPED";


    //constructors, the last three parameters are scan filters and can be null
    public BleScanner (Context mContextApplication, Context mContextActivity, View view, String name, String macAddress, ParcelUuid serviceUuids) {
        this.mContextApplication = mContextApplication;
        this.mContextActivity = mContextActivity;
        this.view = view;
        this.name = name;
        this.macAddress = macAddress;
        this.serviceUuids = serviceUuids;

        scanningStatusIntent = new Intent(mContextActivity, ScannerFragment.class);


        initializeBluetooth();
        startScan();
        scannerList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerViewScanner);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }


    // Initialize bluetooth
    public void initializeBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContextApplication.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        logMessages("initialized ble");
    }

    // Scan for selected device by MacAddress
    public void startScan() {
        //Set scan settings and filter
        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        if(serviceUuids != null){
            scanFilter = new ScanFilter.Builder().setServiceUuid(serviceUuids).build();
        }
        if(name != null){
        scanFilter = new ScanFilter.Builder().setDeviceName(name).build();
        }
        if(macAddress != null) {
            scanFilter = new ScanFilter.Builder().setDeviceAddress(macAddress).build();
        }


        //Start scanning on a timer
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scanning) {
                        stopScanning();
                    }
                }
            }, SCAN_PERIOD);
            if (serviceUuids != null | name != null | macAddress != null) {
                scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
            }
            else {
                scanner.startScan(scanCallback);
            }
            scanning = true;
            //textViewLog.setText("");
            logMessages("Scanning . . . ");
            Intent scanningStatusIntent = new Intent();
            scanningStatusIntent.setAction(ACTION_BLE_SCANNING_STARTED);
            mContextActivity.sendBroadcast(scanningStatusIntent);
            logMessages("Sent intent");
        }
    }

    // Stop scanning method
    public void stopScanning() {
        scanner.stopScan(scanCallback);
        scanning = false;
        Intent scanningStatusIntent = new Intent();
        scanningStatusIntent.setAction(ACTION_BLE_SCANNING_STOPPED);
        mContextActivity.sendBroadcast(scanningStatusIntent);
        logMessages("Scanning stopped " + scanning);
    }

    // scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //logMessages("Device discovered: ");
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            discoveredMacAddress = device.getAddress();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                deviceName = device.getAlias();
            }
            else {deviceName = "Unknown name";}
           // logMessages("Device discovered: " + deviceName + " " + discoveredMacAddress);

            //Checks for duplicate macAddresses in arraylist
            int n = scannerList.size();
            if(n > 0){
                for (int i = 0; i < n; i++) {
                    if(discoveredMacAddress.equals(scannerList.get(i).getDeviceMacAddress())){
                        return;
                    }
                }

            }
            //if not already in list, add and send to rv adapter
            scannerList.add(new ScanResults(R.drawable.ic_device_type_flare, deviceName, discoveredMacAddress, deviceType));
          //  mRecyclerView.setAdapter(new ScannerAdapter(scannerList));

        }
    };

    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
    }


}




