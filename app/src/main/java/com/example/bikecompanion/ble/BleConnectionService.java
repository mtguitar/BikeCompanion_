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

@SuppressLint("MissingPermission")

public class BleConnectionService extends LifecycleService {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ConnectService";

    //connection vars
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private int servicesCounter;
    private int connectionCounter;

    HashMap<String, BluetoothGatt> bluetoothDeviceMap;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Initialize bluetooth adapter
     */

    public boolean initialize() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;

    }

    /**
     * Connect to device
     */

    public boolean connectDevice(String deviceMacAddress) {
        initialize();
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth not initialized.");
            return false;
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceMacAddress);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        device.connectGatt(this, false, mBluetoothGattCallback);
        Log.d(TAG, "Connecting . . .");
        return true;
    }

    /**
     * Discover services
     */

    public boolean discoverServices(String deviceMacAddress) {
        BluetoothGatt gatt = getBluetoothDeviceMap().get(deviceMacAddress);
        if (gatt == null || bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter or gatt not initialized. Gatt: " + gatt);
            return false;
        }
        gatt.discoverServices();
        Log.i(TAG, "Start service discovery: " + deviceMacAddress);
        return true;
    }


    /**
     * Disconnect from device
     */

    public boolean disconnectDevice(String deviceMacAddress) {
        BluetoothGatt gatt = getBluetoothDeviceMap().get(deviceMacAddress);
        if (gatt == null || bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter or gatt not initialized. Gatt: " + gatt);
            return false;
        }
        gatt.disconnect();
        Log.d(TAG, "Device disconnected " + deviceMacAddress);
        return true;
    }

    /**
     * Write characteristic
     */

    public void writeCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, byte[] payload) {
        Log.w(TAG, "Received request to write: " + payload[0]);
        BluetoothGatt gatt = getBluetoothDeviceMap().get(deviceMacAddress);

        // Check if bluetooth is initialized
        if (gatt == null || bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        //Check if device has the characteristic we're trying to write and set boolean containsCharacteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++) {
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

        //If device has the characteristic (containsCharacteristic == true) -> check and set the writeType (default or noResponse), set the payload, then write
        if (containsCharacteristic) {
            int writeType;
            BluetoothGattCharacteristic characteristicToWrite = gatt.getService(service).getCharacteristic(characteristic);
            if (isCharacteristicWritable(characteristicToWrite)) {
                writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
            } else if (isCharacteristicWritableWithoutResponse(characteristicToWrite)) {
                writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
            } else {
                Log.d(TAG, "Characteristic not writable");
                return;
            }
            characteristicToWrite.setWriteType(writeType);
            characteristicToWrite.setValue(payload);
            gatt.writeCharacteristic(characteristicToWrite);
            Log.d(TAG, "Writing characteristic: " + characteristicToWrite);
        }

    }

    /**
     * Read characteristic
     */

    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic) {
        Log.w(TAG, "Received request to read characteristic: " + characteristic);
        BluetoothGatt gatt = getBluetoothDeviceMap().get(deviceMacAddress);
        if (gatt == null || bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        Log.d(TAG, String.valueOf(gatt));

        //Check if device has characteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++) {
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
        if (containsCharacteristic) {
            BluetoothGattCharacteristic characteristicToRead = gatt.getService(service).getCharacteristic(characteristic);
            if (characteristicToRead != null && isCharacteristicReadable(characteristicToRead)) {
                gatt.readCharacteristic(characteristicToRead);
                Log.d(TAG, "Reading characteristic: " + characteristicToRead);

            } else {
                Log.d(TAG, "Char not readable");
            }
        }


    }

    /**
     * Subscribe to characteristic notifications
     */

    public void setCharacteristicNotification(String deviceMacAddress, UUID service, UUID characteristic, boolean enabled) {
        BluetoothGatt gatt = getBluetoothDeviceMap().get(deviceMacAddress);
        Log.w(TAG, "Received request to set characteristic notification");
        if (gatt == null || bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        //Check if device has service and characteristic
        boolean containsCharacteristic = false;
        int serviceListSize = gatt.getServices().size();
        for (int i = 0; i < serviceListSize; i++) {
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

        //If device has characteristic, check if notifiable or indicatable, then write CCCD descriptor  to enable notifications on ble device
        if (containsCharacteristic) {
            BluetoothGattCharacteristic characteristicToSubscribe = gatt.getService(service).getCharacteristic(characteristic);
            byte[] payload;
            UUID cccdUuid = Constants.CCCD; //CCCD is the descriptor that controls notifications on the ble device
            BluetoothGattDescriptor descriptor = characteristicToSubscribe.getDescriptor(cccdUuid);

            if (isCharacteristicNotifiable(characteristicToSubscribe) && gatt.setCharacteristicNotification(characteristicToSubscribe, enabled)) {
                payload = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                writeDescriptor(gatt, descriptor, payload);
                Log.d(TAG, "Set notification for characteristic " + characteristic);
            } else if (isCharacteristicIndicatable(characteristicToSubscribe) && gatt.setCharacteristicNotification(characteristicToSubscribe, enabled)) {
                payload = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                writeDescriptor(gatt, descriptor, payload);
                Log.d(TAG, "Set indicator for characteristic " + characteristic);
            } else {
                Log.d(TAG, "Characteristic does not support notifications");
            }
        }
    }

    /**
     * Writes descriptors
     */

    private void writeDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, byte[] payload) {
        descriptor.setValue(payload);
        gatt.writeDescriptor(descriptor);
    }

    /**
     * Callbacks
     */

    // Implements Gatt Callback method and data received methods
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String gattMacAddress = gatt.getDevice().getAddress();
            int operationType;
            int gattStatus;
            String connectionState;
            Log.d(TAG, "hashmaplive: " + gatt.getDevice().getAddress() + " " + newState);

            Log.w(TAG, "New State: " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                connectionCounter = 0;
                gattStatus = Constants.GATT_SUCCESS;
                switch (newState) {
                    case (BluetoothProfile.STATE_CONNECTING):
                        connectionState = Constants.CONNECTION_STATE_CONNECTING;
                        operationType = Constants.OPERATION_CONNECT_DEVICE;
                        break;
                    case (BluetoothProfile.STATE_CONNECTED):
                        connectionState = Constants.CONNECTION_STATE_CONNECTED;
                        operationType = Constants.OPERATION_CONNECT_DEVICE;
                        break;
                    case (BluetoothProfile.STATE_DISCONNECTING):
                        connectionState = Constants.CONNECTION_STATE_DISCONNECTING;
                        operationType = Constants.OPERATION_DISCONNECT_DEVICE;
                        break;
                    case (BluetoothProfile.STATE_DISCONNECTED):
                        connectionState = Constants.CONNECTION_STATE_DISCONNECTED;
                        operationType = Constants.OPERATION_DISCONNECT_DEVICE;
                        break;
                    default:
                        operationType = Constants.OPERATION_UNKNOWN;
                        connectionState = Constants.CONNECTION_STATE_UNKNOWN;
                    }

                broadcastUpdateState(operationType, gatt, connectionState, gattStatus);
                Log.d(TAG, "New State: " + gattMacAddress + " " + newState);

                getBluetoothDeviceMap().put(gattMacAddress, gatt);
                Log.d(TAG, "Put in hashMap: " + gattMacAddress);

            } else {
                gattStatus = Constants.GATT_ERROR;
                if (connectionCounter < 2) {
                    Log.d(TAG, "Problem connecting/disconnecting. Trying again.");
                    connectionCounter++;
                    connectDevice(gattMacAddress);
                } else {
                    connectionCounter = 0;
                    Log.d(TAG, "Problem connecting/disconnecting, newState: " + newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        operationType = Constants.OPERATION_DISCONNECT_DEVICE;
                        connectionState = Constants.CONNECTION_STATE_CONNECTED;
                    } else {
                        operationType = Constants.OPERATION_CONNECT_DEVICE;
                        connectionState = Constants.CONNECTION_STATE_DISCONNECTED;
                    }
                    broadcastUpdateState(operationType, gatt, connectionState, gattStatus);
                }
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            String connectionState;
            int operationType;
            int gattStatus;

            Log.d(TAG, "hashmaplive: " + gatt.getDevice().getAddress() + "Services discovered" );

            if (status == BluetoothGatt.GATT_SUCCESS) {
                connectionState = Constants.CONNECTION_STATE_SERVICES_DISCOVERED;
                operationType = Constants.OPERATION_DISCOVER_SERVICES;
                gattStatus = Constants.GATT_SUCCESS;

                broadcastUpdateState(operationType, gatt, connectionState, gattStatus);
                Log.w(TAG, "Service discovery successful");
                servicesCounter = 0;

            } else {
                gatt.close();
                if (servicesCounter < 2) {
                    servicesCounter++;
                    String gattMacAddress = gatt.getDevice().getAddress();

                    discoverServices(gattMacAddress);
                    Log.w(TAG, "Service discovery failed. Trying again");
                } else {
                    connectionState = Constants.CONNECTION_STATE_SERVICES_UNKNOWN;
                    operationType = Constants.OPERATION_DISCOVER_SERVICES;
                    gattStatus = Constants.GATT_ERROR;

                    broadcastUpdateState(operationType, gatt, connectionState, gattStatus);
                    servicesCounter = 0;
                    Log.w(TAG, "Service discovery failed");
                }
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                int operationType = Constants.OPERATION_CHARACTERISTIC_READ;
                int gattStatus = Constants.GATT_SUCCESS;
                broadcastUpdateCharacteristic(operationType, gatt, characteristic, gattStatus);
                Log.w(TAG, "Received characteristicRead" + characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.w(TAG, "Received characteristicChanged" + characteristic + " " + Arrays.toString(characteristic.getValue()));
            int operationType = Constants.OPERATION_CHARACTERISTIC_CHANGED;
            int gattStatus = Constants.GATT_SUCCESS;

            broadcastUpdateCharacteristic(operationType, gatt, characteristic, gattStatus);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                int operationType = Constants.OPERATION_CHARACTERISTIC_WRITE;
                int gattStatus = Constants.GATT_SUCCESS;

                broadcastUpdateCharacteristic(operationType, gatt, characteristic, gattStatus);
                Log.w(TAG, "Received characteristicWrite" + characteristic);
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                int operationType = Constants.OPERATION_DESCRIPTOR_WRITE;
                int gattStatus = Constants.GATT_SUCCESS;

                broadcastUpdateDescriptor(operationType, gatt, descriptor, gattStatus);
                Log.w(TAG, "Received descriptorWrite" + descriptor);
            }
        }
    };

    /**
     * Broadcast updates
     */

// Broadcast updates to connection state changes
    private void broadcastUpdateState(int operationType, BluetoothGatt gatt, String connectionState, int status) {
        String action = Constants.ACTION_GATT_STATE_CHANGE;
        String gattMacAddress = gatt.getDevice().getAddress();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.GATT_MAC_ADDRESS, gattMacAddress);
        bundle.putString(Constants.CONNECTION_STATE, connectionState);
        bundle.putInt(Constants.GATT_OPERATION_TYPE, operationType);
        bundle.putInt(Constants.GATT_STATUS, status);

        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, bundle);
        sendBroadcast(intent);
    }

    // Broadcast updates to characteristics
    private void broadcastUpdateCharacteristic(int operationType, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        //Test with Byte data
        String action = Constants.ACTION_CHARACTERISTIC_CHANGE_BYTE;

        String gattMacAddress = gatt.getDevice().getAddress();
        String characteristicUUID = (characteristic.getUuid()).toString();
        byte[] characteristicValue = characteristic.getValue();

        Log.d(TAG, "characteristic value: " + characteristicValue);

        Bundle characteristicBundleByte = new Bundle();
        characteristicBundleByte.putString(Constants.GATT_MAC_ADDRESS, gattMacAddress);
        characteristicBundleByte.putString(Constants.CHARACTERISTIC_UUID, characteristicUUID);
        characteristicBundleByte.putByteArray(Constants.CHARACTERISTIC_VALUE_BYTE, characteristicValue);
        characteristicBundleByte.putInt(Constants.GATT_OPERATION_TYPE, operationType);

        final Intent intentByte = new Intent(action);
        intentByte.putExtra(Constants.EXTRA_DATA, characteristicBundleByte);
        sendBroadcast(intentByte);
    }

    // Broadcast updates to descriptors
    private void broadcastUpdateDescriptor(int operationType, BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        String action = Constants.ACTION_DESCRIPTOR_CHANGE;
        String gattMacAddress = gatt.getDevice().getAddress();
        String descriptorUUID = descriptor.getUuid().toString();

        Log.d(TAG, "Bundle: " + gattMacAddress + " " + descriptorUUID);

        Bundle characteristicBundle = new Bundle();
        characteristicBundle.putString(Constants.GATT_MAC_ADDRESS, gattMacAddress);
        characteristicBundle.putString(Constants.DESCRIPTOR_UUID, descriptorUUID);
        characteristicBundle.putInt(Constants.GATT_OPERATION_TYPE, operationType);

        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, characteristicBundle);
        sendBroadcast(intent);

    }


    /**
     * Binder
     */

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

    /**
     * Hashmap to store device info
     */

    private HashMap<String, BluetoothGatt> getBluetoothDeviceMap() {
        if (bluetoothDeviceMap == null) {
            bluetoothDeviceMap = new HashMap<>();
            return bluetoothDeviceMap;
        }
        return bluetoothDeviceMap;
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
     * Helper methods to check if characteristic is readable, writable, notifiable
     */

    //@return Returns true if property is writable
    public static boolean isCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE)) != 0;
    }

    //@return Returns true if property is writable w/o response
    public static boolean isCharacteristicWritableWithoutResponse(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    //@return Returns true if property is readable
    public static boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    //@return Returns true if property supports notification
    public boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    //@return Returns true if property supports indications
    public boolean isCharacteristicIndicatable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;
    }


}