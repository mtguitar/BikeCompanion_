package com.example.bikecompanion.sharedClasses;

import java.util.UUID;

public class Characteristic {
    String name;
    UUID serviceUUID;
    UUID characteristicUUID;
    boolean read;
    boolean notify;
    boolean write;

    public Characteristic(String name, UUID serviceUUID, UUID characteristicUUID, boolean read, boolean notify, boolean write) {
        this.name = name;
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.read = read;
        this.notify = notify;
        this.write = write;
    }
}
