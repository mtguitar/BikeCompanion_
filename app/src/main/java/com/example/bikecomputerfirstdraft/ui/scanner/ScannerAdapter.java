package com.example.bikecomputerfirstdraft.ui.scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;

import java.util.ArrayList;

public class ScannerAdapter extends RecyclerView.Adapter<ScannerAdapter.ScannerViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<ScanResults> scanResultsArrayList;


    public ScannerAdapter(ArrayList<ScanResults> scanResultsArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.scanResultsArrayList = scanResultsArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ScannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextName;
        public TextView mTextDescription;


        public ScannerViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view_scan_results);
            mTextName = itemView.findViewById(R.id.text_view_scanner_device_name);
            mTextDescription = itemView.findViewById(R.id.text_view_scanner_device_mac_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAbsoluteAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                    String name = (String) mTextName.getText();
                    String description = (String) mTextDescription.getText();
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
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextDescription.setText(currentItem.getDeviceMacAddress());
        holder.mTextName.setText(currentItem.getDeviceName());

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



