package com.example.bikecompanion.databases.bikeUnused;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.bikecompanion.databases.entities.Bike;


@Database(entities = Bike.class, version = 1)
public abstract class MyBikesDataBase extends RoomDatabase {

    private static MyBikesDataBase instance;

    public abstract MyBikesDao myBikesDao();

    public static synchronized MyBikesDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MyBikesDataBase.class, "bikes_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;

    }

    private static Callback roomCallback = new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void>{

        MyBikesDao myBikesDao;

        private PopulateDBAsyncTask(MyBikesDataBase db){
            myBikesDao = db.myBikesDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }


}
