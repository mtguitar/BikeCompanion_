package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyDevicesFragment extends Fragment {

    private MyDevicesViewModel myDevicesViewModel;
    private ArrayList<Device> devices;

    public static MyDevicesFragment newInstance() {
        return new MyDevicesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDevicesViewModel = new ViewModelProvider(this).get(MyDevicesViewModel.class);



    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_devices, container, false);

        FloatingActionButton fabNewDevice = view.findViewById(R.id.floating_action_button_my_devices);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        DeviceAdapter deviceAdapter = new DeviceAdapter();
        recyclerView.setAdapter(deviceAdapter);

        myDevicesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                deviceAdapter.setDevices(devices);
            }
        });


        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MyDevicesFragmentDirections.actionNavMyDevicesToNavAddDevice();
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        return view;
    }



}