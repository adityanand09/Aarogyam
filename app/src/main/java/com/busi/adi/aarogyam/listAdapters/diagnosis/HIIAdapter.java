package com.busi.adi.aarogyam.listAdapters.diagnosis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.busi.adi.aarogyam.Model.IssueInfo;
import com.busi.adi.aarogyam.R;

import java.util.List;

public class HIIAdapter extends ArrayAdapter<IssueInfo> {
    public HIIAdapter(@NonNull Context context, @NonNull List<IssueInfo> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.issue_details_item, parent, false);
        }
        TextView title = convertView.findViewById(R.id.issue_info_title);
        TextView content = convertView.findViewById(R.id.issue_info_description);
        Log.d("TAG", "getView: " + getItem(position));
        IssueInfo info = getItem(position);
        title.setText(info.IssueTitle);
        content.setText(info.IssueInfo);
        return convertView;
    }
}
