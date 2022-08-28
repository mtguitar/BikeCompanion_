package com.example.bikecomputerfirstdraft.ui.addSensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.bikecomputerfirstdraft.R;

public class AddSensorFragment extends Fragment {

    private AddSensorViewModel mViewModel;


    public static AddSensorFragment newInstance() {
        return new AddSensorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_sensor, container, false);

        Button buttonSelectFlare = view.findViewById(R.id.buttonSelectFlare);
        Button buttonSelectSpeed = view.findViewById(R.id.buttonSelectSpeed);
        Button buttonSelectOther = view.findViewById(R.id.buttonSelectOther);

        buttonSelectFlare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });
        buttonSelectSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });
        buttonSelectOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = AddSensorFragmentDirections.actionNavAddSensorToNavScanner();
                Navigation.findNavController(view).navigate(action);
            }
        });

        return view;




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddSensorViewModel.class);
        // TODO: Use the ViewModel
    }



}