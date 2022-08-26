package com.example.bikecomputerfirstdraft.devices;

import java.util.UUID;

public class BleDevice {
    String assignedName;
    String bleName;
    String macAddress;
    String deviceType;


    public BleDevice(String assignedName, String bleName, String macAddress, String deviceType){
        this.assignedName = assignedName;
        this.macAddress = macAddress;
        this.deviceType = deviceType;
        this.bleName = bleName;
        }

    // Devices
    BleDevice flareOne = new BleDevice(
            "Flare One",
            "Flare RT",
            "F8:EF:93:1C:EC:DB",
            "light");

    BleDevice flareTwo = new BleDevice(
            "Flare Two",
            "Flare RT",
            "F5:95:D9:24:C7:3A",
            "light");

    // Flare MAC Addresses
    public final static String AC_FLARE_MAC_ADDRESS = "F8:EF:93:1C:EC:DB";
    public final static String AVENTON_FLARE_MAC_ADDRESS = "F8:EF:93:1C:EC:DB";

    // Light mode UUIDs
    public final static UUID UUID_SERVICE_LIGHT_MODE = UUID.fromString("71261000-3692-ae93-e711-472ba41689c9");
    public final static UUID UUID_CHARACTERISTIC_LIGHT_MODE = UUID.fromString("71261001-3692-ae93-e711-472ba41689c9");

    // Battery UUIDs
    public final static UUID UUID_SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_CHARACTERISTIC_BATTERY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


}
