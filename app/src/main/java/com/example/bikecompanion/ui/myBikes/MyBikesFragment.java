package com.example.bikecompanion.ui.myBikes;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bikecompanion.R;
import com.example.bikecompanion.ui.myDevices.MyDevicesFragmentDirections;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyBikesFragment extends Fragment {

    private MyBikesViewModel mViewModel;
    private View view;

    public static MyBikesFragment newInstance() {
        return new MyBikesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_bikes, container, false);
        initFAB();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyBikesViewModel.class);
        // TODO: Use the ViewModel
    }


    private void initFAB(){
        FloatingActionButton fabNewDevice = view.findViewById(R.id.floating_action_button_my_bikes);
        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MyDevicesFragmentDirections.actionNavMyDevicesToNavAddDevice();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
    }
}