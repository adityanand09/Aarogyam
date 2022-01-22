package com.aarogyam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aarogyam.Model.HistoryItem;
import com.aarogyam.Model.IssueInfo;
import com.aarogyam.adapters.HistoryAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistorySearched extends AppCompatActivity {

    private List<IssueInfo> mList;
    private static HistorySearched activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_searched);
        Intent intent = getIntent();
        mList = intent.getParcelableArrayListExtra("issueinfo");
        activity = this;
        displaySearched();
    }

    private void displaySearched(){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.his_searched, new DiagnosisFragment(mList, App.HEALTH_ISSUE_INFO, ListView.CHOICE_MODE_NONE, "Detailed Info"))
                .commit();
    }
}
