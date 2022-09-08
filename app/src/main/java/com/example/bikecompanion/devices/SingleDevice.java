package com.example.bikecompanion.devices;

import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.UUID;

public class SingleDevice {
    public String assignedName;
    public String bleName;
    public String macAddress;
    public String deviceType;
    public ParcelUuid serviceUuid;

    public ArrayList<Characteristic> characteristics;
    

    private class Characteristic{
        String charName;
        UUID serviceUUID;
        UUID charUUID;
        private Characteristic (String charName, UUID serviceUUID, UUID charUUID){
            this.charName = charName;
            this.serviceUUID = serviceUUID;
            this.charUUID = charUUID;
        }
    }


}




