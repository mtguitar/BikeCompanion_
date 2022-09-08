package com.example.bikecompanion.ble;

public class FormatBleData {

    public byte[] convertStingtoByte(String string) {
        int intFromString = Integer.parseInt(string, 16);
        byte intToByte = (byte) intFromString;
        byte[] byteArray = new byte[1];
        byteArray[0] = (byte) (intFromString);
        return byteArray;
    }


}
