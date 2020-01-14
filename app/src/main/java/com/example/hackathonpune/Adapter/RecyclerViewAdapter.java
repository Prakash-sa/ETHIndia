package com.example.hackathonpune.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hackathonpune.Algorithms.ImageConverter;
import com.example.hackathonpune.Algorithms.StoreImage;
import com.example.hackathonpune.ConstantsIt;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.R;
import com.github.clans.fab.FloatingActionButton;

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
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
    String username;

    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList,String username) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
        this.username=username;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        final String imagenameis=UploadInfo.getImageName();
        holder.imageNameTextView.setText(imagenameis);
        holder.imageNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Click it",Toast.LENGTH_LONG).show();
                new ImageSave().execute(imagenameis);
            }
        });
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView imageNameTextView;
        public FloatingActionButton floatingActionButton;
        public ViewHolder(View itemView) {
            super(itemView);
            imageNameTextView=itemView.findViewById(R.id.imagename);
            floatingActionButton=itemView.findViewById(R.id.menudis);
        }


    }

    private class ImageSave extends AsyncTask<String,Void,Void>{

        String filepath;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURL+"js");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("name" , username);
                    obj.put("imagename",strings);

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
                    Bitmap bitmap=imageConverter.getBitmapFromString(server_response);
                    StoreImage storeImage=new StoreImage();
                    filepath=storeImage.storeImage(context,bitmap);

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
            Toast.makeText(context,"File Stored at: "+filepath,Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
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