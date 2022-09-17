package com.example.bikecompanion.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;

@Database(entities = {Device.class, Bike.class, BikeDeviceCrossRef.class}, version = 1, exportSchema = false)
public abstract class EntitiesDataBase extends RoomDatabase {

    private static EntitiesDataBase instance;

    public abstract EntitiesDao entitiesDao();

    public static synchronized EntitiesDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    EntitiesDataBase.class,
                    "entities_database")
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

        EntitiesDao entitiesDao;

        private PopulateDBAsyncTask(EntitiesDataBase db){
            entitiesDao = db.entitiesDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }


}
