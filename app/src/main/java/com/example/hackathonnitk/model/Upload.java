package com.example.hackathonnitk.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.hackathonnitk.Algorithms.ImageConverter;
import com.example.hackathonnitk.Algorithms.StoreImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Upload {

    Ipfssendflask ipfssendflask=new Ipfssendflask();
    final String url11= "";

    public void upploadimageflask(final String username,String image) {
        final String url1=url11+"jj";

        final String image11=image;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(url1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    OutputStream os=urlConnection.getOutputStream();

                    DataOutputStream wr = new DataOutputStream(os);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("key1" , image11);

                        wr.writeBytes(obj.toString());
                        Log.i("JSON Input", obj.toString());
                        wr.flush();
                        wr.close();
                        os.close();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    int responseCode = urlConnection.getResponseCode();
                    Log.i("RsponseCode", "is "+responseCode);

                    if(responseCode == HttpURLConnection.HTTP_OK){
                        String server_response = readStream(urlConnection.getInputStream());
                        Log.i("Response",server_response);

                        ipfssendflask.upploadimageflask(username,server_response);


                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void upploadimagedownload(final Context context,String image) {
        final String url1=""+"js";
        final String filepath;
        final String image11=image;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(url1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    OutputStream os=urlConnection.getOutputStream();

                    DataOutputStream wr = new DataOutputStream(os);

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("key1" , image11);

                        wr.writeBytes(obj.toString());
                        Log.i("JSON Input", obj.toString());
                        wr.flush();
                        wr.close();
                        os.close();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    int responseCode = urlConnection.getResponseCode();
                    Log.i("RsponseCode", "is "+responseCode);

                    if(responseCode == HttpURLConnection.HTTP_OK){
                        String server_response = readStream(urlConnection.getInputStream());
                        Log.i("Response",server_response);
                        ImageConverter imageConverter=new ImageConverter();
                        Bitmap bm=imageConverter.getBitmapFromString(server_response);
                        StoreImage storeImage=new StoreImage();
                        storeImage.storeImage(context,bm);

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
