package com.busi.adi.aarogyam.account;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.busi.adi.aarogyam.App;
import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.Model.ProfileDetails;
import com.busi.adi.aarogyam.R;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity implements View.OnClickListener , OnCompleteAccountActivity {

    private static final String TAG = "Profile_frag";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private EditText full_name, gender, blood_group, phone, username;
    private TextView email, dob;
    private ImageButton edit_profile;
    private Button update_profile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setTitle("Personal Details");
        mAuth = FirebaseAuth.getInstance();

        if(mAuth != null){
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("profile");
        }

        username = findViewById(R.id.prof_username);
        full_name = findViewById(R.id.prof_name);
        dob = findViewById(R.id.prof_dob);
        gender = findViewById(R.id.prof_gender);
        blood_group = findViewById(R.id.prof_bg);
        phone = findViewById(R.id.prof_mobile);
        email = findViewById(R.id.prof_email);
        edit_profile = findViewById(R.id.prof_edit);
        update_profile = findViewById(R.id.prof_update);
        progressBar = findViewById(R.id.progress_profile);

        DisableChange();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        progressBar.setVisibility(View.VISIBLE);
        if(activeNetwork != null && activeNetwork.isConnected()) {
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        App.profile = snapshot.getValue(ProfileDetails.class);
                        username.setText(App.profile.getUsername());
                        full_name.setText(App.profile.getFull_name());
                        dob.setText(App.profile.getDob());
                        gender.setText(App.profile.getGender());
                        blood_group.setText(App.profile.getBlood_group());
                        phone.setText(App.profile.getPhone());
                        email.setText(App.profile.getEmail());
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            new CustomToast(Profile.this, "App not connected to internet").show();
        }

        edit_profile.setOnClickListener(this);
        update_profile.setOnClickListener(this);
        dob.setOnClickListener(this);
    }

    public void EnableChange(){
        username.setEnabled(true);
        full_name.setEnabled(true);
        dob.setEnabled(true);
        gender.setEnabled(true);
        blood_group.setEnabled(true);
        phone.setEnabled(true);
        update_profile.setVisibility(View.VISIBLE);
    }

    public void DisableChange(){
        username.setEnabled(false);
        full_name.setEnabled(false);
        dob.setEnabled(false);
        gender.setEnabled(false);
        blood_group.setEnabled(false);
        phone.setEnabled(false);
        update_profile.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prof_edit:
                EnableChange();
                break;
            case R.id.prof_dob:
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Calendar cal = Calendar.getInstance();
                try {
                    Date d = sdf.parse(sdf.format(cal.getTime()));
                    cal.setTime(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DatePickerDialog dpd = new DatePickerDialog(Profile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        try {
                            cal.setTime(sdf.parse(i1+1+"/"+i2+"/"+i));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String date = i2 + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + ", " + i;
                        dob.setText(date);
                    }
                }, cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.prof_update:
                DisableChange();
                progressBar.setVisibility(View.VISIBLE);
                UpdateProfile updateProfile = new UpdateProfile(Profile.this);
                String un = username.getText().toString();
                if(un.charAt(0)!='@') un = '@'+un;
                updateProfile.updateProfile(un,
                        full_name.getText().toString(),
                        dob.getText().toString(),
                        gender.getText().toString(),
                        blood_group.getText().toString(),
                        phone.getText().toString(),
                        email.getText().toString());
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void OnComplete(String message) {
        progressBar.setVisibility(View.GONE);
        new CustomToast(this, message).show();
    }
}
