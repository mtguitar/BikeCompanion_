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
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.example.bikecompanion.sharedClasses.CharacteristicData;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyDevicesFragment extends Fragment implements MyDevicesListenerInterface {

    private final static String TAG = "FlareLog MyDevicesFrag";

    private boolean boundToService = false;

    private int itemsOpen = 0;
    private View lastItemOpen;
    private ImageView lastArrowOpen;
    private String lastVisibleDevice;
    private String connectedDeviceMacAddress;
    private String visibleDeviceMacAddress;

    private SharedEntitiesViewModel sharedEntitiesViewModel;
    private List<Device> devices;
    private View constraintLayoutDeviceInfo;

    private String gattMacAddress;
    private String connectionState;
    private HashMap<String, String> connectionStateHashMap;
    private String characteristicUUID;
    private String characteristicValueString;
    private String characteristicValueInt;

    private TextView textViewDeviceBattery;
    private TextView textViewDeviceModel;
    private TextView textViewDeviceMode;
    private TextView textViewDeviceManufacturer;
    private Button buttonRemoveDevice;
    private Button buttonConnectDisconnectDevice;
    private TextView textViewDeviceState;
    private TextView textViewDeviceName;
    private TextView textViewMacAddress;
    private TextView textViewDeviceTest;
    private ImageView imageViewArrow;

    private View rowBattery;
    private View rowMode;
    private View rowManufacturer;
    private View rowModel;
    private View rowState;



    private View view;
    private MyDevicesAdapter deviceAdapter;


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

        textViewDeviceTest = view.findViewById(R.id.text_view_devices_test);

        initFAB();
        initRecyclerViewer();
        initObservers();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (itemsOpen >= 1) {
            lastItemOpen.setVisibility(View.GONE);
            lastArrowOpen.setRotation(0);
            itemsOpen--;
            clearTextViews();

            //Loop through devices in hashmap and disconnect any that is connected
            for (Map.Entry<String, String> entry: connectionStateHashMap.entrySet()) {
                String macAddress = entry.getKey();
                String connectionState = entry.getValue();
                if (connectionState.equals(Constants.CONNECTION_STATE_CONNECTED)){
                   sharedEntitiesViewModel.disconnectDevice(macAddress);
                }
            }
        }
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

    private void initRecyclerViewer() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_bikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        deviceAdapter = new MyDevicesAdapter(this);
        recyclerView.setAdapter(deviceAdapter);
    }

    private void initObservers() {
        //list of devices from devices_table
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                Log.d(TAG, "Received devices live data");
                deviceAdapter.setDevices(devices);
            }
        });

        //connectionStateHashmap key = macAddress, value = connectionState (connected, services discovered, disconnected)
        sharedEntitiesViewModel.getConnectionStateHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap connectionStateHashMapArg) {
                connectionStateHashMap = connectionStateHashMapArg;
                gattMacAddress = connectionStateHashMap.get(Constants.GATT_MAC_ADDRESS);
                connectionState = connectionStateHashMap.get(gattMacAddress);

                updateConnectionState();
                if (connectionState.equals(Constants.CONNECTION_STATE_SERVICES_DISCOVERED)) {
                    readCharacteristics();
                }
                textViewDeviceTest.setText(gattMacAddress + ": " + connectionState);
            }
        });
        /*

        //updates on read or notify event.  HashMap includes macAddress, characteristic UUID, characteristic value
        sharedEntitiesViewModel.getDeviceDataHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap deviceDataHashMap) {
                gattMacAddress = (String) deviceDataHashMap.get(Constants.GATT_MAC_ADDRESS);
                characteristicUUID = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_UUID);
                characteristicValueString = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_STRING);
                characteristicValueInt = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_INT);
                Log.d(TAG, "Received device data: " + gattMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValueInt);

                updateCharacteristics(gattMacAddress, characteristicUUID, characteristicValueString, characteristicValueInt);
            }
        });
        */

        //updates on read or notify event.  HashMap includes macAddress, characteristic UUID, characteristic value

        sharedEntitiesViewModel.getCharacteristicQueueLive().observe(getActivity(), new Observer<ConcurrentLinkedQueue>() {
            @Override
            public void onChanged(ConcurrentLinkedQueue concurrentLinkedQueue) {
                ConcurrentLinkedQueue<CharacteristicData> queue = concurrentLinkedQueue;
                if (queue.peek() == null) {
                    return;
                }
                CharacteristicData characteristicData = queue.poll();
                String gattMacAddress = characteristicData.getCharacteristicMacAddress();
                String characteristicUUID = characteristicData.getCharacteristicUUID();
                byte[] characteristicValueByte = characteristicData.getCharacteristicValue();

                updateCharacteristics(gattMacAddress, characteristicUUID, characteristicValueByte);

            }
        });

    }


    @Override
    public void onItemClick(int position, View itemView, List<Device> devices) {

        textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
        textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_id);
        textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
        textViewDeviceModel = itemView.findViewById(R.id.text_view_device_model);
        textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
        textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
        textViewDeviceState = itemView.findViewById(R.id.text_view_device_state);

        rowBattery = itemView.findViewById(R.id.row_battery);
        rowMode = itemView.findViewById(R.id.row_mode);
        rowManufacturer = itemView.findViewById(R.id.row_manufacturer);
        rowModel = itemView.findViewById(R.id.row_model);
        rowState = itemView.findViewById(R.id.row_connection_state);

        clearTextViews();

        buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
        buttonConnectDisconnectDevice = itemView.findViewById(R.id.button_device_connect);
        imageViewArrow = itemView.findViewById(R.id.image_view_arrow);

        constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);

        //if the clicked recyclerView item is not currently expanded
        if (constraintLayoutDeviceInfo.getVisibility() == View.GONE) {
            //If one or more recyclerView items are already expanded
            if (itemsOpen >= 1) {
                lastItemOpen.setVisibility(View.GONE);
                lastArrowOpen.setRotation(0);
                itemsOpen--;
                clearTextViews();

                //Disconnect the last visible device if currently connected
                String lastVisibleDeviceConnectionState = connectionStateHashMap.get(lastVisibleDevice);
                if(lastVisibleDeviceConnectionState.equals(Constants.CONNECTION_STATE_CONNECTED)){
                    sharedEntitiesViewModel.disconnectDevice(lastVisibleDevice);
                    Log.d(TAG, "Item clicked, trying to disconnect: " + lastVisibleDevice);
                }
            }
            //If no recyclerView items are currently expanded
            if (itemsOpen == 0) {
                constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
                clearTextViews();
                imageViewArrow.setRotation(180);

                Device currentDevice = devices.get(position);
                visibleDeviceMacAddress = currentDevice.getDeviceMacAddress();
                sharedEntitiesViewModel.connectDevice(visibleDeviceMacAddress);

                buttonConnectDisconnectDevice.setText(Constants.BUTTON_TEXT_CONNECTING);
                buttonConnectDisconnectDevice.setEnabled(false);

                lastVisibleDevice = visibleDeviceMacAddress;
                lastItemOpen = constraintLayoutDeviceInfo;
                lastArrowOpen = imageViewArrow;
                itemsOpen++;
            }
        }
        //If the clicked recyclerView item is already expanded
        else {
            //Deflate the item
            constraintLayoutDeviceInfo.setVisibility(View.GONE);
            //Change the arrow rotation
            imageViewArrow.setRotation(0);
            //clear the item's textViews so they don't show up next time it's expanded
            clearTextViews();

            //Disconnect the visible device if currently connected
            String visibleDeviceConnectionState = connectionStateHashMap.get(visibleDeviceMacAddress);
            if (visibleDeviceConnectionState.equals(Constants.CONNECTION_STATE_CONNECTED)) {
                Log.d(TAG, "Item clicked, trying to disconnect: " + visibleDeviceMacAddress);
                sharedEntitiesViewModel.disconnectDevice(visibleDeviceMacAddress);
            }
        }
    }

    @Override
    public void onButtonClickRemoveDevice(int position, List<Device> devices) {
        sharedEntitiesViewModel.disconnectDevice(connectedDeviceMacAddress);
        Device currentDevice = devices.get(position);
        sharedEntitiesViewModel.delete(currentDevice);

        constraintLayoutDeviceInfo.setVisibility(View.GONE);
    }

    @Override
    public void onButtonClickConnectDisconnect(int position, List<Device> devices) {
        Device currentDevice = devices.get(position);
        String deviceMacAddress = currentDevice.getDeviceMacAddress();
        String deviceConnectionState = connectionStateHashMap.get(deviceMacAddress);

        if (deviceConnectionState.equals(Constants.CONNECTION_STATE_DISCONNECTED)) {
            sharedEntitiesViewModel.connectDevice(deviceMacAddress);
            buttonConnectDisconnectDevice.setText(Constants.BUTTON_TEXT_CONNECTING);
            buttonConnectDisconnectDevice.setEnabled(false);
            Log.d(TAG, "Button Clicked, connecting: " + deviceMacAddress);
        } else {
            sharedEntitiesViewModel.disconnectDevice(deviceMacAddress);
            buttonConnectDisconnectDevice.setText(Constants.BUTTON_TEXT_DISCONNECTING);
            buttonConnectDisconnectDevice.setEnabled(false);
            Log.d(TAG, "Button Clicked, disconnecting: " + deviceMacAddress);
        }

        clearTextViews();
    }

    private void updateConnectionState() {
        //displays connection state in textView
        if (textViewDeviceState != null && gattMacAddress.contentEquals(textViewMacAddress.getText())) {
            textViewDeviceState.setText(connectionState);
        }
        //if connected, changes button to "disconnect"
        if (connectionState.equals(Constants.CONNECTION_STATE_CONNECTED)) {
            connectedDeviceMacAddress = gattMacAddress;
            if (gattMacAddress.equals(visibleDeviceMacAddress)) {
                buttonConnectDisconnectDevice.setEnabled(true);
                buttonConnectDisconnectDevice.setText("Disconnect");
            }
        }
        //if disconnected, changes button to "Connect," sets isConnected to false, and deletes connectedDeviceMacAddress
        if (connectionState.equals(Constants.CONNECTION_STATE_DISCONNECTED)) {
            if (gattMacAddress.equals(visibleDeviceMacAddress)) {
                buttonConnectDisconnectDevice.setEnabled(true);
                buttonConnectDisconnectDevice.setText("Connect");
            }
            connectedDeviceMacAddress = "";
        }
    }

    private void readCharacteristics() {
        sharedEntitiesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_BATTERY, GenericDeviceType.UUID_CHARACTERISTIC_BATTERY);
        sharedEntitiesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_DEVICE_MANUFACTURER, GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER);
        sharedEntitiesViewModel.readCharacteristics(gattMacAddress, GenericDeviceType.UUID_SERVICE_DEVICE_MODEL, GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MODEL);
        sharedEntitiesViewModel.readCharacteristics(gattMacAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE);
        sharedEntitiesViewModel.setCharacteristicNotification(gattMacAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, true);
    }

    private void updateCharacteristics(String macAddress, String charUuidString, byte[] characteristicValue) {
        if (!macAddress.equals(visibleDeviceMacAddress)) {
            Log.d(TAG, "Received info for different device");
            return;
        }
        int intValue = characteristicValue[0];
        String stringValue = new String(characteristicValue);

        switch (charUuidString) {
            case (GenericDeviceType.UUID_CHARACTERISTIC_BATTERY_STRING):
                textViewDeviceBattery.setText(String.valueOf(intValue));
                rowBattery.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Battery");
                break;
            case (GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER_STRING):
                textViewDeviceManufacturer.setText(stringValue);
                rowManufacturer.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Manufacturer");
                break;
            case (GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MODEL_STRING):
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
        }
    }
    /*

    private void updateCharacteristics(String macAddress, String charUuidString, String characteristicValueString, String charValueInt) {
        if (!macAddress.equals(visibleDeviceMacAddress)) {
            Log.d(TAG, "Received info for different device");
            return;
        }
        switch (charUuidString) {
            case (GenericDeviceType.UUID_CHARACTERISTIC_BATTERY_STRING):
                textViewDeviceBattery.setText(charValueInt);
                rowBattery.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Battery");
                break;
            case (GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER_STRING):
                textViewDeviceManufacturer.setText(characteristicValueString);
                rowManufacturer.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Manufacturer");
                break;
            case (GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_MODEL_STRING):
                textViewDeviceModel.setText(characteristicValueString);
                rowModel.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Model");
                break;
            case (FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE_STRING):
                String lightMode = convertLightMode(charValueInt);
                textViewDeviceMode.setText(lightMode);
                rowMode.setVisibility(View.VISIBLE);
                Log.d(TAG, "Set Light Mode");
                break;
        }
    }

     */

    private void clearTextViews() {
        rowBattery.setVisibility(View.GONE);
        rowMode.setVisibility(View.GONE);
        rowManufacturer.setVisibility(View.GONE);
        rowModel.setVisibility(View.GONE);
        rowState.setVisibility(View.GONE);
        /*
        textViewDeviceBattery.setText("");
        textViewDeviceModel.setText("");
        textViewDeviceMode.setText("");
        textViewDeviceManufacturer.setText("");
        textViewDeviceState.setText("");

         */
    }

    String convertLightMode(String characteristicValueInt) {
        String lightModeString;
        switch (characteristicValueInt) {
            case (FlareRTDeviceType.DAY_SOLID_MODE_INT):
                lightModeString = FlareRTDeviceType.DAY_SOLID_MODE_NAME;
                break;
            case (FlareRTDeviceType.DAY_BLINK_MODE_INT):
                lightModeString = FlareRTDeviceType.DAY_BLINK_MODE_NAME;
                break;
            case (FlareRTDeviceType.DAY_BLINK_MODE_2_INT):
                lightModeString = FlareRTDeviceType.DAY_BLINK_MODE_2_NAME;
                break;
            case (FlareRTDeviceType.NIGHT_SOLID_MODE_INT):
                lightModeString = FlareRTDeviceType.NIGHT_SOLID_MODE_NAME;
                break;
            case (FlareRTDeviceType.NIGHT_BLINK_MODE_INT):
                lightModeString = FlareRTDeviceType.NIGHT_BLINK_MODE_NAME;
                break;
            case (FlareRTDeviceType.OFF_MODE_INT):
                lightModeString = FlareRTDeviceType.OFF_MODE_NAME;
                break;
            default:
                lightModeString = "Unknown";
        }
        return lightModeString;
    }
}