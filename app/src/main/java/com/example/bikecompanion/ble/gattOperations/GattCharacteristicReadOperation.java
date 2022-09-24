package com.example.bikecompanion.ble.gattOperations;

import android.util.Log;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

import java.util.UUID;

public class GattCharacteristicReadOperation extends GattOperation{
    private String deviceMacAddress;
    private UUID service;
    private UUID characteristic;
    private BleConnectionService bleConnectionService;
    public final String operationType = Constants.OPERATION_CHARACTERISTIC_READ;


    public GattCharacteristicReadOperation(String deviceMacAddress, UUID service, UUID characteristic, BleConnectionService bleConnectionService) {
        this.deviceMacAddress = deviceMacAddress;
        this.service = service;
        this.characteristic = characteristic;
        this.bleConnectionService = bleConnectionService;
    }


    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, BleConnectionService bleConnectionService) {
        Log.d(super.TAG, "Operation read received:" + deviceMacAddress + " " + characteristic);
        this.deviceMacAddress = deviceMacAddress;
        this.service = service;
        this.characteristic = characteristic;
        this.bleConnectionService = bleConnectionService;
    }


    @Override
    public void execute() {
        bleConnectionService.readCharacteristic(deviceMacAddress, service, characteristic);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }

    @Override
    public String getOperationType(){
        return operationType;
    }

}
