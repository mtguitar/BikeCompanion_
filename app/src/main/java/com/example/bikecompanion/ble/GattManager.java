package com.example.bikecompanion.ble;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.bikecompanion.ble.gattOperations.GattCharacteristicNotifyOperation;
import com.example.bikecompanion.ble.gattOperations.GattCharacteristicReadOperation;
import com.example.bikecompanion.ble.gattOperations.GattCharacteristicWriteOperation;
import com.example.bikecompanion.ble.gattOperations.GattConnectOperation;
import com.example.bikecompanion.ble.gattOperations.GattDisconnectOperation;
import com.example.bikecompanion.ble.gattOperations.GattOperation;
import com.example.bikecompanion.constants.Constants;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GattManager {

    private final static String TAG = "FlareLog GattManager";

    private ConcurrentLinkedQueue<GattOperation> queue;
    private boolean operationRunning = false;
    private Handler handler;
    private static final long OPERATION_TIMEOUT = 1000;
    private BleConnectionService bleConnectionService;
    private ConcurrentLinkedQueue operationQueue;

    private HashMap<String, String> connectionStateHashMap;
    private static MutableLiveData<HashMap> connectionStateHashMapLive;
    private MutableLiveData<Boolean> isConnected;
    private HashMap<String, String> deviceDataHashMap;
    private static MutableLiveData<HashMap> deviceDataHashMapLive;

    private boolean boundToService;

    private Context context;


    public GattManager(Application application) {
        context = application.getApplicationContext();
        bindService();
        registerBroadcastReceiver(context);
        queue = new ConcurrentLinkedQueue<>();
    }


    /**
     * Queue
     */

    private ConcurrentLinkedQueue getQueue(){
        if (queue == null){
            Log.d(TAG, "Queue is null");
        }
        return queue;
    }

    private void addToQueue(GattOperation operation){
        getQueue().add(operation);
        Log.d(TAG, "Added to queue: " + operation);
        processQueue();
    }

    private int processQueue(){
        Log.d(TAG, "Processing queue");
        getQueue();
        int size = queue.size();
        if (size == 0){
            Log.d(TAG, "Queue is empty: " + size);
            return 2;
        }
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        executeQueue();
        return 0;
    }

    private void executeQueue(){
        if (!operationRunning){
            operationRunning = true;
            GattOperation currentOperation = queue.poll();
            executeOperation(currentOperation);
            int size = queue.size();
            Log.d(TAG, "Executing queue. Size now: " + size);


            // Sets a timer
            handler = new Handler();
            handler.postDelayed(() -> {
                if (operationRunning) {
                    Log.d(TAG, "Timeout");
                    operationRunning = false;
                    processQueue();
                }
            }, OPERATION_TIMEOUT);

        }
        else{
            return;
        }
    }

    private void executeOperation(GattOperation operation){
        operation.execute();
    }

    /**
     * methods to interact with BleConnectionService
     */

    public void connectDevice(String deviceMacAddress) {
        GattConnectOperation gattConnectOperation = new GattConnectOperation(deviceMacAddress, bleConnectionService);
        addToQueue(gattConnectOperation);
    }

    public void disconnectDevice(String deviceMacAddress) {
        GattDisconnectOperation gattDisconnectOperation = new GattDisconnectOperation(deviceMacAddress, bleConnectionService);
        addToQueue(gattDisconnectOperation);
    }


    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic) {
        GattCharacteristicReadOperation gattCharacteristicReadOperation = new GattCharacteristicReadOperation(deviceMacAddress, service, characteristic, bleConnectionService);
        addToQueue(gattCharacteristicReadOperation);
    }


    public void writeCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, byte[] payload) {
        GattCharacteristicWriteOperation gattCharacteristicWriteOperation = new GattCharacteristicWriteOperation(deviceMacAddress, service, characteristic, payload, bleConnectionService);
        addToQueue(gattCharacteristicWriteOperation);
    }

    public void setCharacteristicNotification(String deviceMacAddress, UUID service, UUID characteristic, boolean enabled) {
        GattCharacteristicNotifyOperation gattCharacteristicNotifyOperation = new GattCharacteristicNotifyOperation(deviceMacAddress, service, characteristic, enabled, bleConnectionService);
        addToQueue(gattCharacteristicNotifyOperation);
    }

    public void bindService() {
        if (!boundToService) {
            Intent intent = new Intent(context, BleConnectionService.class);
            context.bindService(intent, serviceConnection, context.BIND_AUTO_CREATE);
        }

    }

    // serviceConnection object to connect to BluetoothLeService
    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
            boundToService = true;
            Log.d(TAG, "Bound to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleConnectionService = null;
        }
    };

    public void registerBroadcastReceiver(Context context) {
        //Set intent filters and register receiver to listen for updates
        createIntentFilter();
        context.registerReceiver(gattUpdateReceiver, createIntentFilter());
    }

    //Intent filters for receiving intents
    public static IntentFilter createIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_GATT_STATE_CHANGE);
        intentFilter.addAction(Constants.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(Constants.ACTION_READ_CHARACTERISTIC_BATTERY);
        intentFilter.addAction(Constants.ACTION_READ_CHARACTERISTIC_MANUFACTURER);

        return intentFilter;
    }

    //Broadcast receiver
    public BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connectionState;
            String gattMacAddress;

            operationRunning = false;
            Log.d(TAG, "Operation finished");
            processQueue();
            final String action = intent.getAction();
            Bundle extras = intent.getBundleExtra(Constants.EXTRA_DATA);

            if (action.equals(Constants.ACTION_GATT_STATE_CHANGE)) {
                //get connectionState and macAddress from intent extras
                connectionState = extras.getString(Constants.GATT_CONNECTION_STATE);
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);
                // this indicates which device's characteristic changed
                getConnectionStateHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                //put connectionState and macAddress into hashmap
                getConnectionStateHashMap().put(gattMacAddress, connectionState);

                //put hashmap into MutableLiveData
                getConnectionStateHashMapLive().postValue(getConnectionStateHashMap());
                if (connectionState.equals(Constants.GATT_CONNECTED)) {
                    getIsConnected().postValue(true);
                }
                if (connectionState.equals(Constants.GATT_DISCONNECTED)) {
                    getIsConnected().postValue(false);
                }
            }
            if (action.equals(Constants.ACTION_DATA_AVAILABLE)) {
                //get data and macAddress from intent extras
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);
                String characteristicUUID = extras.getString(Constants.CHARACTERISTIC_UUID);
                String characteristicValueString = extras.getString(Constants.CHARACTERISTIC_VALUE_STRING);
                int characteristicValueInt = extras.getInt(Constants.CHARACTERISTIC_VALUE_INT);

                //put connectionState and macAddress into hashmap
                getDeviceDataHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_UUID, characteristicUUID);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_VALUE_STRING, characteristicValueString);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_VALUE_INT, String.valueOf(characteristicValueInt));

                //put hashmap into MutableLiveData
                getDeviceDataHashMapLive().postValue(getDeviceDataHashMap());

            }

        }
    };


    /**
     * LiveData
     */

    public HashMap<String, String> getConnectionStateHashMap() {
        if (connectionStateHashMap == null) {
            connectionStateHashMap = new HashMap<String, String>();
        }
        return connectionStateHashMap;
    }

    public static MutableLiveData<HashMap> getConnectionStateHashMapLive() {
        if (connectionStateHashMapLive == null) {
            connectionStateHashMapLive = new MutableLiveData<>();
        }
        return connectionStateHashMapLive;
    }

    public MutableLiveData<Boolean> getIsConnected() {
        if (isConnected == null) {
            isConnected = new MutableLiveData<>();
        }
        return isConnected;
    }

    public HashMap<String, String> getDeviceDataHashMap() {
        if (deviceDataHashMap == null) {
            deviceDataHashMap = new HashMap<String, String>();
        }
        return deviceDataHashMap;
    }

    public static MutableLiveData<HashMap> getDeviceDataHashMapLive() {
        if (deviceDataHashMapLive == null) {
            deviceDataHashMapLive = new MutableLiveData<>();
        }
        return deviceDataHashMapLive;
    }



}
