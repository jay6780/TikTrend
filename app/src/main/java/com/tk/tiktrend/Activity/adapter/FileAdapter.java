package com.tk.tiktrend.Activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tk.tiktrend.Activity.FullViewVideoActivity;
import com.tk.tiktrend.R;
import com.tk.tiktrend.file.VideoFile;


import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<VideoFile> videoFileList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_thumb;
        TextView file_name;

        public ViewHolder(View view) {
            super(view);
            image_thumb = view.findViewById(R.id.image_thumb);
            file_name = view.findViewById(R.id.file_name);
        }
    }

    public FileAdapter(Context context, List<VideoFile> videoFileList) {
        this.context = context;
        this.videoFileList = videoFileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        VideoFile data = videoFileList.get(position);
        holder.file_name.setText(data.title);
        Glide.with(context)
                .asBitmap()
                .centerCrop()
                .load(data.path)
                .into(holder.image_thumb);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewFullvideo = new Intent(context, FullViewVideoActivity.class);
                viewFullvideo.putExtra("videoURl",data.path);
                context.startActivity(viewFullvideo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFileList.size();
    }

}