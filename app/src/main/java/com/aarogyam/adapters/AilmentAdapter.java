package com.aarogyam.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aarogyam.Model.AilmentInfo;
import com.aarogyam.R;
import com.aarogyam.listeners.ListItemClicks;

import java.util.List;

public class AilmentAdapter extends RecyclerView.Adapter<AilmentAdapter.ViewHolder> {

    private List<AilmentInfo> file_list;
    private ListItemClicks mListener;

    public AilmentAdapter(List<AilmentInfo> files, ListItemClicks listener){
        this.file_list = files;
        Log.d("TAG", "AilmentAdapter: " + files.isEmpty());
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
        holder.itemName.setText(file_list.get(position).getFileName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onListItemClicked(file_list.get(position).getRecord());
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onListItemLongClicked("locally", file_list.get(position).getFileName(), position);
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