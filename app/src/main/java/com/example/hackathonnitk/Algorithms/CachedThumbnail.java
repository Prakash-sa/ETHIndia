package com.example.hackathonnitk.Algorithms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.hackathonnitk.R;
import com.example.hackathonnitk.model.CacheParams;

import java.io.File;

public class CachedThumbnail extends AsyncTask<CacheParams, Void, Bitmap> {
    private Bitmap bitmap = null;
    private ImageView iv = null;

    @Override
    protected Bitmap doInBackground(CacheParams... params) {
        File file = new File(params[0].getStr());
        if (file.exists())
            bitmap = BitmapFactory.decodeFile(file.getPath());
        //        else {
        //            // implement the retrieval process from IPFS
        //            bitmap = BitmapFactory.decodeResource(contexts[0].getResources(), R.drawable.image);
        //            Log.d("GetCache", bitmap.getByteCount() + "");
        //        }
        iv = params[0].getImageView();
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null)
            iv.setImageBitmap(bitmap);
        else
            iv.setImageResource(R.drawable.image);
        super.onPostExecute(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
