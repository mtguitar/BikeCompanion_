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
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.R;
import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button buttonConnect = view.findViewById(R.id.button_home_connect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FlareLog", "clicked");
                connectDevice("F8:EF:93:1C:EC:DB");
            }
        });







        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

}