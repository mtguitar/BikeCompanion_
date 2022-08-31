package com.example.bikecomputerfirstdraft.ui.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.bikecomputerfirstdraft.ble.BleScannerService;
import com.example.bikecomputerfirstdraft.constants.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ScannerFragment extends Fragment implements RecyclerViewInterface{

    private static final String TAG = "FlareLog";

    private boolean scanning = true;

    private static Button buttonStopScan;
    private static TextView textViewScanTitle;
    private static View progressBarScan;

    private ArrayList scanResults;
    private RecyclerView recyclerView;

    private String name;
    private String macAddress;
    private String serviceUuids;
    private String deviceType;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //This is where we initialize the data. Normally this would be from a remote server

        name = ScannerFragmentArgs.fromBundle(getArguments()).getName();
        macAddress = ScannerFragmentArgs.fromBundle(getArguments()).getMacAddress();
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
        //might need to change context
        recyclerView.setAdapter(new ScannerAdapter(scanResults, this));
        this.scanResults = scanResults;
    }

    //Sends intent to BleScannerService
    private void sendCommandToService(String action) {
        Intent scanningServiceIntent = new Intent(requireContext(), BleScannerService.class);
        scanningServiceIntent.setAction(action);
        if (name != null){
            scanningServiceIntent.putExtra("name", name);
        }
        if (macAddress != null){
            scanningServiceIntent.putExtra("macAddress", macAddress);
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
}