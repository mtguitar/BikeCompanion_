package com.example.bikecomputerfirstdraft.ui.myDevices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyDevicesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyDevicesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}