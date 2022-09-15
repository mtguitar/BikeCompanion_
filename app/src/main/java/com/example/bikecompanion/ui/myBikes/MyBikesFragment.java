package com.example.bikecompanion.ui.myBikes;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bikecompanion.R;
import com.example.bikecompanion.adapters.myBikes.MyBikesAdapter;
import com.example.bikecompanion.adapters.myBikes.MyBikesListenerInterface;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.sharedClasses.AlertFragment;
import com.example.bikecompanion.ui.activities.MainActivity;
import com.example.bikecompanion.ui.myDevices.SharedEntitiesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyBikesFragment extends Fragment implements MyBikesListenerInterface {

    private final static String TAG = "FlareLog BikeFrag";
    private MyBikesViewModel mViewModel;
    private View view;
    private Button buttonAddBike;
    private Button buttonAddBikeCancel;
    private EditText editTextBikeName;
    private MyBikesAdapter bikeAdapter;
    private View cardViewAddBike;

    private SharedEntitiesViewModel sharedEntitiesViewModel;

    public static MyBikesFragment newInstance() {
        return new MyBikesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        sharedEntitiesViewModel = new ViewModelProvider(this).get(SharedEntitiesViewModel.class);
        view = inflater.inflate(R.layout.fragment_my_bikes, container, false);

        initViews();
        initOnClickListeners();
        initRecyclerViewer();
        initObservers();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyBikesViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initViews(){
        buttonAddBike = view.findViewById(R.id.button_add_bike);
        buttonAddBikeCancel = view.findViewById(R.id.button_add_bike_cancel);
        editTextBikeName = view.findViewById(R.id.edit_text_bike_name);
        cardViewAddBike = view.findViewById(R.id.card_view_add_bike);



    }

    private void initObservers() {


        sharedEntitiesViewModel.getAllBikes().observe(getViewLifecycleOwner(), new Observer<List<Bike>>() {
            @Override
            public void onChanged(List<Bike> bikes) {
                Log.d(TAG, "Received bike live data");
                bikeAdapter.setBikes(bikes);
            }

        });
    }

    private void initOnClickListeners(){
        //FAB
        FloatingActionButton fabNewDevice = view.findViewById(R.id.fac_add_bike);
        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAddBike.setVisibility(View.VISIBLE);

            }
        });

        buttonAddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bikeName = String.valueOf(editTextBikeName.getText());
                if (!bikeName.equals("")){
                    Bike bike = new Bike(bikeName);
                    sharedEntitiesViewModel.insert(bike);
                    cardViewAddBike.setVisibility(View.GONE);
                }
                else{
                    return;
                }


            }
        });

        buttonAddBikeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewAddBike.setVisibility(View.GONE);
            }
        });
    }

    private void initRecyclerViewer(){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_bikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        bikeAdapter = new MyBikesAdapter(this);
        recyclerView.setAdapter(bikeAdapter);
    }


    @Override
    public void onItemClick(int position, View itemView, List<Bike> bike) {

    }

    @Override
    public void onButtonClickRemove(int position, List<Bike> bike) {
        Bike currentBike = bike.get(position);
        sharedEntitiesViewModel.delete(currentBike);

    }

    @Override
    public void onButtonClickEdit(int position, List<Bike> bike) {


    }
}