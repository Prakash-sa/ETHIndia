package com.example.hackathonpune.Algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.hackathonpune.R;

import java.io.File;

public class CachedThumbnail {
    public Bitmap getThumbnail(Context context, String filename) {
        File file = new File(context.getCacheDir(), filename);
        if (file.exists())
            return BitmapFactory.decodeFile(file.getPath());
        else {
            // implement the retrieval process from IPFS
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
            Log.d("", bmp.getByteCount() + "");
            return bmp;
        }
    }
}
