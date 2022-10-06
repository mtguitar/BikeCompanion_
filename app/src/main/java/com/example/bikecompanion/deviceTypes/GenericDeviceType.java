package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.R;

import java.util.ArrayList;
import java.util.UUID;

public class GenericDeviceType extends AbstractDeviceType {

    private final static String DEVICE_TYPE = "Unknown";
    private final static int icon = R.drawable.ic_device_type_other_sensor;

    @Override
    public ArrayList<UUID> getAdvertisedServiceList() {
        return null;
    }

    @Override
    public DeviceTypeEnum getDeviceType() {
        return null;
    }

    @Override
    public int getIcon() {
        return icon;
    }
}
