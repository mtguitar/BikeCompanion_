package com.example.bikecompanion.ble.gattOperations;

import android.bluetooth.BluetoothDevice;

public abstract class GattOperation {
    protected final static String TAG = "FlareLog GattOp";
    private final static int DEFAULT_TIMEOUT_IN_MILLIS = 10000;
    private BluetoothDevice mDevice;
    public int operationType;


    public abstract void execute();

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getTimeoutInMillis() {
        return DEFAULT_TIMEOUT_IN_MILLIS;
    }

    public abstract boolean hasAvailableCompletionCallback();

    public int getOperationType() {
        return operationType;
    }


}
