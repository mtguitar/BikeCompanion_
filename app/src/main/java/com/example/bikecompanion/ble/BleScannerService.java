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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.EntitiesRepository;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.ui.scanner.ScannerListenerInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("MissingPermission")
public class BleScannerService extends LifecycleService {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ScannerService";

    //vars
    private boolean scanning = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Handler handler;
    private List<Device> devices;

    //filter and scan settings vars
    private ScanFilter scanFilter;
    private ScanSettings scanSettings;
    private static final long SCAN_PERIOD =3000;

    private ParcelUuid serviceUuids = null;
    private DeviceType deviceType;

    //scanResults vars
    private ArrayList<ScannerListenerInterface> scanResults;
    private String deviceName;
    private String deviceMacAddress;
    public static MutableLiveData<ArrayList<ScannerListenerInterface>> scannerLiveDataList;


    public BleScannerService() {
    }

    //makes sure next scan does not have any leftover filters
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BleScannerService Destroyed");
        stopSelf();
    }

    /**
     * Binder
     */

    //Allows fragments to bind to this service
    public class LocalBinder extends Binder {
        public BleScannerService getService() {
            return BleScannerService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    /**
     * Code related to scanning
     */

    // Scans for devices
    public void startScan(ParcelUuid serviceUuids, DeviceType deviceType, List<Device> devices) {
        this.devices = devices;
        this.deviceType = deviceType;

        //initialize ble
        initializeBluetooth();

        //Set scan settings and filter
        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        if (!deviceType.equals(DeviceType.GENERIC)){
            scanFilter =
                    new ScanFilter.Builder().setServiceUuid(serviceUuids).build();
        }

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
        scanning = true;

        //send intent to fragment alerting it that scanning has started
        sendIntentToFragment(Constants.ACTION_BLE_SCANNING_STARTED);
        Log.d(TAG, "Scanning");
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
        if (handler != null) {
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
     * LiveData
     */
    public static MutableLiveData<ArrayList<ScannerListenerInterface>> getScanResults() {
        if (scannerLiveDataList == null){
            scannerLiveDataList = new MutableLiveData<>();
        }
        return scannerLiveDataList;
    }

    private void addScanResults(String deviceName, String deviceMacAddress, DeviceType deviceType)
    {
        if (scannerLiveDataList == null) {
            scannerLiveDataList = new MutableLiveData<>();
        }
        if (scanResults == null) {
            scanResults = new ArrayList<>();
        }
        //checks to see if last device discovered is already in list of scan results
        for (ScannerListenerInterface scanResult : scanResults) {
            if (deviceMacAddress.equals(scanResult.getDeviceMacAddress())) {
                return;
            }
        }
        //checks to see if last device discovered is already in myDevices
        if (devices != null) {
            for (Device device : devices) {
                if (deviceMacAddress.equals(device.getDeviceMacAddress())) {
                    return;
                }
            }
        }
        //if deviceMacAddress not already in list, add device to scannerResults
        int image = deviceType.getIcon();
        scanResults.add(new ScannerListenerInterface(image, deviceName, deviceMacAddress, deviceType));
        scannerLiveDataList.postValue(scanResults);
    }

}




















