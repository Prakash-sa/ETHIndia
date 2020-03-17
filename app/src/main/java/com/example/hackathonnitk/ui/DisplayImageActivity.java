package com.example.hackathonnitk.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonnitk.Algorithms.CacheImage;
import com.example.hackathonnitk.Algorithms.ImageConverter;
import com.example.hackathonnitk.Algorithms.StoreImage;
import com.example.hackathonnitk.ConstantsIt;
import com.example.hackathonnitk.model.ImageUploadInfo;
import com.example.hackathonnitk.R;
import com.example.hackathonnitk.model.ImagesList;
import com.example.hackathonnitk.model.Parsejson;
import com.example.hackathonnitk.Adapter.RecyclerViewAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;

    private String username;
    private String filename;
    private List<String>imagestring=new ArrayList<>();

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener firebaseAUthList;
    private CacheImage cacheImage;

    ImageConverter imageConverter;
    Parsejson parsejson;
    Bitmap bitmap;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private static final int PICK_VIDEO_CAMERA=3;
    private static final int PICK_FROM_AUDIO=4;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 100;

    FloatingActionButton uploadcamera,uploadgallery,uploadvideo,uploadaudio;
    RecyclerView.Adapter adapter ;
    TextView textView;
    private Button bt_refresh;
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

        imageConverter=new ImageConverter();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        uploadcamera=findViewById(R.id.uploadcamera);
        uploadgallery=findViewById(R.id.uploadgallery);
        uploadvideo=findViewById(R.id.uploadvideo);
        uploadaudio=findViewById(R.id.uploadaudio);
        textView=findViewById(R.id.errormessage);
        bt_refresh=findViewById(R.id.action_refresh);
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

        uploadaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(audioIntent, PICK_FROM_AUDIO);
            }
        });

        // initialise cacheObject
        //cacheImage = new CacheImage(DisplayImageActivity.this);

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
                String[] tmp=imgDecodableString.split("/");
                filename=tmp[tmp.length - 1];
                cursor.close();

                new ImageUploadIPFSandML().execute(imgDecodableString);

//
//                try {
//                    cacheImage.cacheFromBitmap(bitmap, filename);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    new ImageUploadIPFSandML().execute(encodedImage);
//                    // Log.i("Imagesis",encodedImage);
//                }
            }
            if(requestCode==PICK_FROM_AUDIO) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                String[] tmp=imgDecodableString.split("/");
                filename=tmp[tmp.length - 1];
                cursor.close();

                File originalFile= new File(imgDecodableString);
                String encodedBase64 = null;
                try {
                    FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
                    byte[] bytes = new byte[(int)originalFile.length()];
                    fileInputStreamReader.read(bytes);
                    encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("Audio",encodedBase64);

                new AudioUploadIPFSandML().execute(encodedBase64);

            }
            if(requestCode==PICK_FROM_CAMERA){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String filepath= null;
                try {
                    filepath = (new StoreImage()).storeImage(DisplayImageActivity.this,imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] filpathstringdisplit=filepath.split("/");
                String encodedImage = imageConverter.getStringFromBitmap(imageBitmap);
                filename=filpathstringdisplit[filpathstringdisplit.length-1];
              new ImageUploadIPFSandMLCamera().execute(encodedImage);


            }
            if(requestCode==PICK_VIDEO_CAMERA){

                Uri selectedVideoUri = data.getData();
                String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
                Cursor cursor = managedQuery(selectedVideoUri, projection, null, null, null);
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                Log.i("VideoPath",filePath);
                String[] filepahtstring=filePath.split("/");
                filename=filepahtstring[filepahtstring.length-1];
                String baseVideo=getBase64FromPath(filePath);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                imageConverter.getStringFromBitmap(thumb);
                Log.i("VideoString ",baseVideo);
                new VideoUploadIPFSandML().execute(baseVideo);
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
        if(id1==R.id.action_video_call){
            Intent videointent=new Intent(DisplayImageActivity.this,VideoActivity.class);
            videointent.putExtra("Username",username);
            startActivity(videointent);
        }
        if(id1==R.id.action_refresh)(new ImageIPFS()).execute();
        if(id1==R.id.action_link_file){
            Intent intent=new Intent(this,Linkqrcode.class);
            intent.putExtra("Username",username);
            startActivity(intent);
        }
        if(id1==R.id.action_qr_scanner){
            Intent intent=new Intent(this,ScanneQrCode.class);
            intent.putExtra("Username",username);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }

    private Integer flagtext=0;
    private class ImageIPFS  extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... strings) {
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
                            if(s.length()<64)continue;
                            String temp="";
                            Integer count=0;
                            for(int j=60;j<s.length();j++){
                                if(s.charAt(j)=='.')count++;
                                if(count.intValue()>=2)break;
                                temp+=s.charAt(j);
                            }
                            temp=temp.replaceAll("\n","");
                            imagestring.add(temp);
                        }
                        if(imagestring.size()>0) {
                            Log.i("imagelist is", imagesList.getString().get(0));
                            Log.i("name is", imagestring.get(0));
                        }
                    }else {
                        flagtext=1;

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

         //   progressDialog.setCancelable(false);
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

            textView.setVisibility(View.INVISIBLE);

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
        boolean filenameexist;
        @Override
        protected Void doInBackground(String... strings) {
            bitmap = BitmapFactory.decodeFile(strings[0]);
            cacheImage.cacheFromBitmap(bitmap, filename);


            try {
                FileOutputStream out = new FileOutputStream(strings[0]);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String encodedImage = imageConverter.getStringFromBitmap(bitmap);
            boolean isthere=imagestring.contains(filename);
            if(isthere){
                filenameexist=true;
                return null;
            }
            filenameexist=false;

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
                    obj.put("image" , encodedImage);
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DisplayImageActivity.this);
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(filenameexist)
                Toast.makeText(DisplayImageActivity.this,"Cannot Upload Same Images",Toast.LENGTH_LONG).show();
            new ImageIPFS().execute();
        }

    }

    private class AudioUploadIPFSandML extends AsyncTask<String,Void,Void>{
        boolean filenameexist;
        @Override
        protected Void doInBackground(String... strings) {


            boolean isthere=imagestring.contains(filename);
            if(isthere){
                filenameexist=true;
                return null;
            }
            filenameexist=false;

            try {
                URL url = new URL(ConstantsIt.LOCALURLSENDVIDEO);
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DisplayImageActivity.this);
            progressDialog.setMessage("Uploading Audio...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(filenameexist)
                Toast.makeText(DisplayImageActivity.this,"Cannot Upload Same Audio",Toast.LENGTH_LONG).show();
            new ImageIPFS().execute();
        }

    }

    private class ImageUploadIPFSandMLCamera extends AsyncTask<String,Void,Void>{
        boolean filenameexist;
        @Override
        protected Void doInBackground(String... strings) {
            boolean isthere=imagestring.contains(filename);
            if(isthere){
                filenameexist=true;
                return null;
            }
            filenameexist=false;

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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DisplayImageActivity.this);
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(filenameexist)
                Toast.makeText(DisplayImageActivity.this,"Cannot Upload Same Images",Toast.LENGTH_LONG).show();
            new ImageIPFS().execute();
        }

    }

    private class VideoUploadIPFSandML extends AsyncTask<String,Void,Void>{
        boolean filenameexist;
        @Override
        protected Void doInBackground(String... strings) {
            boolean isthere=imagestring.contains(filename);
            if(isthere){
                filenameexist=true;
                return null;
            }
            filenameexist=false;

            try {
                URL url = new URL(ConstantsIt.LOCALURLSENDVIDEO);
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DisplayImageActivity.this);
            progressDialog.setMessage("Uploading Video...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(filenameexist)
                Toast.makeText(DisplayImageActivity.this,"Cannot Upload Same Videos",Toast.LENGTH_LONG).show();
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

    public static String getBase64FromPath(String path) {
        String base64 = "";
        try {/*from   w w w .  ja  va  2s  .  c om*/
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

}
