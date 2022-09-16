package com.example.bikecompanion.databases.bikeUnused;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.example.bikecompanion.databases.entities.Bike;

import java.util.List;

/**
 * This repo holds all the data for each bike .
 * It puts the data in LiveData.
 * It manages a database (using Room) of bikes the user has added.

 */

public class MyBikesRepository {

    private MyBikesDao myBikesDao;
    private LiveData<List<Bike>> allBikes;

    private final static String TAG = "FlareLog Repo";


    private Context context;

    public MyBikesRepository(Application application){
        MyBikesDataBase myBikesDataBase = MyBikesDataBase.getInstance(application);
        myBikesDao = myBikesDataBase.myBikesDao();
        allBikes = myBikesDao.getAllBikes();

        context = application.getApplicationContext();
    }

    public void insert (Bike bike){
        new InsertBikeAsyncTask(myBikesDao).execute(bike);
    }

    public void update (Bike bike){
        new UpdateBikeAsyncTask(myBikesDao).execute(bike);
    }

    public void delete (Bike bike){
        new DeleteBikeAsyncTask(myBikesDao).execute(bike);

    }

    public void deleteAllBikes (){
        new DeleteAllBikesAsyncTask(myBikesDao).execute();

    }

    public LiveData<List<Bike>> getAllBikes(){
        return allBikes;
    }

    //AsyncTasks to ensure that we are not working on the main thread
    private static class InsertBikeAsyncTask extends AsyncTask<Bike, Void, Void>{
        private MyBikesDao myBikesDao;

        private InsertBikeAsyncTask(MyBikesDao myBikeDao){
            this.myBikesDao = myBikeDao;
        }

        @Override
        protected Void doInBackground(Bike... bike) {
            myBikesDao.insert(bike[0]);
            return null;
        }
    }

    private static class UpdateBikeAsyncTask extends AsyncTask<Bike, Void, Void>{
        private MyBikesDao myBikesDao;

        private UpdateBikeAsyncTask(MyBikesDao myBikeDao){
            this.myBikesDao = myBikesDao;
        }

        @Override
        protected Void doInBackground(Bike... bike) {
            myBikesDao.update(bike[0]);
            return null;
        }
    }

    private static class DeleteBikeAsyncTask extends AsyncTask<Bike, Void, Void>{
        private MyBikesDao myBikesDao;

        private DeleteBikeAsyncTask(MyBikesDao myBikesDao){
            this.myBikesDao = myBikesDao;
        }

        @Override
        protected Void doInBackground(Bike... bike) {
            myBikesDao.delete(bike[0]);
            return null;
        }
    }

    private static class DeleteAllBikesAsyncTask extends AsyncTask<Void, Void, Void> {
        private MyBikesDao myBikesDao;

        private DeleteAllBikesAsyncTask(MyBikesDao myBikesDao){
            this.myBikesDao = myBikesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            myBikesDao.deleteAllBikes();
            return null;
        }
    }



    /*
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

     */



}
