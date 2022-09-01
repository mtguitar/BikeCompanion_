package com.example.bikecomputerfirstdraft.ui.myDevices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;

import java.util.ArrayList;
import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{

    private List<Device> devices = new ArrayList<>();

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
        private TextView textViewDeviceName;
        private TextView textViewMacAddress;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
            textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_mac_address);
        }
    }


}
