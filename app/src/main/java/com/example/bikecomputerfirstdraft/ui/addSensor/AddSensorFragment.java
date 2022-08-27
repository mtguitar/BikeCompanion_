package com.example.bikecomputerfirstdraft.ui.addSensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecomputerfirstdraft.R;

public class AddSensorFragment extends Fragment {

    private AddSensorViewModel mViewModel;

    public static AddSensorFragment newInstance() {
        return new AddSensorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_sensor, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddSensorViewModel.class);
        // TODO: Use the ViewModel
    }

}