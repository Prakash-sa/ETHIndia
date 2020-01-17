package com.example.hackathonpune.Algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheImage {
    private File cacheDir = null;
    // private Context context = null;
    private File file = null;
    private Bitmap bitmap = null;

    // constructor
    public CacheImage(Context context) {
        this.cacheDir = context.getCacheDir();
        // this.context = context;

    }

    // generate compressed image for caching
    public void cacheFromBitmap(Bitmap bitmap, String fileName) throws IOException {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        while (this.bitmap.getByteCount() > 30 * 1024) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            this.bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            this.bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()));
            stream.close();
        }

        this.file = new File(this.cacheDir, fileName);
        if (!this.file.exists())
            this.file.createNewFile();

        // save the created object
        saveCacheObject();
    }

    // save method for separating save process from creation process
    public void saveCacheObject() throws IOException {
        if (this.file.exists()) {
            try (FileOutputStream out = new FileOutputStream(this.file)) {
                this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (this.file.createNewFile()) {
            try (FileOutputStream out = new FileOutputStream(this.file)) {
                this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    // provide option to delete last cache file
    public void deleteCacheObject() {
        this.file.delete();
    }

    // delete cached thumbnail
    public void deleteCacheObject(String filename) {
        File temp = new File(cacheDir, filename);
        if (temp.exists())
            temp.delete();
    }
}
