package com.example.hackathonpune.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.os.AsyncTask;
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
import com.example.hackathonpune.Algorithms.StoreImage;
import com.example.hackathonpune.ConstantsIt;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.MainActivity;
import com.example.hackathonpune.R;
import com.example.hackathonpune.model.ImagesList;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.icu.util.ULocale.getName;

public class DisplayImageActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    private String username;
    private String filename;
    private List<String>imagestring=new ArrayList<>();

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener firebaseAUthList;

    ImageConverter imageConverter;
    Parsejson parsejson;
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            startActivity(new Intent(DisplayImageActivity.this, Signinup.class));
        }
        if(currentUser!=null)username=currentUser.getEmail();
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
        textView=findViewById(R.id.errormessage);
        textView.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(DisplayImageActivity.this,3));


        (new ImageIPFS()).execute();



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
                File file= new File(selectedImage.getPath());
                filename=file.getName();
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(imgDecodableString);
                String encodedImage = imageConverter.getStringFromBitmap(bitmap);
               // Log.i("Imagesis",encodedImage);
                new ImageUploadIPFSandML().execute(encodedImage);
            }
            if(requestCode==PICK_FROM_CAMERA){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String encodedImage = imageConverter.getStringFromBitmap(imageBitmap);
                new ImageUploadIPFSandML().execute(encodedImage);

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
                new ImageUploadIPFSandML().execute(baseVideo);
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


    private class ImageIPFS  extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLGETFILENAME);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("user" , username);

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
                    if(!server_response.equals("No filename for this user")) {
                       if(!imagestring.isEmpty()) imagestring.clear();

                        Gson gson=new Gson();
                        ImagesList imagesList=gson.fromJson(server_response,ImagesList.class);
                        List<String>imagess=imagesList.getString();
                        for(int i=0;i<imagess.size();i++){
                            String s=imagess.get(i);
                            if(s.length()<46)continue;
                            String temp="";
                            Integer count=0;
                            for(int j=47;j<s.length();j++){
                                if(s.charAt(j)=='.')count++;
                                if(count.intValue()>=2)break;
                                temp+=s.charAt(j);
                            }
                            imagestring.add(temp);
                        }
                        Log.i("imagelist is",imagesList.getString().get(0));
                        Log.i("name is",imagestring.get(0));

                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DisplayImageActivity.this);
            progressDialog.setMessage("Loading Images From Database.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(imagestring.isEmpty()){
                progressDialog.cancel();
                textView.setVisibility(View.VISIBLE);

                return;
            }

            ImageUploadInfo imageUploadInfo=null;
            list.clear();
            for(int i=0;i<imagestring.size();i++){
                imageUploadInfo=new ImageUploadInfo(imagestring.get(i));
                list.add(imageUploadInfo);
            }
            adapter = new RecyclerViewAdapter(DisplayImageActivity.this, list,username);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();

        }
    }

    private class ImageUploadIPFSandML extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLIMAGEUPLOAD);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("user" , username);
                    obj.put("name",filename);
                    obj.put("image" , strings[0]);
                 //   Log.i("imagesis",strings[0]);

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
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ImageIPFS().execute();
        }

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
