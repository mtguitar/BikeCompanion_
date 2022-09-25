package com.example.bikecompanion.ui.sharedViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bikecompanion.ble.GattManager;
import com.example.bikecompanion.databases.EntitiesRepository;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databases.relations.DeviceWithBikes;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedEntitiesViewModel extends AndroidViewModel {

    private final static String TAG = "FlareLog MDViewModel";

    private EntitiesRepository repository;
    private GattManager gattManager;
    private LiveData<List<Device>> allDevices;
    private LiveData<String> connectionState;
    private LiveData<List<Bike>> allBikes;
    private List<Bike> bikeList;
    private List<Device> deviceList;


    public SharedEntitiesViewModel(@NonNull Application application) {
        super(application);
        repository = new EntitiesRepository(application);
        gattManager = GattManager.getInstance(application);
        allDevices = repository.getAllDevices();
        allBikes = repository.getAllBikes();
    }

    public void insert(Bike bike) {
        repository.insertBike(bike);
    }

    public void update(Bike bike) {
        repository.updateBike(bike);
    }

    public void delete(Bike bike) {
        repository.deleteBike(bike);
    }

    public void deleteAllBikes() {
        repository.deleteAllBikes();
    }

    public LiveData<List<Bike>> getAllBikes() {
        return allBikes;
    }

    public List<Bike> getBikeList() {
        return bikeList;
    }

    public LiveData<List<BikeWithDevices>> getBikeWithDevices() {
        return repository.getBikesWithDevices();
    }

    public LiveData<List<DeviceWithBikes>> getDeviceWithBikes() {
        return repository.getDeviceWithBikes();
    }


    public void insert(BikeDeviceCrossRef bikeDeviceCrossRef) {
        repository.insertBikeDeviceCrossRef(bikeDeviceCrossRef);
    }


    public void insert(Device device) {
        repository.insertDevice(device);
    }

    public void update(Device device) {
        repository.updateDevice(device);
    }

    public void delete(Device device) {
        repository.deleteDevice(device);
    }

    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }


    /**
     * BleConnection methods
     */

    public void bindService() {
        gattManager.bindService();
    }

    public void connectDevice(String deviceMacAddress) {
        gattManager.connectDevice(deviceMacAddress);
    }

    public void discoverServices(String deviceMacAddress) {
        gattManager.discoverServices(deviceMacAddress);
    }

    public void disconnectDevice(String deviceMacAddress) {
        gattManager.disconnectDevice(deviceMacAddress);
    }

    public void readCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID) {
        gattManager.readCharacteristic(deviceMacAddress, serviceUUID, characteristicUUID);

    }

    public void writeCharacteristics(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID, byte[] payload) {
        gattManager.writeCharacteristic(deviceMacAddress, serviceUUID, characteristicUUID, payload);

    }

    public void setCharacteristicNotification(String deviceMacAddress, UUID serviceUUID, UUID characteristicUUID, boolean enabled) {
        gattManager.setCharacteristicNotification(deviceMacAddress, serviceUUID, characteristicUUID, enabled);
    }


    /**
     * LiveData getters
     */

    public LiveData<HashMap> getConnectionStateHashMapLive() {
        return gattManager.getConnectionStateHashMapLive();
    }


    public LiveData<ConcurrentLinkedQueue> getCharacteristicQueueLive(){
        return gattManager.getCharacteristicQueueLive();
    }


}