package com.example.bikecomputerfirstdraft.ble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

public class RegisterBroadcastReceiver {

    public RegisterBroadcastReceiver(Context context, BroadcastReceiver broadcastReceiver, String[] actionFilters){
        final IntentFilter intentFilter = new IntentFilter();
        for (String filter : actionFilters) {
            intentFilter.addAction(filter);
        }
        Log.d("FlareLog", "Intent filters with new class!");

        context.registerReceiver(broadcastReceiver, intentFilter);
        Log.d("FlareLog", "Registered with new class!");
    }



}
