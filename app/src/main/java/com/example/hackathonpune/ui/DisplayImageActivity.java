package com.example.hackathonpune.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonpune.Algorithms.ImageConverter;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.MainActivity;
import com.example.hackathonpune.R;
import com.example.hackathonpune.model.Ipfssendflask;
import com.example.hackathonpune.model.Parsejson;
import com.example.hackathonpune.model.Receive;
import com.example.hackathonpune.Adapter.RecyclerViewAdapter;
import com.example.hackathonpune.model.Upload;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    private Ipfssendflask ipfssendflask=new Ipfssendflask();

    private String username;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener firebaseAUthList;

    ImageConverter imageConverter;
    Receive receive;
    Upload upload;
    Bitmap bitmap;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private static final int PICK_VIDEO_CAMERA=3;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 100;

    FloatingActionButton uploadcamera,uploadgallery,uploadvideo;
    RecyclerView.Adapter adapter ;
    TextView textView;
    ProgressDialog progressDialog;
    List<ImageUploadInfo> list = new ArrayList<>();
    List<String>keyofimage=new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        username=currentUser.getEmail();
        if(currentUser==null){
            startActivity(new Intent(DisplayImageActivity.this, Signinup.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        setTitle("DiskSpace");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(DisplayImageActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
               // Toast.makeText(DisplayImageActivity.this, "Please give your permission.", Toast.LENGTH_LONG).show();
            }
        }

        if(checkPermissionwrite()){
          //  Toast.makeText(this,"Write on",Toast.LENGTH_LONG).show();
        }
        else requestPermission();

        upload=new Upload();
        receive=new Receive();
        imageConverter=new ImageConverter();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        uploadcamera=findViewById(R.id.uploadcamera);
        uploadgallery=findViewById(R.id.uploadgallery);
        uploadvideo=findViewById(R.id.uploadvideo);
        textView=findViewById(R.id.sizeis);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(DisplayImageActivity.this,3));



        progressDialog = new ProgressDialog(DisplayImageActivity.this);
        progressDialog.setMessage("Loading Images From Database.");
        progressDialog.show();

        updaterecyclerview();



        uploadgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);

            }
        });

        uploadcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
                }
            }
        });

        uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new
                        Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"SelectVideo"),PICK_VIDEO_CAMERA);
            }
        });



    }

    private void updaterecyclerview() {
        ipfssendflask.upploadimagedownload(username);

        ImageUploadInfo imageUploadInfo=null;
        Parsejson parsejson=new Parsejson();
        List<String>s=parsejson.getlist();
        list.clear();
        for(int i=0;i<s.size();i++){
            imageUploadInfo=new ImageUploadInfo("",s.get(i),0.0);
            list.add(imageUploadInfo);
        }
        adapter = new RecyclerViewAdapter(DisplayImageActivity.this, list,keyofimage);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();

    }

    private boolean checkPermissionwrite() {
        int result = ContextCompat.checkSelfPermission(DisplayImageActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(DisplayImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DisplayImageActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DisplayImageActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DisplayImageActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(DisplayImageActivity.this, "Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DisplayImageActivity.this, "Write Permission granted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayImageActivity.this, "Write Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode==PICK_FROM_GALLARY) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(imgDecodableString);
                String encodedImage = imageConverter.getStringFromBitmap(bitmap);
                upload.upploadimageflask(username,encodedImage);
            }
            if(requestCode==PICK_FROM_CAMERA){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                    String encodedImage = imageConverter.getStringFromBitmap(imageBitmap);
                    upload.upploadimageflask(username,encodedImage);

            }
            if(requestCode==PICK_VIDEO_CAMERA){

                Uri selectedVideoUri = data.getData();
                String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
                Cursor cursor = managedQuery(selectedVideoUri, projection, null, null, null);
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                String baseVideo=imageConverter.getStringFromBitmap(thumb);
                Log.i("VideoString ",baseVideo);
                upload.upploadimageflask(username,baseVideo);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id1=item.getItemId();
        if(id1==R.id.action_signout){
            mAuth.signOut();
            startActivity(new Intent(DisplayImageActivity.this,Signinup.class));
            finish();
        }

        return super.onOptionsItemSelected(item);

    }



}
