package com.example.bikecompanion.sharedClasses;

import com.example.bikecompanion.deviceTypes.FlareRTDeviceType;
import com.example.bikecompanion.deviceTypes.GenericDeviceType;
import com.example.bikecompanion.deviceTypes.SpeedCadenceDeviceType;

import java.util.ArrayList;

public class CharacteristicData {
    String characteristicMacAddress;
    String characteristicUUID;
    byte[] characteristicValue;
    private static ArrayList<Characteristic> charactersiticList;

    public CharacteristicData(String characteristicMacAddress, String characteristicUUID, byte[] characteristicValue) {
        this.characteristicMacAddress = characteristicMacAddress;
        this.characteristicUUID = characteristicUUID;
        this.characteristicValue = characteristicValue;
    }

    public static ArrayList<Characteristic> getCharacteristicList() {
        if (charactersiticList == null) {
            charactersiticList = new ArrayList<>();
            GenericDeviceType.getCharacteristicList();
            FlareRTDeviceType.getCharacteristicList();
            SpeedCadenceDeviceType.getCharacteristicList();

            charactersiticList.addAll(GenericDeviceType.getCharacteristicList());
            charactersiticList.addAll(FlareRTDeviceType.getCharacteristicList());
            charactersiticList.addAll(SpeedCadenceDeviceType.getCharacteristicList());
        }
        return charactersiticList;

    }


    public String getCharacteristicMacAddress() {
        return characteristicMacAddress;
    }

    public void setCharacteristicMacAddress(String characteristicMacAddress) {
        this.characteristicMacAddress = characteristicMacAddress;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }

    public byte[] getCharacteristicValue() {
        return characteristicValue;
    }

    public void setCharacteristicValue(byte[] characteristicValue) {
        this.characteristicValue = characteristicValue;
    }
}
