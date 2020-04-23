package com.example.inventorymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    public static TextView resultTextView;
    private Button scanButton, firstPictureButton;
    //private ImageView mImageView;

    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.barcodetextview);
        scanButton = findViewById(R.id.buttonscan);
        firstPictureButton = findViewById(R.id.firstPicButton);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mProgressRef = new ProgressDialog(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        firstPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
                catch (Exception e)
                {
                    e.getStackTrace();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            mProgressRef.setMessage("Uploading Image...");
            mProgressRef.show();

            Uri File = data.getData();
            //StorageReference filepath = mStorageRef.child("Photos").child(uri.getLastPathSegment());
            StorageReference filePath = mStorageRef.child("images/item.jpg");

            filePath.putFile(File).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressRef.dismiss();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    Log.i("The URL : ", downloadUrl.toString());
                }
            });
        }
    }
}
