package com.example.bikecompanion.adapters.myBikes;

import android.view.View;

import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.List;

public interface SelectDeviceListenerInterface {
    void onItemClick(int position, View itemView, List<Device> device);

    void onCheckBoxClick(int position, List<Device> device);

}

