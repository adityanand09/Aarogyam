package com.busi.adi.aarogyam.account;

import androidx.annotation.NonNull;

import com.busi.adi.aarogyam.App;
import com.busi.adi.aarogyam.Model.ProfileDetails;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfile {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;

    private OnCompleteAccountActivity onComplete;

    public UpdateProfile(OnCompleteAccountActivity callback){
        mAuth = FirebaseAuth.getInstance();
        this.onComplete = callback;
    }

    public void updateProfile(String userName, String full_name, String dob, String gender, String blood_group, String phone, String userEmail) {
        if(mAuth != null){
            FirebaseUser mUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference().child("users").child(mUser.getUid());
            ProfileDetails profile = new ProfileDetails(userName, full_name, dob, gender, blood_group, phone, userEmail);
            App.profile = profile;
            mReference.child("profile").setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) onComplete.OnComplete("Account updated");
                }
            });
        }
    }
}
