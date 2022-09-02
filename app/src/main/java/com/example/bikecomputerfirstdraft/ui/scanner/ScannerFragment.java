package com.example.bikecomputerfirstdraft.ui.scanner;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleConnection;
import com.example.bikecomputerfirstdraft.ble.BleConnectionService;
import com.example.bikecomputerfirstdraft.ble.BleScannerService;
import com.example.bikecomputerfirstdraft.ble.FormatBleData;
import com.example.bikecomputerfirstdraft.constants.Constants;
import com.example.bikecomputerfirstdraft.deviceTypes.FlareRT;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.bikecomputerfirstdraft.constants.Constants.ACTION_DATA_AVAILABLE;
import static com.example.bikecomputerfirstdraft.constants.Constants.ACTION_GATT_CONNECTED;
import static com.example.bikecomputerfirstdraft.constants.Constants.ACTION_GATT_DISCONNECTED;
import static com.example.bikecomputerfirstdraft.constants.Constants.ACTION_GATT_SERVICES_DISCOVERED;
import static com.example.bikecomputerfirstdraft.constants.Constants.EXTRA_DATA;

public class ScannerFragment extends Fragment implements RecyclerViewInterface{

    private static final String TAG = "FlareLog";

    private boolean scanning = true;

    private static Button buttonStopScan;
    private static TextView textViewScanTitle;
    private static View progressBarScan;
    private static TextView textViewDeviceManufacturer;
    private static TextView textViewDeviceType;
    private static TextView textViewDeviceBattery;



    private ArrayList scanResults;
    private RecyclerView recyclerView;

    private String deviceName;
    private String deviceMacAddress;
    private String serviceUuids;
    private String deviceType = "light_set";


    //bluetooth vars
    private BleConnectionService bleConnectionService;
    private BluetoothAdapter bluetoothAdapter;
    private Intent gattServiceIntent;


    //connection state booleans
    private boolean deviceConnected = false;
    private boolean boundToService = false;
    private boolean servicesDiscovered = false;
    private boolean subscribeToNotification = false;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //This is where we initialize the data. Normally this would be from a remote server

        deviceName = ScannerFragmentArgs.fromBundle(getArguments()).getName();
        deviceMacAddress = ScannerFragmentArgs.fromBundle(getArguments()).getMacAddress();
        serviceUuids = ScannerFragmentArgs.fromBundle(getArguments()).getServiceUuids();
        deviceType = ScannerFragmentArgs.fromBundle(getArguments()).getDeviceType();

        sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //view vars
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        buttonStopScan = (Button)view.findViewById(R.id.buttonStopScan);
        textViewScanTitle = view.findViewById(R.id.textViewScanTitle);
        progressBarScan = view.findViewById(R.id.progressBarScan);
        recyclerView = view.findViewById(R.id.recyclerViewScanner);
        textViewDeviceManufacturer = view.findViewById(R.id.text_view_scanner_manufacturer);
        textViewDeviceType = view.findViewById(R.id.text_view_scanner_device_type);
        textViewDeviceBattery = view.findViewById(R.id.text_view_scanner_battery);



        //creates recyclerView but does not show until there is data in it
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if (scanResults != null){
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }


        //Setup observer of livedata for recyclerView, calls updateRecyclerView when data changes
        final Observer<ArrayList<ScanResults>> observerScanResults;
        observerScanResults = new Observer<ArrayList<ScanResults>>(){
            public void onChanged(@Nullable final ArrayList scanResults) {
                updateRecycleViewer(scanResults);
            }
        };

        //start observing scan results
        BleScannerService.getScanResults().observe(getActivity(), observerScanResults);

        //Intent filters to listen for scanning updates
        scannerUpdateIntentFilter();
        getActivity().registerReceiver(scannerUpdateReceiver, scannerUpdateIntentFilter());

        //set button onclick listener
        buttonStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scanning){
                    sendCommandToService(Constants.ACTION_PAUSE_SERVICE);
                }
                else{
                    if(scanResults != null) {
                        scanResults.clear();
                        updateRecycleViewer(scanResults);
                    }
                    sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE);
                }
                Log.d(Constants.TAG, "button clicked");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scanResults != null) {
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }
    }

    public void updateRecycleViewer(ArrayList scanResults){
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        recyclerView.setAdapter(scannerAdapter);
        this.scanResults = scanResults;
    }

    //Sends intent to BleScannerService
    private void sendCommandToService(String action) {
        Intent scanningServiceIntent = new Intent(requireContext(), BleScannerService.class);
        scanningServiceIntent.setAction(action);
        if (deviceName != null){
            scanningServiceIntent.putExtra("name", deviceName);
        }
        if (deviceMacAddress != null){
            scanningServiceIntent.putExtra("macAddress", deviceMacAddress);
        }
        if (serviceUuids != null) {
            scanningServiceIntent.putExtra("serviceUuids", serviceUuids);
        }
        scanningServiceIntent.putExtra("deviceType", deviceType);
        requireContext().startService(scanningServiceIntent);
        Log.d(Constants.TAG, "sent intent to scanner service " + action);
    }

    //Intent filters for receiving intents
    private static IntentFilter scannerUpdateIntentFilter () {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_BLE_SCANNING_STARTED);
        intentFilter.addAction(Constants.ACTION_BLE_SCANNING_STOPPED);
        return intentFilter;
    }

    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    private BroadcastReceiver scannerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Scanning broadcast received");

            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constants.ACTION_BLE_SCANNING_STARTED.equals(action)){
                scanning = true;
                textViewScanTitle.setText("Scanning for devices");
                buttonStopScan.setText("Stop Scan");
                progressBarScan.setVisibility(View.VISIBLE);
                Log.d(TAG, "Scanning Started Intent Received");
            }
            else if (Constants.ACTION_BLE_SCANNING_STOPPED.equals(action)){
                scanning = false;
                if (scanResults == null){
                    textViewScanTitle.setText("No devices found");
                }
                else {
                    textViewScanTitle.setText("Select to add to myDevices");
                }
                buttonStopScan.setText("Scan Again");
                buttonStopScan.setEnabled(true);
                progressBarScan.setVisibility(View.GONE);
                Log.d(TAG, "Scanning stopped intent received");

            }


        }
    };



    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "Clicked item RV");

        //Get device info when clicked
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        deviceName = scannerAdapter.scanResultsArrayList.get(position).getDeviceName();
        deviceMacAddress = scannerAdapter.scanResultsArrayList.get(position).getDeviceMacAddress();
        deviceType = scannerAdapter.scanResultsArrayList.get(position).getDeviceType();

        connectDevice(deviceMacAddress);

        Snackbar snackbar = Snackbar.make(getView(), "Card Clicked: " + deviceName + " " + deviceMacAddress, Snackbar.LENGTH_SHORT);
        snackbar.show();


        Log.d(TAG, deviceName + " " + deviceMacAddress);

    }

    @Override
    public void onButtonClick(int position) {

        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        String deviceName = scannerAdapter.scanResultsArrayList.get(position).getDeviceName();
        String deviceMacAddress = scannerAdapter.scanResultsArrayList.get(position).getDeviceMacAddress();
        String deviceType = scannerAdapter.scanResultsArrayList.get(position).getDeviceType();


        Snackbar snackbar = Snackbar.make(getView(), "Button Clicked: " + deviceName + " " + deviceMacAddress, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }



    //Bluetooth connection methods

    public void connectDevice(String deviceMacAddress){
        getActivity().registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

        if (!boundToService) {
            gattServiceIntent = new Intent(getActivity(), BleConnectionService.class);
            getActivity().bindService(gattServiceIntent, serviceConnection, getActivity().BIND_AUTO_CREATE);
        }
        else {
            bleConnectionService.connectDevice(deviceMacAddress);
        }

    }

    //write to characteristic notification
    public void writeCharacteristic(String payload){
        FormatBleData formatBleData = new FormatBleData();
        byte[] payloadToWrite = formatBleData.convertStingtoByte(payload);

    }

    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
            // If bluetoothLeService is initialized, connect to device
            if (!bleConnectionService.initialize()) {
                Log.d(TAG, "Failed to initialize BluetoothLeService");
            }
            boundToService = true;

            bleConnectionService.connectDevice(deviceMacAddress);
            Log.d(TAG, "Trying to connect to " + deviceName);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleConnectionService = null;
        }
    };

    // Creates gatt intent filters to receive intents from BluetoothLeService
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the BluetoothLeService.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    public BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MainActivity:  broadcast received");
            final String action = intent.getAction();
            final String characteristic = intent.getStringExtra(EXTRA_DATA);
            if (ACTION_GATT_CONNECTED.equals(action)){
                deviceConnected = true;
                Log.d(TAG, deviceName + " connected");
                //textViewConnectedDevices.append(deviceName + "\n");

            }
            else if (ACTION_GATT_DISCONNECTED.equals(action)){
                deviceConnected = false;
                Log.d(TAG, deviceName + " disconnected");
            }
            else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                Log.d(TAG, deviceName + " services discovered");
                servicesDiscovered = true;
                //TODO change these to generic constants for all devices

                bleConnectionService.readCharacteristic(FlareRT.UUID_SERVICE_BATTERY, FlareRT.UUID_CHARACTERISTIC_BATTERY);


            }
            else if (ACTION_DATA_AVAILABLE.equals(action)){
                //textViewDeviceType.setText(deviceType);

                byte[] battery2 = intent.getByteArrayExtra(BleConnectionService.EXTRA_DATA);
                String battery = "test";
                battery = String.valueOf(battery2[0]);
                Log.d(TAG, "received new data intent" + battery2[0]);
                if(battery != null){
                    textViewDeviceBattery.setText(battery + "%");
                }
                //bleConnectionService.readCharacteristic(FlareRT.UUID_SERVICE_DEVICE_MANUFACTURER, FlareRT.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER);

/*
                }
                if(characteristic.equals(FlareRT.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER)){
                    String deviceManufacturer = intent.getStringExtra(BleConnectionService.EXTRA_DATA);
                    Log.d(TAG, deviceName + " " + deviceManufacturer);
                    //textViewDeviceManufacturer.setText(deviceManufacturer[0]);
                }

 */



                Log.d(TAG, deviceName + " characteristic: " + characteristic);

            }


        }
    };



}