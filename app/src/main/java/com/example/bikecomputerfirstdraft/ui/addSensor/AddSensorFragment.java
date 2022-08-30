package com.example.bikecomputerfirstdraft.ui.addSensor;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleScannerService;
import com.example.bikecomputerfirstdraft.other.Constant;
import com.example.bikecomputerfirstdraft.ui.scanner.ScannerFragment;
import com.google.android.material.snackbar.Snackbar;

public class AddSensorFragment extends Fragment {

    private String name = null;
    private String macAddress = null;
    private ParcelUuid serviceUuids = null;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sensor, container, false);

        Button buttonSelectFlare = view.findViewById(R.id.buttonSelectFlare);
        Button buttonSelectSpeed = view.findViewById(R.id.buttonSelectSpeed);
        Button buttonSelectOther = view.findViewById(R.id.buttonSelectOther);


        buttonSelectFlare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = "Flare RT";
                macAddress = null;
                serviceUuids = null;
                click();

            }
        });
        buttonSelectSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = null;
                macAddress = "D2:F7:80:A2:43:27";
                serviceUuids = null;
                click();

            }
        });
        buttonSelectOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = null;
                macAddress = null;
                serviceUuids = null;
                click();
                /*
                sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE, null, null, null);
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
                Snackbar snackbar = Snackbar.make(view, "Selected: " + name, Snackbar.LENGTH_SHORT);
                snackbar.show();

                 */
            }
        });

        return view;

    }

    //
    private void click(){

        NavDirections action = AddSensorFragmentDirections.sensorToScanner(name, macAddress, serviceUuids);
        Navigation.findNavController(getView()).navigate(action);
        Snackbar snackbar = Snackbar.make(getView(),"Selected: " + name, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    //Sends intent to BleScannerService
    private void sendScanningFilters(String action, String name, String macAddress, ParcelUuid serviceUuids) {
        Intent scanningFiltersIntent = new Intent(requireContext(), ScannerFragment.class);

        scanningFiltersIntent.setAction(action);
        if (name != null){
            scanningFiltersIntent.putExtra("name", name);
        }
        if (macAddress != null){
            scanningFiltersIntent.putExtra("macAddress", macAddress);
        }
        if (serviceUuids != null) {
            scanningFiltersIntent.putExtra("serviceUuids", serviceUuids);
        }

        Log.d(Constant.TAG, "sent intent to scanner service " + action);
    }



}