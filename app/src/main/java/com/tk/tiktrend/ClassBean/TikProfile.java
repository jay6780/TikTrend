package com.tk.tiktrend.ClassBean;

public class TikProfile {
    String VideoUrl;
    String nickname;
    String avatar;
    String videoId;


    public TikProfile(String videoId,String VideoUrl,String nickname,String avatar){
        this.VideoUrl = VideoUrl;
        this.nickname = nickname;
        this.avatar = avatar;
        this.videoId = videoId;

    }

    public String getVideoId() {
        return videoId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }
}
