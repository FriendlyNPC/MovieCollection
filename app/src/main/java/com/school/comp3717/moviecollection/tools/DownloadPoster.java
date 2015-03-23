package com.school.comp3717.moviecollection.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.List;

public class DownloadPoster {

    private static final String URL          = "http://image.tmdb.org/t/p/w185";
    private static final String TAG          = "DownloadPoster";
    private static final int    FADE_IN_TIME = 500;

    public static void getPoster(Context context, String posterUrl, ImageView imageView) {
        String imageUrl = URL + posterUrl;
        List<Bitmap> bitmaps;
        bitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(imageUrl, ImageLoader.getInstance().getMemoryCache());

        if (bitmaps.isEmpty()) {
            Log.d(TAG, "Poster not found in cache");
            ImageLoader.getInstance().displayImage(imageUrl, imageView);
        } else {
            Log.d(TAG, "Poster found in cache");
            // Transition drawable with a transparent drawable and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(context.getResources(), bitmaps.get(0))
                    });
            // Set background to loading bitmap
            imageView.setImageDrawable(
                    new BitmapDrawable(context.getResources(), bitmaps.get(0)));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        }
    }
}
