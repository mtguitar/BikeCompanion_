package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.annotation.SuppressLint;
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

import java.util.List;

public class MyDevicesFragment extends Fragment implements RecyclerViewListenerMyDevices {

    private MyDevicesViewModel myDevicesViewModel;
    private List<Device> devices;
    private View constraintLayoutDeviceInfo;

    public static MyDevicesFragment newInstance() {
        return new MyDevicesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDevicesViewModel = new ViewModelProvider(this).get(MyDevicesViewModel.class);


    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_devices, container, false);

        FloatingActionButton fabNewDevice = view.findViewById(R.id.floating_action_button_my_devices);
        constraintLayoutDeviceInfo = view.findViewById(R.id.constraint_layout_device_info);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        DeviceAdapter deviceAdapter = new DeviceAdapter(this);
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


    @Override
    public void onItemClick(int position, View constraintLayoutDeviceInfo) {
        this.constraintLayoutDeviceInfo = constraintLayoutDeviceInfo;
        if(constraintLayoutDeviceInfo.getVisibility() == View.GONE){
            constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
        }
        else {
            constraintLayoutDeviceInfo.setVisibility(View.GONE);
        }

    }

    @Override
    public void onButtonClick(int position, List<Device> devices) {
        Device currentDevice = devices.get(position);
        myDevicesViewModel.delete(currentDevice);

        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }
}