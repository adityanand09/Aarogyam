package com.busi.adi.aarogyam;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.busi.adi.aarogyam.Model.AilmentRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class LocallyAddedHistory extends AppCompatActivity implements View.OnClickListener {

    private EditText ailmentName, description, medicines;
    private ImageView ailImg, imgDwnld;
    private Button save;

    private String ailName, ailDes, ailMed;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Uri filePath;

    private ProgressDialog progressDialog;

    private AilmentRecord ailmentRecord;

    private Activity activity;

    private Uri mUri = null;

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        filePath = data.getData();
                        Glide.with(getApplicationContext()).load(filePath).into(ailImg);
                    }
                }
            });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locally_added_history);
        setTitle("Ailment record");
        activity = this;
        Intent intent = getIntent();
        ailmentRecord = intent.getParcelableExtra("ailmentRecord");
        ailmentName = findViewById(R.id.ail_name);
        description = findViewById(R.id.ail_des);
        medicines = findViewById(R.id.ail_med);
        ailImg = findViewById(R.id.ail_img);
        imgDwnld = findViewById(R.id.img_dwnld);
        ailImg.setOnClickListener(this);
        save = findViewById(R.id.his_submit);
        save.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        if(ailmentRecord != null){
            ShowRecord();
        }
        imgDwnld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUri != null){
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(mUri);
                    request.setTitle("Medi");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"image.png");

                    downloadmanager.enqueue(request);
                    new CustomToast(getApplicationContext(), "Downloading Image").show();
                }else{
                    new CustomToast(getApplicationContext(), "No Image").show();
                }
            }
        });
    }

    private void ShowRecord() {
        ailmentName.setText(ailmentRecord.ailmentName);
        description.setText(ailmentRecord.description);
        medicines.setText(ailmentRecord.medicines);
        String image_path = ailmentRecord.ailImgPath;
        storageRef = storage.getReference().child(image_path);
        Log.d("TAG", "ShowRecord: " + image_path);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Image");
        progressDialog.show();
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mUri = uri;
                Log.d("TAG", "onSuccess: " + uri);
                Glide.with(LocallyAddedHistory.this).load(uri.toString()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        new CustomToast(LocallyAddedHistory.this, "Image download failed").show();
                        progressDialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }
                }).into(ailImg);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                new CustomToast(LocallyAddedHistory.this, "Image download failed").show();
            }
        });
    }

    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ail_img:
                SelectImage();
                break;
            case R.id.his_submit:
                UploadRecord();
                break;
        }
    }

    private boolean success = false;
    private void UploadRecord() {
        ailName = ailmentName.getText().toString();
        if(ailName.isEmpty()){
            ailmentName.setError("Enter Name");
            ailmentName.requestFocus();
        }else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            if(mAuth != null){
                UploadImage();
                reference = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("history/locally").child(ailName);
                String ailDes = description.getText().toString();
                String ailMed = medicines.getText().toString();
                String ailImg = "users/" + mAuth.getCurrentUser().getUid() + "/history/locally/" + ailName;
                AilmentRecord record = new AilmentRecord(ailName, ailDes, ailMed, ailImg);
                reference.setValue(record).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new CustomToast(LocallyAddedHistory.this, "Description uploaded").show();
                        if(success) {
                            progressDialog.dismiss();
                        }else{
                            success = true;
                        }
                    }
                });
            }
        }

    }

    private void UploadImage() {
        if (filePath != null) {
            storageRef = storage.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("history/locally").child(ailName);
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(success) {
                        progressDialog.dismiss();
                    }else{
                        success = true;
                    }
                    new CustomToast(getApplicationContext(), "Image uploaded").show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new CustomToast(getApplicationContext(), "Image upload failed").show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress
                            = (100.0
                            * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });
        }else{
            progressDialog.dismiss();
        }
    }
}
