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

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("MissingPermission")
public class BleScannerService extends LifecycleService {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog";

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
    private ParcelUuid serviceUuids;

    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    private ArrayList<ScanResults> scanResults;

    public BleScannerService() {
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "received intent from add sensor fragment");
        String action = intent.getAction();
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

    public void onDestroy() {
        name = null;
        macAddress = null;
        serviceUuids = null;
        stopScanning();
        super.onDestroy();
    }


    /**
     * Code related to scanning
     */

    // Scan for selected devices
    public void startScan() {

        //initialize ble
        initializeBluetooth();

        //Set scan settings and filter
        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        if(serviceUuids != null){
            scanFilter = new ScanFilter.Builder().setServiceUuid(serviceUuids).build();
            Log.d(TAG, "added serviceUUID to scan filter");
        }
        if(name != null){
            scanFilter = new ScanFilter.Builder().setDeviceName(name).build();
            Log.d(TAG, "added name to scan filter");
        }
        if(macAddress != null) {
            scanFilter = new ScanFilter.Builder().setDeviceAddress(macAddress).build();
            Log.d(TAG, "added macAddress to scan filter");
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
            if (serviceUuids != null || name != null || macAddress != null) {
                scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
                Log.d(TAG, "scanning with filter");
            }
            else {
                //scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
                scanner.startScan(scanCallback);
                Log.d(TAG, "scanning without filter");
            }
            scanning = true;
            logMessages("Scanning . . . ");
            sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STARTED);
        }

    }



    // scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //logMessages("Device discovered: ");
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

    // Stop scanning method
    public void stopScanning() {
        //stop timer
        handler.removeCallbacksAndMessages(null);
        //stop scanner
        scanner.stopScan(scanCallback);
        scanning = false;
        //send intent to fragment that scanning is stopped
        sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STOPPED);

        logMessages("Scanning stopped");
    }

    public void initializeBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        logMessages("initialized ble");
    }

    private void sendIntentToFragment(String action) {
        Intent scanningStatusIntent = new Intent(action);
        sendBroadcast(scanningStatusIntent);
        Log.d(Constant.TAG, "sent intent to fragment " + action);
    }



    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
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
        logMessages("Posted scan result " + deviceName + discoveredMacAddress);
    }


    /**
     * Set up foreground service, and notification
     */

/*
    private void startForegroundService() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constant.NOTIFICATION_CHANNEL_ID)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_flare)
                        .setContentTitle("BikeComp")
                        .setContentText("Scanning")
                        .setContentIntent(createPendingIntent())
                ;

        startForeground(Constant.NOTIFICATION_ID, notificationBuilder.build());

    }

    private void createNotificationChannel(NotificationManager notificationManager){
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    Constant.NOTIFICATION_CHANNEL_ID,
                    Constant.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

    }

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return resultPendingIntent;
    }

 */


}




















