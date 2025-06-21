package com.tk.tiktrend.Activity.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tk.tiktrend.ClassBean.TikProfile;
import com.tk.tiktrend.CustomView.FullScreenVideoView;
import com.tk.tiktrend.R;

import java.util.List;

public class TikTrendAdapter extends RecyclerView.Adapter<TikTrendAdapter.ViewHolder> {
    private Context context;
    private List<TikProfile> tikProfileList;
    private boolean isPause = false;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FullScreenVideoView fullVideo;
        public TextView nickname;
        ImageView play_btn;
        public ImageView avatar;
        public ViewHolder(View view) {
            super(view);
            fullVideo = view.findViewById(R.id.fullVideo);
            nickname = view.findViewById(R.id.nickname);
            play_btn = view.findViewById(R.id.play_btn);
            avatar = view.findViewById(R.id.avatar);
        }

    }

    public void pauseOutside(boolean paused){
        isPause = paused;
        notifyDataSetChanged();
    }
    public TikTrendAdapter(Context context, List<TikProfile> tikProfileList) {
        this.context = context;
        this.tikProfileList = tikProfileList;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(tikProfileList.get(position).getVideoId());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tik_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TikProfile tikProfile = tikProfileList.get(position);

        if(isPause){
            holder.fullVideo.pause();
        }
        holder.fullVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                holder.play_btn.setVisibility(View.INVISIBLE);
                isPause = false;

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.seekTo(0);
                        mp.start();
                    }
                });
            }

        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.fullVideo.isPlaying()){
                    holder.fullVideo.pause();
                    holder.play_btn.setVisibility(View.VISIBLE);
                }else{
                    holder.play_btn.setVisibility(View.INVISIBLE);
                    holder.fullVideo.start();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tikProfileList.size();
    }
}