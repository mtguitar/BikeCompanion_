package com.example.bikecompanion.ui.settingsDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikecompanion.databinding.FragmentGalleryBinding;

public class SettingsFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);


        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView textView = binding.textGallery;

        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}