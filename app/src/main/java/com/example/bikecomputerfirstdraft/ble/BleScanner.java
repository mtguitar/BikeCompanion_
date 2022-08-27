package com.example.bikecomputerfirstdraft.ble;

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
import android.graphics.Color;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerAdapter;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@SuppressLint("MissingPermission")

public class BleScanner {

    private final static String TAG = "FlareLog";


    //vars
    private boolean scanning = false;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Context mContext;
    private Handler handler;

    //filter and scan settings vars
    private ScanFilter scanFilter;
    String name;
    String macAddress;
    ParcelUuid serviceUuids;
    private ScanSettings scanSettings;
    private static final long SCAN_PERIOD = 5000;

    //recyclerView vars
    private View view;
    private RecyclerView mRecyclerView;


    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    private ArrayList<ScannerItem> scannerList;



    //constructors, the last three parameters are scan filters and can be null
    public BleScanner (Context mContext, View view, String name, String macAddress, ParcelUuid serviceUuids) {
        this.mContext = mContext;
        this.view = view;
        this.name = name;
        this.macAddress = macAddress;
        this.serviceUuids = serviceUuids;

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
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
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
        }
    }

    // Stop scanning method
    public void stopScanning() {
        scanner.stopScan(scanCallback);
        logMessages("Scanning stopped");
        scanning = false;
    }

    // scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            logMessages("Device discovered: ");
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            discoveredMacAddress = device.getAddress();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                deviceName = device.getAlias();
            }
            else {deviceName = "Unknown name";}
            logMessages("Device discovered: " + deviceName + " " + discoveredMacAddress);

            //Checks for duplicate macAddresses in arraylist
            int n = scannerList.size();
            if(n > 0){
                for (int i = 0; i < n; i++) {
                    if(discoveredMacAddress.equals(scannerList.get(i).getTextDescription())){
                        return;
                    }
                }

            }
            //if not already in list, add and send to rv adapter
            scannerList.add(new ScannerItem(R.drawable.ic_flare, deviceName, discoveredMacAddress));
            mRecyclerView.setAdapter(new ScannerAdapter(scannerList));

        }
    };

    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
    }


}




