package com.example.bikecompanion.sharedClasses;

public class CharacteristicData {
    String characteristicMacAddress;
    String characteristicUUID;
    byte[] characteristicValue;

    public CharacteristicData(String characteristicMacAddress, String characteristicUUID, byte[] characteristicValue) {
        this.characteristicMacAddress = characteristicMacAddress;
        this.characteristicUUID = characteristicUUID;
        this.characteristicValue = characteristicValue;
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
