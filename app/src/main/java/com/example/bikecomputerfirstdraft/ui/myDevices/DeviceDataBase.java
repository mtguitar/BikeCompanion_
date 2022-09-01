package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Device.class, version = 1)
public abstract class DeviceDataBase extends RoomDatabase {


    private static DeviceDataBase instance;

    public abstract DeviceDao deviceDao();

    public static synchronized DeviceDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DeviceDataBase.class, "device_databse")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;

    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{

        DeviceDao deviceDao;

        private PopulateDBAsyncTask(DeviceDataBase db){
            deviceDao = db.deviceDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.insert(new Device("Name 1", "Ble Name 1", "MAC 1", "Type 1"));
            deviceDao.insert(new Device("Name 2", "Ble Name 2", "MAC 2", "Type 2"));
            deviceDao.insert(new Device("Name 3", "Ble Name 3", "MAC 3", "Type 3"));
            deviceDao.insert(new Device("Name 4", "Ble Name 4", "MAC 4", "Type 4"));

            return null;
        }
    }


}
