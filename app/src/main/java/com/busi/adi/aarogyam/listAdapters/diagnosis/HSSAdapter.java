package com.busi.adi.aarogyam.listAdapters.diagnosis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.busi.adi.aarogyam.Model.HealthSymptomSelector;
import com.busi.adi.aarogyam.R;

import java.util.List;

public class HSSAdapter extends ArrayAdapter<HealthSymptomSelector> {
    private static final String TAG = "HSSAdapter";
    public HSSAdapter(@NonNull Context context, @NonNull List<HealthSymptomSelector> hss) {
        super(context, 0, hss);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.health_item, parent, false);
        }
        CheckedTextView tv = view.findViewById(R.id.hi_item);
        tv.setText(getItem(position).Name);
        Log.d(TAG, "getView: ");
        return view;
    }
}
