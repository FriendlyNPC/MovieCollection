package com.school.comp3717.moviecollection.tools;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;


public class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
    private static final int       FADE_IN_TIME = 1000;
    private              ImageView bmImage;
    private              Context   context;

    public DownloadPosterTask(Context context, ImageView bmImage) {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            // Transition drawable with a transparent drawable and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(context.getResources(), result)
                    });
            // Set background to loading bitmap
            bmImage.setImageDrawable(
                    new BitmapDrawable(context.getResources(), result));

            bmImage.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        }
    }
}


