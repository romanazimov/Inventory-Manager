package com.example.inventorymanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST = 1;
    private File image;
    private String pathToFile;
    private ImageView imageView;

    @Override
    public void onClick(View view) {
        imageView = findViewById(R.id.imageView);
        dispatchTakePictureIntent();
    }

    public void dispatchTakePictureIntent() {
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

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save the file's path for use with ACTION_VIEW intents
        pathToFile = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            //imageView.setImageBitmap(myBitmap);
            MainActivity.imageView.setImageBitmap(myBitmap);
        }

    }
}
