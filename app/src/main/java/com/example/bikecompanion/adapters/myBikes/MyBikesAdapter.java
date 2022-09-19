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
import com.example.bikecompanion.databases.relations.BikeWithDevices;

import java.util.ArrayList;
import java.util.List;


public class MyBikesAdapter extends RecyclerView.Adapter<MyBikesAdapter.BikeViewHolder>{

    private List<Bike> bikeList;
    private List<Device> deviceList;
    private List<BikeWithDevices> bikesWithDevicesList;

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
        Bike currentViewBike = bikeList.get(position);
        String currentViewBikeName = currentViewBike.getBikeName();
        holder.textViewBikeName.setText(currentViewBike.getBikeName());
        holder.textViewBikeMake.setText(currentViewBike.getBikeMake());
        holder.textViewBikeModel.setText(currentViewBike.getBikeModel());

        //Add devices belonging to each bike
        if (bikesWithDevicesList != null)
        {
            //loops through each bike, checking if it matches name of the current bike in the recyclerView
            holder.textViewBikeDevices.setText("");
            int listSize = bikesWithDevicesList.size();
            for (int i = 0; i < listSize; i++) {
                String listBikeName = bikesWithDevicesList.get(i).bike.getBikeName();
                if(listBikeName.equals(currentViewBikeName)){
                    //loops through each device in the list and adds it to the textView, followed by a new line
                    int deviceListSize = bikesWithDevicesList.get(i).deviceList.size();
                    for (int j = 0; j < deviceListSize; j++)
                    {
                        String listDeviceName = bikesWithDevicesList.get(i).deviceList.get(j).getDeviceAssignedName();
                        String listDeviceMacAddress = bikesWithDevicesList.get(i).deviceList.get(j).getDeviceMacAddress();
                        holder.textViewBikeDevices.append(listDeviceName + " " + listDeviceMacAddress + "\n");
                    }
                }
            }
        }

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


    public void setBikesWithDevices(List<BikeWithDevices> bikesWithDevices){
        bikesWithDevicesList = bikesWithDevices;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        if (bikeList != null) {
            return bikeList.size();
        }
        else {
            return 0;
        }
    }

}
