package com.example.bikecomputerfirstdraft;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.UUID;
@SuppressLint("MissingPermission")

public class BluetoothLeService extends Service {

    private final static String TAG = "FlareLog";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private int connectionState = STATE_DISCONNECTED;

    private String deviceName;

    private final static int STATE_DISCONNECTED = 0;
    private final static int STATE_CONNECTING = 1;
    private final static int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // Flare MAC Addresses
    public final static String AC_FLARE_MAC_ADDRESS = "F8:EF:93:1C:EC:DB";
    public final static String AVENTON_FLARE_MAC_ADDRESS = "F8:EF:93:1C:EC:DB";

    // Light mode UUIDs
    public final static UUID UUID_SERVICE_LIGHT_MODE = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public final static UUID UUID_CHARACTERISTIC_LIGHT_MODE = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");

    // Battery UUIDs
    public final static UUID UUID_SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");



    // Write characteristic
    public void writeCharacteristic(UUID service, UUID characteristic, byte[] payload) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        Log.w(TAG, "Trying to write: " + payload[0]);
        int writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;

        BluetoothGattCharacteristic characteristicToWrite = mBluetoothGatt.getService(service).getCharacteristic(characteristic);

        characteristicToWrite.setWriteType(writeType);
        characteristicToWrite.setValue(payload);
        mBluetoothGatt.writeCharacteristic(characteristicToWrite);
    }


    // Read characteristic
    public void readCharacteristic(UUID service, UUID characteristic) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristicToRead = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.readCharacteristic(characteristicToRead);
    }

    // Subscribe to characteristic notifications
    public void setCharacteristicNotification(UUID service, UUID characteristic, boolean enabled){
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristicToSubscribe = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.setCharacteristicNotification(characteristicToSubscribe, enabled);
        Log.w(TAG, "Subscribed to characteristic " + characteristic);
    }


    // Implements Gatt Callback method and data received methods

    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            String newState1 = String.valueOf(newState);
            Log.w(TAG, newState1);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                mBluetoothGatt = gatt;
                broadcastUpdate(intentAction);

                Log.d(TAG, "Device Connected");

                mBluetoothGatt.discoverServices();
                Log.i(TAG, "Start service discovery ");

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] lightMode = characteristic.getValue();
            Log.i("Flare Test", "Light Mode is " + lightMode[0]);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.w(TAG, "Characteristic changed");
        }

    };


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    //Initializes a reference to the local Bluetooth adapter.
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    // Connect to the device
    public boolean connectDevice(String deviceMacAddress) {
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceMacAddress);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        Log.d(TAG, "Connecting . . .");
        connectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnectDevice() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        Log.d(TAG, "Device disconnected");
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
        }
        sendBroadcast(intent);
    }




}