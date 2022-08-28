package com.busi.adi.aarogyam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.busi.adi.aarogyam.Model.IssueInfo;
import com.busi.adi.aarogyam.health.DiagnosisFragment;

import java.util.List;

public class SearchedHistory extends AppCompatActivity {

    private List<IssueInfo> mList;
    private static SearchedHistory activity;

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
