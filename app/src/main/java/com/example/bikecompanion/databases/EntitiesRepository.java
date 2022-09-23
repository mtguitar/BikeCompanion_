package com.example.bikecompanion.databases;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databases.relations.DeviceWithBikes;

import java.util.List;

/**
 * This repo holds all the data used to populate MyDevicesFragment.
 * It puts the data in LiveData.
 * It manages a database (using Room) of ble devices the user has added to myDevices.
 * Fragments interact with this repo through viewModels.
 */

public class EntitiesRepository {

    private final static String TAG = "FlareLog Repo";

    private EntitiesDao entitiesDao;
    private LiveData<List<Device>> allDevices;
    private LiveData<List<Bike>> allBikes;
    private Context context;

    public EntitiesRepository(Application application) {
        EntitiesDataBase entitiesDataBase = EntitiesDataBase.getInstance(application);
        entitiesDao = entitiesDataBase.entitiesDao();
        allDevices = entitiesDao.getAllDevices();
        allBikes = entitiesDao.getAllBikes();
        context = application.getApplicationContext();
    }


    /**
     * LiveData
     */

    public LiveData<List<BikeWithDevices>> getBikesWithDevices() {
        LiveData<List<BikeWithDevices>> bikesWithDevices = entitiesDao.getBikesWithDevices();
        return bikesWithDevices;
    }

    public LiveData<List<DeviceWithBikes>> getDeviceWithBikes() {
        LiveData<List<DeviceWithBikes>> devicesWithBikes = entitiesDao.getDevicesWithBikes();
        return devicesWithBikes;
    }

    /**
     * Methods to interact with entitiesDao (which interacts with database)
     */

    public void insertBikeDeviceCrossRef(BikeDeviceCrossRef bikeDeviceCrossRef) {
        new InsertBikeDeviceCrossRefAsyncTask(entitiesDao).execute(bikeDeviceCrossRef);
    }

    public void insertBike(Bike bike) {
        new InsertBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void updateBike(Bike bike) {
        new UpdateBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void deleteBike(Bike bike) {
        new DeleteBikeAsyncTask(entitiesDao).execute(bike);
    }

    public void deleteAllBikes() {
        new DeleteAllDevicesAsyncTask(entitiesDao).execute();
    }

    public LiveData<List<Bike>> getAllBikes() {
        return allBikes;
    }

    public void insertDevice(Device device) {
        new InsertDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void updateDevice(Device device) {
        new UpdateDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void deleteDevice(Device device) {
        new DeleteDeviceAsyncTask(entitiesDao).execute(device);
    }

    public void deleteAllDevices() {
        new DeleteAllDevicesAsyncTask(entitiesDao).execute();
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }


    /**
     * AsyncTasks for writing/reading to/from db to ensure that we are not working on the main thread
     */

    private static class InsertBikeDeviceCrossRefAsyncTask extends android.os.AsyncTask<BikeDeviceCrossRef, Void, Void> {
        private EntitiesDao entitiesDao;

        private InsertBikeDeviceCrossRefAsyncTask(EntitiesDao entitiesDao) {
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

        private InsertBikeAsyncTask(EntitiesDao entitiesDao) {
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

        private UpdateBikeAsyncTask(EntitiesDao entitiesDao) {
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

        private DeleteBikeAsyncTask(EntitiesDao entitiesDao) {
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

        private DeleteAllBikesAsyncTask(EntitiesDao entitiesDao) {
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

        private InsertDeviceAsyncTask(EntitiesDao entitiesDao) {
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

        private UpdateDeviceAsyncTask(EntitiesDao entitiesDao) {
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

        private DeleteDeviceAsyncTask(EntitiesDao entitiesDao) {
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

        private DeleteAllDevicesAsyncTask(EntitiesDao entitiesDao) {
            this.entitiesDao = entitiesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            entitiesDao.deleteAllDevices();
            return null;
        }
    }

}
