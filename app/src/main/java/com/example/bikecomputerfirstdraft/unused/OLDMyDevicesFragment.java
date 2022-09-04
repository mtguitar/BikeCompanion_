package com.example.bikecomputerfirstdraft.unused;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecomputerfirstdraft.databinding.FragmentSlideshowBinding;
import com.example.bikecomputerfirstdraft.ui.myDevices.MyDevicesViewModel;

public class OLDMyDevicesFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private MyDevicesViewModel myDevicesViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyDevicesViewModel slideshowViewModel =
                new ViewModelProvider(this).get(MyDevicesViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


       // myDevicesViewModel = ViewModelProvider.of(this).get(OLDMyDevicesViewModel.class);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}