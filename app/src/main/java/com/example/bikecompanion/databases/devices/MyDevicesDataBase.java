package com.example.bikecompanion.databases.devices;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = MyDevice.class, version = 1)
public abstract class MyDevicesDataBase extends RoomDatabase {

    private static MyDevicesDataBase instance;

    public abstract MyDevicesDao deviceDao();

    public static synchronized MyDevicesDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MyDevicesDataBase.class, "device_database")
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

        MyDevicesDao deviceDao;

        private PopulateDBAsyncTask(MyDevicesDataBase db){
            deviceDao = db.deviceDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }


}
