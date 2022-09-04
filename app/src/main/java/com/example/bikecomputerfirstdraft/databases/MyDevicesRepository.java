package com.example.bikecomputerfirstdraft.databases;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.bikecomputerfirstdraft.ui.myDevices.MyDevice;

import java.util.List;

public class MyDevicesRepository {

    private MyDevicesDao deviceDao;
    private LiveData<List<MyDevice>> allDevices;

    public MyDevicesRepository(Application application){
        MyDevicesDataBase deviceDataBase = MyDevicesDataBase.getInstance(application);
        deviceDao = deviceDataBase.deviceDao();
        allDevices = deviceDao.getAllDevices();
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







}
