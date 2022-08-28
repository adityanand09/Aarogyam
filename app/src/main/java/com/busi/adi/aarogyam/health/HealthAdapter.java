package com.busi.adi.aarogyam.health;

import android.content.Context;

import com.busi.adi.aarogyam.App;
import com.busi.adi.aarogyam.Model.HealthDiagnosis;
import com.busi.adi.aarogyam.Model.HealthItem;
import com.busi.adi.aarogyam.Model.HealthSymptomSelector;
import com.busi.adi.aarogyam.Model.IssueInfo;
import com.busi.adi.aarogyam.listAdapters.diagnosis.HDAdapter;
import com.busi.adi.aarogyam.listAdapters.diagnosis.HIAdapter;
import com.busi.adi.aarogyam.listAdapters.diagnosis.HIIAdapter;
import com.busi.adi.aarogyam.listAdapters.diagnosis.HSSAdapter;

import java.util.List;

public class HealthAdapter {

    private static final String TAG = "HA";
    List<?> mList = null;
    Context mContext = null;
    public HealthAdapter(List<?> list, Context context){
        mList = list;
        mContext = context;
    }

    public Object load(int itemId){
        switch (itemId){
            case App.BODY_LOCATION_SELECTOR:
            case App.BODY_SUB_LOCATION_SELECTOR:
                return new HIAdapter(mContext, (List<HealthItem>) mList);
            case App.HEALTH_SYMPTOM_SELECTOR:
                return new HSSAdapter(mContext, (List<HealthSymptomSelector>) mList);
            case App.LOAD_DIAGNOSIS_DATA:
                return new HDAdapter(mContext, (List<HealthDiagnosis>) mList);
            case App.HEALTH_ISSUE_INFO:
                return new HIIAdapter(mContext, (List<IssueInfo>)mList);
        }
        return null;
    }
}
