package com.example.bikecompanion.unused;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.databinding.FragmentSlideshowBinding;
import com.example.bikecompanion.ui.myDevices.SharedEntitiesViewModel;

public class OLDMyDevicesFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private SharedEntitiesViewModel myDevicesViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedEntitiesViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SharedEntitiesViewModel.class);

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