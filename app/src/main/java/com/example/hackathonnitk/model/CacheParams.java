package com.example.hackathonnitk.model;

import android.widget.ImageView;

public class CacheParams {
    private String str = null;
    private ImageView imageView = null;

    public CacheParams(ImageView iv, String filename) {
        this.setImageView(iv);
        this.setStr(filename);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
