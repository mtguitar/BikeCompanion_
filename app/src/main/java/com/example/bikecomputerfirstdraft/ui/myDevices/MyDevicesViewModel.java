package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bikecomputerfirstdraft.databases.MyDevicesRepository;

import java.util.List;

public class MyDevicesViewModel extends AndroidViewModel {

    private MyDevicesRepository repository;
    private LiveData<List<MyDevice>> allDevices;

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


}