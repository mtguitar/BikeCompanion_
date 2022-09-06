package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bikecomputerfirstdraft.databases.MyDevicesRepository;

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

    public void sendCommandToService (Class serviceClass, String action, Bundle extras) {
        repository.sendCommandToService(serviceClass, action, extras);
    }

    public void bindService(){
        repository.bindService();
    }

    public void readCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID){
        repository.readCharacteristics(deviceMacAddress, serviceUUID, characteristicUUID);

    }

    public MutableLiveData<HashMap> getConnectionStateHashMapLive (){
        return repository.getConnectionStateHashMapLive();
    }


}