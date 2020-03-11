package com.example.hackathonnitk.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class Parsejson {

    List<String>imagestring;
    List<String>imagename;

    public List<String> getstring(String response){
        GsonBuilder gsonBuilder=new GsonBuilder();
        Gson gson=gsonBuilder.create();
        ImagesList imagesList=  gson.fromJson(response,ImagesList.class) ;
        imagestring=imagesList.image_list;
        imagename.clear();
        return imagename;

    }


}
