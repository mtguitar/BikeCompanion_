package com.example.bikecompanion.ble;

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

import com.example.bikecompanion.R;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.ui.scanner.ScanResults;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("MissingPermission")
public class BleScannerService extends LifecycleService {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ScannerService";

    //vars
    private boolean scanning = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Handler handler;

    //filter and scan settings vars
    private ScanFilter scanFilter;
    private ScanSettings scanSettings;
    private static final long SCAN_PERIOD =3000;

    private ParcelUuid serviceUuids = null;
    private String deviceType = "Unknown";

    //scanResults vars
    private ArrayList<ScanResults> scanResults;
    private String deviceName;
    private String deviceMacAddress;


    public BleScannerService() {
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String action = intent.getAction();
        getIntentExtras(intent);

        Log.d(TAG, "Received intent from fragment: " + action + " " + deviceType);

        switch (action){
            case Constants.ACTION_START_OR_RESUME_SERVICE:
                startScan();
                Log.d(TAG, "Started service");
                break;

            case Constants.ACTION_STOP_SERVICE:
                Log.d(TAG, "Stopped service");
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
        Log.d(TAG, "BleScannerService Destroyed");
        stopSelf();
    }

    private void getIntentExtras(Intent intent){
        deviceType = intent.getStringExtra("deviceType");
        if (intent.hasExtra("serviceUuids")) {
            serviceUuids = ParcelUuid.fromString(intent.getStringExtra("serviceUuids"));
        }

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
                new ScanFilter.Builder().setServiceUuid(serviceUuids).build();

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
        sendIntentToFragment(Constants.ACTION_BLE_SCANNING_STARTED);
    }


    //scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            deviceMacAddress = device.getAddress();
            deviceName = device.getName();
            if (deviceName == null){
                deviceName = "Unknown";
            }
            addScanResults(deviceName, deviceMacAddress, deviceType);
        }
    };

    //Stops scanning
    public void stopScanning() {
        //stop scan timer
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        //stop scanner
        scanner.stopScan(scanCallback);
        scanning = false;
        //send intent to fragment that scanning is stopped
        sendIntentToFragment(Constants.ACTION_BLE_SCANNING_STOPPED);

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
        //Log.d(TAG, "Sent intent to fragment " + action);
    }


    /**
     * LiveData code
     */
    public static MutableLiveData<ArrayList<ScanResults>> scannerLiveDataList = new MutableLiveData<>();

    public static MutableLiveData<ArrayList<ScanResults>> getScanResults() {
        return scannerLiveDataList;
    }

    private void addScanResults(String deviceName, String deviceMacAddress, String deviceType) {
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
                if(deviceMacAddress.equals(scanResults.get(i).getDeviceMacAddress())){
                    return;
                }
            }
        }
        //if deviceMacAddress not already in list, add device to scannerResults
        int image;
        if (deviceType.contains(Constants.DEVICE_TYPE_LIGHT)){
            image = R.drawable.ic_device_type_light;
        }
        else if (deviceType.contains(Constants.DEVICE_TYPE_SPEED)){
            image = R.drawable.ic_speed;
        }
        else {
            image = R.drawable.ic_device_type_other_sensor;
        }
        scanResults.add(new ScanResults(image, deviceName, deviceMacAddress, deviceType));
        scannerLiveDataList.postValue(scanResults);
    }



}




















