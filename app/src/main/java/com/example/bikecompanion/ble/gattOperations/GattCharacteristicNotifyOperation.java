package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

import java.util.UUID;

public class GattCharacteristicNotifyOperation extends GattOperation{
    private String deviceMacAddress;
    private UUID service;
    private UUID characteristic;
    private boolean enabled;
    private BleConnectionService bleConnectionService;
    private int operationType = Constants.OPERATION_DESCRIPTOR_WRITE;

    public GattCharacteristicNotifyOperation(String deviceMacAddress, UUID service, UUID characteristic, boolean enabled, BleConnectionService bleConnectionService) {
        this.deviceMacAddress = deviceMacAddress;
        this.service = service;
        this.characteristic = characteristic;
        this.enabled = enabled;
        this.bleConnectionService = bleConnectionService;
    }

    @Override
    public void execute() {
        bleConnectionService.setCharacteristicNotification(deviceMacAddress, service, characteristic, enabled);

    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }

    @Override
    public int getOperationType(){
        return operationType;
    }



}
