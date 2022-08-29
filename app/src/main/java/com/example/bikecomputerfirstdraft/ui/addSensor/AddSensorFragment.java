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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleScannerService;
import com.example.bikecomputerfirstdraft.other.Constant;

public class AddSensorFragment extends Fragment {

    private AddSensorViewModel mViewModel;



    public static AddSensorFragment newInstance() {
        return new AddSensorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_sensor, container, false);

        Button buttonSelectFlare = view.findViewById(R.id.buttonSelectFlare);
        Button buttonSelectSpeed = view.findViewById(R.id.buttonSelectSpeed);
        Button buttonSelectOther = view.findViewById(R.id.buttonSelectOther);

        buttonSelectFlare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "Flare RT";
                sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE, name, null, null);
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });
        buttonSelectSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE, null, null, null);
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });
        buttonSelectOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommandToService(Constant.ACTION_START_OR_RESUME_SERVICE, null, null, null);
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddSensorViewModel.class);
        // TODO: Use the ViewModel
    }

    //Sends intent to BleScannerService
    private void sendCommandToService(String action, String name, String macAddress, ParcelUuid serviceUuids) {
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


        requireContext().startService(scanningServiceIntent);
        Log.d(Constant.TAG, "sent intent to scanner service " + action);
    }



}