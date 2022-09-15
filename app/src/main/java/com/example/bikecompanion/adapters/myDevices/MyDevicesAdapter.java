package com.example.bikecompanion.adapters.myDevices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.databases.entities.Device;

import java.util.ArrayList;
import java.util.List;


public class MyDevicesAdapter extends RecyclerView.Adapter<MyDevicesAdapter.DeviceViewHolder>{

    private List<Device> devices = new ArrayList<>();
    private MyDevicesListenerInterface listener;


    public MyDevicesAdapter(MyDevicesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewDeviceBattery;
        private TextView textViewDeviceType;
        private TextView textViewDeviceMode;
        private TextView textViewDeviceManufacturer;
        private TextView textViewDeviceMacAddress;
        private TextView textViewDeviceName;
        private TextView textViewMacAddress;
        private View constraintLayoutDeviceInfo;
        private Device currentDevice;

        private Button switchAutoConnect;
        private Button buttonDisconnectDevice;
        private Button buttonRemoveDevice;


        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
            textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_id);
            textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
            textViewDeviceType = itemView.findViewById(R.id.text_view_device_model);
            textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
            textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
            textViewDeviceMacAddress = itemView.findViewById(R.id.text_view_scanner_device_mac_address);
            switchAutoConnect = itemView.findViewById(R.id.switch_primary_bike);
            buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
            buttonDisconnectDevice = itemView.findViewById(R.id.button_device_connect);
            constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, itemView, devices);

                        }
                    }
                }
            });

            buttonRemoveDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickRemove(position, devices);
                        }
                    }

                }
            });

            buttonDisconnectDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickDisconnect(position, devices);
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device currentDevice = devices.get(position);
        holder.textViewDeviceName.setText(currentDevice.getDeviceAssignedName());
        holder.textViewMacAddress.setText(currentDevice.getDeviceMacAddress());

    }

    public void setDevices(List<Device> devices){
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
