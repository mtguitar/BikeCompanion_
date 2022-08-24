package com.example.bikecomputerfirstdraft;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.bikecomputerfirstdraft.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "FlareLog";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static UUID LIGHT_MODE_SERVICE_UUID = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public static UUID LIGHT_MODE_CHARACTERISTIC_UUID = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");

    public UUID SERVICE_UUID;
    public UUID CHARACTERISTIC_UUID;


    private TextView textViewLog;
    private TextView textViewLightMode;
    private TextView textViewConnectedDevices;

    private String deviceName;
    private String deviceMacAddress;

    private boolean scanning = false;
    private boolean deviceConnected = false;
    private boolean boundToService = false;
    private boolean servicesDiscovered = false;
    private boolean deviceFound = false;
    public boolean connectIfFound = false;
    private boolean subscribeToNotification = false;

    private BluetoothLeService bluetoothLeService;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanSettings scanSettings;
    private Intent gattServiceIntent;
    private ScanFilter scanFilter;

    private final static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private final static String PERMISSION_BACKGROUND_LOCATION =
            "Manifest.permission.ACCESS_BACKGROUND_LOCATION";

    Handler handler;

    private static final long SCAN_PERIOD = 5000;

    // Class to define devices
    public static class Device {
        String assignedName;
        String bleName;
        String macAddress;
        String deviceType;

        // Constructor
        public Device(String assignedName, String bleName, String macAddress, String deviceType){
            this.assignedName = assignedName;
            this.macAddress = macAddress;
            this.deviceType = deviceType;
            this.bleName = bleName;
        }
    }

    // Devices
    Device flareOne = new Device(
            "Flare One",
            "Flare RT",
            "F8:EF:93:1C:EC:DB",
            "light");

    Device flareTwo = new Device(
            "Flare Two",
            "Flare RT",
            "F5:95:D9:24:C7:3A",
            "light");

    /**
     * Start of onCreate
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //textViewLog = findViewById(R.id.textViewLog);
        //textViewLightMode = findViewById(R.id.textViewLightMode);
        //textViewConnectedDevices = findViewById(R.id.textViewConnectedDevices);

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        initViewObjects();

        //Request permissions
        int PERMISSION_ALL = 1;
        ActivityCompat.requestPermissions
                (MainActivity.this, PERMISSIONS, PERMISSION_ALL);
        ActivityCompat.requestPermissions
                (MainActivity.this,
                        new String[]{PERMISSION_BACKGROUND_LOCATION},
                        PERMISSION_ALL);


    }

    /**
     * End of onCreate
     *
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initViewObjects() {
        // Make textViewLog scrollable
        //textViewLog.setMovementMethod(new ScrollingMovementMethod());

        // Buttons
        //createConnectButton(R.id.buttonConnectFlareOne, flareOne, true, LIGHT_MODE_SERVICE_UUID, LIGHT_MODE_CHARACTERISTIC_UUID);
        //createConnectButton(R.id.buttonConnectFlareTwo, flareTwo, true, LIGHT_MODE_SERVICE_UUID, LIGHT_MODE_CHARACTERISTIC_UUID);
        //createReadButton(R.id.buttonCheckMode, LIGHT_MODE_SERVICE_UUID, LIGHT_MODE_CHARACTERISTIC_UUID);

        //TODO create buttons for read and write

    }

    // Initialize scanner
    public void initializeScanner() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
    }

    // Scan for selected device by MacAddress
    public void scanDevice(String deviceMacAddress) {
        //Set scan settings and filter
        initializeScanner();
        scanSettings =
                new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanFilter =
                new ScanFilter.Builder().setDeviceAddress(deviceMacAddress).build();

        //Start scanning on a timer
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scanning) {
                        stopScanning();
                        logMessages(deviceName + " not found");
                    }
                }
            }, SCAN_PERIOD);
            scanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
            scanning = true;
            //textViewLog.setText("");
            logMessages("Scanning for " + deviceName);
        }
    }

    // scanCallback object to receive scan results
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String discoveredMacAddress = device.getAddress();
            if (discoveredMacAddress != null) {
                if (discoveredMacAddress.equals(deviceMacAddress)) {
                    logMessages("Found " + deviceName);
                    deviceFound = true;
                    stopScanning();
                }
                if (connectIfFound) {
                    connectDevice(deviceMacAddress);
                }
            }
        }
    };


    private void connectDevice(String deviceMacAddress){
        // Stop scan and initialize BluetoothLEService
        // Once BluetoothLeService is initialized, device will auto connect
        // Checks if BluetoothLeService is bound
        // If not bound, binds to BluetoothLeService and calls serviceConnection
        // If bound, call BluetoothLeService's connected method directly, passing deviceMacAddress
        if (!boundToService) {
            gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        }
        else {
            bluetoothLeService.connectToDevice(deviceMacAddress);
            logMessages("Trying to connect to " + deviceName);
        }

    }

    private void subscribeToNotification(){
        bluetoothLeService.setCharacteristicNotification(SERVICE_UUID, CHARACTERISTIC_UUID, true);
        subscribeToNotification = false;
    }

    // Stop scanning method
    public void stopScanning(){
        initializeScanner();
        scanner.stopScan(scanCallback);
        logMessages("Scanning stopped");
        scanning = false;
    }

    // serviceConnection object to connect to BluetoothLeService
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            // If bluetoothLeService is initialized, connect to device
            if (!bluetoothLeService.initialize()) {
                logMessages("Failed to initialize BluetoothLeService");
                finish();
            }
            boundToService = true;

            bluetoothLeService.connectToDevice(flareOne.macAddress);
            logMessages("Trying to connect to " + deviceName);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothLeService = null;
        }
    };

    // Creates gatt intent filters
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.
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
            final String characteristic = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                deviceConnected = true;
                logMessages(deviceName + " connected");
                //textViewConnectedDevices.append(deviceName + "\n");

            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                deviceConnected = false;
                logMessages(deviceName + " disconnected");
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                logMessages(deviceName + " services discovered");
                servicesDiscovered = true;
                if(subscribeToNotification){
                    subscribeToNotification();
                }
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                logMessages(deviceName + " characteristic: " + characteristic);
                //textViewLightMode.setText(characteristic);

            }


        }
    };


    // Logs messages to UI and logCat
    private void logMessages(String logMessage) {
        Log.d(TAG, logMessage);
        //textViewLog.append(logMessage + "\n");
    }

    // Method to create buttons to connect to devices
    // If you want to read a characteristic when first connected, set readOnConnect and give UUIDs
    private void createConnectButton(int buttonId, Device device, boolean subscribe, UUID service, UUID characteristic){
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                connectIfFound = true;
                subscribeToNotification = subscribe;
                SERVICE_UUID = service;
                CHARACTERISTIC_UUID = characteristic;
                if (deviceConnected) {
                    bluetoothLeService.disconnect();
                    deviceConnected = false;
                }
                deviceName = device.assignedName;
                deviceMacAddress = device.macAddress;
                scanDevice(device.macAddress);
            }

        });

    }

    // Method to create buttons to read from device
    private void createReadButton(int buttonId, UUID service, UUID characteristic){
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (servicesDiscovered){
                    bluetoothLeService.readCharacteristic(service, characteristic);

                }
            }
        });


    }

    // TODO: Method to create buttons to write to devices
    private void createWriteButton(int buttonId, Device device){
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

    }



}

