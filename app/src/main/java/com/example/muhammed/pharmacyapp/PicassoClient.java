package com.example.muhammed.pharmacyapp;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Bahaa on 22/10/2017.
 */
public class PicassoClient {
    public static void downloadimage(Context c, String imageurl, ImageView img) {
        if (imageurl.length() > 0 && imageurl != null) {
            Picasso.with(c).load(imageurl).into(img);
        } else {
            Picasso.with(c).load(R.mipmap.ic_launcher_person).into(img);
        }

    }
}
