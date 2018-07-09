package com.example.muhammed.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class FullScreenImage extends AppCompatActivity {

    ImageView imageView;
    String imagePath;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = (ImageView) findViewById(R.id.imageFullScreen);
        intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");

        PicassoClient.downloadimage(FullScreenImage.this, imagePath, imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
