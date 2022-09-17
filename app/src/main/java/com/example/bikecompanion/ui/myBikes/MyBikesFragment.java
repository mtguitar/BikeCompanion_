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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikecompanion.R;
import com.example.bikecompanion.adapters.myBikes.MyBikesAdapter;
import com.example.bikecompanion.adapters.myBikes.MyBikesListenerInterface;
import com.example.bikecompanion.adapters.myBikes.SelectDeviceAdapter;
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
    private EditText editTextBikeMake;
    private EditText editTextBikeModel;
    private TextView textViewBikeDevices;

    private MyBikesAdapter bikeAdapter;
    private SelectDeviceAdapter selectDeviceAdapter;
    private View cardViewAddBike;



    private ArrayList<Integer> checkedDevices;

    private List<Device> deviceList;

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
        editTextBikeMake = view.findViewById(R.id.edit_text_bike_make);
        editTextBikeModel = view.findViewById(R.id.edit_text_bike_model);
        cardViewAddBike = view.findViewById(R.id.card_view_add_bike);
        textViewBikeDevices = view.findViewById(R.id.text_view_bike_devices);





    }

    private void initObservers() {
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {

                Log.d(TAG, "Received devices live data ");
                if (devices != null){
                    selectDeviceAdapter.setCheckBoxes(devices);
                }

            }

        });

        sharedEntitiesViewModel.getAllBikes().observe(getViewLifecycleOwner(), new Observer<List<Bike>>() {
            @Override
            public void onChanged(List<Bike> bikes) {
                Log.d(TAG, "Received bikes live data ");
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
                String bikeMake = String.valueOf(editTextBikeMake.getText());
                String bikeModel = String.valueOf(editTextBikeModel.getText());

                if (!bikeName.equals("")){
                    Bike bike = new Bike(bikeName, bikeMake, bikeModel);
                    sharedEntitiesViewModel.insert(bike);
                    //TODO: add devices


                    cardViewAddBike.setVisibility(View.GONE);
                }
                else{
                    return;
                }
                editTextBikeName.getText().clear();
                editTextBikeMake.getText().clear();
                editTextBikeModel.getText().clear();


            }
        });

        //Add bike to db
        buttonAddBikeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewAddBike.setVisibility(View.GONE);
                editTextBikeName.getText().clear();
            }
        });
    }

    private void initRecyclerViewer(){
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_bikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        bikeAdapter = new MyBikesAdapter(this);
        recyclerView.setAdapter(bikeAdapter);

        RecyclerView recyclerViewSelectDevice = view.findViewById(R.id.recycler_view_select_device);
        recyclerViewSelectDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectDeviceAdapter = new SelectDeviceAdapter(this);
        recyclerViewSelectDevice.setAdapter(selectDeviceAdapter);

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
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose devices to add to bike");
        // add a checkbox list
        if (deviceList == null) {
            return;
        }
        Log.d(TAG, String.valueOf(deviceList.size()));
        String[] deviceNames = new String[2];

        for (int i = 0; i < deviceList.size(); i++)
        {
            deviceNames[i] = deviceList.get(i).getDeviceBleName() + " " + "(" + deviceList.get(i).getDeviceMacAddress() + ")";
        }


        boolean[] checkedItems = {true, true};
        builder.setMultiChoiceItems(deviceNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
            }
        });


        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
            }
        });
        builder.setNegativeButton("Cancel", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCheckBoxClick(int position, List<Device> device) {
        String mac = device.get(position).getDeviceMacAddress();
        int deviceId = device.get(position).getDeviceId();
        if (getCheckedDevices().contains(deviceId)){
            getCheckedDevices().remove(Integer.valueOf(deviceId));
            Toast.makeText(getActivity(), "Removed: " + mac + " " + deviceId, Toast.LENGTH_SHORT).show();
        }
        else{
            getCheckedDevices().add(deviceId);
            Toast.makeText(getActivity(), "Added: " + mac + " " + deviceId, Toast.LENGTH_SHORT).show();
        }


    }

    public ArrayList<Integer> getCheckedDevices() {
        if (checkedDevices == null)
        {
            checkedDevices = new ArrayList<>();
        }
        return checkedDevices;
    }

}