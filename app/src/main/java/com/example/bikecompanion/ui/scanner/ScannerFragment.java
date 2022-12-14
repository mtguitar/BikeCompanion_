package com.example.bikecompanion.ui.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.adapters.scanner.RecyclerViewInterface;
import com.example.bikecompanion.adapters.scanner.ScannerAdapter;
import com.example.bikecompanion.ble.BleScannerService;
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.sharedClasses.RegisterBroadcastReceiver;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;

import java.util.ArrayList;
import java.util.List;

public class ScannerFragment extends Fragment implements RecyclerViewInterface {

    private static final String TAG = "FlareLog ScanFrag";

    private ScannerViewModel scannerViewModel;

    private List<Device> devices;

    //scanning vars
    private boolean scanning = true;
    private String serviceUuids;
    private DeviceType deviceType;
    private ArrayList scanResults;
    private RecyclerView recyclerView;
    private String deviceName;
    private String deviceMacAddress;

    //view vars
    private View view;
    private static Button buttonStopScan;
    private static TextView textViewScanTitle;
    private static View progressBarScan;
    private static Button buttonAddToMyDevices;

    private SharedEntitiesViewModel sharedEntitiesViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        scannerViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        scannerViewModel.bindService();
        sharedEntitiesViewModel = new ViewModelProvider(this).get(SharedEntitiesViewModel.class);

        getVarsFromPreviousFragment();

        //SendCommand to BleScannerService to start scanning
        scannerViewModel.startScan(ParcelUuid.fromString(serviceUuids), deviceType, devices);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scanner, container, false);

        setupViews();
        setOnClickListeners();
        setupRecyclerViewer();
        initObservers();
        registerBroadcastReceiver();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initObservers();
        if(scanResults != null) {
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerViewModel.stopScan();
//        sendCommandToService(BleScannerService.class, Constants.ACTION_STOP_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerViewModel.stopScan();
//        sendCommandToService(BleScannerService.class, Constants.ACTION_STOP_SERVICE);
    }

    private void getVarsFromPreviousFragment(){
        //vars passed from other fragment
        serviceUuids = ScannerFragmentArgs.fromBundle(getArguments()).getServiceUuids();
        deviceType = ScannerFragmentArgs.fromBundle(getArguments()).getDeviceType();

    }

    private void setupViews() {
        buttonStopScan = view.findViewById(R.id.buttonStopScan);
        textViewScanTitle = view.findViewById(R.id.textViewScanTitle);
        progressBarScan = view.findViewById(R.id.progressBarScan);
        buttonAddToMyDevices = view.findViewById(R.id.button_scanner_add_to_my_devices);
    }

    /**
     * RecyclerView setup
     */

    //Sets up RecyclerViewer and starts observing data to go in it
    private void setupRecyclerViewer(){
        //creates recyclerView but does not show until there is data in it
        recyclerView = view.findViewById(R.id.recyclerViewScanner);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if (scanResults != null){
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }
    }
    public void updateRecycleViewer(ArrayList scanResults){
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        recyclerView.setAdapter(scannerAdapter);
        this.scanResults = scanResults;
    }

    /**
     * On clickListeners
     */

    private void setOnClickListeners(){
        //set scan button onclick listener
        buttonStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scanning){
                    scannerViewModel.stopScan();
//                    sendCommandToService(BleScannerService.class, Constants.ACTION_STOP_SERVICE);
                }
                else{
                    if(scanResults != null) {
                        scanResults.clear();
                        updateRecycleViewer(scanResults);
                    }
                    scannerViewModel.startScan(ParcelUuid.fromString(serviceUuids), deviceType, devices);
//                    sendCommandToService(BleScannerService.class, Constants.ACTION_START_OR_RESUME_SERVICE);
                }
            }
        });

    }

    // Saves characteristics when user clicks on a scan result
    @Override
    public void onItemClick(int position) {

        //Get device info when clicked
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        deviceName = scannerAdapter.scanResults.get(position).getDeviceName();
        deviceMacAddress = scannerAdapter.scanResults.get(position).getDeviceMacAddress();
        deviceType = scannerAdapter.scanResults.get(position).getDeviceType();
    }

    //Adds device to MyDevices database then navigates to MyDevices frag
    @Override
    public void onButtonClick(int position) {
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        String deviceName = scannerAdapter.scanResults.get(position).getDeviceName();
        String deviceMacAddress = scannerAdapter.scanResults.get(position).getDeviceMacAddress();
        DeviceType deviceType = scannerAdapter.scanResults.get(position).getDeviceType();

        Device newDevice = new Device(deviceName, deviceName, deviceMacAddress, deviceType);
        LiveData<List<Device>> allDevices = sharedEntitiesViewModel.getAllDevices();
        sharedEntitiesViewModel.insert(newDevice);

        NavDirections action = ScannerFragmentDirections.actionNavScannerToNavMyDevices();
        Navigation.findNavController(getView()).navigate(action);
    }


    /**
     * Broadcast receiver
     */

    void registerBroadcastReceiver(){
        String[] filters = {Constants.ACTION_BLE_SCANNING_STOPPED, Constants.ACTION_BLE_SCANNING_STARTED};
        new RegisterBroadcastReceiver(getActivity(), scannerUpdateReceiver, filters);

    }

    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    private BroadcastReceiver scannerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constants.ACTION_BLE_SCANNING_STARTED.equals(action)){
                scanning = true;
                textViewScanTitle.setText("Scanning for devices");
                buttonStopScan.setText("Stop Scan");
                progressBarScan.setVisibility(View.VISIBLE);
            }
            if (Constants.ACTION_BLE_SCANNING_STOPPED.equals(action)){
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
            }
        }
    };

    private void initObservers(){
        final Observer<ArrayList<ScannerListenerInterface>> observerScanResults;
        observerScanResults = scanResults -> updateRecycleViewer(scanResults);

        //start observing scan results
        BleScannerService.getScanResults().observe(getActivity(), observerScanResults);

        //Observe changes to list of all devices
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), devices -> updateDevices(devices));
        Log.d(TAG, "Update to device list");
    }

    private void updateDevices(List<Device> devices) {
        this.devices = devices;
    }


}