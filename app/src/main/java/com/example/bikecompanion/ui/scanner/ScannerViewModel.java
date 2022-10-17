package com.example.bikecompanion.ui.scanner;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.bikecompanion.ble.BleConnectionService;
import com.example.bikecompanion.ble.BleScannerService;
import com.example.bikecompanion.ble.GattManager;
import com.example.bikecompanion.ble.gattOperations.GattOperation;
import com.example.bikecompanion.databases.EntitiesRepository;
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.sharedClasses.CharacteristicData;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ScannerViewModel extends ViewModel {

    private final static String TAG = "FlareLog ScannerVM";

    private Context context;
    private ConcurrentLinkedQueue<GattOperation> operationQueue;
    private GattOperation pendingOperation;
    private ConcurrentLinkedQueue<CharacteristicData> characteristicQueue;
    private Handler handler;
    private BleScannerService bleScannerService;

    private boolean boundToService;


    public void bindService() {
        if (!boundToService) {
            Intent intent = new Intent(context, BleConnectionService.class);
            context.bindService(intent, serviceConnection, context.BIND_AUTO_CREATE);
        }
    }

    // serviceConnection object to connect to BluetoothLeService
    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleScannerService = ((BleScannerService.LocalBinder) service).getService();
            boundToService = true;
            Log.d(TAG, "Bound to service: " + service + " " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleScannerService = null;
            Log.d(TAG, "Service disconnected");
        }
    };

    public void startScan(ParcelUuid serviceUuids, DeviceType deviceType){
        bleScannerService.startScan(serviceUuids, deviceType);
    }

    public void stopScan(){
        bleScannerService.stopScanning();
    }


//    //Sends intents to BleScannerService
//    private void sendCommandToService(Class serviceClass, String action) {
//        Intent bleServiceIntent = new Intent(requireContext(), serviceClass);
//        bleServiceIntent.setAction(action);
//        if(serviceUuids != null) {
//            bleServiceIntent.putExtra("serviceUuids", serviceUuids);
//        }
//        if(deviceType != null) {
//            bleScannerService.setDeviceType(deviceType);
//        }
//        requireContext().startService(bleServiceIntent);
//        Log.d(TAG, "Sent intent to " + serviceClass + " " + action);
//    }





}