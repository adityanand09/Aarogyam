package com.busi.adi.aarogyam.account;

import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUp {

    private static FirebaseAuth mAuth;

    private OnCompleteAccountActivity onComplete;
    public SignUp(OnCompleteAccountActivity onComplete){
        mAuth = FirebaseAuth.getInstance();
        this.onComplete = onComplete;
    }

    public void doSignUp(String userEmail, String userPass, String fullname){
        mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UpdateProfile updateProfile = new UpdateProfile(onComplete);
                updateProfile.updateProfile("@"+fullname.toLowerCase(),
                        fullname,
                        "NA",
                        "NA",
                        "NA",
                        "NA",
                        userEmail);
            }else{
                String message;
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    message = "Weak password";
                } catch (Exception e) {
                    message = e.getMessage();
                }
                onComplete.OnComplete(message);
            }
        });
    }
}
