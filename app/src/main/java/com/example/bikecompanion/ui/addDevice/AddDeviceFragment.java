package com.example.bikecompanion.ui.addDevice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.QuickContactBadge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.bikecompanion.R;
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;
import com.google.android.material.snackbar.Snackbar;

public class AddDeviceFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);

        Button buttonSelectFlare = view.findViewById(R.id.button_select_flare);
        Button buttonSelectSpeed = view.findViewById(R.id.button_select_speed);
        Button buttonSelectOther = view.findViewById(R.id.button_select_other);

        buttonSelectFlare.setOnClickListener(v -> click(FlareRTDeviceType.STRING_SERVICE_ADVERTISED_1, DeviceType.REAR_LIGHT));
        buttonSelectSpeed.setOnClickListener(v -> click(SpeedCadenceDeviceType.STRING_SERVICE_ADVERTISED_1, DeviceType.SPEED_CADENCE));
        buttonSelectOther.setOnClickListener(v -> click(GenericDeviceType.STRING_SERVICE_ADVERTISED_1, DeviceType.GENERIC));

        return view;
    }

    private void click(String serviceUuids, DeviceType deviceType){
        NavDirections action = AddDeviceFragmentDirections.deviceToScanner(serviceUuids, deviceType);
        Navigation.findNavController(getView()).navigate(action);
        Snackbar snackbar = Snackbar.make(getView(),"Selected: " + serviceUuids, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


}