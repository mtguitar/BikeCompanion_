package com.example.bikecompanion.sharedClasses;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;

import java.util.ArrayList;
import java.util.List;

public class AlertFragment extends DialogFragment {

    private SharedEntitiesViewModel sharedEntitiesViewModel;
    private String[] deviceList;


    public Dialog onCreateDialog(Bundle savedInstanceState, SharedEntitiesViewModel sharedEntitiesViewModel) {
        this.sharedEntitiesViewModel = sharedEntitiesViewModel;
        ArrayList<Integer> selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Select Devices")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(deviceList, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which);
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(which);
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    private void initObservers() {
        sharedEntitiesViewModel.getAllDevices().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> devices) {


                for (int i = 0; i < devices.size(); i++) {
                    deviceList[i] = devices.get(i).getDeviceBleName();
                }

            }

        });
    }
}