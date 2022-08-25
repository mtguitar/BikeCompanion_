package com.example.bikecomputerfirstdraft.ui.scanner;

public class ScannerItem {
    private int mImageResource;
    private String mTextName;
    private String mTextDescription;

    public ScannerItem(int mImageResource, String mTextName, String mTextDescription){
        mImageResource = mImageResource;
        mTextName = mTextName;
        mTextDescription = mTextDescription;
    }

    public int getmImageResource(){
        return mImageResource;
    }

    public String getmTextName(){
        return mTextName;
    }

    public String getmTextDescription(){
        return mTextDescription;
    }

}
