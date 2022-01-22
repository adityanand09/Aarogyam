package com.aarogyam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aarogyam.Model.ProfileDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity implements View.OnClickListener {

    private static int iun = View.GONE, iue = View.VISIBLE, iup = View.GONE, iucp = View.GONE, ugm = View.GONE;
    private static boolean iuee = true;
    private static EditText input_user_name, input_user_email, input_user_pass, input_user_confirm_pass;
    private static Button submit_user_login;
    private static ProgressBar mProgressBar;
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;
    private static String full_name, userEmail, userPass, userConfirmPass;
    private static TextView user_greeting_msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        input_user_name = findViewById(R.id.input_user_name);
        input_user_email = findViewById(R.id.input_user_email);
        input_user_pass = findViewById(R.id.input_user_pass);
        input_user_confirm_pass = findViewById(R.id.input_user_confirm_pass);
        user_greeting_msg = findViewById(R.id.user_greeting_msg);
        mProgressBar = findViewById(R.id.account_progress_bar);
        if(savedInstanceState != null){
            input_user_name.setVisibility(savedInstanceState.getInt("iun"));
            input_user_email.setVisibility(savedInstanceState.getInt("iue"));
            input_user_email.setEnabled(savedInstanceState.getBoolean("iuee"));
            input_user_pass.setVisibility(savedInstanceState.getInt("iup"));
            input_user_confirm_pass.setVisibility(savedInstanceState.getInt("iucp"));
            user_greeting_msg.setVisibility(savedInstanceState.getInt("ugm"));
        }
        setTitle("Login");
        mAuth = FirebaseAuth.getInstance();
        input_user_name.setOnClickListener(this);
        input_user_email.setOnClickListener(this);
        input_user_pass.setOnClickListener(this);
        input_user_confirm_pass.setOnClickListener(this);
        submit_user_login = findViewById(R.id.submit_user_login);

        submit_user_login.setOnClickListener(view1 -> {
            userEmail = input_user_email.getText().toString().trim();
            mProgressBar.setVisibility(View.VISIBLE);
            if(userEmail.isEmpty()){
                input_user_email.setError("Email required");
                input_user_email.requestFocus();
                mProgressBar.setVisibility(View.GONE);
                return;
            }
            iuee = false;
            input_user_email.setEnabled(false);
            checkForRegisteredEmail();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("iun", iun);
        outState.putInt("iue", iue);
        outState.putBoolean("iuee", iuee);
        outState.putInt("iup", iup);
        outState.putInt("iucp", iucp);
        outState.putInt("ugm", ugm);
        super.onSaveInstanceState(outState);
    }

    private void doSignUp() {
        if(getInput()) {
            mProgressBar.setVisibility(View.VISIBLE);
            createAccount();
        }
    }

    private void doSignIn() {
        if(getInput()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(mAuth!=null){
                            mDatabase = FirebaseDatabase.getInstance();
                            mReference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            mReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        App.profile = snapshot.getValue(ProfileDetails.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        clearInput();
                        new CustomToast(Account.this, "You are in").show();
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            new CustomToast(Account.this, "Invalid credentials").show();
                        } catch(Exception e) {
                        }
                    }
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public boolean getInput(){
        if(userEmail.isEmpty()){
            input_user_email.setError("Email required");
            input_user_email.requestFocus();
            return false;
        }
        if(input_user_name.getVisibility() == View.VISIBLE){
            full_name = input_user_name.getText().toString().trim();
            if (full_name.isEmpty()) {
                input_user_name.setError("Name required");
                input_user_name.requestFocus();
                return false;
            }
        }
        userPass = input_user_pass.getText().toString().trim();
        if(userPass.isEmpty()){
            input_user_pass.setError("Password required");
            input_user_pass.requestFocus();
            return false;
        }
        if(input_user_confirm_pass.getVisibility() == View.VISIBLE){
            userConfirmPass = input_user_confirm_pass.getText().toString().trim();
            if(userConfirmPass.isEmpty()){
                input_user_confirm_pass.setError("Confirm password required");
                input_user_confirm_pass.requestFocus();
                return false;
            }
            if(!userPass.equals(userConfirmPass)){
                input_user_confirm_pass.setError("Password do not match");
                input_user_confirm_pass.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void clearInput(){
        userEmail = null;
        full_name = null;
        userPass = null;
        userConfirmPass = null;
        input_user_name.setText(null);
        input_user_email.setText(null);
        input_user_pass.setText(null);
        input_user_confirm_pass.setText(null);
    }

    public void checkForRegisteredEmail(){
        mAuth.fetchSignInMethodsForEmail(userEmail).addOnCompleteListener(task -> {
            mProgressBar.setVisibility(View.GONE);
            if(task.isSuccessful()){
                if(task.getResult().getSignInMethods().isEmpty()){
                    setTitle("Sign Up");
                    if(input_user_name.getVisibility() == View.GONE) {
                        iun = View.VISIBLE;
                        iup = View.VISIBLE;
                        iucp = View.VISIBLE;
                        ugm = View.VISIBLE;
                        input_user_pass.setVisibility(View.VISIBLE);
                        input_user_name.setVisibility(View.VISIBLE);
                        input_user_confirm_pass.setVisibility(View.VISIBLE);
                        user_greeting_msg.setText("New User");
                        user_greeting_msg.setVisibility(View.VISIBLE);
                        submit_user_login.setText("Sign Up");
                    }else {
                        doSignUp();
                    }
                }else{
                    if(input_user_pass.getVisibility() == View.GONE){
                        iup = View.VISIBLE;
                        ugm = View.VISIBLE;
                        input_user_pass.setVisibility(View.VISIBLE);
                        user_greeting_msg.setText("Welcome Back");
                        user_greeting_msg.setVisibility(View.VISIBLE);
                    }else {
                        doSignIn();
                    }
                }
            }
        });
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                updateProfile();
            }else{
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    input_user_pass.setError("Weak password");
                    input_user_pass.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            }

        });
    }

    private void updateProfile() {
        if(mAuth != null){
            FirebaseUser mUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference().child("users").child(mUser.getUid());
            ProfileDetails profile = new ProfileDetails("@"+full_name.toLowerCase(), full_name, "null", "null", "null", "null", userEmail);
            App.profile = profile;
            mReference.child("profile").setValue(profile);
            mProgressBar.setVisibility(View.GONE);
            clearInput();
            new CustomToast(Account.this, "Account Created").show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(input_user_pass.getVisibility() == View.VISIBLE) {
            setTitle("Login");
            input_user_name.setVisibility(View.GONE);
            input_user_pass.setVisibility(View.GONE);
            input_user_confirm_pass.setVisibility(View.GONE);
            user_greeting_msg.setVisibility(View.GONE);
            input_user_email.setEnabled(true);
            submit_user_login.setText("LOGIN");
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.input_user_name:
            case R.id.input_user_email:
            case R.id.input_user_pass:
            case R.id.input_user_confirm_pass:
                EditText et = view.findViewById(view.getId());
                et.setError(null);
        }
    }
}
