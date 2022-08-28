package com.busi.adi.aarogyam.account;


import androidx.annotation.NonNull;

import com.busi.adi.aarogyam.App;
import com.busi.adi.aarogyam.Model.ProfileDetails;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn {
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mReference;


    private OnCompleteAccountActivity onComplete;

    public SignIn(OnCompleteAccountActivity onComplete){
        this.onComplete = onComplete;
        mAuth = FirebaseAuth.getInstance();
    }

    public void doSignIn(String userEmail, String userPass) {
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
                                onComplete.OnComplete("Something went wrong");
                            }
                        });
                    }
                    onComplete.OnComplete("You are in");
                } else {
                    String message;
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        message = e.getMessage();
                    } catch(Exception e) {
                        message = e.getMessage();
                    }
                    onComplete.OnComplete(message);
                }
            }
        });
    }
}
