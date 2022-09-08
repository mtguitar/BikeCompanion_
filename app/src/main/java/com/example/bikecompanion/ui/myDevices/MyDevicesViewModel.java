package com.example.bikecompanion.ui.myDevices;

import android.app.Application;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bikecompanion.databases.MyDevicesRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyDevicesViewModel extends AndroidViewModel {

    private final static String TAG = "FlareLog MDViewModel";

    private MyDevicesRepository repository;
    private LiveData<List<MyDevice>> allDevices;
    private LiveData<String> connectionState;




    public MyDevicesViewModel(@NonNull Application application) {
        super(application);
        repository = new MyDevicesRepository(application);
        allDevices = repository.getAllDevices();

    }

    public void insert(MyDevice device){
        repository.insert(device);
    }

    public void update(MyDevice device){
        repository.update(device);
    }

    public void delete(MyDevice device){
        repository.delete(device);
    }

    public void deleteAllNotes(){
        repository.deleteAllDevices();
    }

    public LiveData<List<MyDevice>> getAllDevices(){
        return allDevices;
    }


    /**
     * BleConnection methods
     *
     */


/*
    public void sendCommandToService (Class serviceClass, String action, Bundle extras) {
        repository.sendCommandToService(serviceClass, action, extras);
    }

 */

    public void bindService(){
        repository.bindService();
    }

    public void connectDevice(String deviceMacAddress){
        repository.connectDevice(deviceMacAddress);
    }

    public void disconnectDevice(String deviceMacAddress){
        repository.disconnectDevice(deviceMacAddress);
    }

    public void readCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID){
        repository.readCharacteristic(deviceMacAddress, serviceUUID, characteristicUUID);

    }

    public void writeCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID, byte[] payload){
        repository.writeCharacteristic(deviceMacAddress, serviceUUID, characteristicUUID, payload);

    }

    public void setCharacteristicNotification(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID, boolean enabled){
        repository.setCharacteristicNotification(deviceMacAddress, serviceUUID, characteristicUUID, enabled);

    }


    /**
     * LiveData getters
     */

    public LiveData<HashMap> getConnectionStateHashMapLive(){
        return repository.getConnectionStateHashMapLive();
    }

    public LiveData<Boolean> getIsConnected(){
        return repository.getIsConnected();
    }

    public LiveData<HashMap> getDeviceDataHashMapLive(){
        return repository.getDeviceDataHashMapLive();
    }





}