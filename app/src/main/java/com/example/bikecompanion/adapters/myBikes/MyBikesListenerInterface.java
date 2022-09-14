package com.example.bikecompanion.adapters.myBikes;

import android.view.View;

import com.example.bikecompanion.databases.bike.MyBike;
import com.example.bikecompanion.databases.devices.MyDevice;

import java.util.List;

public interface MyBikesListenerInterface {
    void onItemClick(int position, View itemView, List<MyBike> bike);

    void onButtonClickAdd(int position, List<MyBike> bike);

    void onButtonClickDisconnect(int position, List<MyBike> bike);
}
