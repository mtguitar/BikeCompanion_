package com.example.bikecompanion.adapters.myBikes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.databases.relations.BikeWithDevices;
import com.example.bikecompanion.databases.relations.DeviceWithBikes;

import java.util.ArrayList;
import java.util.List;


public class DeviceCheckBoxAdapter extends RecyclerView.Adapter<DeviceCheckBoxAdapter.SelectDeviceViewHolder> {

    private final static String TAG = "FlareLog SelectAdapt";
    private List<Device> device = new ArrayList<>();
    private final MyBikesListenerInterface listener;
    private List<BikeWithDevices> bikeWithDevicesList;
    private List<DeviceWithBikes> deviceWithBikesList;
    private Bike bikeToEdit;


    public DeviceCheckBoxAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public SelectDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_checkbox, parent, false);
        return new SelectDeviceViewHolder(itemView);
    }

    class SelectDeviceViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private ImageView selectDeviceImageView;


        public SelectDeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_select_device);
            selectDeviceImageView = itemView.findViewById(R.id.image_view_checkbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckBoxClick(position, device, checkBox);
                        }
                    }

                }
            });

        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectDeviceViewHolder holder, int position) {
        Device currentDevice = device.get(position);
        String currentDeviceName = currentDevice.getDeviceBleName();
        String currentDeviceMac = currentDevice.getDeviceMacAddress();
        holder.checkBox.setChecked(false);
        String checkBoxText = currentDeviceName + " " + currentDeviceMac;
        holder.checkBox.setText(checkBoxText);

        //Check if bikeToEdit is set, in which case we are editing a bike, not creating a new one
        if (bikeToEdit != null) {
            String bikeToEditName = bikeToEdit.getBikeName();

            //loop through list deviceWithBikes to see if bikeToEdit is in list
            //if in list, check box, otherwise, uncheck box
            int deviceListSize = deviceWithBikesList.size();
            for (int i = 0; i < deviceListSize; i++) {
                String deviceWithBikesListMac = deviceWithBikesList.get(i).device.getDeviceMacAddress();
                if (deviceWithBikesListMac.equals(currentDeviceMac)) {
                    int bikeListSize = deviceWithBikesList.get(i).bikeList.size();
                    for (int j = 0; j < bikeListSize; j++) {
                        String bikeListName = deviceWithBikesList.get(i).bikeList.get(j).getBikeName();
                        Log.d(TAG, "bikeListName: " + bikeListName + " bikeToEditName: " + bikeToEditName);
                        if (bikeListName.equals(bikeToEditName)) {
                            holder.checkBox.setChecked(true);
                            Log.d(TAG, "Checkbox true");
                        } else {
                            Log.d(TAG, "Checkbox false");
                        }
                    }

                }
            }

            //add image based on deviceType
            String deviceType = currentDevice.getDeviceType();
            deviceType = deviceType.toLowerCase();
            Log.d(TAG, "Current device type: " + deviceType);
            if (deviceType.contains(Constants.DEVICE_TYPE_LIGHT)) {
                holder.selectDeviceImageView.setImageResource(R.drawable.ic_device_type_light);
            } else if (deviceType.contains(Constants.DEVICE_TYPE_SPEED)) {
                holder.selectDeviceImageView.setImageResource(R.drawable.ic_device_type_csc);
            } else {
                holder.selectDeviceImageView.setImageResource(R.drawable.ic_device_type_other_sensor);
            }

        }
    }

    public void setCheckBoxes(List<Device> devices) {
        this.device = devices;
        notifyDataSetChanged();
    }

    public void setBikeWithDevices(List<BikeWithDevices> bikeWithDevices) {
        bikeWithDevicesList = bikeWithDevices;
        notifyDataSetChanged();
    }

    public void setDeviceWithBikes(List<DeviceWithBikes> deviceWithBikes) {
        deviceWithBikesList = deviceWithBikes;
        notifyDataSetChanged();
    }


    public void setBikeToEdit(Bike bikeToEdit) {
        this.bikeToEdit = bikeToEdit;
        Log.d(TAG, "received bikeToEdit: " + this.bikeToEdit.getBikeName());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return device.size();
    }


}
