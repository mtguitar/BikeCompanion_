package com.example.bikecompanion.ui.myDevices;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import com.example.bikecompanion.R;
import com.example.bikecompanion.adapters.MyDevicesAdapter;
import com.example.bikecompanion.adapters.MyDevicesListenerInterface;
import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyDevicesFragment extends Fragment implements MyDevicesListenerInterface {

    private final static String TAG = "FlareLog MyDevicesFrag";

    private boolean boundToService = false;
    private boolean isConnected = false;

    private View lastItemOpen;
    private ImageView lastArrowOpen;
    private int itemsOpen = 0;

    private String lastDeviceConnected;
    private String connectedDeviceMacAddress;
    private String visibleDeviceMacAddress;

    private MyDevicesViewModel myDevicesViewModel;
    private List<MyDevice> devices;
    private View constraintLayoutDeviceInfo;

    private String gattMacAddress;
    private String connectionState;
    private HashMap<String, String> connectionStateHashMap;
    private String characteristicUUID;
    private String characteristicValueString;
    private String characteristicValueByte;


    private TextView textViewDeviceBattery;
    private TextView textViewDeviceModel;
    private TextView textViewDeviceMode;
    private TextView textViewDeviceManufacturer;
    private Button switchAutoConnect;
    private Button buttonRemoveDevice;
    private Button buttonDisconnectDevice;
    private TextView textViewDeviceState;
    private TextView textViewDeviceName;
    private TextView textViewMacAddress;
    private TextView textViewDeviceTest;
    private ImageView imageViewArrow;

    private View view;
    private MyDevicesAdapter deviceAdapter;


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
            public void onChanged(HashMap connectionStateHashMapArg) {
                connectionStateHashMap = connectionStateHashMapArg;
                gattMacAddress = connectionStateHashMap.get(Constants.GATT_MAC_ADDRESS);
                connectionState = connectionStateHashMap.get(gattMacAddress);

                updateConnectionState();
                if (connectionState.equals(Constants.GATT_SERVICES_DISCOVERED)){
                    myDevicesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_BATTERY, GenericDeviceType.UUID_CHARACTERISTIC_BATTERY);
                }

                textViewDeviceTest.setText(gattMacAddress + ": " + connectionState);
            }
        });

        myDevicesViewModel.getIsConnected().observe(getActivity(), new Observer<Boolean>(){
            @Override
            public void onChanged(Boolean isConnectedBoolean) {
                isConnected = isConnectedBoolean;

            }
        });

        myDevicesViewModel.getDeviceDataHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap deviceDataHashMap) {
                gattMacAddress = (String) deviceDataHashMap.get(Constants.GATT_MAC_ADDRESS);
                characteristicUUID = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_UUID);
                characteristicValueString = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_STRING);
                characteristicValueByte = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_BYTE);
                Log.d(TAG, "Received device data: " + gattMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValueByte);

                updateCharacteristics();
            }
        });





    }


    @Override
    public void onItemClick(int position, View itemView, List<MyDevice> devices) {

        textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
        textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_mac_address);

        textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
        textViewDeviceModel = itemView.findViewById(R.id.text_view_device_model);
        textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
        textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
        textViewDeviceState = itemView.findViewById(R.id.text_view_device_state);
        clearTextViews();

        switchAutoConnect = itemView.findViewById(R.id.switch_auto_connect);
        buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
        buttonDisconnectDevice = itemView.findViewById(R.id.button_device_disconnect);
        imageViewArrow = itemView.findViewById(R.id.image_view_arrow);

        constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);


        if(constraintLayoutDeviceInfo.getVisibility() == View.GONE){
            if(itemsOpen >= 1){
                lastItemOpen.setVisibility(View.GONE);
                lastArrowOpen.setRotation(0);
                itemsOpen--;
                clearTextViews();

                Log.d(TAG, "isConnected: " + String.valueOf(isConnected));
                if(isConnected) {
                    myDevicesViewModel.disconnectDevice(lastDeviceConnected);
                    Log.d(TAG, "Item clicked, trying to disconnect: " + lastDeviceConnected);
                }
            }
            if (itemsOpen == 0){
                constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
                imageViewArrow.setRotation(180);

                MyDevice currentDevice = devices.get(position);
                visibleDeviceMacAddress = currentDevice.getMacAddress();
                myDevicesViewModel.connectDevice(visibleDeviceMacAddress);

                lastDeviceConnected = visibleDeviceMacAddress;
                lastItemOpen = constraintLayoutDeviceInfo;
                lastArrowOpen = imageViewArrow;
                itemsOpen++;
            }

        }
        else {
            constraintLayoutDeviceInfo.setVisibility(View.GONE);
            imageViewArrow.setRotation(0);
            clearTextViews();
            Log.d(TAG, "isConnected: " + String.valueOf(isConnected));
            if(isConnected) {
                Log.d(TAG, "Item clicked, trying to disconnect: " + visibleDeviceMacAddress);
                myDevicesViewModel.disconnectDevice(visibleDeviceMacAddress);
            }
        }
    }

    @Override
    public void onButtonClickRemove(int position, List<MyDevice> devices) {
        myDevicesViewModel.disconnectDevice(connectedDeviceMacAddress);
        MyDevice currentDevice = devices.get(position);
        myDevicesViewModel.delete(currentDevice);

        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }

    @Override
    public void onButtonClickDisconnect(int position, List<MyDevice> devices) {
        MyDevice currentDevice = devices.get(position);
        String deviceMacAddress = currentDevice.getMacAddress();
        if (connectionStateHashMap.get(deviceMacAddress).equals(Constants.GATT_DISCONNECTED)){
            myDevicesViewModel.connectDevice(deviceMacAddress);
            Log.d(TAG, "Button Clicked, sent connect command to: " + deviceMacAddress);
        }
        else {
            myDevicesViewModel.disconnectDevice(deviceMacAddress);
            Log.d(TAG, "Button Clicked, sent disconnect command to: " + deviceMacAddress);
        }


        clearTextViews();


    }


    private void updateConnectionState(){
        if (textViewDeviceState != null && gattMacAddress.equals(textViewMacAddress.getText())) {
            textViewDeviceState.setText(connectionState);
        }
        if(connectionState.equals(Constants.GATT_CONNECTED)){
            connectedDeviceMacAddress = gattMacAddress;
            if (gattMacAddress.equals(visibleDeviceMacAddress)){
                buttonDisconnectDevice.setText("Disconnect");
            }
            isConnected = true;
        }
        if(connectionState.equals(Constants.GATT_DISCONNECTED)){
            if (gattMacAddress.equals(visibleDeviceMacAddress)){
                buttonDisconnectDevice.setText("Connect");
            }
            connectedDeviceMacAddress = "";
            isConnected = false;
        }

    }

    private void updateCharacteristics(){
        if(!gattMacAddress.equals(visibleDeviceMacAddress)){
            Log.d(TAG, "Received info for different device");
            return;
        }
        if(characteristicUUID.equals(GenericDeviceType.STRING_CHARACTERISTIC_BATTERY)){
            textViewDeviceBattery.setText(characteristicValueByte);
            Log.d(TAG, "Set Battery");
        }
        if(characteristicUUID.equals(GenericDeviceType.STRING_CHARACTERISTIC_MANUFACTURER)){
            textViewDeviceManufacturer.setText(characteristicValueString);
            Log.d(TAG, "Set Manufacturer");
        }
        if(characteristicUUID.equals(GenericDeviceType.STRING_CHARACTERISTIC_MODEL)){
            textViewDeviceModel.setText(characteristicValueString);
            Log.d(TAG, "Set Model");
        }
        if(characteristicUUID.equals(FlareRTDeviceType.STRING_CHARACTERISTIC_LIGHT_MODE)){
            String lightMode = convertLightMode(characteristicValueByte);
            textViewDeviceMode.setText(lightMode);
            Log.d(TAG, "Set Light Mode");
        }


        if(textViewDeviceBattery.getText().equals("")){
            textViewDeviceBattery.setText("Retrieving . . .");
            myDevicesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_BATTERY, GenericDeviceType.UUID_CHARACTERISTIC_BATTERY);
        }

        else if(textViewDeviceManufacturer.getText().equals("")){
            Log.d(TAG, "Checking Manufacturer");
            textViewDeviceManufacturer.setText("Retrieving . . .");
            myDevicesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_MANUFACTURER, GenericDeviceType.UUID_CHARACTERISTIC_MANUFACTURER);
        }

        else if(textViewDeviceModel.getText().equals("")){
            Log.d(TAG, "Checking Model");
            textViewDeviceModel.setText("Retrieving . . .");
            myDevicesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_MODEL, GenericDeviceType.UUID_CHARACTERISTIC_MODEL);
        }
        else if(textViewDeviceMode.getText().equals("")){
            textViewDeviceMode.setText("Retrieving . . .");
            myDevicesViewModel.setCharacteristicNotification(gattMacAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, true);
            myDevicesViewModel.readCharacteristics(gattMacAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE);
        }

    }

    private void clearTextViews(){
        textViewDeviceBattery.setText("");
        textViewDeviceModel.setText("");
        textViewDeviceMode.setText("");
        textViewDeviceManufacturer.setText("");
        textViewDeviceState.setText("");
    }

    String convertLightMode(String characteristicValueByte){
        String lightModeString;
        if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.DAY_SOLID_MODE_BYTE[0]))){
            lightModeString = FlareRTDeviceType.DAY_SOLID_MODE_NAME;
        }
        else if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.DAY_BLINK_MODE_BYTE[0]))){
            lightModeString = FlareRTDeviceType.DAY_BLINK_MODE_NAME;
        }
        else if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.DAY_BLINK_MODE_2_BYTE[0]))){
            lightModeString = FlareRTDeviceType.DAY_BLINK_MODE_2_NAME;
        }
        else if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.NIGHT_SOLID_MODE_BYTE[0]))){
            lightModeString = FlareRTDeviceType.NIGHT_SOLID_MODE_NAME;
        }
        else if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.NIGHT_BLINK_MODE_BYTE[0]))){
            lightModeString = FlareRTDeviceType.NIGHT_BLINK_MODE_NAME;
        }
        else if (characteristicValueByte.equals(String.valueOf(FlareRTDeviceType.OFF_MODE_BYTE[0]))){
            lightModeString = FlareRTDeviceType.OFF_MODE_NAME;
        }
        else {
            lightModeString = "Unknown";
        }

        return lightModeString;

    }









}