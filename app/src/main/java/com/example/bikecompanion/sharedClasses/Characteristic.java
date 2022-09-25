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

    public String getName() {
        return name;
    }

    public UUID getServiceUUID() {
        return serviceUUID;
    }

    public UUID getCharacteristicUUID() {
        return characteristicUUID;
    }

    public boolean isReadable() {
        return read;
    }

    public boolean isNotify() {
        return notify;
    }

    public boolean isWriteable() {
        return write;
    }
}
