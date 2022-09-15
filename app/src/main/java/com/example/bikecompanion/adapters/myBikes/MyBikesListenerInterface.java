package com.example.bikecompanion.adapters.myBikes;

import android.view.View;

import com.example.bikecompanion.databases.entities.Bike;

import java.util.List;

public interface MyBikesListenerInterface {
    void onItemClick(int position, View itemView, List<Bike> bike);

    void onButtonClickAdd(int position, List<Bike> bike);

    void onButtonClickDisconnect(int position, List<Bike> bike);
}
