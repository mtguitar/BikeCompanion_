package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MyDevicesViewModel extends AndroidViewModel {

    private DeviceRepository repository;
    private LiveData<List<Device>> allDevices;

    public MyDevicesViewModel(@NonNull Application application) {
        super(application);
        repository = new DeviceRepository(application);
        allDevices = repository.getAllDevices();
    }

    public void insert(Device device){
        repository.insert(device);
    }

    public void update(Device device){
        repository.update(device);
    }

    public void delete(Device device){
        repository.delete(device);
    }

    public void deleteAllNotes(){
        repository.deleteAllDevices();
    }

    public LiveData<List<Device>> getAllDevices(){
        return allDevices;
    }


}