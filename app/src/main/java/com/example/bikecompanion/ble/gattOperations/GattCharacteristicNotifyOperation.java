package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;

import java.util.UUID;

public class GattCharacteristicNotifyOperation extends GattOperation{
    String deviceMacAddress;
    UUID service;
    UUID characteristic;
    boolean enabled;
    BleConnectionService bleConnectionService;

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
}
