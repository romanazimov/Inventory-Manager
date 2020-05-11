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

        //mStorageRef = FirebaseStorage.getInstance().getReference();

        //mProgressRef = new ProgressDialog(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        /*
        firstPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PhotoActivity.class));
            }
        });

         */

        firstPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Make sure there's camera activity to handle the camera intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create an empty File for the soon created File
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                // Error when creating the File
                e.printStackTrace();
            }
            // If File created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.inventorymanager.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST);
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",   // suffix
                storageDir      // directory
        );
        // Save the file's path for use with ACTION_VIEW intents
        pathToFile = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathToFile);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            galleryAddPic();
        }
    }

}