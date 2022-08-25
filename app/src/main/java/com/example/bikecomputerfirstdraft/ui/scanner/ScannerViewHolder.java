package com.example.bikecomputerfirstdraft.ui.scanner;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikecomputerfirstdraft.R;

public class ScannerViewHolder extends RecyclerView.ViewHolder{

    public ImageView mImageView;
    public TextView mTextName;
    public TextView mTextDescription;

    public ScannerViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.imageViewScanResults);
        mTextName = itemView.findViewById(R.id.textViewName);
        mTextDescription = itemView.findViewById(R.id.textViewDescription);
    }


}
