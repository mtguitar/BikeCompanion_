package com.example.bikecompanion.adapters.myBikes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.ArrayList;
import java.util.List;


public class SelectDeviceAdapter extends RecyclerView.Adapter<SelectDeviceAdapter.SelectDeviceViewHolder>{

    private List<Device> device = new ArrayList<>();
    private MyBikesListenerInterface listener;


    public SelectDeviceAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public SelectDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_checkbox_item, parent, false);
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
    }

    public void setCheckBoxes(List<Device> devices){
        this.device = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return device.size();
    }

}
