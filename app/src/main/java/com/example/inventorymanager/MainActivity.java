/*
package com.example.inventorymanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static TextView resultTextView;
    public static ImageView imageView;
    private Button scanButton;
    private Button firstPictureButton;
    private final int REQUEST = 1;
    private String pathToFile;
    private File image;
    private PhotoActivity myPhotoActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        resultTextView = findViewById(R.id.barcodetextview);
        scanButton = findViewById(R.id.buttonscan);
        firstPictureButton = findViewById(R.id.firstPicButton);
        imageView = findViewById(R.id.imageView);
        myPhotoActivity = new PhotoActivity(this.getPackageManager(),this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));


        //mStorageRef = FirebaseStorage.getInstance().getReference();

        //mProgressRef = new ProgressDialog(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        firstPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = myPhotoActivity.dispatchTakePictureIntent();
                if(takePictureIntent != null) {
                    startActivityForResult(takePictureIntent, REQUEST);
                }
            }
        });

        pathToFile = myPhotoActivity.getPhotoURI().toString();
        Log.d("Location: ",  pathToFile);
    }
}
*/

package com.example.inventorymanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private StorageReference storageReference;
    public static TextView resultTextView, firstPic;
    public static ImageView imageView;
    private Button scanButton, firstPictureButton;

    private final int IMAGE_REQUEST_ID = 1;

    private UploadTask uploadTask;
    private Uri imageUri;
    String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        resultTextView     = findViewById(R.id.barcodetextview);
        scanButton         = findViewById(R.id.buttonscan);
        firstPictureButton = findViewById(R.id.firstPicButton);
        imageView          = findViewById(R.id.imageView);
        firstPic           = findViewById(R.id.firstPic);

        storageReference = FirebaseStorage.getInstance().getReference();

        //mProgressRef = new ProgressDialog(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        firstPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestImage();
            }
        });
    }

    private void requestImage(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_ID);
    }


    private void saveInFirebase() {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please wait...");
            progressDialog.show();

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { //onFailure
                    Toast.makeText(MainActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //onProgress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Saved" + (int) progress + "%");
                            //selectBtn.setEnabled(true);
                            //saveBtn.setEnabled(false);
                            Toast.makeText(MainActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)/* throws Exception */ {
                                    downloadUrl = storageReference.child("Images/").getDownloadUrl().toString();
                                    System.out.println(downloadUrl);
                                    return storageReference.child("img" + IMAGE_REQUEST_ID).getDownloadUrl();
                                }
                            })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) { //onSuccess
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Got product image url successfully", Toast.LENGTH_SHORT).show();
                                                Log.i("DIRECT LINK", task.getResult().toString());
                                                firstPic.setText(task.getResult().toString());
                                                // https://bumptech.github.io/glide/
                                                Glide.with(MainActivity.this).load(task.getResult()).into(imageView);
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadTask = storageReference.child("img" + IMAGE_REQUEST_ID).putFile(imageUri);

            try {
                Bitmap bitmapImg = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //imageView.setImageBitmap(bitmapImg);
                saveInFirebase();
                //selectBtn.setEnabled(false);
                //saveBtn.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}