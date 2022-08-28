package com.example.bikecomputerfirstdraft.ble;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.MainActivity;
import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.other.Constant;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerItem;

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
    private static final long SCAN_PERIOD =10000;

    private String name;
    private String macAddress;
    private ParcelUuid serviceUuids;

    //recyclerView vars
    private View view;
    private RecyclerView mRecyclerView;

    //scanResults vars
    private String discoveredMacAddress;
    private String deviceName;
    ArrayList<ScannerItem> scannerResults;

    //communication with fragment
    public static String ACTION_BLE_SCANNING_STARTED = Constant.ACTION_BLE_SCANNING_STARTED;
    public static String ACTION_BLE_SCANNING_STOPPED = Constant.ACTION_BLE_SCANNING_STOPPED;


    public BleScannerService() {
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (intent.hasExtra("name")){
            name = intent.getStringExtra("name");
        }
        if (intent.hasExtra("macAddress")){
            macAddress = intent.getStringExtra("macAddress");
        }
        if (intent.hasExtra("serviceUuids")) {
            serviceUuids = ParcelUuid.fromString(intent.getStringExtra("serviceUuids"));
        }

        switch (action){
            case Constant.ACTION_START_OR_RESUME_SERVICE:
                if(isFirstRun){
                    startForegroundService();
                    isFirstRun = false;
                    startScan();
                    Log.d(Constant.TAG, "Started service");
                }
                else{
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


    /**
     * Code related to scanning
     */

    public void initializeBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        logMessages("initialized ble");
    }

    // Scan for selected devices
    public void startScan() {

        //initialize ble
        initializeBluetooth();

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
                scanning = true;
            }
            else {
                scanner.startScan(scanCallback);
                scanning = true;
            }
            logMessages("Scanning . . . ");
            sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STARTED);
        }

    }

    // Stop scanning method
    public void stopScanning() {
        scanner.stopScan(scanCallback);
        logMessages("Scanning stopped");
        sendIntentToFragment(Constant.ACTION_BLE_SCANNING_STOPPED);
        scanning = false;
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
            addScanResults(deviceName, discoveredMacAddress);

            //mRecyclerView.setAdapter(new ScannerAdapter(scannerList));
        }
    };

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

    public  static MutableLiveData<ArrayList<ScannerItem>> scannerLiveDataList = new MutableLiveData<>();
    public static MutableLiveData<String> isScanning = new MutableLiveData();

    public static MutableLiveData<ArrayList<ScannerItem>> getScanResults() {
        return scannerLiveDataList;
    }

    private void addScanResults(String deviceName, String discoveredMacAddress) {
        if (scannerLiveDataList == null) {
            scannerLiveDataList = new MutableLiveData<>();
        }
        if (scannerResults == null) {
            scannerResults = new ArrayList<>();
        }
        //checks to see if deviceMacAddress is already in list
        int n = scannerResults.size();
        if(n > 0){
            for (int i = 0; i < n; i++) {
                if(discoveredMacAddress.equals(scannerResults.get(i).getTextDescription())){
                    return;
                }
            }
        }
        //if not already in list, add
        scannerResults.add(new ScannerItem(R.drawable.ic_flare, deviceName, discoveredMacAddress));
        getScanResults().postValue(scannerResults);
        logMessages("Posted scan result " + deviceName + discoveredMacAddress);
    }

    private void updateScanStatus(String scanningStatus) {
        isScanning.postValue(scanningStatus);
    }


    /**
     * Set up foreground service, and notification
     */


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


}




















