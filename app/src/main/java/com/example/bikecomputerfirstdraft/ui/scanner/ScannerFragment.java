package com.example.bikecomputerfirstdraft.ui.scanner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ble.BleScanner;

public class ScannerFragment extends Fragment {

    private static final String TAG = "FlareLog";
    private ScannerViewModel mViewModel;

    private RecyclerView mRecyclerView;
    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //scannerList = new ArrayList<>();
        Context context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        BleScanner bleScanner = new BleScanner(context, view);






        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        // TODO: Use the ViewModel

    }


/*
    public void addDevice(String name, String description){
        scannerList.add(new ScannerItem(R.drawable.ic_flare, "FlareRT", "bike light"));
        scannerList.add(new ScannerItem(R.drawable.ic_cadence, "Wahoo Sensor", "cadence sensor"));
        scannerList.add(new ScannerItem(R.drawable.ic_speed, "Random Sensor", "speed sensor"));
        scannerList.add(new ScannerItem(R.drawable.ic_flare, name, description));

    }

 */





}