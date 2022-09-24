package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

public class GattDiscoverServicesOperation extends GattOperation{
    private String deviceMacAddress;
    private BleConnectionService bleConnectionService;
    public final String operationType = Constants.OPERATION_DISCOVER_SERVICES;

    public GattDiscoverServicesOperation(String deviceMacAddress, BleConnectionService bleConnectionService){
        this.deviceMacAddress = deviceMacAddress;
        this.bleConnectionService = bleConnectionService;
    }

    @Override
    public void execute() {
        bleConnectionService.discoverServices(deviceMacAddress);
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
