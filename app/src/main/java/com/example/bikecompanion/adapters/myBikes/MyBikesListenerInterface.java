package com.example.bikecompanion.adapters.myBikes;

import android.view.View;
import android.widget.CheckBox;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.List;

public interface MyBikesListenerInterface {
    void onItemClick(int position, View itemView, List<Bike> bike);

    void onButtonClickRemove(int position, List<Bike> bike);

    void onButtonClickEdit(int position, List<Bike> bike);

    void onCheckBoxClick(int position, List<Device> device, CheckBox checkBox);
}

