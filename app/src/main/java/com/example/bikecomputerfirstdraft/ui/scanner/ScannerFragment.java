package com.example.bikecomputerfirstdraft.ui.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleScannerService;
import com.example.bikecomputerfirstdraft.other.Constant;

import java.util.ArrayList;

public class ScannerFragment extends Fragment implements RecyclerViewInterface{

    private static final String TAG = "FlareLog";
    private ScannerViewModel mViewModel;
    private boolean scanning = false;

    private String name;
    private String macAddress;
    private ParcelUuid serviceUuids;
    private View view;

    private static Button buttonStopScan;
    private static TextView textViewScanTitle;

    private ArrayList scanResults;
    private RecyclerView recyclerView;


    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }

    public void deliverScanResults(ArrayList scanResults){
        this.scanResults = scanResults;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //This is where we initialize the data. Normally this would be from a remote server
        //Send intent to BleScannerService
        sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //view vars
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        buttonStopScan = (Button)view.findViewById(R.id.buttonStopScan);
        textViewScanTitle = view.findViewById(R.id.textViewScanTitle);
        recyclerView = view.findViewById(R.id.recyclerViewScanner);

        //creates recyclerView but does not show until there is data in it
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        if (scanResults != null){
            recyclerView.setAdapter(new ScannerAdapter(scanResults, this));
        }

        //Setup observer of livedata for recyclerView, calls updateRecyclerView when data changes
        final Observer<ArrayList<ScannerItem>> observerScanResults;
        observerScanResults = new Observer<ArrayList<ScannerItem>>(){
            public void onChanged(@Nullable final ArrayList scanResults) {
                updateRecycleViewer(scanResults);
            }
        };

        //start observing scan results
        BleScannerService.getScanResults().observe(getActivity(), observerScanResults);



        //Intent filters to listen for scanning updates
        scannerUpdateIntentFilter ();
        getActivity().registerReceiver(scannerUpdateReceiver, scannerUpdateIntentFilter());

        //set button onclick listener
        buttonStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scanning){
                    sendCommandToService(Constant.ACTION_PAUSE_SERVICE);
                }
                else{
                    sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE);
                }
                Log.d(Constant.TAG, "button clicked");
            }
        });


        return view;
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
        requireContext().startService(scanningServiceIntent);
        Log.d(Constant.TAG, "sent intent to scanner service " + action);
    }

    //Intent filters for receiving intents
    private static IntentFilter scannerUpdateIntentFilter () {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_BLE_SCANNING_STARTED);
        intentFilter.addAction(Constant.ACTION_BLE_SCANNING_STOPPED);
        return intentFilter;
    }

    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    private BroadcastReceiver scannerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Scanning broadcast received");

            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constant.ACTION_BLE_SCANNING_STARTED.equals(action)){
                scanning = true;
                textViewScanTitle.setText("Scanning for devices . . .");
                buttonStopScan.setText("Scanning");
                buttonStopScan.setEnabled(false);
                Log.d(TAG, "Scanning Started Intent Received");
            }
            else if (Constant.ACTION_BLE_SCANNING_STOPPED.equals(action)){
                scanning = false;
                textViewScanTitle.setText("Select a device or scan again");
                buttonStopScan.setText("Scan Again");
                buttonStopScan.setEnabled(true);
                Log.d(TAG, "Scanning stopped intent received");

            }


        }
    };



    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "clicked item RV");
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        name = scannerAdapter.scannerList.get(position).getTextName();
        String description = scannerAdapter.scannerList.get(position).getTextDescription();
        Log.d(TAG, name + " " + description);





    }
}