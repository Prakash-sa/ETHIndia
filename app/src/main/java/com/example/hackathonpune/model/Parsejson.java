package com.example.hackathonpune.model;

import android.util.Log;

import org.json.JSONObject;

import java.util.List;

public class Parsejson {

    List<String>imagestring;

    public void getstring(String response){
        try{
            JSONObject jsonObject=null;
            jsonObject=new JSONObject(response);
            JSONObject timeobject=jsonObject.getJSONObject("");
            imagestring.add(timeobject.getString("Image"));
        }catch (Exception e){
            e.printStackTrace();
            Log.i("Parsejson",e.getLocalizedMessage());
        }

    }
     public List<String> getlist(){
        return imagestring;
     }
}
