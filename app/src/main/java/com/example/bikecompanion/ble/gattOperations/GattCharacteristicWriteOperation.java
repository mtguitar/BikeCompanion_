package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

import java.util.UUID;

public class GattCharacteristicWriteOperation extends GattOperation{
    private String deviceMacAddress;
    private UUID service;
    private UUID characteristic;
    private byte[] payload;
    private BleConnectionService bleConnectionService;
    public final String operationType = Constants.OPERATION_CHARACTERISTIC_WRITE;


    public GattCharacteristicWriteOperation(String deviceMacAddress, UUID service, UUID characteristic, byte[] payload, BleConnectionService bleConnectionService) {
        this.deviceMacAddress = deviceMacAddress;
        this.service = service;
        this.characteristic = characteristic;
        this.payload = payload;
        this.bleConnectionService = bleConnectionService;
    }


    @Override
    public void execute() {
        bleConnectionService.writeCharacteristic(deviceMacAddress, service, characteristic, payload);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }

    @Override
    public String getOperationType(){
        return operationType;
    }
}
