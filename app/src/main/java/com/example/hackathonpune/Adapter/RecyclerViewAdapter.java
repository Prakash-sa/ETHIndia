package com.example.hackathonpune.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hackathonpune.Algorithms.Bitmaptransfer;
import com.example.hackathonpune.Algorithms.ImageConverter;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.R;
import com.example.hackathonpune.ui.ViewImage;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
    List<String>keyofimage;

    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList,List<String> keyofimage) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
        this.keyofimage=keyofimage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);
        ImageConverter imageConverter=new ImageConverter();


        final Bitmap imagebitmap=imageConverter.getBitmapFromString(UploadInfo.getImageURL());
        holder.imageView.setImageBitmap(imagebitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewImage.class);
                intent.putExtra("keyis",keyofimage.get(position));
                androidx.core.util.Pair<View,String>pair1=androidx.core.util.Pair.create((View)holder.imageView,"imagetran");

                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,pair1);
                Bitmaptransfer bitmaptransfer=new Bitmaptransfer();
                holder.imageView.buildDrawingCache();
                bitmaptransfer.setBitmap_transfer(holder.imageView.getDrawingCache());
                context.startActivity(intent,optionsCompat.toBundle());

            }
        });
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView imageNameTextView;
        public FloatingActionButton floatingActionButton;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            floatingActionButton=itemView.findViewById(R.id.menudis);
        }


    }


}