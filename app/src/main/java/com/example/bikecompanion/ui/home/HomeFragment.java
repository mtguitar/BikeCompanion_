package com.example.bikecompanion.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.R;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databinding.FragmentHomeBinding;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private final static String TAG = "FlareLog Home";
    private FragmentHomeBinding binding;
    private SharedEntitiesViewModel sharedEntitiesViewModel;

    private List<Bike> bikeList;
    private List<Device> deviceList;
    private List<BikeWithDevices> bikeWithDevicesList;


    private View view;


    private String deviceToConnect = Constants.AVENTON_FLARE_MAC_ADDRESS;
    private String gattMacAddress;
    private String connectionState;
    private HashMap<String, String> connectionStateHashMap;
    private String characteristicUUID;
    private String characteristicValueString;
    private String characteristicValueByte;

    //Front Light
    Button buttonBlinkSolidFront;
    Button buttonDayNightFront;
    Button buttonOffFront;
    TextView textViewFrontMode;
    ImageView imageViewModeFront;
    ImageView imageViewFrontBattery;

    boolean frontLightConnected = false;
    String macAddressFront;


    //Rear Light
    Button buttonBlinkSolidRear;
    Button buttonDayNightRear;
    Button buttonOffRear;
    TextView textViewRearMode;
    ImageView imageViewModeRear;
    ImageView imageViewRearBattery;

    boolean rearLightConnected = false;
    String macAddressRear;

    //Distance
    TextView textViewHomeDistance;

    //Speed
    TextView textViewHomeSpeed;
    boolean speedConnected = false;

    //Cadence
    TextView textViewHomeCadence;
    boolean cadenceConnected = false;

    //Bike Name
    TextView textViewHomeBikeName;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        sharedEntitiesViewModel = new ViewModelProvider(this).get(SharedEntitiesViewModel.class);
        sharedEntitiesViewModel.bindService();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();
        initDatabaseObservers();
        initBleServiceObservers();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //sharedEntitiesViewModel.disconnectDevice(deviceToConnect);
    }


    public void initViews() {
        //Front Light
        buttonBlinkSolidFront = view.findViewById(R.id.button_home_blink_solid_front);
        buttonDayNightFront = (Button) view.findViewById(R.id.button_home_day_night_front);
        buttonOffFront = view.findViewById(R.id.button_home_off_front);
        imageViewModeFront = view.findViewById(R.id.image_view_home_mode_front);
        textViewFrontMode = view.findViewById(R.id.text_view_home_mode_front);
        imageViewFrontBattery = view.findViewById(R.id.image_view_home_battery_front);

        //Rear Light
        buttonBlinkSolidRear = view.findViewById(R.id.button_home_blink_solid_rear);
        buttonDayNightRear = view.findViewById(R.id.button_home_day_night_rear);
        buttonOffRear = view.findViewById(R.id.button_home_off_rear);
        textViewRearMode = view.findViewById(R.id.text_view_home_mode_rear);
        imageViewModeRear = view.findViewById(R.id.image_view_home_mode_rear);
        imageViewRearBattery = view.findViewById(R.id.image_view_home_battery_rear);

        //Distance
        textViewHomeDistance = view.findViewById(R.id.text_view_home_distance);

        //Speed
        textViewHomeSpeed = view.findViewById(R.id.text_view_home_speed);

        //Cadence
        textViewHomeCadence = view.findViewById(R.id.text_view_home_cadence);

        //Bike Name
        textViewHomeBikeName = view.findViewById(R.id.text_view_home_bike_name);
    }

    private void initDatabaseObservers() {
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {

                Log.d(TAG, "Received devices live data ");
                deviceList = devices;

            }
        });

        sharedEntitiesViewModel.getAllBikes().observe(getViewLifecycleOwner(), new Observer<List<Bike>>() {
            @Override
            public void onChanged(List<Bike> bikes) {
                Log.d(TAG, "Received bikes live data ");
                bikeList = bikes;
                //todo specify which bike to initiate
                initBike();
            }
        });

        sharedEntitiesViewModel.getBikeWithDevices().observe(getViewLifecycleOwner(), new Observer<List<BikeWithDevices>>() {
            @Override
            public void onChanged(List<BikeWithDevices> bikeWithDevices) {
                Log.d(TAG, "Received BikeWithDevices live data ");
                bikeWithDevicesList = bikeWithDevices;
            }
        });

    }


    private void initBleServiceObservers() {
        //observes changes to BLE device connection state
        sharedEntitiesViewModel.getConnectionStateHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap connectionStateHashMapArg) {
                connectionStateHashMap = connectionStateHashMapArg;
                gattMacAddress = connectionStateHashMap.get(Constants.GATT_MAC_ADDRESS);
                connectionState = connectionStateHashMap.get(gattMacAddress);

                //If services discovered, calls read characteristic method
                if (connectionState.equals(Constants.GATT_SERVICES_DISCOVERED)) {

                }

            }
        });

        //observes changes to BLE device data
        sharedEntitiesViewModel.getDeviceDataHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap deviceDataHashMap) {
                gattMacAddress = (String) deviceDataHashMap.get(Constants.GATT_MAC_ADDRESS);
                characteristicUUID = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_UUID);
                characteristicValueString = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_STRING);
                characteristicValueByte = (String) deviceDataHashMap.get(Constants.CHARACTERISTIC_VALUE_BYTE);
                Log.d(TAG, "Received device data: " + gattMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValueByte);

            }
        });

    }

    //todo add parameter to specify which bike to initiate
    private void initBike(){
        if (bikeList == null)
        {
            return;
        }
        Bike bike = bikeList.get(0);
        String bikeName = bike.getBikeName();

        textViewHomeBikeName.setText(bikeName);

    }

    public void connectDevice(String deviceMacAddress){
        //myDevicesViewModel.connectDevice(deviceMacAddress);
    }

    private void initOnClickListeners(){
        //frontLight
        buttonDayNightFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectDevice(deviceToConnect);
                writeCharacteristic(macAddressFront, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.DAY_BLINK_MODE_BYTE);
            }
        });

        buttonBlinkSolidFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeCharacteristic(macAddressFront, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.NIGHT_SOLID_MODE_BYTE);
            }
        });
        buttonOffFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeCharacteristic(macAddressFront, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.OFF_MODE_BYTE);
            }
        });

        //rearLight
        buttonDayNightRear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectDevice(deviceToConnect);
                writeCharacteristic(macAddressRear, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.DAY_BLINK_MODE_BYTE);
            }
        });

        buttonBlinkSolidRear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeCharacteristic(macAddressRear, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.NIGHT_SOLID_MODE_BYTE);
            }
        });
        buttonOffRear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               writeCharacteristic(macAddressRear, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.OFF_MODE_BYTE);
            }
        });
    }


    private void writeCharacteristic(String macAddress, UUID serviceUUID, UUID characteristicUUID, byte[] payload){
        sharedEntitiesViewModel.writeCharacteristics(macAddress, serviceUUID, characteristicUUID, payload);
    }
    /*



    private void initObservers() {

        //This observes changes to BLE device connection state.
        myDevicesViewModel.getConnectionStateHashMapLive().observe(getActivity(), new Observer<HashMap>() {
            @Override
            public void onChanged(HashMap connectionStateHashMapArg) {
                connectionStateHashMap = connectionStateHashMapArg;
                gattMacAddress = connectionStateHashMap.get(Constants.GATT_MAC_ADDRESS);
                connectionState = connectionStateHashMap.get(gattMacAddress);

                //If services discovered, calls read characteristic method
                if (connectionState.equals(Constants.GATT_SERVICES_DISCOVERED)) {

                }

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

            }
        });
    }



    */


}