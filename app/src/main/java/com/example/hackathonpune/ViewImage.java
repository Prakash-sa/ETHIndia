package com.example.hackathonpune;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ViewImage extends AppCompatActivity {

    ImageView imageView2;
    String url;
    FloatingActionButton downloadupbt,downloadseenbt,deletebt;

    ImageConverter imageConverter;
    Upload upload;
    Bitmaptransfer bitmaptransfer;
    StoreImage storeimage;

    public Context mcontext;

    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));

        setContentView(R.layout.activity_view_image);

        this.mcontext=ViewImage.this;
        imageView2=findViewById(R.id.imageView2);
        downloadupbt=findViewById(R.id.downloadupgr);
        downloadseenbt=findViewById(R.id.downloadseen);
        deletebt=findViewById(R.id.deleteimage);
        Intent intent=getIntent();
        final String keyofimage= intent.getStringExtra("keyis");
        upload=new Upload();
        bitmaptransfer=new Bitmaptransfer();
        imageConverter=new ImageConverter();
        storeimage=new StoreImage();



        imageView2.setImageBitmap(bitmaptransfer.getBitmap_transfer());


        downloadupbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send=imageConverter.getStringFromBitmap(bitmaptransfer.getBitmap_transfer());
                upload.upploadimagedownload(ViewImage.this,send);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ViewImage.this,"FileSavedAt: "+"/storage/emulated/0/DCIM/Camera/",Toast.LENGTH_LONG).show();
                    }
                }, 4000 );//time in milisecond



            }
        });
        downloadseenbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StoreImage storeImage=new StoreImage();
                    storeImage.storeImage(ViewImage.this,bitmaptransfer.getBitmap_transfer());
                    Toast.makeText(ViewImage.this,"FileSavedAt: "+"/storage/emulated/0/DCIM/Camera/",Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        deletebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(MainActivity.Database_Path);
                ref.child(keyofimage).removeValue();
                Log.i("key",keyofimage);
                startActivity(new Intent(ViewImage.this,DisplayImageActivity.class));
                finish();
            }
        });
    }

}
