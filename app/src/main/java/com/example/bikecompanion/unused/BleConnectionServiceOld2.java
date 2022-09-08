package com.example.bikecompanion.unused;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;

import androidx.lifecycle.LifecycleService;

import java.util.UUID;

import static com.example.bikecompanion.constants.Constants.STATE_DISCONNECTED;

@SuppressLint("MissingPermission")

public class BleConnectionServiceOld2 extends LifecycleService {


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

    /*
    //liveData
    MutableLiveData<String[]> connectedDevicesData = new MutableLiveData<>();
    MutableLiveData<Integer> connectionStateData = new MutableLiveData<Integer>(connectionState);

    @Override
    public int onStartCommand (@Nullable Intent intent, int flags, int startId){
        action = intent.getAction();
        Log.d(TAG, "Received intent from fragment: " + action);

        if (action.equals(ACTION_CONNECT_TO_DEVICE) && intent.hasExtra("deviceMacAddress")) {
            deviceMacAddress = intent.getStringExtra("deviceMacAddress");
            connectDevice(deviceMacAddress);
            Log.d(TAG, "Received intent extra: " + deviceMacAddress);
        }
        if (action.contains(ACTION_READ_CHARACTERISTIC)){

            String characteristic = intent.getStringExtra(Constants.EXTRA_DATA);
            readCharacteristic(characteristic);
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
    public void readCharacteristic(UUID service, UUID characteristic) {
        Log.w(TAG, "Received request to read characteristic");
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristicToRead = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        Log.d(TAG, String.valueOf(characteristicToRead));
        mBluetoothGatt.readCharacteristic(characteristicToRead);
        Log.w(TAG, "Reading characteristic");

    }

    // Subscribe to characteristic notifications
    public void setCharacteristicNotification(UUID service, UUID characteristic, boolean enabled){
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
            String newStateString = String.valueOf(newState);
            Log.w(TAG, newStateString);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                connectionState = STATE_CONNECTED;
                mBluetoothGatt = gatt;
                Log.d(TAG, "Device Connected");

                mBluetoothGatt.discoverServices();
                Log.i(TAG, "Start service discovery ");

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                Log.d(TAG, "Device Disconnected");
            }

        }


        String state_disconnected = ACTION_GATT_CONNECTED;


        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdateState(ACTION_GATT_SERVICES_DISCOVERED);
                Log.w(TAG, "onServicesDiscovered received: " + status);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "Received characteristic" + characteristic);
                broadcastUpdateCharacteristic(gatt, characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.w(TAG, "Received changed characteristic" + characteristic + " " + characteristic.getValue());
            broadcastUpdateCharacteristic(gatt, characteristic);
        }


    };


    // Broadcast updates to connection state changes
    private void broadcastUpdateState (final String intentConnectionState) {
        final Intent intent = new Intent(intentConnectionState);
        sendBroadcast(intent);
    }
    // Broadcast updates to characteristics
    private void broadcastUpdateCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String action = Constants.ACTION_DATA_AVAILABLE;
        String characteristicMacAddress = gatt.getDevice().getAddress();
        String characteristicUUID = (characteristic.getUuid()).toString();
        String characteristicValueString = characteristic.getStringValue(0);
        byte[] characteristicValue = characteristic.getValue();

        Log.d(TAG, "Bundle: " + characteristicMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValue[0]);


        Bundle characteristicBundle = new Bundle();
        characteristicBundle.putString(Constants.CHARACTERISTIC_MAC_ADDRESS,characteristicMacAddress);
        characteristicBundle.putString(Constants.CHARACTERISTIC_UUID, characteristicUUID);
        characteristicBundle.putString(Constants.CHARACTERISTIC_VALUE_STRING, characteristicValueString);
        characteristicBundle.putByte(Constants.CHARACTERISTIC_VALUE_BYTE, characteristicValue[0]);



        final Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTRA_DATA, characteristicBundle);
        sendBroadcast(intent);
    }

    //Allows fragments to bind to this service
    public class LocalBinder extends Binder {
        public BleConnectionServiceOld2 getService() {
            return BleConnectionServiceOld2.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
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