package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.bikecomputerfirstdraft.adapters.MyDevicesAdapter;
import com.example.bikecomputerfirstdraft.adapters.MyDevicesListenerInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MyDevicesFragment extends Fragment implements MyDevicesListenerInterface {

    private final static String TAG = "FlareLog";

    private View lastItemOpen;
    private int itemsOpen = 0;



    private MyDevicesViewModel myDevicesViewModel;
    private List<MyDevice> devices;
    private View constraintLayoutDeviceInfo;


    private TextView textViewDeviceBattery;
    private TextView textViewDeviceType;
    private TextView textViewDeviceMode;
    private TextView textViewDeviceManufacturer;
    private Button switchAutoConnect;
    private Button buttonRemoveDevice;
    private TextView textViewDeviceMacAddress;
    private TextView textViewDeviceName;
    private TextView textViewMacAddress;
    private MyDevice currentDevice;



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
        MyDevicesAdapter deviceAdapter = new MyDevicesAdapter(this);
        recyclerView.setAdapter(deviceAdapter);

        myDevicesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<MyDevice>>() {
            @Override
            public void onChanged(List<MyDevice> devices) {
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
    public void onItemClick(int position, View itemView, List<MyDevice> devices) {



        textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
        textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_mac_address);
        textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
        textViewDeviceType = itemView.findViewById(R.id.text_view_device_type);
        textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
        textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
        textViewDeviceMacAddress = itemView.findViewById(R.id.text_view_scanner_device_mac_address);
        switchAutoConnect = itemView.findViewById(R.id.switch_auto_connect);
        buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
        constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);

        ConnectToBle connectToBle = new ConnectToBle();

        if(constraintLayoutDeviceInfo.getVisibility() == View.GONE){
            if(itemsOpen == 1){
                lastItemOpen.setVisibility(View.GONE);
                itemsOpen--;
            }
            lastItemOpen = constraintLayoutDeviceInfo;
            itemsOpen++;

            constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
            MyDevice currentDevice = devices.get(position);
            String deviceMacAddress = currentDevice.getMacAddress();
            connectToBle.registerBroadcastReceiver(getActivity());
            connectToBle.connectDevice(getActivity(), deviceMacAddress);


            final Observer<String> modeObserver = new Observer<String>() {
                @Override
                public void onChanged(@Nullable final String newMode) {
                    // Update the UI, in this case, a TextView.
                    textViewDeviceMode.setText(newMode);
                }
            };

            // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            connectToBle.characteristicMode.observe(this, modeObserver);




        }
        else {
            constraintLayoutDeviceInfo.setVisibility(View.GONE);

        }


    }

    public void closePreviousItem(){


    }

    @Override
    public void onButtonClick(int position, List<MyDevice> devices) {
        MyDevice currentDevice = devices.get(position);
        myDevicesViewModel.delete(currentDevice);

        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }
}