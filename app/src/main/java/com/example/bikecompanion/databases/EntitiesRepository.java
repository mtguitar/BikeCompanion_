package com.example.bikecompanion.databases;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databases.relations.DeviceWithBikes;

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

public class EntitiesRepository {

    private EntitiesDao entitiesDao;

    private LiveData<List<Device>> allDevices;
    private LiveData<List<Bike>> allBikes;

    private BleConnectionService bleConnectionService;

    private HashMap<String, String> connectionStateHashMap;
    private static MutableLiveData<HashMap> connectionStateHashMapLive;
    private MutableLiveData<Boolean> isConnected;

    private HashMap<String, String> deviceDataHashMap;
    private static MutableLiveData<HashMap> deviceDataHashMapLive;


    private boolean boundToService;
    private final static String TAG = "FlareLog Repo";


    private Context context;

    public EntitiesRepository(Application application){
        EntitiesDataBase entitiesDataBase = EntitiesDataBase.getInstance(application);
        entitiesDao = entitiesDataBase.entitiesDao();
        allDevices = entitiesDao.getAllDevices();
        allBikes = entitiesDao.getAllBikes();

        context = application.getApplicationContext();
        registerBroadcastReceiver(context);
    }

    public LiveData<List<BikeWithDevices>> getBikesWithDevices(){
        LiveData<List<BikeWithDevices>> bikesWithDevices = entitiesDao.getBikesWithDevices();
        return bikesWithDevices;

    }

    public LiveData<List<DeviceWithBikes>> getDeviceWithBikes(){
        LiveData<List<DeviceWithBikes>> devicesWithBikes = entitiesDao.getDevicesWithBikes();
        return devicesWithBikes;
    }

    public void insertBikeDeviceCrossRef(BikeDeviceCrossRef bikeDeviceCrossRef)
    {
        new InsertBikeDeviceCrossRefAsyncTask(entitiesDao).execute(bikeDeviceCrossRef);
    }


    public void insertBike(Bike bike){
        new InsertBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void updateBike(Bike bike){
        new UpdateBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void deleteBike(Bike bike){
        new DeleteBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void deleteAllBikes (){
        new DeleteAllDevicesAsyncTask(entitiesDao).execute();
    }

    public LiveData<List<Bike>> getAllBikes(){
        return allBikes;
    }





    public void insertDevice(Device device){
        new InsertDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void updateDevice(Device device){
        new UpdateDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void deleteDevice(Device device){
        new DeleteDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void deleteAllDevices (){
        new DeleteAllDevicesAsyncTask(entitiesDao).execute();
    }

    public LiveData<List<Device>> getAllDevices(){
        return allDevices;
    }







    /**
     * AsyncTasks for writing/reading to/from db to ensure that we are not working on the main thread
     */



    private static class InsertBikeDeviceCrossRefAsyncTask extends android.os.AsyncTask<BikeDeviceCrossRef, Void, Void> {
        private EntitiesDao entitiesDao;

        private InsertBikeDeviceCrossRefAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(BikeDeviceCrossRef... bikeDeviceCrossRef) {
            entitiesDao.insertBikeDeviceCrossRef(bikeDeviceCrossRef[0]);
            return null;
        }
    }

    //Bikes
    private static class InsertBikeAsyncTask extends android.os.AsyncTask<Bike, Void, Void> {
        private EntitiesDao entitiesDao;

        private InsertBikeAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Bike... bikes) {
            entitiesDao.insertBike(bikes[0]);
            return null;
        }
    }




    private static class UpdateBikeAsyncTask extends android.os.AsyncTask<Bike, Void, Void> {
        private EntitiesDao entitiesDao;

        private UpdateBikeAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Bike... bikes) {
            entitiesDao.updateBike(bikes[0]);
            return null;
        }
    }

    private static class DeleteBikeAsyncTask extends android.os.AsyncTask<Bike, Void, Void> {
        private EntitiesDao entitiesDao;

        private DeleteBikeAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Bike... bikes) {
            entitiesDao.deleteBike(bikes[0]);
            return null;
        }
    }

    private static class DeleteAllBikesAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        private EntitiesDao entitiesDao;

        private DeleteAllBikesAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            entitiesDao.deleteAllBikes();
            return null;
        }
    }





    //Devices
    private static class InsertDeviceAsyncTask extends android.os.AsyncTask<Device, Void, Void> {
        private EntitiesDao entitiesDao;

        private InsertDeviceAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            entitiesDao.insertDevice(devices[0]);
            return null;
        }
    }

    private static class UpdateDeviceAsyncTask extends android.os.AsyncTask<Device, Void, Void> {
        private EntitiesDao entitiesDao;

        private UpdateDeviceAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            entitiesDao.updateDevice(devices[0]);
            return null;
        }
    }

    private static class DeleteDeviceAsyncTask extends android.os.AsyncTask<Device, Void, Void> {
        private EntitiesDao entitiesDao;

        private DeleteDeviceAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            entitiesDao.deleteDevice(devices[0]);
            return null;
        }
    }

    private static class DeleteAllDevicesAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        private EntitiesDao entitiesDao;

        private DeleteAllDevicesAsyncTask(EntitiesDao entitiesDao){
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            entitiesDao.deleteAllDevices();
            return null;
        }
    }


    /**
     * methods to interact with BleConnectionService
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
                // this indicates which device's characteristic changed
                getConnectionStateHashMap().put(Constants.GATT_MAC_ADDRESS, gattMacAddress);
                //put connectionState and macAddress into hashmap
                getConnectionStateHashMap().put(gattMacAddress, connectionState);

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


    /**
     * LiveData
     */

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
