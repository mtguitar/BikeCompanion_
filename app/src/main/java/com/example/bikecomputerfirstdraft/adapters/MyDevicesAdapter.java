package com.example.bikecomputerfirstdraft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ui.myDevices.MyDevice;

import java.util.ArrayList;
import java.util.List;


public class MyDevicesAdapter extends RecyclerView.Adapter<MyDevicesAdapter.DeviceViewHolder>{

    private List<MyDevice> devices = new ArrayList<>();
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
        private Button switchAutoConnect;
        private Button buttonRemoveDevice;
        private TextView textViewDeviceMacAddress;
        private TextView textViewDeviceName;
        private TextView textViewMacAddress;
        private View constraintLayoutDeviceInfo;
        private MyDevice currentDevice;


        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
            textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_mac_address);
            textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
            textViewDeviceType = itemView.findViewById(R.id.text_view_device_type);
            textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
            textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
            textViewDeviceMacAddress = itemView.findViewById(R.id.text_view_scanner_device_mac_address);
            switchAutoConnect = itemView.findViewById(R.id.switch_auto_connect);
            buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
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
                            listener.onButtonClick(position, devices);
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        MyDevice currentDevice = devices.get(position);
        holder.textViewDeviceName.setText(currentDevice.getAssignedName());
        holder.textViewMacAddress.setText(currentDevice.getMacAddress());

    }

    public void setDevices(List<MyDevice> devices){
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
