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
import com.example.bikecompanion.adapters.myDevices.MyDevicesAdapter;
import com.example.bikecompanion.adapters.myDevices.MyDevicesListenerInterface;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.deviceTypes.AbstractDeviceType;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;
import com.example.bikecompanion.sharedClasses.CharacteristicData;
import com.example.bikecompanion.sharedClasses.RequestDeviceCharacteristic;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyDevicesFragment extends Fragment implements MyDevicesListenerInterface {

    private final static String TAG = "FlareLog MyDevicesFrag";

    private View view;
    private SharedEntitiesViewModel sharedEntitiesViewModel;
    private MyDevicesAdapter deviceAdapter;
//    private HashMap<String, String> connectionStateHashMap;
    private List<Device> deviceList;
    private ArrayList<View> rowList;

    //variables related to keeping track of recyclerView items/devices/views
    private int itemsOpen = 0;
    private View lastItemOpen;
    private ImageView lastArrowOpen;
    private String lastVisibleDevice;
    private String connectedDeviceMacAddress;
    private String visibleDeviceMacAddress;
    private View constraintLayoutDeviceInfo;
    private Device currentDevice;

    //views contained within each recyclerView item
    private View itemView;
    private TextView textViewDeviceName;
    private TextView textViewMacAddress;
    private TextView textViewDeviceBattery;
    private TextView textViewDeviceModel;
    private TextView textViewDeviceMode;
    private TextView textViewDeviceManufacturer;
    private TextView textViewDeviceLocation;
    private TextView textViewDeviceFeature;
    private TextView textViewDeviceState;
    private Button buttonRemoveDevice;
    private Button buttonConnectDisconnectDevice;
    private ImageView imageViewArrow;
    private View rowBattery;
    private View rowMode;
    private View rowManufacturer;
    private View rowModel;
    private View rowLocation;
    private View rowFeature;
    private View rowProgressBar;
    private View progressBarDeviceData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedEntitiesViewModel = new ViewModelProvider(this).get(SharedEntitiesViewModel.class);
        sharedEntitiesViewModel.bindService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_devices, container, false);
        initFAB();
        initRecyclerView();
        initObservers();
        return view;
    }

    private void initFAB() {
        FloatingActionButton fabNewDevice = view.findViewById(R.id.fac_add_device);
        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MyDevicesFragmentDirections.actionNavMyDevicesToNavAddDevice();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
    }
//TODO Move all the shareentitiesviewmodel stuff with the cardviews over to the adapter and figure out how to keep open state persistent
    private void initRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_bikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        deviceAdapter = new MyDevicesAdapter(this, sharedEntitiesViewModel);
        recyclerView.setAdapter(deviceAdapter);
    }

    private void initObservers() {
        //list of devices from devices_table
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                deviceList = devices;
                deviceAdapter.setDevices(devices);
                if (deviceList != null && getConnectionStateHashMap().size() == 0){
                    initConnectionStateHashMap();
                }
                Log.d(TAG, "Received devices live data from db: " + devices);

            }
        });

        //connectionStateHashmap key = macAddress, value = connectionState (connected, services discovered, disconnected)
        sharedEntitiesViewModel.getConnectionStateHashMapLive().observe(getActivity(), connectionStateHashMapArg -> {
            //merge existing hashMap with updated hashMap
            getConnectionStateHashMap().putAll(connectionStateHashMapArg);

            //checks which device was updated and calls updateConnectionState to update UI
            String gattMacAddress = getConnectionStateHashMap().get(Constants.GATT_MAC_ADDRESS);
            String connectionState = getConnectionStateHashMap().get(gattMacAddress);
            String gattStatus = getConnectionStateHashMap().get(Constants.GATT_STATUS);
            Log.d(TAG, "Received ConnectionStateHashMapLive connectionState: " + gattMacAddress + " " + connectionState);
            updateConnectionStateUI(gattMacAddress, connectionState);

            if (gattStatus.equals(Constants.GATT_ERROR)){
                return;
            }
            if (Constants.CONNECTION_STATE_CONNECTED.equals(connectionState)){
                sharedEntitiesViewModel.discoverServices(gattMacAddress);
                Log.d(TAG, "Discover services: " + gattMacAddress);
            }
            if (Constants.CONNECTION_STATE_SERVICES_DISCOVERED.equals(connectionState)) {
                int size = deviceList.size();
                for (int i = 0; i < size; i++){
                    String deviceMacAddress = deviceList.get(i).getDeviceMacAddress();
                    if (gattMacAddress.equals(deviceMacAddress)) {
                        Device device = deviceList.get(i);
                        requestDeviceCharacteristic(device);
                    }
                    return;
                }
            }
        });

        //Queue of characteristic objects.  A new object is added to the queue everytime there is a change to a characteristic
        sharedEntitiesViewModel.getCharacteristicQueueLive().observe(getActivity(), concurrentLinkedQueue -> {
            ConcurrentLinkedQueue<CharacteristicData> queue = concurrentLinkedQueue;
            if (queue.peek() == null) {
                return;
            }
            CharacteristicData characteristicData = queue.poll();
            String gattMacAddress = characteristicData.getCharacteristicMacAddress();
            String characteristicUUID = characteristicData.getCharacteristicUUID();
            byte[] characteristicValueByte = characteristicData.getCharacteristicValue();

            updateCharacteristicUI(gattMacAddress, characteristicUUID, characteristicValueByte);
        });

        //Boolean to check if operations are pending
        sharedEntitiesViewModel.getOperationsPendingLive().observe(getActivity(), operationsPending -> {
            Log.d(TAG, "OperationsPending: " + operationsPending);
            if (rowProgressBar == null){
                Log.d(TAG, "Progress bar is null");
                return;
            }
            if (!operationsPending){
                rowProgressBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Progress bar is gone");
            }
            else{
                rowProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }


    //initializes connectionStateHashMap and sets value for each device in deviceList to disconnected
    private void initConnectionStateHashMap(){
        for (Device device : deviceList) {
            String deviceMacAddress = device.getDeviceMacAddress();
            sharedEntitiesViewModel.setConnectionStateHashMap(deviceMacAddress, Constants.CONNECTION_STATE_DISCONNECTED);
        }
    }

    //On click listener for clicking on an "device" item in the recyclerView
    @Override
    public void onRVItemClick(int position, View itemView, List<Device> devices) {
        textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
        textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_id);
        textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
        textViewDeviceModel = itemView.findViewById(R.id.text_view_device_model);
        textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
        textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
        textViewDeviceState = itemView.findViewById(R.id.text_view_device_state);
        textViewDeviceLocation = itemView.findViewById(R.id.text_view_CSC_location);
        textViewDeviceFeature = itemView.findViewById(R.id.text_view_CSC_mode);
        buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
        buttonConnectDisconnectDevice = itemView.findViewById(R.id.button_device_connect);
        progressBarDeviceData = itemView.findViewById(R.id.progress_bar_data);
        imageViewArrow = itemView.findViewById(R.id.image_view_arrow);
        rowProgressBar = itemView.findViewById(R.id.row_progress_bar);

        currentDevice = devices.get(position);
        this.itemView = itemView;
        constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);
        rowList = new ArrayList<>();
        Collections.addAll(rowList,
                rowBattery = itemView.findViewById(R.id.row_battery),
                rowMode = itemView.findViewById(R.id.row_mode),
                rowManufacturer = itemView.findViewById(R.id.row_manufacturer),
                rowModel = itemView.findViewById(R.id.row_model),
                rowLocation = itemView.findViewById(R.id.row_csc_location),
                rowFeature = itemView.findViewById(R.id.row_CSC_mode)
        );

        //if the clicked recyclerView item is not currently expanded
        if (constraintLayoutDeviceInfo.getVisibility() == View.GONE) {
            //If another recyclerView item is already expanded -> Disconnect the last visible device
            String lastVisibleDeviceConnectionState = getConnectionStateHashMap().get(lastVisibleDevice);
            if (lastVisibleDeviceConnectionState != null &&
                    !lastVisibleDeviceConnectionState.equals(Constants.CONNECTION_STATE_DISCONNECTED)) {
                sharedEntitiesViewModel.disconnectDevice(lastVisibleDevice);
                Log.d(TAG, "Item clicked, trying to disconnect: " + lastVisibleDevice);
            }
            //Connect device that is clicked
            visibleDeviceMacAddress = currentDevice.getDeviceMacAddress();
            sharedEntitiesViewModel.connectDevice(visibleDeviceMacAddress);
            Log.d(TAG, "Connect device: " + visibleDeviceMacAddress);
            itemsOpen++;
        }
        //If the clicked recyclerView item is already expanded -> Disconnect the visible device
        else {
            if (visibleDeviceMacAddress == null || getConnectionStateHashMap().get(visibleDeviceMacAddress) == null){
                return;
            }
            String visibleDeviceConnectionState = getConnectionStateHashMap().get(visibleDeviceMacAddress);
            if (!visibleDeviceConnectionState.equals(Constants.CONNECTION_STATE_DISCONNECTED)) {
                Log.d(TAG, "Item clicked, trying to disconnect: " + visibleDeviceMacAddress);
                sharedEntitiesViewModel.disconnectDevice(visibleDeviceMacAddress);
                Log.d(TAG, "Disconnect device: " + visibleDeviceMacAddress);
            }
        }
    }

    @Override
    public void onButtonClickRemoveDevice(int position, List<Device> devices) {
        Device currentDevice = devices.get(position);
        String currentDeviceMacAddress = currentDevice.getDeviceMacAddress();
        //Disconnect device if it's connected
        if (currentDeviceMacAddress.equals(connectedDeviceMacAddress)){
            sharedEntitiesViewModel.disconnectDevice(currentDeviceMacAddress);
            Log.d(TAG, "Disconnect device: " + currentDeviceMacAddress);
        }
        //Delete device from db
        sharedEntitiesViewModel.delete(currentDevice);
        Log.d(TAG, "Delete device: " + currentDevice);

        //When a device is removed, the next device in the list is automatically inflated, so this deflates it
        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }

    @Override
    public void onButtonClickConnectDisconnect(int position, List<Device> devices) {
        Device currentDevice = devices.get(position);
        String deviceMacAddress = currentDevice.getDeviceMacAddress();
        String deviceConnectionState = getConnectionStateHashMap().get(deviceMacAddress);

        if (Constants.CONNECTION_STATE_DISCONNECTED.equals(deviceConnectionState)) {
            sharedEntitiesViewModel.connectDevice(deviceMacAddress);
            //buttonConnectDisconnectDevice.setText(Constants.BUTTON_TEXT_CONNECTING);
            textViewDeviceState.setText(Constants.CONNECTION_STATE_CONNECTING_NAME);
            Log.d(TAG, "Button Clicked, connecting: " + deviceMacAddress);
        } else {
            sharedEntitiesViewModel.disconnectDevice(deviceMacAddress);
            //buttonConnectDisconnectDevice.setText(Constants.BUTTON_TEXT_DISCONNECTING);
            textViewDeviceState.setText(Constants.CONNECTION_STATE_DISCONNECTING_NAME);
            Log.d(TAG, "Button Clicked, disconnecting: " + deviceMacAddress);
        }
        buttonConnectDisconnectDevice.setEnabled(false);
        hideCharacteristicRows();
    }

    private void updateConnectionStateUI(String gattMacAddress, String connectionState) {
        //displays connection state in textView
        if (gattMacAddress.equals(visibleDeviceMacAddress)){
            //Set textViewDeviceState
            if (textViewDeviceState != null && gattMacAddress.equals(textViewMacAddress.getText())) {
                String connectionStateName = GenericDeviceType.getConnectionStateNameHashMap().get(connectionState);
                textViewDeviceState.setText(connectionStateName);
                buttonConnectDisconnectDevice.setEnabled(true);
            }
            //if connected, changes button to "disconnect"
            if (Constants.CONNECTION_STATE_CONNECTED.equals(connectionState)) {
                connectedDeviceMacAddress = gattMacAddress;
                buttonConnectDisconnectDevice.setText("Disconnect");
            }
            //if disconnected, changes button to "Connect," sets isConnected to false, and deletes connectedDeviceMacAddress
            if (Constants.CONNECTION_STATE_DISCONNECTED.equals(connectionState)) {
                buttonConnectDisconnectDevice.setEnabled(true);
                buttonConnectDisconnectDevice.setText("Connect");
                connectedDeviceMacAddress = null;
            }
        }
    }

    private void requestDeviceCharacteristic(Device device) {
        RequestDeviceCharacteristic.updateCharacteristic(sharedEntitiesViewModel, device);
        Log.d(TAG, "Request device characteristics: " + device);
    }

    private void updateCharacteristicUI(String macAddress, String characteristicUUIDString, byte[] characteristicValue) {
        if (!macAddress.equals(visibleDeviceMacAddress)) {
            Log.d(TAG, "Received characteristic data for different device");
            return;
        }
        int intValue = characteristicValue[0];
        String stringValue = new String(characteristicValue);

        switch (characteristicUUIDString) {
            case (AbstractDeviceType.UUID_CHARACTERISTIC_BATTERY_STRING):
                textViewDeviceBattery.setText(String.valueOf(intValue));
                rowBattery.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Battery");
                break;
            case (AbstractDeviceType.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER_STRING):
                textViewDeviceManufacturer.setText(stringValue);
                rowManufacturer.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Manufacturer");
                break;
            case (AbstractDeviceType.UUID_CHARACTERISTIC_DEVICE_MODEL_STRING):
                textViewDeviceModel.setText(stringValue);
                rowModel.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Model");
                break;
            case (FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE_STRING):
                String lightMode = FlareRTDeviceType.getLightModeHashMap().get(intValue);
                textViewDeviceMode.setText(lightMode);
                rowMode.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Light Mode");
                break;
            case (SpeedCadenceDeviceType.UUID_CHARACTERISTIC_CSC_FEATURE_STRING):
                String feature = SpeedCadenceDeviceType.getCSCFeatureMap().get(intValue);
                textViewDeviceFeature.setText(feature);
                rowFeature.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set CSC Feature");
                break;
            case (SpeedCadenceDeviceType.UUID_CHARACTERISTIC_CSC_LOCATION_STRING):
                String location = SpeedCadenceDeviceType.getCSCSensorLocationMap().get(intValue);
                textViewDeviceLocation.setText(location);
                rowLocation.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set CSC Location");
                break;
        }
    }

    private void hideCharacteristicRows() {
        if (rowList != null) {
            for (View row : rowList) {
                row.setVisibility(View.GONE);
            }
        }
    }

    private Map<String, String> getConnectionStateHashMap(){
        return sharedEntitiesViewModel.getConnectionStateHashMap();
    }

}