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

import java.util.ArrayList;
import java.util.List;


public class SelectDeviceAdapter extends RecyclerView.Adapter<SelectDeviceAdapter.BikeViewHolder>{

    private List<Bike> bike = new ArrayList<>();
    private MyBikesListenerInterface listener;


    public SelectDeviceAdapter(MyBikesListenerInterface listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public BikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_item, parent, false);
        return new BikeViewHolder(itemView);
    }

    class BikeViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewBikeName;
        private TextView textViewBikeId;
        private Button buttonBikeEdit;
        private Button buttonBikeRemove;


        public BikeViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBikeName = itemView.findViewById(R.id.text_view_my_bike_name);
            textViewBikeId = itemView.findViewById(R.id.text_view_my_bike_make);
            buttonBikeEdit = itemView.findViewById(R.id.button_bike_edit);
            buttonBikeRemove = itemView.findViewById(R.id.button_bike_remove);


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

            buttonBikeRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClickRemove(position, bike);
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
                            listener.onButtonClickEdit(position, bike);
                        }
                    }

                }
            });


        }
    }

    @Override
    public void onBindViewHolder(@NonNull BikeViewHolder holder, int position) {
        Bike currentBike = bike.get(position);
        holder.textViewBikeName.setText(currentBike.getBikeName());
        //holder.textViewBikeId.setText(String.valueOf(currentBike.getBikeId());

    }

    public void setBikes(List<Bike> bike){
        this.bike = bike;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bike.size();
    }

}
