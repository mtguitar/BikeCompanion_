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
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.other.Constant;
import com.example.bikecomputerfirstdraft.ui.scanner.ScanResults;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("MissingPermission")
public class BleScannerService extends LifecycleService {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog BleScannerServ";

    //vars
    private boolean scanning = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Handler handler;

    //filter and scan settings vars
    private ScanFilter scanFilter;
    private ScanSettings scanSettings;
    private static final long SCAN_PERIOD =5000;

    private String name = null;
    private String macAddress = null;
    private ParcelUuid serviceUuids = null;

    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    private ArrayList<ScanResults> scanResults;

    public BleScannerService() {
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.d(TAG, "Received intent from fragment: " + action);
        if (intent.hasExtra("name")){
            name = intent.getStringExtra("name");
            macAddress = null;
            serviceUuids = null;
        }
        else if (intent.hasExtra("macAddress")){
            macAddress = intent.getStringExtra("macAddress");
            name = null;
            serviceUuids = null;
        }
        else if (intent.hasExtra("serviceUuids")) {
            serviceUuids = ParcelUuid.fromString(intent.getStringExtra("serviceUuids"));
            name = null;
            macAddress = null;
        }

        Log.d("flareIntent", name + " " + macAddress + " " + serviceUuids + " received from intent");
        switch (action){
            case Constant.ACTION_START_OR_RESUME_SERVICE:
                if(isFirstRun){
                    //startForegroundService();
                    isFirstRun = false;

                    startScan();
                    Log.d(Constant.TAG, "Started service" + "is first run?" + isFirstRun);
                }
                else{
                    if(scanning){
                        stopScanning();
                    }
                    startScan();
                    Log.d(Constant.TAG, "Resuming service");
                }
                break;

            case Constant.ACTION_PAUSE_SERVICE:
                Log.d(Constant.TAG, "Paused service");
                if(scanning){
                    stopScanning();
                }
                break;

            case Constant.ACTION_STOP_SERVICE:
                Log.d(Constant.TAG, "Stopped service");
                if(scanning){
                    stopScanning();
                }
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //makes sure next scan does not have any leftover filters
    public void onDestroy() {
        super.onDestroy();
        stopScanning();
        Log.d(TAG, "BleScannerService Destroyed");
    }

    /**
     * Code related to scanning
     */

    // Scans for devices
    public void startScan() {

        //initialize ble
        initializeBluetooth();

        //Set scan settings and filter
        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanFilter =
                new ScanFilter.Builder().setDeviceName(name).setDeviceAddress(macAddress).setServiceUuid(serviceUuids).build();

        // Sets a timer
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scanning) {
                    stopScanning();
                }
            }
        }, SCAN_PERIOD);
        scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
        Log.d(TAG, "Scanning");

        scanning = true;

        //send intent to fragment alerting it that scanning has started
        sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STARTED);

    }


    //scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            discoveredMacAddress = device.getAddress();
            deviceName = device.getName();
            if (deviceName == null){
                deviceName = "Unknown";
            }
            addScanResults(deviceName, discoveredMacAddress);
        }
    };

    //Stops scanning
    public void stopScanning() {
        //stop scan timer
        handler.removeCallbacksAndMessages(null);
        //stop scanner
        scanner.stopScan(scanCallback);
        scanning = false;
        //send intent to fragment that scanning is stopped
        sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STOPPED);

        Log.d(TAG, "Scanning stopped");

        //stop BleScannerService
        stopSelf();
    }

    //initializes device's bluetooth adapter ble scanner
    public void initializeBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        Log.d(TAG, "Initialized bluetooth scanner");
    }

    //method for sending intents to ScannerFragment
    private void sendIntentToFragment(String action) {
        Intent scanningStatusIntent = new Intent(action);
        sendBroadcast(scanningStatusIntent);
        Log.d(Constant.TAG, "sent intent to fragment " + action);
    }


    /**
     * LiveData code
     */
    public  static MutableLiveData<ArrayList<ScanResults>> scannerLiveDataList = new MutableLiveData<>();

    public static MutableLiveData<ArrayList<ScanResults>> getScanResults() {
        return scannerLiveDataList;
    }

    private void addScanResults(String deviceName, String discoveredMacAddress) {
        if (scannerLiveDataList == null) {
            scannerLiveDataList = new MutableLiveData<>();
        }
        if (scanResults == null) {
            scanResults = new ArrayList<>();
        }
        //checks to see if deviceMacAddress is already in list
        int n = scanResults.size();
        if(n > 0){
            for (int i = 0; i < n; i++) {
                if(discoveredMacAddress.equals(scanResults.get(i).getTextDescription())){
                    return;
                }
            }
        }
        //if deviceMacAddress not already in list, add device to scannerReults
        int image = R.drawable.other_sensor;
        if (deviceName.contains("Flare")){
            image = R.drawable.flare;
        }
        if (deviceName.contains("Wahoo")){
            image = R.drawable.speed;
        }
        scanResults.add(new ScanResults(image, deviceName, discoveredMacAddress));
        scannerLiveDataList.postValue(scanResults);
        Log.d(TAG, "Posted scan result " + deviceName + discoveredMacAddress);
    }



}




















