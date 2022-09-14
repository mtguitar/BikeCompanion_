package com.example.bikecompanion.adapters.myBikes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.databases.bike.MyBike;
import com.example.bikecompanion.databases.devices.MyDevice;

import java.util.ArrayList;
import java.util.List;


public class MyBikesAdapter extends RecyclerView.Adapter<MyBikesAdapter.BikeViewHolder>{

    private List<MyBike> bike = new ArrayList<>();
    private MyBikesListenerInterface listener;


    public MyBikesAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new BikeViewHolder(itemView);
    }

    class BikeViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewDeviceBattery;
        private TextView textViewDeviceType;
        private TextView textViewDeviceMode;
        private TextView textViewDeviceManufacturer;
        private TextView textViewDeviceMacAddress;
        private TextView textViewDeviceName;
        private TextView textViewMacAddress;
        private View constraintLayoutDeviceInfo;
        private MyDevice currentDevice;

        private Button switchPrimaryBike;
        private Button buttonEdit;
        private Button buttonAddDevices;


        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);

            switchPrimaryBike = itemView.findViewById(R.id.switch_primary_bike);
            buttonEdit = itemView.findViewById(R.id.button_device_edit);
            buttonAddDevices = itemView.findViewById(R.id.button_add_devices);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, itemView, bike);

                        }
                    }
                }
            });

            buttonAddDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickAdd(position, bike);
                        }
                    }

                }
            });

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickDisconnect(position, bike);
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        MyBike currentBike = bike.get(position);
        //holder.textViewDeviceName.setText(currentBike.getName());

    }

    public void setBikes(List<MyBike> bike){
        this.bike = bike;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bike.size();
    }

}
