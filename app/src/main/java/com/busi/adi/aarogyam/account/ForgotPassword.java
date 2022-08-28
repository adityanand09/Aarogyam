package com.busi.adi.aarogyam.account;

import androidx.annotation.NonNull;

import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword {

    FirebaseAuth mAuth;
    private OnCompleteAccountActivity onComplete;
    public ForgotPassword(OnCompleteAccountActivity onComplete){
        this.onComplete = onComplete;
        mAuth = FirebaseAuth.getInstance();
    }
    public void ResetPassword(String userEmail){
        mAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onComplete.OnComplete("Password reset link sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onComplete.OnComplete(e.getMessage());
            }
        });
    }
}
