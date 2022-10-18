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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bikecompanion.ble.BleScannerService;
import com.example.bikecompanion.ble.GattManager;
import com.example.bikecompanion.ble.gattOperations.GattOperation;
import com.example.bikecompanion.databases.EntitiesRepository;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.deviceTypes.DeviceType;
import com.example.bikecompanion.sharedClasses.CharacteristicData;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ScannerViewModel extends AndroidViewModel {

    private final static String TAG = "FlareLog ScannerVM";

    private Context context;
    private EntitiesRepository repository;
    private ConcurrentLinkedQueue<GattOperation> operationQueue;
    private GattOperation pendingOperation;
    private ConcurrentLinkedQueue<CharacteristicData> characteristicQueue;
    private Handler handler;
    private BleScannerService bleScannerService;
    private DeviceType deviceType;
    private ParcelUuid serviceUuids;
    private boolean pendingScanRequest;
    private List<Device> devices;
    private LiveData<List<Device>> allDevices;


    private boolean boundToService;

    public ScannerViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        repository = new EntitiesRepository(application);
        allDevices = repository.getAllDevices();
        bindService();
    }


    public void bindService() {
        if (!boundToService) {
            Intent intent = new Intent(context, BleScannerService.class);
            context.bindService(intent, serviceConnection, context.BIND_AUTO_CREATE);
        }
    }

    // serviceConnection object to connect to BluetoothLeService
    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleScannerService = ((BleScannerService.LocalBinder) service).getService();
            boundToService = true;
            if (pendingScanRequest){
                startScan(serviceUuids, deviceType, devices);
            }
            Log.d(TAG, "Bound to service: " + service + " " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleScannerService = null;
            Log.d(TAG, "Service disconnected");
        }
    };

    public void startScan(ParcelUuid serviceUuids, DeviceType deviceType, List<Device> devices){
        if (boundToService){
            bleScannerService.startScan(serviceUuids, deviceType, devices);
            pendingScanRequest = false;
        }
        else {
            this.devices = devices;
            this.serviceUuids = serviceUuids;
            this.deviceType = deviceType;
        }
    }

    public void stopScan(){
        bleScannerService.stopScanning();
    }



    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }





}