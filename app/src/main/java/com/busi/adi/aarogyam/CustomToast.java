package com.busi.adi.aarogyam;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    Context ctxt;
    String msg;
    public CustomToast(Context context, String message) {
        msg = message;
        ctxt = context;
    }

    public void show() {
        Toast toast = Toast.makeText(ctxt, msg, Toast.LENGTH_LONG);
        View view = toast.getView();
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FFFFFFFF"));
        text.setBackgroundColor(Color.parseColor("#000000"));
        view.setBackgroundColor(Color.parseColor("#000000"));
        toast.show();
    }

}
