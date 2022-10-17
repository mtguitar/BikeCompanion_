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
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;
import com.google.android.material.snackbar.Snackbar;

public class AddDeviceFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);

        Button buttonSelectFlare = view.findViewById(DeviceType.REAR_LIGHT.getIcon());
        Button buttonSelectSpeed = view.findViewById(DeviceType.SPEED_CADENCE.getIcon());
        Button buttonSelectOther = view.findViewById(DeviceType.GENERIC.getIcon());


        buttonSelectFlare.setOnClickListener(view1 -> click(FlareRTDeviceType.STRING_SERVICE_ADVERTISED_1, DeviceType.REAR_LIGHT));
        buttonSelectSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(SpeedCadenceDeviceType.STRING_SERVICE_ADVERTISED_1, DeviceType.SPEED_CADENCE);

            }
        });
        buttonSelectOther.setOnClickListener(view12 -> click(null, DeviceType.GENERIC));

        return view;
    }

    private void click(String serviceUuids, DeviceType deviceType){
        NavDirections action = AddDeviceFragmentDirections.deviceToScanner(serviceUuids, deviceType);
        Navigation.findNavController(getView()).navigate(action);
        Snackbar snackbar = Snackbar.make(getView(),"Selected: " + serviceUuids, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


}