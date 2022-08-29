package com.example.bikecomputerfirstdraft.ui.scanner;

public class ScanResults {
    private int ImageResource;
    private String TextName;
    private String TextDescription;

    public ScanResults(int imageResource, String textName, String textDescription){
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
