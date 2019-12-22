package com.example.hackathonpune;

import java.util.List;

public class ImageUploadInfo {
    public String imageName;

    public String imageURL;
    public Double size;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name,String url,Double size) {

        this.imageName = name;
        this.imageURL= url;
        this.size=size;
    }

    public String getImageName() {
        return imageName;
    }

    public Double getSize(){return size;}

    public String getImageURL() {
        return imageURL;
    }
}

