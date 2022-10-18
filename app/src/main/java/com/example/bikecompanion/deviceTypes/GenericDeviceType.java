package com.example.bikecompanion.deviceTypes;

import com.example.bikecompanion.R;

import java.util.ArrayList;
import java.util.UUID;

public class GenericDeviceType extends AbstractDeviceType {

    private final static String DEVICE_TYPE = "Unknown";
    private final static int icon = R.drawable.ic_device_type_other_sensor;
    private static ArrayList<UUID> advertisedServiceList;

    /**
     * Advertised service UUIDs
     */
    public final static String STRING_SERVICE_ADVERTISED_1 = "0000180a-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_SERVICE_ADVERTISED_1 = UUID.fromString(STRING_SERVICE_ADVERTISED_1);



    @Override
    public ArrayList<UUID> getAdvertisedServiceList() {
        if (advertisedServiceList == null) {
            advertisedServiceList = new ArrayList<>();
        }
        advertisedServiceList.add(UUID_SERVICE_ADVERTISED_1);
        return advertisedServiceList;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.GENERIC;
    }

    public static int getIcon() {
        return icon;
    }
}
