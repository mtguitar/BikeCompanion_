package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.os.Bundle;
import android.util.Log;
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
import com.example.bikecomputerfirstdraft.ble.BleConnectionService;
import com.example.bikecomputerfirstdraft.constants.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

public class MyDevicesFragment extends Fragment implements MyDevicesListenerInterface {

    private final static String TAG = "FlareLog MyDevicesFrag";

    private boolean boundToService = false;

    private View lastItemOpen;
    private int itemsOpen = 0;
    private String lastDeviceConnected;

    private MyDevicesViewModel myDevicesViewModel;
    private List<MyDevice> devices;
    private View constraintLayoutDeviceInfo;

    private String gattMacAddress;
    private String connectionState;

    private TextView textViewDeviceBattery;
    private TextView textViewDeviceType;
    private TextView textViewDeviceMode;
    private TextView textViewDeviceManufacturer;
    private Button switchAutoConnect;
    private Button buttonRemoveDevice;
    private TextView textViewDeviceState;
    private TextView textViewDeviceName;
    private TextView textViewMacAddress;
    private MyDevice currentDevice;

    private TextView textViewDeviceTest;

    private View view;
    private MyDevicesAdapter deviceAdapter;
    private BleConnectionService bleConnectionService;



    public static MyDevicesFragment newInstance() {
        return new MyDevicesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDevicesViewModel = new ViewModelProvider(this).get(MyDevicesViewModel.class);
        myDevicesViewModel.bindService();

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_devices, container, false);

        textViewDeviceTest = view.findViewById(R.id.text_view_devices_test);

        initFAB();
        initRecyclerViewer();
        initObservers();

        return view;
    }

    private void initFAB(){
        FloatingActionButton fabNewDevice = view.findViewById(R.id.floating_action_button_my_devices);
        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MyDevicesFragmentDirections.actionNavMyDevicesToNavAddDevice();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
    }

    private void initRecyclerViewer(){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        deviceAdapter = new MyDevicesAdapter(this);
        recyclerView.setAdapter(deviceAdapter);
    }

    private void initObservers(){
        myDevicesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<MyDevice>>() {
            @Override
            public void onChanged(List<MyDevice> devices) {
                Log.d(TAG, "Received devices live data");
                deviceAdapter.setDevices(devices);
            }

        });
        myDevicesViewModel.getConnectionStateHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap connectionStateHashMap) {
                connectionState = (String) connectionStateHashMap.get(Constants.GATT_CONNECTION_STATE);
                gattMacAddress = (String) connectionStateHashMap.get(Constants.GATT_MAC_ADDRESS);
                updateCards();

                textViewDeviceTest.setText(gattMacAddress + ": " + connectionState);
            }
        });

    }


    @Override
    public void onItemClick(int position, View itemView, List<MyDevice> devices) {

        textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
        textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_mac_address);
        textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
        textViewDeviceType = itemView.findViewById(R.id.text_view_device_type);
        textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
        textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
        textViewDeviceState = itemView.findViewById(R.id.text_view_device_state);

        switchAutoConnect = itemView.findViewById(R.id.switch_auto_connect);
        buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);

        constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);


        if(constraintLayoutDeviceInfo.getVisibility() == View.GONE){
            if(itemsOpen >= 1){
                lastItemOpen.setVisibility(View.GONE);
                itemsOpen--;

                Bundle extras = new Bundle();
                extras.putString("deviceMacAddress", lastDeviceConnected);
                myDevicesViewModel.sendCommandToService(BleConnectionService.class, Constants.ACTION_DISCONNECT_DEVICE, extras);
            }
            if (itemsOpen == 0){
                constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
                MyDevice currentDevice = devices.get(position);
                String deviceMacAddress = currentDevice.getMacAddress();
                Bundle extras = new Bundle();
                extras.putString("deviceMacAddress", deviceMacAddress);
                myDevicesViewModel.sendCommandToService(BleConnectionService.class, Constants.ACTION_CONNECT_TO_DEVICE, extras);

                lastDeviceConnected = deviceMacAddress;
                lastItemOpen = constraintLayoutDeviceInfo;
                itemsOpen++;
            }

        }
        else {
            constraintLayoutDeviceInfo.setVisibility(View.GONE);
        }


    }


    @Override
    public void onButtonClickRemove(int position, List<MyDevice> devices) {
        MyDevice currentDevice = devices.get(position);
        myDevicesViewModel.delete(currentDevice);

        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }

    @Override
    public void onButtonClickDisconnect(int position, List<MyDevice> devices) {
        MyDevice currentDevice = devices.get(position);
        String macAddress = currentDevice.getMacAddress();
        Bundle extras = new Bundle();
        extras.putString("deviceMacAddress", macAddress);
        myDevicesViewModel.sendCommandToService(BleConnectionService.class, Constants.ACTION_DISCONNECT_DEVICE, extras);
        Log.d(TAG, "Button Clicked, sent disconnect command");

    }



    public void updateCards(){
        if (textViewDeviceState != null && gattMacAddress.equals(textViewMacAddress.getText())){
            textViewDeviceState.setText(connectionState);
        }

    }



}