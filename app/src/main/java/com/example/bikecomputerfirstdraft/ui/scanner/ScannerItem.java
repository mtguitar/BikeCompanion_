package com.example.bikecomputerfirstdraft.ui.scanner;

public class ScannerItem {
    private int ImageResource;
    private String TextName;
    private String TextDescription;

    public ScannerItem(int imageResource, String textName, String textDescription){
        ImageResource = imageResource;
        TextName = textName;
        TextDescription = textDescription;
    }

    public int getImageResource(){
        return ImageResource;
    }

    public String getTextName(){
        return TextName;
    }

    public String getTextDescription(){
        return TextDescription;
    }

}
