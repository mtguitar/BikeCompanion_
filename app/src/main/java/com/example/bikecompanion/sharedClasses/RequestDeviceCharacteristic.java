package com.example.bikecompanion.sharedClasses;

import android.util.Log;

import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.deviceTypes.DeviceType;
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
        String macAddress = device.getDeviceMacAddress();
        DeviceType deviceType = device.getDeviceType();
        ArrayList<Characteristic> characteristicList = GenericDeviceType.getCharacteristicList();
        if (deviceType.equals(FlareRTDeviceType.getDeviceType().toString())) {
            characteristicList = FlareRTDeviceType.getCharacteristicList();
        }
        if (device.getDeviceType().equals(SpeedCadenceDeviceType.DEVICE_TYPE)) {
            characteristicList.addAll(SpeedCadenceDeviceType.getCharacteristicList());
        }
        for (Characteristic characteristic : characteristicList) {
            UUID serviceUUID = characteristic.getServiceUUID();
            UUID characteristicUUID = characteristic.getCharacteristicUUID();
            if (characteristic.isReadable()) {
                sharedEntitiesViewModel.readCharacteristics(macAddress, serviceUUID, characteristicUUID);
                Log.d(TAG, "Requesting to read characteristic: " + characteristicUUID);
            }
            if (characteristic.isNotify()) {
                sharedEntitiesViewModel.setCharacteristicNotification(macAddress, serviceUUID, characteristicUUID, true);
            }

        }

    }

}
