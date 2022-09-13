package com.example.bikecompanion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.R;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databinding.FragmentHomeBinding;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.ui.myDevices.MyDevicesViewModel;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private final static String TAG = "FlareLog Home";
    private FragmentHomeBinding binding;
    private MyDevicesViewModel myDevicesViewModel;


    private View view;


    private String deviceToConnect = Constants.AVENTON_FLARE_MAC_ADDRESS;
    private String gattMacAddress;
    private String connectionState;
    private HashMap<String, String> connectionStateHashMap;
    private String characteristicUUID;
    private String characteristicValueString;
    private String characteristicValueByte;

    //Front Light
    Button buttonHomeBlinkSolidFront;
    Button buttonHomeDayNightFront;
    Button buttonHomeOffFront;
    ImageView imageViewHomeModeFront;
    TextView textViewFrontMode;
    ImageView imageViewFrontBattery;
    boolean frontLightConnected = false;


    //Rear Light
    Button buttonHomeBlinkSolidRear;
    Button buttonHomeDayNightRear;
    Button buttonHomeOffRear;
    TextView textViewRearMode;
    ImageView imageViewHomeModeRear;
    ImageView imageViewRearBattery;
    boolean rearLightConnected = false;

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
        myDevicesViewModel.connectDevice(deviceMacAddress);
    }


    private void initOnClickListeners(){
        Button buttonDayNight = view.findViewById(R.id.button_home_day_night);
        Button buttonBlinkSolid = view.findViewById(R.id.button_home_blink_solid);
        Button buttonOff = view.findViewById(R.id.button_home_off);

        String macAddress = Constants.AVENTON_FLARE_MAC_ADDRESS;


        buttonDayNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectDevice(deviceToConnect);
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.DAY_BLINK_MODE_BYTE);
            }
        });

        buttonBlinkSolid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDevicesViewModel.writeCharacteristics(macAddress, FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, FlareRTDeviceType.NIGHT_SOLID_MODE_BYTE);
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