package com.busi.adi.aarogyam.listAdapters.diagnosis;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.busi.adi.aarogyam.Model.HealthDiagnosis;
import com.busi.adi.aarogyam.R;

import java.util.List;

public class HDAdapter extends ArrayAdapter<HealthDiagnosis> {
    public HDAdapter(@NonNull Context context, @NonNull List<HealthDiagnosis> list) {
        super(context, 0, list);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.health_item, parent, false);
        }
        CheckedTextView ctv = view.findViewById(R.id.hi_item);
        ctv.setText(getItem(position).Issue.Name);
        return view;
    }
}
