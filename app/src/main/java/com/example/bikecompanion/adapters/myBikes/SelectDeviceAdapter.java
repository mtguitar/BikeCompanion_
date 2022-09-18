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

import java.util.ArrayList;
import java.util.List;


public class SelectDeviceAdapter extends RecyclerView.Adapter<SelectDeviceAdapter.SelectDeviceViewHolder>{

    private final static String TAG = "FlareLog SelectAdapt";
    private List<Device> device = new ArrayList<>();
    private MyBikesListenerInterface listener;
    private List<BikeWithDevices> bikesWithDevicesList;
    private Bike bikeToEdit;


    public SelectDeviceAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public SelectDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_checkbox, parent, false);
        return new SelectDeviceViewHolder(itemView);
    }

    class SelectDeviceViewHolder extends RecyclerView.ViewHolder{
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

                        if (position != RecyclerView.NO_POSITION){
                            listener.onCheckBoxClick(position, device);
                        }
                    }

                }
            });

        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectDeviceViewHolder holder, int position) {
        Device currentDevice = device.get(position);
        holder.checkBox.setText(currentDevice.getDeviceBleName() + " " + currentDevice.getDeviceMacAddress());

        //get deviceWithBike for currentDevice
        //loop through list of bikes to see if currentBike is in list
        //if in list, check box, otherwise, uncheck box

        //add image based on deviceType
        String deviceType = currentDevice.getDeviceType();
        deviceType = deviceType.toLowerCase();
        Log.d(TAG, "Current device type: " + deviceType);
        if (deviceType.contains(Constants.DEVICE_TYPE_LIGHT)){
            holder.selectDeviceImageView.setImageResource(R.drawable.ic_device_type_light);
        }
        else if (deviceType.contains(Constants.DEVICE_TYPE_SPEED)){
            holder.selectDeviceImageView.setImageResource(R.drawable.ic_speed);
        }
        else {
            holder.selectDeviceImageView.setImageResource(R.drawable.ic_device_type_other_sensor);
        }

    }

    public void setCheckBoxes(List<Device> devices){
        this.device = devices;
        notifyDataSetChanged();
    }

    public void setBikesWithDevices(List<BikeWithDevices> bikesWithDevices){
        bikesWithDevicesList = bikesWithDevices;
        notifyDataSetChanged();
    }

    public void setBikeToEdit(Bike bikeToEdit) {
        this.bikeToEdit = bikeToEdit;
    }

    @Override
    public int getItemCount() {
        return device.size();
    }

}
