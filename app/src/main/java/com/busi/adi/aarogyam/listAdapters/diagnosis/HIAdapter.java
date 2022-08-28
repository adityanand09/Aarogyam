package com.busi.adi.aarogyam.listAdapters.diagnosis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.busi.adi.aarogyam.Model.HealthItem;
import com.busi.adi.aarogyam.R;

import java.util.List;

public class HIAdapter extends ArrayAdapter<HealthItem> {
    public HIAdapter(@NonNull Context context, @NonNull List<HealthItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.health_item, parent, false);
        }
        CheckedTextView ctv = view.findViewById(R.id.hi_item);
        ctv.setText(getItem(position).Name);
        return view;
    }
}
