package com.busi.adi.aarogyam.Token;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.busi.adi.aarogyam.App;
import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.Model.AccessToken;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.busi.adi.aarogyam.listeners.OnComplete;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class FetchAccessToken extends AsyncTask<String, Void, AccessToken> {

    private Activity activity;
    private OnComplete onComplete;

    public FetchAccessToken(Activity activity, OnComplete onComplete){
        this.activity = activity;
        this.onComplete = onComplete;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected AccessToken doInBackground(String... strings) {
        AccessToken accessToken = null;
        try {
            URL authUrl = getUrlFromString(strings[0]);
            if(authUrl != null) {
                HttpURLConnection hc = createConnection(authUrl, strings[1], strings[2]);
                if(hc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    accessToken = getAccessToken(hc.getInputStream());
                    hc.disconnect();
                }
            }
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CustomToast(activity, "Something went Wrong").show();
                }
            });

        }
        return accessToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private HttpURLConnection createConnection(URL authUrl, String username, String pass) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        HttpURLConnection hc = null;
        String hashedString = getPassHash(authUrl.toString(), pass);
        hc = (HttpURLConnection) authUrl.openConnection();
        hc.setRequestMethod("GET");
        hc.setDoInput(true);
        hc.setDoOutput(true);
        hc.setRequestProperty("Authorization", "Bearer " + username + ":" + hashedString);
        hc.connect();
        return hc;
    }

    private URL getUrlFromString(String string) throws MalformedURLException {
        URL url = new URL(string);
        return url;
    }

    private AccessToken getAccessToken(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccessToken accessToken = null;
        accessToken = mapper.readValue(inputStream, AccessToken.class);
        return accessToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPassHash(String authUrl, String pass) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(pass.getBytes(), "HmacMD5");
        String computedHashString = "";
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(keySpec);
        byte[] result = mac.doFinal(authUrl.getBytes());
        Base64.Encoder encoder = Base64.getEncoder();
        computedHashString = encoder.encodeToString(result);
        return computedHashString;
    }
    @Override
    protected void onPostExecute(AccessToken accessToken) {
        App.accessToken = accessToken;
        onComplete.OnComplete("access_token", null);
    }

}
