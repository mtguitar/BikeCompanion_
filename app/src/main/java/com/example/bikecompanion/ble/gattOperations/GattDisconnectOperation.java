package com.example.bikecompanion.ble.gattOperations;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;

public class GattDisconnectOperation extends GattOperation {

    private String deviceMacAddress;
    private BleConnectionService bleConnectionService;
    public final int operationType = Constants.OPERATION_DISCONNECT_DEVICE;

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

    @Override
    public int getOperationType(){
        return operationType;
    }
}
