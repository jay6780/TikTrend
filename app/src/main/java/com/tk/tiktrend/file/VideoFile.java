package com.tk.tiktrend.file;

public class VideoFile {
    public String title,path;
    public long size,lastModified;

    public VideoFile(String title, String path, long size, long lastModified) {
        this.title = title;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
    }
}