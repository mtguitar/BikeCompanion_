package com.example.bikecompanion.unused;

/*
@SuppressLint("MissingPermission")
public class BleConnectionService2 extends Service {

    public boolean isFirstRun = true;
    private final static String TAG = "FlareLog ConnectService";

    //connection vars
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress;
    private String deviceMacAddress;
    public int connectionState = STATE_DISCONNECTED;

    //constructor
    public BleConnectionService2() {
    }

    @Override
    public int onStartCommand (@Nullable Intent intent, int flags, int startId){
        String action = intent.getAction();
        Log.d(TAG, "Received intent from fragment: " + action);
        if (intent.hasExtra("macAddress")) {
            deviceMacAddress = intent.getStringExtra("macAddress");
        }

        Log.d(TAG, "Received intent extra: " + deviceMacAddress);
        if (action.equals(Constants.ACTION_CONNECT_TO_DEVICE) && deviceMacAddress != null){
            connectDevice(deviceMacAddress);
        }

        return super.onStartCommand(intent, flags, startId);
    }





    public void onDestroy() {
        super.onDestroy();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        Log.d(TAG, "BleConnectionService Destroyed");
        stopSelf();
    }

    // Connect to the device
    public boolean connectDevice (String deviceMacAddress){
        initializeBluetooth();
        this.deviceMacAddress = deviceMacAddress;
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("F8:EF:93:1C:EC:DB");
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothDeviceAddress = device.getAddress();

        if (mBluetoothAdapter == null || mBluetoothDeviceAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }


        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        connectionState = STATE_CONNECTING;
        return true;
    }


    // Disconnect from the device
    public void disconnectDevice () {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        Log.d(TAG, "Device disconnected");
    }

    public void initializeBluetooth () {
        mBluetoothManager =
                (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        Log.d(TAG, "Initialized bluetooth");
    }

    // Read characteristic
    public void readCharacteristic (UUID service, UUID characteristic){
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristicToRead = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.readCharacteristic(characteristicToRead);
    }

    // Write characteristic
    public void writeCharacteristic (UUID service, UUID characteristic,byte[] payload){
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

    // Subscribe to characteristic notifications
    public void setCharacteristicNotification (UUID service, UUID characteristic,
                                               boolean enabled){
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        BluetoothGattCharacteristic characteristicToSubscribe = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
        mBluetoothGatt.setCharacteristicNotification(characteristicToSubscribe, enabled);
        Log.w(TAG, "Subscribed to characteristic " + characteristic);
    }


    // Implements Gatt Callback method and data received methods
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentConnectionState;
            Log.w(TAG, "New connection state: " + newState);
            mBluetoothGatt = gatt;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentConnectionState = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                Log.d(TAG, "Device Connected " + deviceMacAddress);
                sendIntentToFragment(intentConnectionState);

                mBluetoothGatt.discoverServices();
                Log.i(TAG, "Start service discovery ");
                readCharacteristic(GenericDevice.UUID_SERVICE_DEVICE_MANUFACTURER, GenericDevice.UUID_CHARACTERISTIC_DEVICE_MANUFACTURER);


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentConnectionState = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                sendIntentToFragment(intentConnectionState);
            }

        }

    };

    @SuppressLint("MissingPermission")
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Services discovered");
            sendIntentToFragment(ACTION_GATT_SERVICES_DISCOVERED);
        }
        else{
            Log.d(TAG, "Problem with service discovery.  Status : " + status);
        }
    }

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            sendIntentToFragment(ACTION_DATA_AVAILABLE, characteristic);
            Log.d(TAG, "read characteristic");
        }
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        sendIntentToFragment(ACTION_DATA_AVAILABLE, characteristic);
        Log.w(TAG, "Characteristic changed");
    }

    //methods for sending intents to ScannerFragment
    private void sendIntentToFragment(String action) {
        Intent scanningStatusIntent = new Intent(action);
        sendBroadcast(scanningStatusIntent);
        Log.d(Constants.TAG, "Sent intent to fragment " + action);
    }

    private void sendIntentToFragment(String action, BluetoothGattCharacteristic characteristic) {
        byte[] characteristicValue = characteristic.getValue();
        Intent scanningStatusIntent = new Intent(action);
        String characteristicString = String.valueOf(characteristic);
        if (characteristicValue != null){
            scanningStatusIntent.putExtra(characteristicString, characteristicValue);
        }
        sendBroadcast(scanningStatusIntent);
        Log.d(Constants.TAG, "Sent intent to fragment " + action);
    }

    //Allows fragments to bind to this service
    public class LocalBinder extends Binder {
        public BleConnectionService2 getService() {

            return BleConnectionService2.this;
        }
    }

    private final IBinder binder = new BleConnectionService2.LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    public static MutableLiveData<ArrayList<ScanResults>> scannerLiveDataList = new MutableLiveData<>();

    public static MutableLiveData<ArrayList<ScanResults>> getScanResults () {
        return scannerLiveDataList;
    }

    private void addScanResults (String deviceName, String deviceMacAddress, String
            deviceType){
        if (scannerLiveDataList == null) {
            scannerLiveDataList = new MutableLiveData<>();
        }
        if (scanResults == null) {
            scanResults = new ArrayList<>();
        }
        //checks to see if deviceMacAddress is already in list
        int n = scanResults.size();
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                if (deviceMacAddress.equals(scanResults.get(i).getDeviceMacAddress())) {
                    return;
                }
            }
        }
        //if deviceMacAddress not already in list, add device to scannerResults
        int image = R.drawable.ic_device_type_other_sensor;
        if (this.deviceType.equals("light")) {
            image = R.drawable.ic_device_type_light;
        }
        if (deviceName.contains("speed")) {
            image = R.drawable.ic_speed;
        }
        scanResults.add(new ScanResults(image, deviceName, deviceMacAddress, deviceType));
        scannerLiveDataList.postValue(scanResults);
        Log.d(TAG, "Posted scan result " + deviceName + deviceMacAddress);
    }




}



*/
















