package com.example.bikecomputerfirstdraft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;
import com.example.bikecomputerfirstdraft.ui.scanner.ScanResults;

import java.util.ArrayList;

public class ScannerAdapter extends RecyclerView.Adapter<ScannerAdapter.ScannerViewHolder> {



    public ArrayList<ScanResults> scanResultsArrayList;
    public RecyclerViewInterface recyclerViewInterface;

    public ScannerAdapter(ArrayList<ScanResults> scanResultsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.scanResultsArrayList = scanResultsArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ScannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewScanResults;
        public TextView textViewScannerDeviceName;
        public TextView textViewScannerDeviceMacAddress;
        public TextView textViewScannerDeviceType;
        public TextView textViewScannerBattery;
        public TextView textViewScannerDeviceManufacturer;
        public Button buttonAddToMyDevices;

        public Button buttonScannerConnect;
        public View constraintLayoutScanResult;

        private ScannerAdapter scannerAdapter;


        public ScannerViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageViewScanResults = itemView.findViewById(R.id.image_view_scan_results);
            textViewScannerDeviceName = itemView.findViewById(R.id.text_view_scanner_device_name);
            textViewScannerDeviceMacAddress = itemView.findViewById(R.id.text_view_scanner_device_mac_address);
            buttonAddToMyDevices = itemView.findViewById(R.id.button_scanner_add_to_my_devices);
            constraintLayoutScanResult = itemView.findViewById(R.id.constraint_layout_device_info);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                            if(constraintLayoutScanResult.getVisibility() == View.GONE){
                                constraintLayoutScanResult.setVisibility(View.VISIBLE);
                            }
                            else {
                                constraintLayoutScanResult.setVisibility(View.GONE);

                            }

                        }
                    }
                }
            });

            buttonAddToMyDevices.setOnClickListener(new View.OnClickListener() {
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



    @NonNull
    @Override
    public ScannerAdapter.ScannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_result_item, parent, false);
        ScannerViewHolder scannerViewHolder = new ScannerViewHolder(v, recyclerViewInterface);
        return scannerViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ScannerAdapter.ScannerViewHolder holder, int position) {
        ScanResults currentItem = scanResultsArrayList.get(position);
        holder.imageViewScanResults.setImageResource(currentItem.getImageResource());
        holder.textViewScannerDeviceMacAddress.setText(currentItem.getDeviceMacAddress());
        holder.textViewScannerDeviceName.setText(currentItem.getDeviceName());
        holder.constraintLayoutScanResult.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return scanResultsArrayList.size();

    }

    public String getItemName(int position){
        String name = getItemName(position);
        return name;
    }



}



