package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;

import java.util.UUID;

public class GattDisconnectOperation extends GattOperation {

    String deviceMacAddress;
    BleConnectionService bleConnectionService;

    public GattDisconnectOperation (String deviceMacAddress, BleConnectionService bleConnectionService){
        this.deviceMacAddress = deviceMacAddress;
        this.bleConnectionService = bleConnectionService;
    }

    @Override
    public void execute() {
        bleConnectionService.disconnectDevice(deviceMacAddress);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }
}
