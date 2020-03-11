package com.example.hackathonnitk.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonnitk.Adapter.RecyclerAdapterEveryone;
import com.example.hackathonnitk.Adapter.RecyclerViewAdapter;
import com.example.hackathonnitk.Algorithms.CacheImage;
import com.example.hackathonnitk.Algorithms.ImageConverter;
import com.example.hackathonnitk.ConstantsIt;
import com.example.hackathonnitk.R;
import com.example.hackathonnitk.model.ImageUploadInfo;
import com.example.hackathonnitk.model.ImagesList;
import com.example.hackathonnitk.model.Parsejson;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.List;

public class Everyone extends AppCompatActivity {

    private RecyclerView recyclerView;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    private static final int PICK_VIDEO_CAMERA=3;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String username;
    private String filename;
    private List<String> imagestring=new ArrayList<>();


    ImageConverter imageConverter;
    Parsejson parsejson;
    Bitmap bitmap;

    RecyclerView.Adapter adapter ;
    ProgressDialog progressDialog;
    List<ImageUploadInfo> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyone);
        setTitle("Public Images");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(Everyone.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                // Toast.makeText(DisplayImageActivity.this, "Please give your permission.", Toast.LENGTH_LONG).show();
            }
        }

        if(checkPermissionwrite()){
            //  Toast.makeText(this,"Write on",Toast.LENGTH_LONG).show();
        }
        else requestPermission();

        TextView textView=findViewById(R.id.errormessage);
        textView.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(Everyone.this,3));

        (new GetAllImages()).execute();

    }


    private boolean checkPermissionwrite() {
        int result = ContextCompat.checkSelfPermission(Everyone.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(Everyone.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Everyone.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Everyone.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Everyone.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
                    Toast.makeText(Everyone.this, "Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Everyone.this, "Write Permission granted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Everyone.this, "Write Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.everyone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id1=item.getItemId();
        if(id1==R.id.action_displayimageactivity){
            startActivity(new Intent(Everyone.this,DisplayImageActivity.class));
        }
        if(id1==R.id.action_refresh_everyone){
            (new GetAllImages()).execute();
        }

        return super.onOptionsItemSelected(item);

    }

    private class GetAllImages extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLGETALLFILENAME);
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
                    if(!server_response.equals("No public images")) {
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
            progressDialog = new ProgressDialog(Everyone.this);
            progressDialog.setMessage("Loading Images From Database.");

            //   progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(imagestring.isEmpty()){
                progressDialog.cancel();
                return;
            }

            ImageUploadInfo imageUploadInfo=null;
            list.clear();
            for(int i=0;i<imagestring.size();i++){
                imageUploadInfo=new ImageUploadInfo(imagestring.get(i));
                list.add(imageUploadInfo);
            }
            adapter = new RecyclerAdapterEveryone(Everyone.this, list,username);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();

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
