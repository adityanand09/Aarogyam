package com.busi.adi.aarogyam.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.R;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Account extends AppCompatActivity implements View.OnClickListener, OnCompleteAccountActivity {

    private static int iun = View.GONE, iue = View.VISIBLE, iup = View.GONE, iucp = View.GONE, ugm = View.GONE, fps = View.VISIBLE;
    private static boolean iuee = true;
    private static EditText input_user_name, input_user_email, input_user_pass, input_user_confirm_pass;
    private static Button submit_user_login;
    private static ProgressBar mProgressBar;
    private static FirebaseAuth mAuth;
    private static String full_name, userEmail, userPass, userConfirmPass;
    private static TextView user_greeting_msg, forgot_pass;

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
        forgot_pass = findViewById(R.id.forgot_pass);
        if(savedInstanceState != null){
            input_user_name.setVisibility(savedInstanceState.getInt("iun"));
            input_user_email.setVisibility(savedInstanceState.getInt("iue"));
            input_user_email.setEnabled(savedInstanceState.getBoolean("iuee"));
            input_user_pass.setVisibility(savedInstanceState.getInt("iup"));
            input_user_confirm_pass.setVisibility(savedInstanceState.getInt("iucp"));
            user_greeting_msg.setVisibility(savedInstanceState.getInt("ugm"));
            forgot_pass.setVisibility(savedInstanceState.getInt("fps"));
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
            CheckForRegisteredEmail();
        });

        forgot_pass.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("iun", iun);
        outState.putInt("iue", iue);
        outState.putBoolean("iuee", iuee);
        outState.putInt("iup", iup);
        outState.putInt("iucp", iucp);
        outState.putInt("ugm", ugm);
        outState.putInt("fps", fps);
        super.onSaveInstanceState(outState);

    }


    public boolean checkEmail(){
        userEmail = input_user_email.getText().toString().trim();
        if(userEmail.isEmpty()){
            input_user_email.setError("Email required");
            input_user_email.requestFocus();
            return false;
        }
        return true;
    }
    public boolean checkInput(){
        if(checkEmail() == false){
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
                input_user_confirm_pass.setError("Password does not match");
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

    public void CheckForRegisteredEmail(){
        mAuth.fetchSignInMethodsForEmail(userEmail).addOnCompleteListener(task -> {
            mProgressBar.setVisibility(View.GONE);
            if(task.isSuccessful()){
                if(task.getResult().getSignInMethods().isEmpty()){

                    //email not registered, perform signup;

                    setTitle("Sign Up");
                    if(input_user_name.getVisibility() == View.GONE) {
                        iun = View.VISIBLE;
                        iup = View.VISIBLE;
                        iucp = View.VISIBLE;
                        ugm = View.VISIBLE;
                        fps = View.GONE;
                        forgot_pass.setVisibility(View.GONE);
                        input_user_pass.setVisibility(View.VISIBLE);
                        input_user_name.setVisibility(View.VISIBLE);
                        input_user_confirm_pass.setVisibility(View.VISIBLE);
                        user_greeting_msg.setText("New User");
                        user_greeting_msg.setVisibility(View.VISIBLE);
                        submit_user_login.setText("Sign Up");
                    }else {
                        if(checkInput() == true) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            SignUp signUp = new SignUp(Account.this);
                            signUp.doSignUp(userEmail, userPass, full_name);
                        }
                    }
                }else{
                    //email is registered, perform signin
                    if(input_user_pass.getVisibility() == View.GONE){
                        iup = View.VISIBLE;
                        ugm = View.VISIBLE;
                        fps = View.VISIBLE;
                        forgot_pass.setVisibility(View.VISIBLE);
                        input_user_pass.setVisibility(View.VISIBLE);
                        user_greeting_msg.setText("Welcome Back");
                        user_greeting_msg.setVisibility(View.VISIBLE);
                    }else {
                        if(checkInput() == true){
                            mProgressBar.setVisibility(View.VISIBLE);
                            SignIn signIn = new SignIn( Account.this);
                            signIn.doSignIn(userEmail, userPass);

                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(input_user_pass.getVisibility() == View.VISIBLE) {
            setTitle("Login");
            input_user_name.setVisibility(View.GONE);
            input_user_pass.setVisibility(View.GONE);
            input_user_confirm_pass.setVisibility(View.GONE);
            user_greeting_msg.setVisibility(View.GONE);
            forgot_pass.setVisibility(View.VISIBLE);
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
                break;
            case R.id.forgot_pass:
                if(checkEmail() == true){
                    ForgotPassword forgotPassword = new ForgotPassword(this);
                    forgotPassword.ResetPassword(userEmail);
                }
        }
    }

    @Override
    public void OnComplete(String message) {
        mProgressBar.setVisibility(View.INVISIBLE);
        clearInput();
        new CustomToast(this, message).show();
        finish();
    }
}
