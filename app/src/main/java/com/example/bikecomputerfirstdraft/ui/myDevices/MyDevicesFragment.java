package com.example.bikecomputerfirstdraft.ui.myDevices;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.databinding.FragmentSlideshowBinding;

import java.util.List;

public class MyDevicesFragment extends Fragment {

    private MyDevicesViewModel myDevicesViewModel;

    public static MyDevicesFragment newInstance() {
        return new MyDevicesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






        myDevicesViewModel = new ViewModelProvider(this).get(MyDevicesViewModel.class);




    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_devices, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        DeviceAdapter deviceAdapter = new DeviceAdapter();
        recyclerView.setAdapter(deviceAdapter);

        myDevicesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {
                deviceAdapter.setDevices(devices);
            }
        });

        return view;
    }



}