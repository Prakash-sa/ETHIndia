package com.example.hackathonnitk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathonnitk.Adapter.RecyclerViewAdapter;
import com.example.hackathonnitk.ConstantsIt;
import com.example.hackathonnitk.R;
import com.example.hackathonnitk.model.ImageUploadInfo;
import com.example.hackathonnitk.model.ImagesList;
import com.google.gson.Gson;

import org.json.JSONArray;
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

public class AddPeople extends AppCompatActivity {

    private EditText editText;
    private TextView textView;
    private Button submitbutton;
    private ImageButton imageButton;
    List usernamelist=new ArrayList<String>();
    private String Username;
    private String Imagename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        Username=getIntent().getStringExtra("Username");
        Imagename=getIntent().getStringExtra("Imagename");
        editText=findViewById(R.id.edittextaddpeople);
        textView=findViewById(R.id.peopleare);
        submitbutton=findViewById(R.id.action_submit);
        imageButton=findViewById(R.id.action_add_person);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText()!=null){
                    String useris=editText.getText().toString();
                    textView.append(useris);
                    textView.append("\n");
                    usernamelist.add(useris);
                    editText.setText("");

                }
            }
        });

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddUserAsync().execute();
                finish();
            }
        });
    }

    private class AddUserAsync  extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLADDPEOPLE);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("user1" , Username);
                    obj.put("name",Imagename);
                    JSONArray jsonArray=new JSONArray(usernamelist);
                    obj.put("users",jsonArray);
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
            Toast.makeText(AddPeople.this,"Added People Successfully",Toast.LENGTH_LONG).show();
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
