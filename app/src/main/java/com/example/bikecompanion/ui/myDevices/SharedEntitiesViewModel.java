package com.example.bikecompanion.ui.myDevices;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.EntitiesRepository;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.relations.BikeWithDevices;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SharedEntitiesViewModel extends AndroidViewModel {

    private final static String TAG = "FlareLog MDViewModel";

    private EntitiesRepository repository;
    private LiveData<List<Device>> allDevices;
    private LiveData<String> connectionState;
    private LiveData<List<Bike>> allBikes;
    private List<Bike> bikeList;
    private List<Device> deviceList;




    public SharedEntitiesViewModel(@NonNull Application application) {
        super(application);
        repository = new EntitiesRepository(application);
        allDevices = repository.getAllDevices();
        allBikes = repository.getAllBikes();

    }

    public void insert(Bike bike){
        repository.insertBike(bike);
    }

    public void update(Bike bike){
        repository.updateBike(bike);
    }

    public void delete(Bike bike){
        repository.deleteBike(bike);
    }

    public void deleteAllBikes(){
        repository.deleteAllBikes();
    }

    public LiveData<List<Bike>> getAllBikes(){
        return allBikes;
    }

    public List<Bike> getBikeList(){
        return bikeList;
    }

    public LiveData<List<BikeWithDevices>> getBikesWithDevices(){
        return repository.getBikesWithDevices();
    }


    public void insert (BikeDeviceCrossRef bikeDeviceCrossRef){
        repository.insertBikeDeviceCrossRef(bikeDeviceCrossRef);
    }






    public void insert(Device device){
        repository.insertDevice(device);
    }

    public void update(Device device){
        repository.updateDevice(device);
    }

    public void delete(Device device){
        repository.deleteDevice(device);
    }

    public void deleteAllDevices(){
        repository.deleteAllDevices();
    }

    public LiveData<List<Device>> getAllDevices(){
        return allDevices;
    }

    public List<Device> getDeviceList(){
        deviceList = repository.getDeviceList();
        return deviceList;
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