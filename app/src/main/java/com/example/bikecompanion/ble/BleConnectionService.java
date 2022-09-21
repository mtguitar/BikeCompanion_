package com.example.bikecompanion.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.example.bikecompanion.constants.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static com.example.bikecompanion.constants.Constants.ACTION_CONNECT_TO_DEVICE;
import static com.example.bikecompanion.constants.Constants.ACTION_DISCONNECT_DEVICE;
import static com.example.bikecompanion.constants.Constants.ACTION_READ_CHARACTERISTIC;

@SuppressLint("MissingPermission")

public class BleConnectionService extends LifecycleService {


    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ConnectService";

    //connection vars
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String deviceMacAddress;
    private String action;
    private UUID characteristicToRead;
    private UUID serviceToRead;
    private String dataType;
    private String deviceName;
    private int servicesCounter;
    private int connectionCounter;

    HashMap<String, BluetoothGatt> bluetoothDevicesMap;

    @Override
    public int onStartCommand (@Nullable Intent intent, int flags, int startId){


        if(bluetoothDevicesMap == null){
            bluetoothDevicesMap = new HashMap<>();
        }

        action = intent.getAction();
        Log.d(TAG, "Received intent from fragment: " + action);

        if (action.equals(ACTION_CONNECT_TO_DEVICE) && intent.hasExtra("deviceMacAddress")) {
            deviceMacAddress = intent.getStringExtra("deviceMacAddress");
            connectDevice(deviceMacAddress);
            Log.d(TAG, "Received intent extra: " + deviceMacAddress);
        }
        if (action.equals(ACTION_DISCONNECT_DEVICE) && intent.hasExtra("deviceMacAddress")) {
            deviceMacAddress = intent.getStringExtra("deviceMacAddress");
            disconnectDevice(deviceMacAddress);
            Log.d(TAG, "Received intent extra: " + deviceMacAddress);
        }
        if (action.contains(ACTION_READ_CHARACTERISTIC)){

            String characteristic = intent.getStringExtra(Constants.EXTRA_DATA);
            Log.d(TAG, characteristic);

        }

        return super.onStartCommand(intent, flags, startId);
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
        initialize();
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceMacAddress);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        Log.d(TAG, "Connecting . . .");
        return true;
    }


    // Disconnect from the device
    public void disconnectDevice(String deviceMacAddress) {
        BluetoothGatt gatt = getBluetoothDevicesMap().get(deviceMacAddress);
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter or gatt not initialized. Gatt: " + gatt);
            return;
        }
        gatt.disconnect();
        Log.d(TAG, "Device disconnected " + deviceMacAddress);
    }

    // Write characteristic
    public void writeCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, byte[] payload) {
        Log.w(TAG, "Received request to write: " + payload[0]);
        BluetoothGatt gatt = getBluetoothDevicesMap().get(deviceMacAddress);
        if (gatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        //Check if device has characteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++){
            UUID listServiceUUID = gatt.getServices().get(i).getUuid();
            if (listServiceUUID.equals(service)) {
                int charListSize = gatt.getService(service).getCharacteristics().size();
                for (int j = 0; j < charListSize; j++) {
                    UUID listCharUUID = gatt.getService(service).getCharacteristics().get(j).getUuid();
                    if (listCharUUID.equals(characteristic)) {
                        containsCharacteristic = true;
                    }
                }
            }
        }

        //If device has characteristic, check if readable, then read
        if (containsCharacteristic){
            BluetoothGattCharacteristic characteristicToWrite = gatt.getService(service).getCharacteristic(characteristic);
            if (characteristicToWrite != null && isCharacteristicWritable(characteristicToWrite)){
                int writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
                characteristicToWrite.setWriteType(writeType);
                characteristicToWrite.setValue(payload);
                gatt.writeCharacteristic(characteristicToWrite);
                Log.d(TAG, "Writing characteristic: " + characteristicToWrite);

            }
            else{
                Log.d(TAG, "Char not writable");
            }

        }

    }

    // Read characteristic
    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic) {
        Log.w(TAG, "Received request to read characteristic: " + characteristic);
        BluetoothGatt gatt = getBluetoothDevicesMap().get(deviceMacAddress);
        if (gatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        Log.d(TAG, String.valueOf(gatt));

        //Check if device has characteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++){
            UUID listServiceUUID = gatt.getServices().get(i).getUuid();
            if (listServiceUUID.equals(service)) {
                int charListSize = gatt.getService(service).getCharacteristics().size();
                for (int j = 0; j < charListSize; j++) {
                    UUID listCharUUID = gatt.getService(service).getCharacteristics().get(j).getUuid();
                    if (listCharUUID.equals(characteristic)) {
                        containsCharacteristic = true;
                    }
                }
            }
        }
        //If device has characteristic, check if readable, then read
        if (containsCharacteristic){
            BluetoothGattCharacteristic characteristicToRead = gatt.getService(service).getCharacteristic(characteristic);
            gatt.readCharacteristic(characteristicToRead);
            Log.d(TAG, "Reading characteristic: " + characteristicToRead);
        }

        /*

        //If device has characteristic, check if readable, then read
        if (containsCharacteristic){
            BluetoothGattCharacteristic characteristicToRead = gatt.getService(service).getCharacteristic(characteristic);
            if (characteristicToRead != null && isCharacteristicReadable(characteristicToRead)){
                gatt.readCharacteristic(characteristicToRead);
                Log.d(TAG, "Reading characteristic: " + characteristicToRead);

            }
            else{
                Log.d(TAG, "Char not readable");
            }
        }

         */


    }

    // Subscribe to characteristic notifications
    public void setCharacteristicNotification(String deviceMacAddress, UUID service, UUID characteristic, boolean enabled){
        BluetoothGatt gatt = getBluetoothDevicesMap().get(deviceMacAddress);
        Log.w(TAG, "Received request to set characteristic notification");
        if (gatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        //Check if device has service and characteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++){
            UUID listServiceUUID = gatt.getServices().get(i).getUuid();
            if (listServiceUUID.equals(service)) {
                int charListSize = gatt.getService(service).getCharacteristics().size();
                for (int j = 0; j < charListSize; j++) {
                    UUID listCharUUID = gatt.getService(service).getCharacteristics().get(j).getUuid();
                    if (listCharUUID.equals(characteristic)) {
                        containsCharacteristic = true;
                    }
                }
            }
        }

        //If device has characteristic, set notify to true/false
        if (containsCharacteristic){

            BluetoothGattCharacteristic characteristicToSubscribe = gatt.getService(service).getCharacteristic(characteristic);
            byte[] payload;
            UUID cccdUuid = Constants.CCCD;
            BluetoothGattDescriptor descriptor = characteristicToSubscribe.getDescriptor(cccdUuid);

            if (isCharacteristicNotifiable(characteristicToSubscribe) && gatt.setCharacteristicNotification(characteristicToSubscribe, enabled)){

                payload = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                writeDescriptor(gatt, descriptor, payload);
                Log.d(TAG, "Set notification for characteristic " + characteristic);
            }
            else if(isCharacteristicIndicatable(characteristicToSubscribe) && gatt.setCharacteristicNotification(characteristicToSubscribe, enabled)){
                payload = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                writeDescriptor(gatt, descriptor, payload);
                Log.d(TAG, "Set indicator for characteristic " + characteristic);

            }
            else {
                Log.d(TAG, "Char does not support notifications");
            }

        }

    }


    private void writeDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, byte[] payload) {
        descriptor.setValue(payload);
        gatt.writeDescriptor(descriptor);
    }


    // Implements Gatt Callback method and data received methods
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String gattMacAddress = gatt.getDevice().getAddress();
            Log.w(TAG, "New State: " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                connectionCounter = 0;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    broadcastUpdateState(Constants.GATT_CONNECTED, gatt);
                    Log.d(TAG, "Device Connected " + gattMacAddress);

                    getBluetoothDevicesMap().put(gattMacAddress, gatt);
                    Log.d(TAG, "Put in hashMap: " + gattMacAddress);

                    gatt.discoverServices();
                    Log.i(TAG, "Start service discovery " + gattMacAddress);

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    broadcastUpdateState(Constants.GATT_DISCONNECTED, gatt);
                    Log.d(TAG, "Device disconnected " + gattMacAddress);

                    getBluetoothDevicesMap().remove(gattMacAddress);
                    Log.d(TAG, "Removed from hashMap: " + gattMacAddress);

                }
            }
            else{
                if (connectionCounter < 2){
                    gatt.close();
                    Log.d(TAG, "Problem connecting/disconnecting. Trying again.");
                    connectionCounter++;
                    connectDevice(gattMacAddress);
                }
                else{
                    gatt.close();
                    connectionCounter = 0;
                    Log.d(TAG, "Problem connecting/disconnecting");
                }

            }

        }


        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdateState(Constants.GATT_SERVICES_DISCOVERED, gatt);
                Log.w(TAG, "Service discovery successful");
                servicesCounter = 0;


            } else {
                if (servicesCounter < 2) {
                    servicesCounter++;
                    String address = gatt.getDevice().getAddress();
                    connectDevice(address);
                    Log.w(TAG, "Service discovery failed. Trying again");
                }
                else {
                    servicesCounter = 0;
                    Log.w(TAG, "Service discovery failed");
                }

            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdateCharacteristic(gatt, characteristic);
                Log.w(TAG, "Received characteristicRead" + characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.w(TAG, "Received characteristicChanged" + characteristic + " " + Arrays.toString(characteristic.getValue()));
            broadcastUpdateCharacteristic(gatt, characteristic);
        }

    };


    // Broadcast updates to connection state changes
    private void broadcastUpdateState (final String connectionState, BluetoothGatt gatt) {
        String action = Constants.ACTION_GATT_STATE_CHANGE;
        String gattMacAddress = gatt.getDevice().getAddress();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.GATT_MAC_ADDRESS, gattMacAddress);
        bundle.putString(Constants.GATT_CONNECTION_STATE, connectionState);


        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, bundle);
        sendBroadcast(intent);
    }
    // Broadcast updates to characteristics
    private void broadcastUpdateCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String action = Constants.ACTION_DATA_AVAILABLE;
        String characteristicMacAddress = gatt.getDevice().getAddress();
        String characteristicUUID = (characteristic.getUuid()).toString();
        String characteristicValueString = characteristic.getStringValue(0);
        int characteristicValueInt = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        Log.d(TAG, "Bundle: " + characteristicMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValueInt);


        Bundle characteristicBundle = new Bundle();
        characteristicBundle.putString(Constants.GATT_MAC_ADDRESS,characteristicMacAddress);
        characteristicBundle.putString(Constants.CHARACTERISTIC_UUID, characteristicUUID);
        characteristicBundle.putString(Constants.CHARACTERISTIC_VALUE_STRING, characteristicValueString);
        characteristicBundle.putInt(Constants.CHARACTERISTIC_VALUE_INT, characteristicValueInt);

        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, characteristicBundle);
        sendBroadcast(intent);
    }

    //Allows fragments to bind to this service
    public class LocalBinder extends Binder {
        public BleConnectionService getService() {
            return BleConnectionService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }


    private HashMap<String, BluetoothGatt> getBluetoothDevicesMap(){
        if (bluetoothDevicesMap == null){
            bluetoothDevicesMap = new HashMap<>();
            return bluetoothDevicesMap;
        };
        return bluetoothDevicesMap;
    }


    /*
      Set up foreground service, and notification
     */

/*
    private void startForegroundService() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constant.NOTIFICATION_CHANNEL_ID)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_flare)
                        .setContentTitle("BikeComp")
                        .setContentText("Scanning")
                        .setContentIntent(createPendingIntent())
                ;

        startForeground(Constant.NOTIFICATION_ID, notificationBuilder.build());

    }

    private void createNotificationChannel(NotificationManager notificationManager){
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    Constant.NOTIFICATION_CHANNEL_ID,
                    Constant.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

    }

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return resultPendingIntent;
    }

 */


    /**
     * @return Returns true if property is writable
     */
    public static boolean isCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    /**
     * @return Returns true if property is readable
     */
    public static boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    /**
     * @return Returns true if property supports notification
     */
    public boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    /**
     * @return Returns true if property supports indications
     */
    public boolean isCharacteristicIndicatable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;
    }







}