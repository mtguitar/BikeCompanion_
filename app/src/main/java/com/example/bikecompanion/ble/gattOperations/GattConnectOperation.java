package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;

public class GattConnectOperation extends GattOperation{
    String deviceMacAddress;
    BleConnectionService bleConnectionService;

    public GattConnectOperation(String deviceMacAddress, BleConnectionService bleConnectionService){
        this.deviceMacAddress = deviceMacAddress;
        this.bleConnectionService = bleConnectionService;
    }

    @Override
    public void execute() {
        bleConnectionService.connectDevice(deviceMacAddress);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }
}
