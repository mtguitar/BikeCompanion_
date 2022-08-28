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
    ArrayList<ScannerItem> scannerList;


    public ScannerAdapter(ArrayList<ScannerItem> scannerList, RecyclerViewInterface recyclerViewInterface) {
        this.scannerList = scannerList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class ScannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextName;
        public TextView mTextDescription;


        public ScannerViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewScanResults);
            mTextName = itemView.findViewById(R.id.textViewName);
            mTextDescription = itemView.findViewById(R.id.textViewDescription);

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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanner_item, parent, false);
        ScannerViewHolder scannerViewHolder = new ScannerViewHolder(v, recyclerViewInterface);
        return scannerViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ScannerAdapter.ScannerViewHolder holder, int position) {
        ScannerItem currentItem = scannerList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextDescription.setText(currentItem.getTextDescription());
        holder.mTextName.setText(currentItem.getTextName());

    }

    @Override
    public int getItemCount() {
        return scannerList.size();

    }

    public String getItemName(int position){
        String name = getItemName(position);
        return name;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }


    }


}



