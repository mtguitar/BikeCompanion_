package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

public class GattConnectOperation extends GattOperation{
    private String deviceMacAddress;
    private BleConnectionService bleConnectionService;
    public final String operationType = Constants.OPERATION_CONNECT_DEVICE;

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

    @Override
    public String getOperationType(){
        return operationType;
    }
}
