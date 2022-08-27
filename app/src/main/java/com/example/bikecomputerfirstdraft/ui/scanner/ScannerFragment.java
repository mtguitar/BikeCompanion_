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
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleScanner;

import java.util.UUID;

public class ScannerFragment extends Fragment {

    //TODO Clean up context activity vs context application, clean up buttons

    Context contextApplication;
    Context contextActivity;
    boolean contextSet = false;

    Button buttonStopScan;


    private static final String TAG = "FlareLog";
    private ScannerViewModel mViewModel;

    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }
/*
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Context contextActivity = context;
    }


 */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        contextApplication = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        contextActivity = getActivity();

        scannerUpdateIntentFilter ();
        getActivity().registerReceiver(scannerUpdateReceiver, scannerUpdateIntentFilter());


        Button buttonStopScan = view.findViewById(R.id.buttonStopScan);
        TextView textViewScanTitle = view.findViewById(R.id.textViewScanTitle);


        ParcelUuid serviceUUID= new ParcelUuid(UUID.fromString("71262000-3692-ae93-e711-472ba41689c9"));

        BleScanner bleScanner = new BleScanner(contextApplication, contextActivity, view, null, null, null);


        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        // TODO: Use the ViewModel

    }

    //intent filters
    private static IntentFilter scannerUpdateIntentFilter () {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleScanner.ACTION_BLE_SCANNING_STARTED);
        intentFilter.addAction(BleScanner.ACTION_BLE_SCANNING_STOPPED);
        return intentFilter;
    }

    private BroadcastReceiver scannerUpdateReceiver = new BroadcastReceiver() {
        @Override

        public void onReceive(Context context, Intent intent) {
            Button buttonStopScan = getView().findViewById(R.id.buttonStopScan);
            Log.d(TAG, "Scanning broadcast received");
            final String action = intent.getAction();
            if (BleScanner.ACTION_BLE_SCANNING_STARTED.equals(action)){
                Log.d(TAG, "Scanning Started Intent Received");
                buttonStopScan.setText("Stop Scan");
            }
            else if (BleScanner.ACTION_BLE_SCANNING_STOPPED.equals(action)){
                Log.d(TAG, "Scanning stopped intent received");
                buttonStopScan.setText("Start Scan");

            }


        }
    };



}