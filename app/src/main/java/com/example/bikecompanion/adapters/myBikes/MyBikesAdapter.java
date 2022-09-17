package com.example.bikecompanion.adapters.myBikes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.databases.entities.Bike;
import com.example.bikecompanion.databases.entities.Device;

import java.util.ArrayList;
import java.util.List;


public class MyBikesAdapter extends RecyclerView.Adapter<MyBikesAdapter.BikeViewHolder>{

    private List<Bike> bikeList;
    private List<Device> deviceList;
    private MyBikesListenerInterface listener;


    public MyBikesAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike, parent, false);
        return new BikeViewHolder(itemView);
    }

    class BikeViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewBikeName;
        private TextView textViewBikeMake;
        private TextView textViewBikeModel;
        private TextView textViewBikeDevices;

        private Button buttonBikeEdit;
        private Button buttonBikeRemove;


        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBikeName = itemView.findViewById(R.id.text_view_my_bike_name);
            textViewBikeMake = itemView.findViewById(R.id.text_view_my_bike_make);
            textViewBikeModel = itemView.findViewById(R.id.text_view_my_bike_model);
            buttonBikeEdit = itemView.findViewById(R.id.button_bike_edit);
            buttonBikeRemove = itemView.findViewById(R.id.button_bike_remove);
            textViewBikeDevices = itemView.findViewById(R.id.text_view_bike_devices);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, itemView, bikeList);

                        }
                    }
                }
            });

            buttonBikeRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickRemove(position, bikeList);
                        }
                    }

                }
            });

            buttonBikeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickEdit(position, bikeList);
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike currentBike = bikeList.get(position);
        int bikeId = currentBike.getBikeId();
        holder.textViewBikeName.setText(currentBike.getBikeName());
        holder.textViewBikeMake.setText(currentBike.getBikeMake());
        holder.textViewBikeModel.setText(currentBike.getBikeModel());
        // TODO add devices to textViewBikeDevice




        //holder.textViewBikeId.setText(String.valueOf(currentBike.getBikeId());

    }

    public void setBikes(List<Bike> bikes){
        if (bikeList == null){
            bikeList = new ArrayList<>();
        }
        bikeList = bikes;
        notifyDataSetChanged();
    }

    public void setDevices(List<Device> devices){
        if (deviceList == null){
            deviceList = new ArrayList<>();
        }
        deviceList = devices;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return bikeList.size();
    }

}
