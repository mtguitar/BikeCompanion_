package com.example.bikecomputerfirstdraft.ui.home.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.example.bikecomputerfirstdraft.constants.Constants;

import java.util.UUID;

import static com.example.bikecomputerfirstdraft.constants.Constants.*;

@SuppressLint("MissingPermission")

public class BleConnectionService extends LifecycleService {


    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ConnectService";

    //connection vars
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String deviceMacAddress;
    public int connectionState = STATE_DISCONNECTED;
    private String action;
    private UUID characteristicToRead;
    private UUID serviceToRead;
    private String dataType;


    private String deviceName;


    @Override
    public int onStartCommand (@Nullable Intent intent, int flags, int startId){
        action = intent.getAction();
        Log.d(TAG, "Received intent from fragment: " + action);

        if (action.equals(ACTION_CONNECT_TO_DEVICE) && intent.hasExtra("macAddress")) {
            deviceMacAddress = intent.getStringExtra("macAddress");
            connectDevice(deviceMacAddress);
            Log.d(TAG, "Received intent extra: " + deviceMacAddress);
        }
        if (action.contains(ACTION_READ_CHARACTERISTIC)){

            String characteristic = intent.getStringExtra(Constants.EXTRA_DATA);
            Log.d(TAG, characteristic);

        }


        return super.onStartCommand(intent, flags, startId);
    }



    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();

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
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        Log.d(TAG, "Connecting . . .");
        connectionState = STATE_CONNECTING;
        return true;
    }


    // Disconnect from the device
    public void disconnectDevice(String deviceMacAddress) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        Log.d(TAG, "Device disconnected");
    }

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
    public void readCharacteristic(UUID service, UUID characteristic, String dataType) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        this.dataType = dataType;
        BluetoothGattCharacteristic characteristicToRead = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.readCharacteristic(characteristicToRead);
        Log.w(TAG, "Reading characteristic");
    }

    // Subscribe to characteristic notifications
    public void setCharacteristicNotification(UUID service, UUID characteristic, boolean enabled, String dataType){
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        this.dataType = dataType;
        BluetoothGattCharacteristic characteristicToSubscribe = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.setCharacteristicNotification(characteristicToSubscribe, enabled);
        Log.w(TAG, "Subscribed to characteristic " + characteristic);
    }


    // Implements Gatt Callback method and data received methods
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentConnectionState;
            String newState1 = String.valueOf(newState);
            Log.w(TAG, newState1);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentConnectionState = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                mBluetoothGatt = gatt;
                broadcastUpdate(intentConnectionState);

                Log.d(TAG, "Device Connected");

                mBluetoothGatt.discoverServices();
                Log.i(TAG, "Start service discovery ");

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentConnectionState = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentConnectionState);
                Log.d(TAG, "Device Disconnected");
            }

        }


        String state_disconnected = ACTION_GATT_CONNECTED;


        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.w(TAG, "onServicesDiscovered received: " + status);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

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


    // Broadcast updates to connection state changes
    private void broadcastUpdate(final String intentConnectionState) {
        final Intent intent = new Intent(intentConnectionState);
        sendBroadcast(intent);
    }
    // Broadcast updates to characteristics
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        byte[] characteristicValue = characteristic.getValue();

        byte[] characteristicTest = characteristic.getValue();
        String charValue = String.valueOf(characteristicTest[0]);
        Log.d(TAG, "read character " + charValue);

        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, charValue);
        sendBroadcast(intent);
    }


    /**
     * Set up foreground service, and notification
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






}