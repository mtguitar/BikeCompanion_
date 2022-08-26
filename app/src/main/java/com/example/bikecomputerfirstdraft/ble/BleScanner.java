package com.example.bikecomputerfirstdraft.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerAdapter;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerItem;

import java.util.ArrayList;

@SuppressLint("MissingPermission")

public class BleScanner {

    private final static String TAG = "FlareLog";

    private AppBarConfiguration mAppBarConfiguration;



    private boolean scanning = false;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanSettings scanSettings;

    private Context mContext;
    private Handler handler;
    private String deviceMacAddress;
    private View view;

    private String discoveredMacAddress;
    private String deviceName;
    private RecyclerView mRecyclerView;

    ArrayList<ScannerItem> scannerList;

    private static final long SCAN_PERIOD = 10000;



    public BleScanner(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;

        initializeScanner();
        startScan();
        scannerList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerViewScanner);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


    }


    // Initialize scanner
    public void initializeScanner() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        logMessages("initialized ble");


    }

    // Scan for selected device by MacAddress
    public void startScan() {
        //Set scan settings and filter
        //initializeScanner();


        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        /*
        scanFilter =
                new ScanFilter.Builder().setDeviceAddress(deviceMacAddress).build();

         */

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
            scanner.startScan(scanCallback);
            //scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
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

    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
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


            scannerList.add(new ScannerItem(R.drawable.ic_flare, deviceName, discoveredMacAddress));
            mRecyclerView.setAdapter(new ScannerAdapter(scannerList));

        }
    };


}




