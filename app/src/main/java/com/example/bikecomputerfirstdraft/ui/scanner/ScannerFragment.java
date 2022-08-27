package com.example.bikecomputerfirstdraft.ui.scanner;

import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import java.util.UUID;

public class ScannerFragment extends Fragment {

    private static final String TAG = "FlareLog";
    private ScannerViewModel mViewModel;

    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        ParcelUuid serviceUUID= new ParcelUuid(UUID.fromString("71262000-3692-ae93-e711-472ba41689c9"));

        BleScanner bleScanner = new BleScanner(context, view, null, null, null);


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        // TODO: Use the ViewModel

    }







}