package com.example.bikecompanion.sharedClasses;

import android.util.Log;

import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;

import java.util.ArrayList;
import java.util.UUID;

public class RequestDeviceCharacteristic {
    private final static String TAG = "FlareLog RequestChar";

    public static void updateCharacteristic(SharedEntitiesViewModel sharedEntitiesViewModel, Device device) {
        Log.d("FlareLog RequestChar", "Requesting chars");
        String gattMacAddress = device.getDeviceMacAddress();
        String deviceType = device.getDeviceType();
        ArrayList<Characteristic> characteristicList = GenericDeviceType.getCharacteristicList();
        if (deviceType.equals(FlareRTDeviceType.DEVICE_TYPE)) {
            characteristicList.addAll(FlareRTDeviceType.getCharacteristicList());
        }
        if (device.getDeviceType().equals(SpeedCadenceDeviceType.DEVICE_TYPE)) {
            characteristicList.addAll(SpeedCadenceDeviceType.getCharacteristicList());
        }
        int size = characteristicList.size();
        for (int i = 0; i < size; i++) {
            UUID serviceUUID = characteristicList.get(i).getServiceUUID();
            UUID characteristicUUID = characteristicList.get(i).getCharacteristicUUID();
            if (characteristicList.get(i).isReadable()) {
                sharedEntitiesViewModel.readCharacteristics(gattMacAddress, serviceUUID, characteristicUUID);
            }
            if (characteristicList.get(i).isNotify()) {
                sharedEntitiesViewModel.setCharacteristicNotification(gattMacAddress, serviceUUID, characteristicUUID, true);
            }
        }
    }

}
