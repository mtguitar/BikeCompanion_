package com.example.bikecomputerfirstdraft.ui.addSensor;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bikecomputerfirstdraft.BluetoothLeService;
import com.example.bikecomputerfirstdraft.MainActivity;
import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.databinding.ActivityMainBinding;
import com.example.bikecomputerfirstdraft.databinding.FragmentAddSensorBinding;
import com.example.bikecomputerfirstdraft.databinding.FragmentGalleryBinding;
import com.example.bikecomputerfirstdraft.databinding.FragmentHomeBinding;
import com.example.bikecomputerfirstdraft.ui.gallery.GalleryViewModel;

import java.util.Collections;
import java.util.UUID;

public class AddSensorFragment extends Fragment {

    private AddSensorViewModel mViewModel;
    private FragmentAddSensorBinding binding;

    public static AddSensorFragment newInstance() {
        return new AddSensorFragment();
    }


    private final static String TAG = "FlareLog";

    private AppBarConfiguration mAppBarConfiguration;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static UUID LIGHT_MODE_SERVICE_UUID = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public static UUID LIGHT_MODE_CHARACTERISTIC_UUID = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");

    public UUID SERVICE_UUID = LIGHT_MODE_SERVICE_UUID;
    public UUID CHARACTERISTIC_UUID = LIGHT_MODE_CHARACTERISTIC_UUID;


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
    MainActivity.Device flareOne = new MainActivity.Device(
            "Flare One",
            "Flare RT",
            "F8:EF:93:1C:EC:DB",
            "light");

    MainActivity.Device flareTwo = new MainActivity.Device(
            "Flare Two",
            "Flare RT",
            "F5:95:D9:24:C7:3A",
            "light");




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddSensorViewModel addSensorViewModel =
                new ViewModelProvider(this).get(AddSensorViewModel.class);

        binding = FragmentAddSensorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button buttonConnect = binding.button2;
        final Button buttonSubscribe = binding.button3;

        buttonConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                connectDevice("F8:EF:93:1C:EC:DB");
            }
        });

        buttonSubscribe.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                subscribeToNotification();
            }
        });

        //final TextView textView = binding.textAddSensor;
        //addSensorViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
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
            gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
            getActivity().bindService(gattServiceIntent, serviceConnection, getActivity().BIND_AUTO_CREATE);
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
            }
            boundToService = true;

            bluetoothLeService.connectToDevice("F8:EF:93:1C:EC:DB");
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


    /*
    // Method to create buttons to connect to devices
    // If you want to read a characteristic when first connected, set readOnConnect and give UUIDs
    private void createConnectButton(int buttonId, MainActivity.Device device, boolean subscribe, UUID service, UUID characteristic){
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
    private void createWriteButton(int buttonId, MainActivity.Device device){
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });

    }


*/


}