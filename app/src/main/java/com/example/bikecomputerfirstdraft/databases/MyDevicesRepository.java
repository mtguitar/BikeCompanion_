package com.example.bikecomputerfirstdraft.databases;

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

import com.example.bikecomputerfirstdraft.ble.BleConnectionService;
import com.example.bikecomputerfirstdraft.constants.Constants;
import com.example.bikecomputerfirstdraft.ui.myDevices.MyDevice;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyDevicesRepository {

    private MyDevicesDao deviceDao;
    private LiveData<List<MyDevice>> allDevices;

    private BleConnectionService bleConnectionService;
    private HashMap<String, String> connectionStateHashMap;
    private static MutableLiveData<HashMap> connectionStateHashMapLive;

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
            bleConnectionService.connectDevice("F8:EF:93:1C:EC:DB");
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



    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    public BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connectionState;
            String gattMacAddress;

            final String action = intent.getAction();
            Bundle extras = intent.getBundleExtra(Constants.EXTRA_DATA);
            if(action.equals(Constants.ACTION_GATT_STATE_CHANGE)){
                connectionState = extras.getString(Constants.GATT_CONNECTION_STATE);
                gattMacAddress = extras.getString(Constants.GATT_MAC_ADDRESS);
                getConnectionStateHashMap().put(Constants.GATT_CONNECTION_STATE, connectionState);
                getConnectionStateHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);

                getConnectionStateHashMapLive().postValue(getConnectionStateHashMap());
            }

        }
    };


    public void readCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID){
        bleConnectionService.readCharacteristic
                (deviceMacAddress, serviceUUID, characteristicUUID);
    }

    public void disconnectDevice(){
        bleConnectionService.stopSelf();
    }


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

    /*
    public static MutableLiveData<String> getConnectionState(){
        if (connectionState == null) {
            connectionState = new MutableLiveData<String>("No device connected");
        }
        Log.d(TAG, "getConnectionState " + connectionState.getValue());
        return connectionState;
    }

     */


}
