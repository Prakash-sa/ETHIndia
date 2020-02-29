package com.example.hackathonnitk.Algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.media.ThumbnailUtils.*;

public class CacheImage {
    private static File file = null;
    private static Bitmap bitmap = null;
    private File cacheDir = null;

    // constructor
    public CacheImage(Context context) {
        this.cacheDir = context.getExternalCacheDir();
        // this.context = context;

    }

    // provide option to delete last cache file
    public static void deleteCacheObject() {
        file.delete();
    }

    // generate compressed image for caching
    public void cacheFromBitmap(Bitmap bmp, String fileName) {

        bitmap = bmp;


        file = new File(cacheDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        (new CacheImageTask()).execute(bmp);
    }

    // save method for separating save process from creation process
    public static void saveCacheObject() throws IOException {
        if (file.exists()) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (file.createNewFile()) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // delete cached thumbnail
    public void deleteCacheObject(String filename) {
        File temp = new File(cacheDir, filename);
        if (temp.exists())
            temp.delete();
    }

    private static class CacheImageTask extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            //            bitmap = Bitmap.createScaledBitmap(bmp, 300, 300, true);
            //            while (bitmap.getByteCount() > 30 * 1024) {
            //                ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            //                bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()));
            //                try {
            //                    stream.close();
            //                } catch (IOException e) {
            //                    e.printStackTrace();
            //                }
            //            }


                bitmap = extractThumbnail(bitmap, 300, 300);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // save the created object
            try {
                saveCacheObject();
            } catch (IOException e) {
                e.printStackTrace();

            }
            super.onPostExecute(aVoid);
        }
    }
}
