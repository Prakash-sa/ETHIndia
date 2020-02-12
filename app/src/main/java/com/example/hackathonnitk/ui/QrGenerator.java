package com.example.hackathonnitk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hackathonnitk.Algorithms.AES;
import com.example.hackathonnitk.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrGenerator extends AppCompatActivity {

    private TextView edittext;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        edittext=findViewById(R.id.qr_code_link);
        image=findViewById(R.id.qr_code);

        String username=getIntent().getStringExtra("Username");
        String imagename=getIntent().getStringExtra("Imagename");

        String useraes=new AES().hashWith256(username);
        String imageaes=new AES().hashWith256(imagename);

        Log.i("AES is given by:- ",useraes+"\n"+imageaes);
        String text="@89#594/-aAF>]=!~sdf54;&\n"+username+"\n"+imagename;
       // edittext.setText(text);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
