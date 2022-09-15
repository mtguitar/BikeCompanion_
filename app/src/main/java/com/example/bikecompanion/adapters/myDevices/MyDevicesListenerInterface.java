package com.example.bikecompanion.adapters.myDevices;

import android.view.View;

import com.example.bikecompanion.databases.entities.Device;

import java.util.List;

public interface MyDevicesListenerInterface {
    void onItemClick(int position, View itemView, List<Device> devices);

    void onButtonClickRemove(int position, List<Device> devices);

    void onButtonClickDisconnect(int position, List<Device> devices);
}
