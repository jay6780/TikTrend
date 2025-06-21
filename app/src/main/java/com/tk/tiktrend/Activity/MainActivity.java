package com.tk.tiktrend.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tk.tiktrend.Activity.adapter.TikTrendAdapter;
import com.tk.tiktrend.ClassBean.TikProfile;
import com.tk.tiktrend.ClassBean.TikTrend;
import com.tk.tiktrend.CustomView.DouYinLoadingDrawable;
import com.tk.tiktrend.Presenter.TikPresenter;
import com.tk.tiktrend.R;
import com.tk.tiktrend.View.TikContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements TikContract.View, View.OnClickListener {
    private ViewPager2 scroll_pager;
    private boolean isMore = true;
    private boolean isLoading = false;
    private boolean isFirstLoad = true;
    private List<TikProfile> tikProfileList = new ArrayList<>();
    private TikTrendAdapter tikTrendAdapter;
    private TikContract.Presenter tikTrendPresenter;
    private ImageView initial_loading,onload_more,download_btn;
    private boolean initialload = true;
    private DouYinLoadingDrawable loading,loading2;
    private TextView title;
    private static final String DOWNLOAD_DIRECTORY = "TikTrend";
    private File destinationFile;
    private static final int REQUEST_WRITE_PERMISSION = 1001;
    private KProgressHUD hud;
    private int PagePosition;
    private ImageView hambuger;
    private DrawerLayout drawerLayout;
    private LinearLayout navigationView,ll_file;
    private TextView tikTrendTxt;
    private View blockview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scroll_pager = findViewById(R.id.scroll_pager);
        onload_more = findViewById(R.id.onload_more);
        initial_loading = findViewById(R.id.initial_loading);
        getSupportActionBar().hide();
        title = findViewById(R.id.title);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ll_file = findViewById(R.id.ll_file);
        tikTrendTxt = findViewById(R.id.tikTrendTxt);
        hambuger =findViewById(R.id.hambuger);
        blockview = findViewById(R.id.blockview);
        download_btn = findViewById(R.id.download_btn);
        scroll_pager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        tikTrendAdapter = new TikTrendAdapter(this,tikProfileList);
        scroll_pager.setAdapter(tikTrendAdapter);
        tikTrendPresenter = new TikPresenter(this);
        download_btn.setOnClickListener(this);
        title.setOnClickListener(this);
        hambuger.setOnClickListener(this);
        ll_file.setOnClickListener(this);
        tikTrendTxt.setText("Welcome to "+getString(R.string.app_name));
        tikTrendTxt.setTextColor(getResources().getColor(R.color.white));
        loadData();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                blockview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                blockview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                blockview.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        scroll_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                PagePosition = position;
                playVideoAtPosition(position);
                if(position >= tikProfileList.size() - 1&& !isLoading && isMore){
                    onLoadmoreTrend();
                }

            }
        });
    }

    private void loadData() {
        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        if(isFirstLoad){
            tikTrendPresenter.loadTrend();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private  void Refresh(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        tikProfileList.clear();
        initialload = true;
        isFirstLoad = false;
        tikTrendPresenter.loadTrend();
    }

    private void onLoadmoreTrend() {
        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(),"Please check network and try again",Toast.LENGTH_SHORT).show();
            return;
        }
        initialload = false;
        isFirstLoad = false;
        tikTrendPresenter.loadTrend();

    }

    private void playVideoAtPosition(int position) {
        if (position < 0 || position >= tikProfileList.size()) return;
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) scroll_pager.getChildAt(0))
                .findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof TikTrendAdapter.ViewHolder) {
            TikTrendAdapter.ViewHolder holder = (TikTrendAdapter.ViewHolder) viewHolder;

            if(tikProfileList.get(position).getNickname().isEmpty()){
                holder.nickname.setText("Anonymous user");
            }else{
                holder.nickname.setText(tikProfileList.get(position).getNickname());
            }

            Glide.with(this)
                    .load(tikProfileList.get(position).getAvatar())
                    .circleCrop()
                    .into(holder.avatar);


            if (holder.fullVideo != null) {
                holder.fullVideo.setVideoPath(tikProfileList.get(position).getVideoUrl());
            }
        }
    }

    @Override
    public void showError(String message) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        tikTrendAdapter.pauseOutside(true);
    }

    @Override
    public void showLoading() {
        if(initialload){
            loading= new DouYinLoadingDrawable();
            initial_loading.setVisibility(View.VISIBLE);
            initial_loading.setImageDrawable(loading);
            loading.start();
        }else{
            loading2 = new DouYinLoadingDrawable();
            onload_more.setVisibility(View.VISIBLE);
            onload_more.setImageDrawable(loading2);
            loading2.start();
        }

    }

    @Override
    public void hideloading() {
        if(initialload){
            initial_loading.setVisibility(View.GONE);
            initial_loading.setImageDrawable(null);
        }else{
            onload_more.setVisibility(View.GONE);
            onload_more.setImageDrawable(null);
        }

    }

    @Override
    public void loadDataInList(TikTrend tikTrend) {
        isLoading = false;
        if(tikTrend!=null && tikTrend.getData() !=null){
            List<TikProfile> tikProfileListdata = new ArrayList<>();
            for (TikTrend.Data data : tikTrend.getData()){
                tikProfileListdata.add(new TikProfile(data.getVideo_id(),data.getPlay(),
                        data.getAuthor().getNickname(),
                        data.getAuthor().getAvatar()));
            }
            tikProfileList.addAll(tikProfileListdata);
            tikTrendAdapter.notifyDataSetChanged();
        }

    }
    private void downloadVideo() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                return;
            }
        }
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait");
        hud.show();

        startDownload();
    }

    private void startDownload() {
        String randomFileName = "video_" + System.currentTimeMillis() + ".mp4";

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File shortvideoDir = new File(downloadsDir, DOWNLOAD_DIRECTORY);
        if (!shortvideoDir.exists()) {
            shortvideoDir.mkdirs();
        }
        destinationFile = new File(shortvideoDir, randomFileName);
        savedVideo(tikProfileList.get(PagePosition).getVideoUrl(), destinationFile);
    }

    private void savedVideo(String path, File destinationFile) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(path).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Failed to save Video", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Download failed: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) return;

                InputStream inputStream = body.byteStream();
                FileOutputStream outputStream = new FileOutputStream(destinationFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                MediaScannerConnection.scanFile(
                        MainActivity.this,
                        new String[]{destinationFile.getAbsolutePath()},
                        new String[]{"video/mp4"},
                        (path, uri) -> runOnUiThread(() -> {
                            hud.dismiss();
                            View rootView = findViewById(android.R.id.content);
                            com.google.android.material.snackbar.Snackbar.make(rootView, "Download complete", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
                        })
                );
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
           case R.id.title:
               Refresh();
               break;
            case R.id.download_btn:
                downloadVideo();
                break;
            case R.id.hambuger:
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
                break;
            case R.id.ll_file:
                startActivity(new Intent(getApplicationContext(), Download_videoActivity.class));
                onPause();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

}