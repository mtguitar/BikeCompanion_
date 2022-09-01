package com.example.bikecomputerfirstdraft.unused;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.bikecomputerfirstdraft.MainActivity;
import com.example.bikecomputerfirstdraft.databinding.FragmentAddSensorBinding;

import java.util.UUID;

public class UnusedCode {



}

/*



@Override
    public void onItemClick(int position) {
        Log.d(TAG, "Clicked item RV");

        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        String deviceName = scannerAdapter.scanResultsArrayList.get(position).getDeviceName();
        String deviceMacAddress = scannerAdapter.scanResultsArrayList.get(position).getDeviceMacAddress();
        String deviceType = scannerAdapter.scanResultsArrayList.get(position).getDeviceType();
        saveToSharedPreferences(macAddress, deviceType);


        Log.d(TAG, deviceName + " " + deviceMacAddress);
        Snackbar snackbar = Snackbar.make(getView(), "Clicked: " + deviceName + " " + deviceMacAddress, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void saveToSharedPreferences(String macAddress, String deviceType){
        Context context = getActivity();
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_MY_DEVICES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(macAddress, deviceType);
        editor.apply();
    }

        if(serviceUuids != null){
            scanFilter = new ScanFilter.Builder().setServiceUuid(serviceUuids).build();
            Log.d(TAG, "Added serviceUUID to scan filter");
        }
        if(name != null){
            scanFilter = new ScanFilter.Builder().setDeviceName(name).build();
            Log.d(TAG, "Added name to scan filter");
        }
        if(macAddress != null) {
            scanFilter = new ScanFilter.Builder().setDeviceAddress(macAddress).build();
            Log.d(TAG, "Added macAddress to scan filter");
        }



    private BroadcastReceiver scanFilterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Scanning broadcast received");

            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constant.ACTION_BLE_SCANNING_STARTED.equals(action)){
                scanning = true;
                textViewScanTitle.setText("Scanning for devices");
                buttonStopScan.setText("Scanning");
                buttonStopScan.setEnabled(false);
                progressBarScan.setVisibility(View.VISIBLE);
                Log.d(TAG, "Scanning Started Intent Received");
            }
            else if (Constant.ACTION_BLE_SCANNING_STOPPED.equals(action)){
                scanning = false;
                textViewScanTitle.setText("Select a device or scan again");
                buttonStopScan.setText("Scan Again");
                buttonStopScan.setEnabled(true);
                progressBarScan.setVisibility(View.GONE);
                Log.d(TAG, "Scanning stopped intent received");

            }


        }
    };




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

    //private byte[] payloadToWrite;

    private static String stringDaySolid = "1";
    private static String stringDayBlink = "7";
    private static String stringNightSolid = "5";
    private static String stringNightBlink = "3F";
    private static String off = "0";


    public byte[] convertStingtoByte(String string) {
        int intFromString = Integer.parseInt(string, 16);
        byte intToByte = (byte) intFromString;
        byte[] byteArray = new byte[1];
        byteArray[0] = (byte) (intFromString);
        return byteArray;
    }



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




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddSensorViewModel addSensorViewModel =
                new ViewModelProvider(this).get(AddSensorViewModel.class);

        binding = FragmentAddSensorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //bluetoothLeService.registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

        initViewObjects();


        final Button buttonConnect = binding.buttonConnect;
        final Button buttonSubscribe = binding.buttonSubscribe;
        final Button buttonTurnOn = binding.buttonTurnOn;
        final Button buttonTurnOff = binding.buttonTurnOff;
        final TextView textViewScanStatus = binding.textViewScanStatus;



        buttonConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                connectDevice("F8:EF:93:1C:EC:DB");
                textViewScanStatus.setText("connecting");
            }
        });

        buttonSubscribe.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                subscribeToNotification();
            }
        });

        buttonTurnOn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                writeCharacteristic(stringDayBlink);
            }
        });

        buttonTurnOff.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                writeCharacteristic(stringNightSolid);
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

            bluetoothLeService.connectDevice("F8:EF:93:1C:EC:DB");
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




}



 */

