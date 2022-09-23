package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;

import java.util.UUID;

public class GattCharacteristicWriteOperation extends GattOperation{
    String deviceMacAddress;
    UUID service;
    UUID characteristic;
    byte[] payload;
    BleConnectionService bleConnectionService;


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
}
