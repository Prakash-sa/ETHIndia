package com.example.hackathonpune.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.hackathonpune.model.ImageUploadInfo;
import com.example.hackathonpune.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;

    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
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
        holder.imageNameTextView.setText(UploadInfo.getImageName());
        holder.imageNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Click it",Toast.LENGTH_LONG).show();
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