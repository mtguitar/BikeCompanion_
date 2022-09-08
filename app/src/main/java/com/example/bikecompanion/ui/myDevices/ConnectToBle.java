package com.example.bikecompanion.ui.myDevices;

import androidx.lifecycle.MutableLiveData;

import com.example.bikecompanion.ble.BleConnectionService;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectToBle {
    private final static String TAG = "FlareLog";

    private String deviceMacAddress;

    private boolean boundToService = false;
    private boolean servicesDiscovered = false;
    private boolean batteryRequested = false;
    private boolean manufacturerRequested = false;
    private boolean modeRequested = false;

    public MutableLiveData<ArrayList<String>> characteristicList = new MutableLiveData<>();
    public MutableLiveData<String> characteristicMacAddress = new MutableLiveData<>();
    public MutableLiveData<String> characteristicBattery = new MutableLiveData<>();
    public MutableLiveData<String> characteristicMode = new MutableLiveData<>();
    public MutableLiveData<HashMap> characteristicModeHMLD = new MutableLiveData<>();
    public HashMap hashMap = new HashMap();

    public String characteristicManufacturer;

    public BleConnectionService bleConnectionService;





    /*
    public void registerBroadcastReceiver(Context context){
        //Set intent filters and register receiver to listen for updates
        createIntentFilter();
        context.registerReceiver(gattUpdateReceiver, createIntentFilter());
    }

    //Intent filters for receiving intents
    public static IntentFilter createIntentFilter () {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Constants.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Constants.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Constants.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(Constants.ACTION_READ_CHARACTERISTIC_BATTERY);
        intentFilter.addAction(Constants.ACTION_READ_CHARACTERISTIC_MANUFACTURER);
        return intentFilter;
    }



    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    public BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constants.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                servicesDiscovered = true;
                readCharacteristics();

            }
            if (Constants.ACTION_DATA_AVAILABLE.equals(action)){
                Bundle characteristicBundle = intent.getBundleExtra(Constants.EXTRA_DATA);

                String characteristicMacAddress = characteristicBundle.getString(Constants.CHARACTERISTIC_MAC_ADDRESS);
                String characteristicUUID = characteristicBundle.getString(Constants.CHARACTERISTIC_UUID);
                String characteristicValueString = characteristicBundle.getString(Constants.CHARACTERISTIC_VALUE_STRING);
                byte characteristicValueByte = characteristicBundle.getByte(Constants.CHARACTERISTIC_VALUE_BYTE);

                Log.d(TAG, "Received characteristic " + characteristicMacAddress + " " + characteristicUUID + " " + characteristicValueString + " " + characteristicValueByte);
                if(characteristicUUID.equals(GenericDeviceType.STRING_CHARACTERISTIC_BATTERY)) {
                    characteristicBattery.setValue(String.valueOf(characteristicValueByte));
                }
                if(characteristicUUID.equals(GenericDeviceType.STRING_CHARACTERISTIC_MANUFACTURER)){
                    characteristicManufacturer = characteristicValueString;
                }
                if(characteristicUUID.equals(FlareRTDeviceType.STRING_CHARACTERISTIC_LIGHT_MODE)){
                    hashMap.put(deviceMacAddress, (String.valueOf(characteristicValueByte)));
                    characteristicModeHMLD.postValue(hashMap);
                }
                readCharacteristics();


                //MyDevicesFragment.textViewDeviceBattery.setText(characteristic)
            }


        }
    };

    // Checks if BluetoothLeService is bound
    // If not bound, binds to BluetoothLeService and calls serviceConnection
    // If bound, call BluetoothLeService's connected method directly, passing deviceMacAddress
    public void connectDevice(Context context, String deviceMacAddress){
        this.deviceMacAddress = deviceMacAddress;
        if (!boundToService) {
            Log.d(TAG, "Not bound, trying to connect " + deviceMacAddress);
            Intent gattServiceIntent = new Intent(context, BleConnectionService.class);
            context.bindService(gattServiceIntent, serviceConnection, context.BIND_AUTO_CREATE);
        }
        else {
            Log.d(TAG, "Already bound, trying to connect " + deviceMacAddress);
            bleConnectionService.connectDevice(deviceMacAddress);
        }

    }


    // Code to manage BleConnectionService lifecycle.
    public final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
            if (!bleConnectionService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            boundToService = true;
            bleConnectionService.connectDevice(deviceMacAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            boundToService = false;
            bleConnectionService = null;
        }
    };

    private void readCharacteristics(){

        if (!manufacturerRequested) {
            bleConnectionService.readCharacteristic
                    (GenericDeviceType.UUID_SERVICE_DEVICE_NAME, GenericDeviceType.UUID_CHARACTERISTIC_DEVICE_NAME);
            manufacturerRequested = true;
            return;
        }
        if (!batteryRequested) {
            bleConnectionService.readCharacteristic
                    (GenericDeviceType.UUID_SERVICE_BATTERY, GenericDeviceType.UUID_CHARACTERISTIC_BATTERY);
            batteryRequested = true;
            return;
        }


        if (!modeRequested) {
            bleConnectionService.readCharacteristic
                    (FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE);

            bleConnectionService.setCharacteristicNotification
                    (FlareRTDeviceType.UUID_SERVICE_LIGHT_MODE, FlareRTDeviceType.UUID_CHARACTERISTIC_LIGHT_MODE, true);
            modeRequested = true;
            return;

        }

    }

    public void disconnectDevice(){
        bleConnectionService.stopSelf();
    }

     */

}
