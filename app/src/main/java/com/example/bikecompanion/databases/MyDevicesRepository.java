package com.example.bikecompanion.databases;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.ui.myDevices.MyDevice;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This repo holds all the data used to populate MyDevicesFragment.
 * It puts the data in LiveData.
 * It manages a database (using Room) of ble devices the user has added to myDevices.
 * It binds to BleConnectionService.
 * It uses BleConnectionService to check connection, read, write, setNotify for devices in myDevices database.
 * It receives info updates from BleConnectionService via a broadcastReceiver.
 * MyDevicesFragment interacts with this repo entirely through MyDevicesViewModel.
 */

public class MyDevicesRepository {

    private MyDevicesDao deviceDao;
    private LiveData<List<MyDevice>> allDevices;

    private BleConnectionService bleConnectionService;

    private HashMap<String, String> connectionStateHashMap;
    private static MutableLiveData<HashMap> connectionStateHashMapLive;
    private MutableLiveData<Boolean> isConnected;

    private HashMap<String, String> deviceDataHashMap;
    private static MutableLiveData<HashMap> deviceDataHashMapLive;


    private boolean boundToService;
    private final static String TAG = "FlareLog Repo";


    private Context context;

    public MyDevicesRepository(Application application){
        MyDevicesDataBase deviceDataBase = MyDevicesDataBase.getInstance(application);
        deviceDao = deviceDataBase.deviceDao();
        allDevices = deviceDao.getAllDevices();

        context = application.getApplicationContext();
        registerBroadcastReceiver(context);
    }

    public void insert (MyDevice device){
        new InsertDeviceAsyncTask(deviceDao).execute(device);
    }

    public void update (MyDevice device){
        new UpdateDeviceAsyncTask(deviceDao).execute(device);
    }

    public void delete (MyDevice device){
        new DeleteDeviceAsyncTask(deviceDao).execute(device);

    }

    public void deleteAllDevices (){
        new DeleteAllDevicesAsyncTask(deviceDao).execute();

    }

    public LiveData<List<MyDevice>> getAllDevices(){
        return allDevices;
    }

    //AsyncTasks to ensure that we are not working on the main thread
    private static class InsertDeviceAsyncTask extends AsyncTask<MyDevice, Void, Void>{
        private MyDevicesDao deviceDao;

        private InsertDeviceAsyncTask(MyDevicesDao deviceDao){
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(MyDevice... devices) {
            deviceDao.insert(devices[0]);
            return null;
        }
    }

    private static class UpdateDeviceAsyncTask extends AsyncTask<MyDevice, Void, Void>{
        private MyDevicesDao deviceDao;

        private UpdateDeviceAsyncTask(MyDevicesDao deviceDao){
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(MyDevice... devices) {
            deviceDao.update(devices[0]);
            return null;
        }
    }

    private static class DeleteDeviceAsyncTask extends AsyncTask<MyDevice, Void, Void>{
        private MyDevicesDao deviceDao;

        private DeleteDeviceAsyncTask(MyDevicesDao deviceDao){
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(MyDevice... devices) {
            deviceDao.delete(devices[0]);
            return null;
        }
    }

    private static class DeleteAllDevicesAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        private MyDevicesDao deviceDao;

        private DeleteAllDevicesAsyncTask(MyDevicesDao deviceDao){
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.deleteAllDevices();
            return null;
        }
    }



    /*
    //Sends intents to BleConnectionService
    public void sendCommandToService(Class serviceClass, String action, Bundle extras) {
        Intent bleServiceIntent = new Intent(context, serviceClass);
        bleServiceIntent.setAction(action);
        if(extras != null){
            bleServiceIntent.putExtras(extras);
        }
        context.startService(bleServiceIntent);
        Log.d(TAG, "Sent intent to " + serviceClass + " " + action);
    }

     */

    public void connectDevice(String deviceMacAddress) {
        bleConnectionService.connectDevice(deviceMacAddress);
    }

    public void disconnectDevice(String deviceMacAddress) {
        bleConnectionService.disconnectDevice(deviceMacAddress);
    }

    public void readCharacteristic(String deviceMacAddress, UUID service, UUID characteristic){
        bleConnectionService.readCharacteristic(deviceMacAddress, service, characteristic);
    }

    public void writeCharacteristic(String deviceMacAddress, UUID service, UUID characteristic, byte[] payload){
        bleConnectionService.writeCharacteristic(deviceMacAddress, service, characteristic, payload);
    }

    public void setCharacteristicNotification(String deviceMacAddress, UUID service, UUID characteristic, boolean enabled){
        bleConnectionService.setCharacteristicNotification(deviceMacAddress, service, characteristic, enabled);
    }


    public void bindService(){
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
            Log.d(TAG, "Bound to service from repository");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleConnectionService = null;
        }
    };

    public void registerBroadcastReceiver(Context context){
        //Set intent filters and register receiver to listen for updates
        createIntentFilter();
        context.registerReceiver(gattUpdateReceiver, createIntentFilter());
    }

    //Intent filters for receiving intents
    public static IntentFilter createIntentFilter () {
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

            final String action = intent.getAction();
            Bundle extras = intent.getBundleExtra(Constants.EXTRA_DATA);

            if(action.equals(Constants.ACTION_GATT_STATE_CHANGE)){
                //get connectionState and macAddress from intent extras
                connectionState = extras.getString(Constants.GATT_CONNECTION_STATE);
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);
                //put connectionState and macAddress into hashmap
                getConnectionStateHashMap().put(Constants.GATT_CONNECTION_STATE, connectionState);
                getConnectionStateHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                //put hashmap into MutableLiveData
                getConnectionStateHashMapLive().postValue(getConnectionStateHashMap());
                if(connectionState.equals(Constants.GATT_CONNECTED)){
                    getIsConnected().postValue(true);
                }
                if(connectionState.equals(Constants.GATT_DISCONNECTED)){
                    getIsConnected().postValue(false);
                }
            }
            if(action.equals(Constants.ACTION_DATA_AVAILABLE)){
                //get data and macAddress from intent extras
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);
                String characteristicUUID = extras.getString(Constants.CHARACTERISTIC_UUID);
                String characteristicValueString = extras.getString(Constants.CHARACTERISTIC_VALUE_STRING);
                byte characteristicValueByte = extras.getByte(Constants.CHARACTERISTIC_VALUE_BYTE);

                //put connectionState and macAddress into hashmap
                getDeviceDataHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_UUID, characteristicUUID);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_VALUE_STRING, characteristicValueString);
                getDeviceDataHashMap().put(Constants.CHARACTERISTIC_VALUE_BYTE, String.valueOf(characteristicValueByte));

                //put hashmap into MutableLiveData
                getDeviceDataHashMapLive().postValue(getDeviceDataHashMap());




            }

        }
    };




    //liveData
    public HashMap<String, String> getConnectionStateHashMap(){
        if (connectionStateHashMap == null) {
            connectionStateHashMap = new HashMap<String, String>();
        }
        return connectionStateHashMap;
    }

    public static MutableLiveData<HashMap> getConnectionStateHashMapLive(){
        if (connectionStateHashMapLive == null) {
            connectionStateHashMapLive = new MutableLiveData<>();
        }
        return connectionStateHashMapLive;
    }

    public MutableLiveData<Boolean> getIsConnected(){
        if (isConnected == null) {
            isConnected = new MutableLiveData<>();
        }
        return isConnected;
    }


    public HashMap<String, String> getDeviceDataHashMap(){
        if (deviceDataHashMap == null) {
            deviceDataHashMap = new HashMap<String, String>();
        }
        return deviceDataHashMap;
    }

    public static MutableLiveData<HashMap> getDeviceDataHashMapLive(){
        if (deviceDataHashMapLive == null) {
            deviceDataHashMapLive = new MutableLiveData<>();
        }
        return deviceDataHashMapLive;
    }



}
