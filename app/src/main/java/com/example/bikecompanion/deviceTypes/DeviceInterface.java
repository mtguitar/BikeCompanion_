package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.R;
import com.example.bikecompanion.sharedClasses.Characteristic;

import java.util.ArrayList;
import java.util.UUID;

public abstract class DeviceInterface {
    public String DEVICE_TYPE;
    public int icon;
    public ArrayList<UUID> advertisedServiceArrayList;
    public ArrayList<Characteristic> characteristicArrayList;


}
