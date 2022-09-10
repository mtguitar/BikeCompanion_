package com.example.bikecompanion.ui.home;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.R;
import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.ble.FormatBleData;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databinding.FragmentHomeBinding;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.ui.myDevices.MyDevicesViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MyDevicesViewModel myDevicesViewModel;

    private View view;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_home, container, false);


        myDevicesViewModel = new ViewModelProvider(this).get(MyDevicesViewModel.class);
        myDevicesViewModel.bindService();

        initOnClickListeners();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        myDevicesViewModel.disconnectDevice(Constants.AVENTON_FLARE_MAC_ADDRESS);
    }


    public void connectDevice(String deviceMacAddress){
        Intent gattServiceIntent = new Intent(getActivity(), BleConnectionService.class);
        getActivity().bindService(gattServiceIntent, serviceConnection, getActivity().BIND_AUTO_CREATE);

    }

    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleConnectionService bleConnectionService;
            bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
            // If bluetoothLeService is initialized, connect to device
            bleConnectionService.connectDevice("F8:EF:93:1C:EC:DB");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

    };

    private void initOnClickListeners(){
        Button buttonConnect = view.findViewById(R.id.buttonConnect);
        Button buttonDayBlink = view.findViewById(R.id.buttonDayBlink);
        Button buttonDaySolid = view.findViewById(R.id.buttonDaySolid);
        Button buttonNightBlink = view.findViewById(R.id.buttonNightBlink);
        Button buttonNightSolid = view.findViewById(R.id.buttonNightSolid);
        Button buttonOff = view.findViewById(R.id.buttonOff);

        String macAddress = Constants.AVENTON_FLARE_MAC_ADDRESS;

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.connectDevice(macAddress);
            }
        });

        buttonDaySolid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.DAY_SOLID_MODE_BYTE);
            }
        });
        buttonDayBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.DAY_BLINK_MODE_BYTE);
            }
        });

        buttonNightSolid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.NIGHT_SOLID_MODE_BYTE);
            }
        });
        buttonNightBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.NIGHT_BLINK_MODE_BYTE);
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.OFF_MODE_BYTE);
            }
        });


    }


}