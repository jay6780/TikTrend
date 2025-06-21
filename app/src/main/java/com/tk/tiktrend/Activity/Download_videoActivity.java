package com.tk.tiktrend.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.tk.tiktrend.Activity.adapter.FileAdapter;
import com.tk.tiktrend.R;
import com.tk.tiktrend.file.FilesExtractor;
import com.tk.tiktrend.file.VideoFile;

import java.util.ArrayList;

public class Download_videoActivity extends AppCompatActivity {
    private ImageView btn_back5;
    private LinearLayout ll_bg;
    private RecyclerView file_recycler;
    private FileAdapter fileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_video);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#262626"));
        }
        btn_back5 = findViewById(R.id.btn_back5);
        ll_bg = findViewById(R.id.ll_bg);
        file_recycler = findViewById(R.id.file_recycler);
        btn_back5.setOnClickListener(view -> onBackPressed());
        FilesExtractor filesExtractor = new FilesExtractor(Download_videoActivity.this);
        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
        file_recycler.setLayoutManager(new GridLayoutManager(this,2));
        fileAdapter = new FileAdapter(this,videoFiles);
        file_recycler.setAdapter(fileAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}