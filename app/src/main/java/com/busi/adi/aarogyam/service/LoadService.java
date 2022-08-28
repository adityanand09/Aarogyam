package com.busi.adi.aarogyam.service;

import android.app.Activity;
import android.os.AsyncTask;

import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.Model.Pair;
import com.busi.adi.aarogyam.listeners.OnComplete;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;

public class LoadService extends AsyncTask<Pair, Void, Object> {

    private OnComplete onComplete;
    Activity activity;
    public LoadService(Activity activity, OnComplete onComplete){
        this.activity = activity;
        this.onComplete = onComplete;
    }

    @Override
    protected Object doInBackground(Pair... pairs) {
        URL url = null;
        HttpURLConnection hc = null;
        try {
            url = new URL(pairs[0].url);
            hc = (HttpURLConnection) url.openConnection();
            hc.setRequestMethod("GET");
            hc.setDoInput(true);
            hc.connect();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(hc.getInputStream(), pairs[0].valueTypeRef);
        }catch (Exception e){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CustomToast(activity, "Something went Wrong").show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        onComplete.OnComplete("service", object);
    }
}
