package com.example.hackathonpune.Adapter;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackathonpune.Algorithms.ImageConverter;
import com.example.hackathonpune.Algorithms.StoreImage;
import com.example.hackathonpune.ConstantsIt;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.R;
import com.example.hackathonpune.ui.DisplayImageActivity;
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
    ProgressDialog progressDialog;
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
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("CardView","True");
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.inflate(R.menu.image_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id2=item.getItemId();

                        if(id2==R.id.action_download){
                            new ImageSave().execute(imagenameis);
                        }
                        if(id2==R.id.action_delete){
                            new DeleteImage().execute(imagenameis);
                            holder.cardView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CardView","True");
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.inflate(R.menu.image_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id2=item.getItemId();

                        if(id2==R.id.action_download){
                            new ImageSave().execute(imagenameis);
                        }
                        if(id2==R.id.action_delete){
                            new DeleteImage().execute(imagenameis);
                            holder.cardView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.imageNameTextView.setText(imagenameis);

    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView imageNameTextView;
        public CardView cardView;
        public FloatingActionButton floatingActionButton;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview1);
            imageNameTextView=itemView.findViewById(R.id.imagename);
            floatingActionButton=itemView.findViewById(R.id.menudis);


        }

    }

    private class ImageSave extends AsyncTask<String,Void,Void>{

        String filepath;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLGETIMAGE);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("user" , username);
                    obj.put("image",strings[0]);

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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(filepath!=null)
            Toast.makeText(context,"File Stored at: "+filepath,Toast.LENGTH_LONG).show();
            else Toast.makeText(context,"Error in Saveing file ",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private class DeleteImage extends AsyncTask<String,Void,Void>{

        String filepath;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(ConstantsIt.LOCALURLDELETEIMAGE);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                OutputStream os=urlConnection.getOutputStream();

                DataOutputStream wr = new DataOutputStream(os);

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("user" , username);
                    obj.put("image",strings[0]);

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
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Removing...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context,"Deteled file",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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