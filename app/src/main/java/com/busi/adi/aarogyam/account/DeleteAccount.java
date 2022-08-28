package com.busi.adi.aarogyam.account;

import androidx.annotation.NonNull;

import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccount {

    private OnCompleteAccountActivity callback;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    public DeleteAccount(OnCompleteAccountActivity callback){
        this.callback = callback;
        mAuth = FirebaseAuth.getInstance();
    }

    public void deleteAccount(){
        if(mAuth != null){
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
            mReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.OnComplete("Account deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.OnComplete("Something went wrong");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.OnComplete("Something went wrong");
                }
            });
        }
    }
}
