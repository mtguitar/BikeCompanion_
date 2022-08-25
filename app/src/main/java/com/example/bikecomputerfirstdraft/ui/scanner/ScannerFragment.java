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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;

import java.util.ArrayList;

public class ScannerFragment extends Fragment {

    private ScannerViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    View root;
    Context context;




    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ArrayList<ScannerItem> scannerList = new ArrayList<>();

        scannerList.add(new ScannerItem(R.drawable.ic_flare, "FlareRT", "bike light"));
        scannerList.add(new ScannerItem(R.drawable.ic_cadence, "Wahoo Sensor", "cadence sensor"));
        scannerList.add(new ScannerItem(R.drawable.ic_speed, "Random Sensor", "speed sensor"));

        root = inflater.inflate(R.layout.fragment_scanner, container, false);
        context = root.getContext();

        mRecyclerView = root.findViewById(R.id.recyclerViewScanner);
        mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new ScannerAdapter(scannerList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        // TODO: Use the ViewModel
    }

}