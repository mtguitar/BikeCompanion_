package com.example.bikecomputerfirstdraft.devices;

public class AbstractBleDevice {


    public static class BleDevice {
        String assignedName;
        String bleName;
        String macAddress;
        String deviceType;

        public BleDevice(String assignedName, String bleName, String macAddress, String deviceType) {
            this.assignedName = assignedName;
            this.macAddress = macAddress;
            this.deviceType = deviceType;
            this.bleName = bleName;

        }

    }

    BleDevice flareRT = new BleDevice("FlareRT", "flare", "9389", "light");
    String name = flareRT.assignedName;
}
