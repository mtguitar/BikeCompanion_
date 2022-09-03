package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ui.scanner.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{


    private List<Device> devices = new ArrayList<>();

    public RecyclerViewInterface recyclerViewInterface;

    public DeviceAdapter() {

    }



    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device currentDevice = devices.get(position);
        holder.textViewDeviceName.setText(currentDevice.getAssignedName());
        holder.textViewMacAddress.setText(currentDevice.getMacAddress());

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void setDevices(List<Device> devices){
        this.devices = devices;
        notifyDataSetChanged();
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
                    if (recyclerViewInterface != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                            if(constraintLayoutDeviceInfo.getVisibility() == View.GONE){
                                constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
                            }
                            else {
                                constraintLayoutDeviceInfo.setVisibility(View.GONE);

                            }

                        }
                    }
                }
            });

            buttonRemoveDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (recyclerViewInterface != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onButtonClick(position);
                        }
                    }

                }
            });


        }
    }


}
