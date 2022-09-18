package com.example.bikecompanion.ui.myBikes;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikecompanion.R;
import com.example.bikecompanion.adapters.myBikes.MyBikesAdapter;
import com.example.bikecompanion.adapters.myBikes.MyBikesListenerInterface;
import com.example.bikecompanion.adapters.myBikes.SelectDeviceAdapter;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.entities.BikeDeviceCrossRef;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.ui.myDevices.SharedEntitiesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyBikesFragment extends Fragment implements MyBikesListenerInterface {

    private final static String TAG = "FlareLog BikeFrag";
    private MyBikesViewModel mViewModel;
    private View view;
    private Button buttonAddBike;
    private Button buttonUpdate;
    private Button buttonCancel;

    private EditText editTextBikeName;
    private EditText editTextBikeMake;
    private EditText editTextBikeModel;

    private TextView textViewBikeDevices;
    private TextView textViewEditAddTitle;

    private Bike bikeToEdit;

    private List<Bike> bikeList;
    private List<Device> deviceList;
    private List<BikeWithDevices> bikesWithDevicesList;
    private List<String> checkedDevices;


    private MyBikesAdapter bikeAdapter;
    private SelectDeviceAdapter selectDeviceAdapter;
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

    private void initViews() {
        buttonAddBike = view.findViewById(R.id.button_bike_add);
        buttonUpdate = view.findViewById(R.id.button_bike_update);
        buttonCancel = view.findViewById(R.id.button_bike_cancel);

        editTextBikeName = view.findViewById(R.id.edit_text_bike_name);
        editTextBikeMake = view.findViewById(R.id.edit_text_bike_make);
        editTextBikeModel = view.findViewById(R.id.edit_text_bike_model);

        cardViewAddBike = view.findViewById(R.id.card_view_add_bike);

        textViewBikeDevices = view.findViewById(R.id.text_view_bike_devices);
        textViewEditAddTitle = view.findViewById(R.id.text_view_edit_add_title);

    }

    private void initObservers() {
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {

                Log.d(TAG, "Received devices live data ");
                deviceList = devices;
                if (devices != null) {
                    selectDeviceAdapter.setCheckBoxes(devices);
                }
            }
        });

        sharedEntitiesViewModel.getAllBikes().observe(getViewLifecycleOwner(), new Observer<List<Bike>>() {
            @Override
            public void onChanged(List<Bike> bikes) {
                Log.d(TAG, "Received bikes live data ");
                bikeList = bikes;
                bikeAdapter.setBikes(bikes);
            }
        });

        sharedEntitiesViewModel.getBikesWithDevices().observe(getViewLifecycleOwner(), new Observer<List<BikeWithDevices>>() {
            @Override
            public void onChanged(List<BikeWithDevices> bikesWithDevices) {
                Log.d(TAG, "Received bikesWithDevices live data ");
                bikesWithDevicesList = bikesWithDevices;
                bikeAdapter.setBikesWithDevices(bikesWithDevices);
            }
        });

    }

    private void initOnClickListeners() {
        //FAB
        FloatingActionButton fabNewDevice = view.findViewById(R.id.fac_add_bike);
        fabNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonUpdate.setVisibility(View.INVISIBLE);
                buttonAddBike.setVisibility(View.VISIBLE);
                textViewEditAddTitle.setText("Add New Bike");
                cardViewAddBike.setVisibility(View.VISIBLE);
            }
        });

        buttonAddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bikeName = String.valueOf(editTextBikeName.getText());
                String bikeMake = String.valueOf(editTextBikeMake.getText());
                String bikeModel = String.valueOf(editTextBikeModel.getText());
                //checks if bikeName is blank
                if (bikeName.equals("")) {
                    Toast.makeText(getActivity(), "Bike name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                //checks if bikeName is already in Bike table (current state of db is assigned to bikeList)
                for (int i = 0; i < bikeList.size(); i++) {
                    String bikeListName = bikeList.get(i).getBikeName();
                    if (bikeListName.equals(bikeName)) {
                        Toast.makeText(getActivity(), "Bike already exists. Please select a unique name.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //inserts new bike into Bike table
                Bike bike = new Bike(bikeName, bikeMake, bikeModel);
                sharedEntitiesViewModel.insert(bike);

                //adds bike/device pair to crossReference table
                int listSize = getCheckedDevices().size();
                for (int i = 0; i < listSize; i++) {
                    String deviceMacAddress = getCheckedDevices().get(i);
                    BikeDeviceCrossRef bikeDeviceCrossRef = new BikeDeviceCrossRef(deviceMacAddress, bikeName);
                    sharedEntitiesViewModel.insert(bikeDeviceCrossRef);
                }

                //cleans up views
                cardViewAddBike.setVisibility(View.GONE);
                editTextBikeName.getText().clear();
                editTextBikeMake.getText().clear();
                editTextBikeModel.getText().clear();
            }

        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bikeName = String.valueOf(editTextBikeName.getText());
                String bikeMake = String.valueOf(editTextBikeMake.getText());
                String bikeModel = String.valueOf(editTextBikeModel.getText());
                //checks if bikeName is blank
                if (bikeName.equals("")) {
                    Toast.makeText(getActivity(), "Bike name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //updates bike in Bike table
                bikeToEdit.setBikeName(bikeName);
                bikeToEdit.setBikeMake(bikeMake);
                bikeToEdit.setBikeModel(bikeModel);
                sharedEntitiesViewModel.update(bikeToEdit);

                //adds bike/device pair to crossReference table
                int listSize = getCheckedDevices().size();
                for (int i = 0; i < listSize; i++) {
                    String deviceMacAddress = getCheckedDevices().get(i);
                    BikeDeviceCrossRef bikeDeviceCrossRef = new BikeDeviceCrossRef(deviceMacAddress, bikeName);
                    sharedEntitiesViewModel.insert(bikeDeviceCrossRef);
                }

                //cleans up views
                cardViewAddBike.setVisibility(View.GONE);
                editTextBikeName.getText().clear();
                editTextBikeMake.getText().clear();
                editTextBikeModel.getText().clear();
            }

        });

        //Add bike to db
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewAddBike.setVisibility(View.GONE);
                editTextBikeName.getText().clear();
                editTextBikeMake.getText().clear();
                editTextBikeModel.getText().clear();
            }
        });
    }

    private void initRecyclerViewer() {
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
        bikeToEdit = bike.get(position);

        buttonAddBike.setVisibility(View.INVISIBLE);
        buttonUpdate.setVisibility(View.VISIBLE);
        cardViewAddBike.setVisibility(View.VISIBLE);
        textViewEditAddTitle.setText("Edit Bike");

        editTextBikeName.setText(bikeToEdit.getBikeName());
        editTextBikeMake.setText(bikeToEdit.getBikeMake());
        editTextBikeModel.setText(bikeToEdit.getBikeModel());

        sharedEntitiesViewModel.get

        //selectDeviceAdapter.setBikesWithDevices(bikesWithDevicesList);

    }

    @Override
    public void onCheckBoxClick(int position, List<Device> device) {
        String mac = device.get(position).getDeviceMacAddress();
        if (getCheckedDevices().contains(mac)) {
            getCheckedDevices().remove(mac);
            Toast.makeText(getActivity(), "Removed: " + mac, Toast.LENGTH_SHORT).show();
        } else {
            getCheckedDevices().add(mac);
            Toast.makeText(getActivity(), "Added: " + mac, Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> getCheckedDevices() {
        if (checkedDevices == null) {
            checkedDevices = new ArrayList<>();
        }
        return checkedDevices;
    }

}