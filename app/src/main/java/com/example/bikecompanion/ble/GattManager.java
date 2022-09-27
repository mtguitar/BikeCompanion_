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
import com.example.bikecompanion.ble.gattOperations.GattDiscoverServicesOperation;
import com.example.bikecompanion.ble.gattOperations.GattOperation;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.sharedClasses.CharacteristicData;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class binds to BleConnectionService.
 * It uses BleConnectionService to check connection, read, write, setNotify for devices in myDevices database.
 * It receives info updates from BleConnectionService via a broadcastReceiver.
 * MyDevicesFragment interacts with this repo through MyDevicesViewModel.
 */

public class GattManager {

    private final static String TAG = "FlareLog GattManager";
    private static final long OPERATION_TIMEOUT = 2000;

    private static GattManager instance;
    private static MutableLiveData<ConcurrentLinkedQueue> characteristicQueueLive;
    private static MutableLiveData<HashMap> connectionStateHashMapLive;
    private static HashMap<String, String> connectionStateHashMap;
    private static MutableLiveData<Boolean> operationsPendingLive;
    private boolean operationsPending = false;

    private Context context;
    private ConcurrentLinkedQueue<GattOperation> operationQueue;
    private GattOperation pendingOperation;
    private ConcurrentLinkedQueue<CharacteristicData> characteristicQueue;
    private Handler handler;
    private BleConnectionService bleConnectionService;

    private boolean boundToService;

    //private HashMap<String, String> deviceDataHashMap;
    //private static MutableLiveData<HashMap> deviceDataHashMapLive;

    public static GattManager getInstance(Application application){
        if (instance == null) {
            synchronized (GattManager.class){
                if (instance == null){
                    instance = new GattManager(application);
                }
            }

        }
        return instance;
    }

    private GattManager(Application application) {
        context = application.getApplicationContext();
        registerBroadcastReceiver(context);
        operationQueue = new ConcurrentLinkedQueue<>();
        characteristicQueue = new ConcurrentLinkedQueue<>();
    }

    /*
     * Operations Queue
     * Rapid ble reads/writes get lost, so this queue makes sure only one operation is processing at
     * a time.  It does not execute the next operation until receiving a response from the broadcast
     * receiver concerning the current operation or the operation times out.
     * until all previous operations have finished or timed out
     *
     */

    private void addToQueue(GattOperation operation){
        operationQueue.add(operation);
        if (operationQueue.size() == 1 && !operationsPending){
            operationsPending = true;
            getOperationsPendingLive().postValue(operationsPending);
        }
        Log.d(TAG, "Added to queue: " + operation);
        if (pendingOperation == null){
            processQueue();
        }
    }

    private void processQueue(){
        Log.d(TAG, "Processing queue");
        if (pendingOperation != null){
            Log.d(TAG, "Operation is already pending:"  + pendingOperation);
            return;
        }
        //Stops any running timer
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        //Get next operation in queue and set it to pendingOperation
        pendingOperation = operationQueue.poll();
        if (pendingOperation != null) {
            executeOperation(pendingOperation);
            // Sets a timer
            handler = new Handler();
            handler.postDelayed(() -> {
                    Log.d(TAG, "Operation timeout");
                    pendingOperation = null;
                    processQueue();
            }, OPERATION_TIMEOUT);
        }
        else{
            operationsPending = false;
            getOperationsPendingLive().postValue(operationsPending);
            Log.d(TAG, "No pending operations");
        }
    }

    private void executeOperation(GattOperation operation){
        operation.execute();
        Log.d(TAG, "Executing Operation: " + operation);
    }

    /*
     * Public methods to interact with BleConnectionService
     */

    public void connectDevice(String deviceMacAddress) {
        GattConnectOperation gattConnectOperation = new GattConnectOperation(deviceMacAddress, bleConnectionService);
        addToQueue(gattConnectOperation);
    }

    public void discoverServices(String deviceMacAddress) {
        GattDiscoverServicesOperation gattDiscoverServicesOperation = new GattDiscoverServicesOperation(deviceMacAddress, bleConnectionService);
        addToQueue(gattDiscoverServicesOperation);
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
            Log.d(TAG, "Bound to service: " + service + " " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleConnectionService = null;
            Log.d(TAG, "Service disconnected");
        }
    };

    public void registerBroadcastReceiver(Context context) {
        //Set intent filters and register receiver to listen for updates
        IntentFilter intentFilters = createIntentFilter();
        context.registerReceiver(gattUpdateReceiver, intentFilters);
    }

    //Intent filters for receiving intents
    public static IntentFilter createIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_GATT_STATE_CHANGE);
        intentFilter.addAction(Constants.ACTION_CHARACTERISTIC_CHANGE);
        intentFilter.addAction(Constants.ACTION_DESCRIPTOR_CHANGE);
        intentFilter.addAction(Constants.ACTION_GATT_ERROR);
        return intentFilter;
    }

    //Broadcast receiver
    public BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connectionState;
            String gattMacAddress;

            final String action = intent.getAction();

            //Get operationType and gattStatus from bundle
            Bundle extras = intent.getBundleExtra(Constants.EXTRA_DATA);
            int gattStatus = extras.getInt(Constants.GATT_STATUS);
            int operationType = extras.getInt(Constants.GATT_OPERATION_TYPE);

            //Check if intent concerns same operationType as pendingOperation.  If so, process next operation
            if (pendingOperation != null){
                int pendingOperationType = pendingOperation.getOperationType();
                Log.d(TAG, "Received intent data for operationType:" + operationType + " Pending: " + pendingOperationType);
                if (operationType == pendingOperationType){
                    pendingOperation = null;
                    Log.d(TAG, "Operation finished: " + action);
                    if (operationQueue.peek() != null) {
                        processQueue();
                    }
                }
            }

            if (action.equals(Constants.ACTION_GATT_STATE_CHANGE)) {

                //Get connectionState and macAddress from intent extras
                connectionState = extras.getString(Constants.CONNECTION_STATE);
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);

                //Update connectionStateHashMap
                // Values associated with status and macAddress keys will be overwritten each time,i.e.
                // there will only be one row for each at any given time.  This lets the fragment know
                // which device's data was most recently updated and the status of the update.
                // There can be many rows of macAddresses - this way we keep a running list of each device
                // and its current connection state.

                //Checks to see if key value pair is already in HashMap
                connectionStateHashMap = getConnectionStateHashMap();
                if (connectionStateHashMap.get(gattMacAddress) != null && connectionStateHashMap.get(gattMacAddress).equals(connectionState)){
                    return;
                }
                getConnectionStateHashMap().put(Constants.GATT_STATUS, String.valueOf(gattStatus));
                getConnectionStateHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                getConnectionStateHashMap().put(gattMacAddress, connectionState);

                //post hashmap into MutableLiveData
                Log.d(TAG, "connectionStateHashMapLive: " + gattMacAddress + " " + connectionState);
                getConnectionStateHashMapLive().postValue(getConnectionStateHashMap());
            }
            if (action.equals(Constants.ACTION_CHARACTERISTIC_CHANGE_BYTE)) {
                //Get macAddress and data from intent extras
                String gattMacAddressByte = extras.getString(Constants.GATT_MAC_ADDRESS);
                String characteristicUUIDByte = extras.getString(Constants.CHARACTERISTIC_UUID);
                byte[] characteristicValueByte = extras.getByteArray(Constants.CHARACTERISTIC_VALUE_BYTE);

                //Instantiate new characteristicData object containing all the data
                CharacteristicData characteristicData = new CharacteristicData(gattMacAddressByte, characteristicUUIDByte, characteristicValueByte);

                //Add the characteristicData object to a queue.
                characteristicQueue.add(characteristicData);

                //Put the queue into MutableLiveData
                getCharacteristicQueueLive().postValue(characteristicQueue);
            }
        }
    };

    /*
     * LiveData
     */

    public static HashMap<String, String> getConnectionStateHashMap() {
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

    public static MutableLiveData<ConcurrentLinkedQueue> getCharacteristicQueueLive() {
        if (characteristicQueueLive == null) {
            characteristicQueueLive = new MutableLiveData<>();
        }
        return characteristicQueueLive;
    }

    public static MutableLiveData<Boolean> getOperationsPendingLive() {
        if (operationsPendingLive == null) {
            operationsPendingLive = new MutableLiveData<>();
        }
        return operationsPendingLive;
    }




}
