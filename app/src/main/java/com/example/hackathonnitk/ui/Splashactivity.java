package com.example.hackathonnitk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hackathonnitk.R;

public class Splashactivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivity);

        imageView=findViewById(R.id.imageView3);
        textView=findViewById(R.id.textView3);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(Splashactivity.this, Everyone.class);
                startActivity(intent);
                finish();
            }
        },2000);


    }
}
