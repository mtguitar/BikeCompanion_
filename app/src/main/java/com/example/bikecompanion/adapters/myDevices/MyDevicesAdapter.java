package com.example.bikecompanion.adapters.myDevices;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecompanion.R;
import com.example.bikecompanion.constants.Constants;
import com.example.bikecompanion.databases.entities.Device;
import com.example.bikecompanion.ui.sharedViewModels.SharedEntitiesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MyDevicesAdapter extends RecyclerView.Adapter<MyDevicesAdapter.DeviceViewHolder>{

    private List<Device> devices = new ArrayList<>();
    private MyDevicesListenerInterface listener;
    private int itemsOpen = 0;
    private View lastItemOpen;
    private ImageView lastArrowOpen;
    private String lastVisibleDevice;
    private String connectedDeviceMacAddress;
    private String visibleDeviceMacAddress;
    private View constraintLayoutDeviceInfo;
    private Device currentDevice;
    private View lastVisibleView;
    private SharedEntitiesViewModel sharedEntitiesViewModel;


    public MyDevicesAdapter(MyDevicesListenerInterface listener, SharedEntitiesViewModel sharedEntitiesViewModel) {
        this.listener = listener;
        this.sharedEntitiesViewModel = sharedEntitiesViewModel;

    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);


        return new DeviceViewHolder(itemView);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        private List<Device> deviceList;
        private ArrayList<View> rowList;

        //variables related to keeping track of recyclerView items/devices/views


        //views contained within each recyclerView item
        private View itemView;
        private TextView textViewDeviceName;
        private TextView textViewMacAddress;
        private TextView textViewDeviceBattery;
        private TextView textViewDeviceModel;
        private TextView textViewDeviceMode;
        private TextView textViewDeviceManufacturer;
        private TextView textViewDeviceLocation;
        private TextView textViewDeviceFeature;
        private TextView textViewDeviceState;
        private Button buttonRemoveDevice;
        private Button buttonConnectDisconnectDevice;
        private ImageView imageViewArrow;
        private View rowBattery;
        private View rowMode;
        private View rowManufacturer;
        private View rowModel;
        private View rowLocation;
        private View rowFeature;
        private View rowProgressBar;
        private View progressBarDeviceData;
        private TextView textViewDeviceType;
        private TextView textViewDeviceMacAddress;
        private Button switchAutoConnect;
        private Button buttonDisconnectDevice;


        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
            textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_id);
            textViewDeviceMacAddress = itemView.findViewById(R.id.text_view_scanner_device_mac_address);
            buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
            buttonDisconnectDevice = itemView.findViewById(R.id.button_device_connect);
            constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            listener.onRVItemClick(position, itemView, devices);

                        }
                    }

                    textViewDeviceName = itemView.findViewById(R.id.text_view_my_device_name);
                    textViewMacAddress = itemView.findViewById(R.id.text_view_my_device_id);
                    textViewDeviceBattery = itemView.findViewById(R.id.text_view_device_battery);
                    textViewDeviceModel = itemView.findViewById(R.id.text_view_device_model);
                    textViewDeviceMode = itemView.findViewById(R.id.text_view_device_mode);
                    textViewDeviceManufacturer = itemView.findViewById(R.id.text_view_device_manufacturer);
                    textViewDeviceState = itemView.findViewById(R.id.text_view_device_state);
                    textViewDeviceLocation = itemView.findViewById(R.id.text_view_CSC_location);
                    textViewDeviceFeature = itemView.findViewById(R.id.text_view_CSC_mode);
                    buttonRemoveDevice = itemView.findViewById(R.id.button_device_remove);
                    buttonConnectDisconnectDevice = itemView.findViewById(R.id.button_device_connect);
                    progressBarDeviceData = itemView.findViewById(R.id.progress_bar_data);
                    imageViewArrow = itemView.findViewById(R.id.image_view_arrow);
                    constraintLayoutDeviceInfo = itemView.findViewById(R.id.constraint_layout_device_info);
                    rowProgressBar = itemView.findViewById(R.id.row_progress_bar);
                    rowList = new ArrayList<>();

                    Collections.addAll(rowList,
                            rowBattery = itemView.findViewById(R.id.row_battery),
                            rowMode = itemView.findViewById(R.id.row_mode),
                            rowManufacturer = itemView.findViewById(R.id.row_manufacturer),
                            rowModel = itemView.findViewById(R.id.row_model),
                            rowLocation = itemView.findViewById(R.id.row_csc_location),
                            rowFeature = itemView.findViewById(R.id.row_CSC_mode)
                    );

                    //if the clicked recyclerView item is not currently expanded
                    if (constraintLayoutDeviceInfo.getVisibility() == View.GONE) {
                        //If another recyclerView items is already expanded
                        if (itemsOpen >= 1) {
                            //deflate the item, rotate the arrow, hide its rows, subtract 1 from itemsOpen
                            lastItemOpen.setVisibility(View.GONE);
                            lastArrowOpen.setRotation(0);
                            //hideRows();
                            itemsOpen--;

                        }
                        //If no recyclerView items are currently expanded
                        if (itemsOpen == 0) {
                            constraintLayoutDeviceInfo.setVisibility(View.VISIBLE);
                            imageViewArrow.setRotation(180);
                            for (View row : rowList){
                                row.setVisibility(View.GONE);
                            }

                            textViewDeviceState.setText(Constants.CONNECTION_STATE_CONNECTING_NAME);
                            buttonConnectDisconnectDevice.setEnabled(false);

                            lastVisibleDevice = visibleDeviceMacAddress;
                            lastItemOpen = constraintLayoutDeviceInfo;
                            lastArrowOpen = imageViewArrow;
                            itemsOpen++;
                        }
                    }
                    //If the clicked recyclerView item is already expanded -> deflate view, rotate arrow, hide old rows
                    else {
                        //Deflate the item, change the arrow rotation,
                        constraintLayoutDeviceInfo.setVisibility(View.GONE);
                        imageViewArrow.setRotation(0);
                        for (View row : rowList){
                            row.setVisibility(View.GONE);
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
                            listener.onButtonClickRemoveDevice(position, devices);
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
                            listener.onButtonClickConnectDisconnect(position, devices);
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
