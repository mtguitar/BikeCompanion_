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

    private ArrayList<ScannerItem> mScannerList;

    public static class ScannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextName;
        public TextView mTextDescription;

        public ScannerViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextName = itemView.findViewById(R.id.textView);
            mTextDescription = itemView.findViewById(R.id.textView);
        }
    }

    public ScannerAdapter(ArrayList<ScannerItem> scannerList){
        mScannerList = scannerList;
    }

    @NonNull
    @Override
    public ScannerAdapter.ScannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanner_item, parent, false);
        ScannerViewHolder scannerViewHolder = new ScannerViewHolder(v);
        return scannerViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ScannerAdapter.ScannerViewHolder holder, int position) {
        ScannerItem currentItem = mScannerList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextDescription.setText(currentItem.getmTextDescription());
        holder.mTextName.setText(currentItem.getmTextName());


    }

    @Override
    public int getItemCount() {
        return mScannerList.size();
    }
}
