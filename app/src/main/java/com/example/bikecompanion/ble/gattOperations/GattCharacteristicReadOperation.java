package com.example.bikecompanion.ble.gattOperations;

import android.util.Log;

import com.example.bikecompanion.ble.BleConnectionService;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GattCharacteristicReadOperation extends GattOperation{
    BleConnectionService bleConnectionService;
    Queue<GattOperation> queue = new ConcurrentLinkedQueue<GattOperation>();

    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, BleConnectionService bleConnectionService) {
        Log.d(super.TAG, "Operation read received:" + deviceMacAddress + " " + characteristic);

        bleConnectionService.readCharacteristic(deviceMacAddress, service, characteristic);
    }
}
