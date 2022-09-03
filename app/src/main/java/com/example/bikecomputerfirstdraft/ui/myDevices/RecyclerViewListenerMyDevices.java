package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.view.View;

import java.util.List;

public interface RecyclerViewListenerMyDevices {
    void onItemClick(int position, View constraintLayout);

    void onButtonClick(int position, List<Device> devices);
}
