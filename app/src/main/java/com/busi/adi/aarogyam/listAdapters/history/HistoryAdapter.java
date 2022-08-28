package com.busi.adi.aarogyam.listAdapters.history;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.busi.adi.aarogyam.Model.HistoryItem;
import com.busi.adi.aarogyam.R;
import com.busi.adi.aarogyam.listeners.ListItemClicks;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> file_list;
    private ListItemClicks mListener;

    public HistoryAdapter(List<HistoryItem> files, ListItemClicks listener){
        this.file_list = files;
        Log.d("TAG", "HistoryAdapter: " + files.isEmpty());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(file_list.get(position).getFile_name());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onListItemClicked(file_list.get(position).info_list);
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onListItemLongClicked("searched", file_list.get(position).getFile_name(), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return file_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        public TextView itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            itemName = (TextView)itemView.findViewById(R.id.filename);
        }
    }
}