package com.example.hackathonnitk.Algorithms;

import android.graphics.Bitmap;

public class Bitmaptransfer {
    private static Bitmap bitmap_transfer;
    public static Bitmap getBitmap_transfer() {
        return bitmap_transfer;
    }

    public static void setBitmap_transfer(Bitmap bitmap_transfer_param) {
        bitmap_transfer = bitmap_transfer_param;
    }
}
