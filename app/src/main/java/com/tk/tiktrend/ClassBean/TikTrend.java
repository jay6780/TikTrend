package com.tk.tiktrend.ClassBean;

import java.util.List;

public class TikTrend {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }
    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data{
        String video_id;
        String region;
        String title;
        String cover;
        String ai_dynamic_cover;
        String origin_cover;
        int duration;
        String play;
        String wmplay;
        long size;
        long wm_size;
        String music;
        Author author;

        public Author getAuthor() {
            return author;
        }

        public String getVideo_id() {
            return video_id;
        }

        public String getRegion() {
            return region;
        }

        public String getTitle() {
            return title;
        }

        public String getCover() {
            return cover;
        }

        public String getAi_dynamic_cover() {
            return ai_dynamic_cover;
        }

        public String getOrigin_cover() {
            return origin_cover;
        }

        public int getDuration() {
            return duration;
        }

        public String getPlay() {
            return play;
        }

        public String getWmplay() {
            return wmplay;
        }

        public long getWm_size() {
            return wm_size;
        }

        public long getSize() {
            return size;
        }

        public String getMusic() {
            return music;
        }
    }
    public static class Author {
        String id;
        String unique_id;
        String nickname;
        String avatar;

        public String getId() {
            return id;
        }

        public String getUnique_id() {
            return unique_id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }
    }

}
