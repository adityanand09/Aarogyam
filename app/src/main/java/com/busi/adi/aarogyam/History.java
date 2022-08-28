package com.busi.adi.aarogyam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.busi.adi.aarogyam.Model.AilmentInfo;
import com.busi.adi.aarogyam.Model.AilmentRecord;
import com.busi.adi.aarogyam.Model.HistoryItem;
import com.busi.adi.aarogyam.Model.IssueInfo;
import com.busi.adi.aarogyam.listAdapters.diagnosis.AilmentAdapter;
import com.busi.adi.aarogyam.listAdapters.history.HistoryAdapter;
import com.busi.adi.aarogyam.listeners.ListItemClicks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity implements ListItemClicks, View.OnClickListener {

    private static ProgressBar pb;
    private static RecyclerView history_list_view;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private HistoryAdapter adapter;
    private AilmentAdapter ailmentAdapter;
    private Button l_a_h, s_h;
    private LinearLayout hs_btn_l;
    private static int VISIB = View.VISIBLE;
    private static boolean ENBLD = true;
    private static List<HistoryItem> mList  = null;
    private static List<AilmentInfo> ailmentInfos = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        setTitle("History");
        pb = findViewById(R.id.progress_history);
        hs_btn_l = findViewById(R.id.hs_btn_l);
        l_a_h = findViewById(R.id.l_a_h_btn);
        s_h = findViewById(R.id.s_h_btn);
        l_a_h.setOnClickListener(this);
        s_h.setOnClickListener(this);
        history_list_view = findViewById(R.id.history_list_view);
        history_list_view.setLayoutManager(layoutManager(getResources().getConfiguration()));
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    private void ViewList(String location) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        pb.setVisibility(View.VISIBLE);
        if(activeNetwork != null && activeNetwork.isConnected()) {
            if(mAuth != null){
                FirebaseUser user = mAuth.getCurrentUser();
                reference = database.getReference().child("users").child(user.getUid()).child("history").child(location);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            if(location.equals("searched")){
                                List<HistoryItem> list = new ArrayList<>();
                                for (DataSnapshot var : snapshot.getChildren()) {
                                    List<IssueInfo> l_list = new ArrayList<>();
                                    for(DataSnapshot var1 : var.getChildren()){
                                        l_list.add(var1.getValue(IssueInfo.class));
                                    }
                                    list.add(new HistoryItem(var.getKey(), l_list));
                                }
                                mList = list;
                                adapter = new HistoryAdapter(mList, History.this);
                                history_list_view.setAdapter(adapter);
                            }else if(location.equals("locally")){
                                List<AilmentInfo> list = new ArrayList<>();
                                for (DataSnapshot var : snapshot.getChildren()) {
                                    list.add(new AilmentInfo(var.getKey(), var.getValue(AilmentRecord.class)));
                                }
                                ailmentInfos = list;
                                ailmentAdapter = new AilmentAdapter(ailmentInfos, History.this);
                                history_list_view.setAdapter(ailmentAdapter);
                            }
                        }else{
                            toggleBtnView(true);
                            history_list_view.setAdapter(null);
                            new CustomToast(History.this, "Not Exists").show();
                        }
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }else{
            pb.setVisibility(View.GONE);
            new CustomToast(History.this, "App not connected to internet").show();
        }
    }

    public GridLayoutManager layoutManager(Configuration configuration){
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        }else {
            return new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        }
    }

    @Override
    public void onListItemClicked(Object object) {
        if(object instanceof AilmentRecord){
            AilmentRecord ailmentRecord = (AilmentRecord) object;
            Intent intent = new Intent(this, LocallyAddedHistory.class);
            intent.putExtra("ailmentRecord", (Parcelable) ailmentRecord);
            startActivity(intent);
        }else {
            List<IssueInfo> issueinfo = (List<IssueInfo>) object;
            Intent intent = new Intent(this, SearchedHistory.class);
            intent.putParcelableArrayListExtra("issueinfo", (ArrayList<? extends Parcelable>) issueinfo);
            startActivity(intent);
        }
    }

    @Override
    public void onListItemLongClicked(String filePath, String fileName, int position) {
        AlertDialog dialog;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Want to delete " + fileName);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mAuth != null){
                    reference = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("history").child(filePath).child(fileName);
                    reference.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error != null){
                                new CustomToast(getApplicationContext(), "Error occured").show();
                            }else{
                                new CustomToast(getApplicationContext(), "Deleted").show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        toggleBtnView(false);
        switch (view.getId()){
            case R.id.l_a_h_btn:
                ViewList("locally");
                break;
            case R.id.s_h_btn:
                ViewList("searched");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(!ENBLD){
            toggleBtnView(true);
            if(ENBLD){
                history_list_view.setAdapter(null);
            }
        }else {
            super.onBackPressed();
        }
    }

    public void toggleBtnView(boolean display){
        ENBLD = display;
        hs_btn_l.setVisibility(ENBLD?View.VISIBLE:View.GONE);
        hs_btn_l.setEnabled(ENBLD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleBtnView(ENBLD);
        if(mList != null && !ENBLD) {
            adapter = new HistoryAdapter(mList, History.this);
            history_list_view.setAdapter(adapter);
        }
    }
}
