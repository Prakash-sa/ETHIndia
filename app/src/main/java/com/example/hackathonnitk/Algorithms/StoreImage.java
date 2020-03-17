package com.example.hackathonnitk.Algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreImage {

    public String storeImage(final Context context,Bitmap image) throws IOException {

        File pictureFile = createImageFile();
        if (pictureFile == null) {
            Log.i("Error", "Error creating media file, check storage permissions: ");
            return "";
        }
        try {
            OutputStream stream = null;

            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);


            Log.i("FileSaveAt",pictureFile.toString());
            Filepathis filepathis=new Filepathis();
            filepathis.setFilepath(pictureFile.toString());
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.i("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.i("Error", "Error accessing file: " + e.getMessage());
        }
        return pictureFile.toString();


    }

    public String saveAudio(Context context,String s){

        File file=createAudioFile();
        byte[] decodedBytes = Base64.decode(s.getBytes(),1);

        try {

            FileOutputStream out = new FileOutputStream(file);
            out.write(decodedBytes);
            out.close();
        } catch (Exception e) {
            Log.e("Error", e.toString());

        }
        return file.toString();

    }



    public String saveVideo(Context context,String s){

        File file=createVideoFile();
        byte[] decodedBytes = Base64.decode(s.getBytes(),1);

        try {

            FileOutputStream out = new FileOutputStream(file);
            out.write(decodedBytes);
            out.close();
        } catch (Exception e) {
            Log.e("Error", e.toString());

        }
        return file.toString();

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image=null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e){
            e.printStackTrace();
        }

        // Save a file: path for using again
        String cameraFilePath = "file://" + image.getAbsolutePath();
        Log.i("Camera path:- ",cameraFilePath);
        return image;
    }

    private File createVideoFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image=null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".mp4",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e){
            e.printStackTrace();
        }

        // Save a file: path for using again
        String cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    private File createAudioFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image=null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".mp3",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e){
            e.printStackTrace();
        }

        // Save a file: path for using again
        String cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }


}
