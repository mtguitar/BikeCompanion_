package com.example.bikecompanion.ui.addDevice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.bikecompanion.R;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;
import com.google.android.material.snackbar.Snackbar;

public class AddDeviceFragment extends Fragment {

    private String name = null;
    private String macAddress = null;
    private String serviceUuids = null;
    private String deviceType;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);

        Button buttonSelectFlare = view.findViewById(R.id.button_select_flare);
        Button buttonSelectSpeed = view.findViewById(R.id.button_select_speed);
        Button buttonSelectOther = view.findViewById(R.id.button_select_other);


        buttonSelectFlare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = null;
                macAddress = null;
                serviceUuids = FlareRTDeviceType.STRING_SERVICE_ADVERTISED_1;
                deviceType = FlareRTDeviceType.DEVICE_TYPE;
                click();
            }
        });
        buttonSelectSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = null;
                macAddress = null;
                serviceUuids = SpeedCadenceDeviceType.STRING_SERVICE_ADVERTISED_1;
                deviceType = SpeedCadenceDeviceType.DEVICE_TYPE;
                click();

            }
        });
        buttonSelectOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = null;
                macAddress = null;
                serviceUuids = null;
                deviceType = "other";
                click();
            }
        });

        return view;
    }

    private void click(){
        NavDirections action = AddDeviceFragmentDirections.deviceToScanner(null, null, serviceUuids, deviceType);
        Navigation.findNavController(getView()).navigate(action);
        Snackbar snackbar = Snackbar.make(getView(),"Selected: " + serviceUuids, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


}