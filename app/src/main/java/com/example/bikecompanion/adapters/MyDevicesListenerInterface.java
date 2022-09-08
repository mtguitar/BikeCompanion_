package com.example.bikecompanion.adapters;

import android.view.View;

import com.example.bikecompanion.ui.myDevices.MyDevice;

import java.util.List;

public interface MyDevicesListenerInterface {
    void onItemClick(int position, View itemView, List<MyDevice> devices);

    void onButtonClickRemove(int position, List<MyDevice> devices);

    void onButtonClickDisconnect(int position, List<MyDevice> devices);
}
