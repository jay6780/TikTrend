package com.tk.tiktrend.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.tk.tiktrend.CustomView.FullScreenVideoView;
import com.tk.tiktrend.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FullViewVideoActivity extends AppCompatActivity {
    private ImageView btn_back;
    private FullScreenVideoView mPlayerView;
    private TextView time;
    private String path;
    private SeekBar seekBar;
    private Handler mSeekHandler = new Handler(Looper.getMainLooper());
    private Runnable mSeekRunnable;
    private int mDuration = 0;
    private ImageView btn_play, download;
    private static final String DOWNLOAD_DIRECTORY = "Manyakol.com";
    private File destinationFile;
    private static final int REQUEST_WRITE_PERMISSION = 1001;
    private KProgressHUD hud;
    private boolean isSeekBarTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_full_view_video);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        path = getIntent().getStringExtra("videoURl");
        btn_back = findViewById(R.id.btn_back);
        mPlayerView = findViewById(R.id.player);
        btn_play = findViewById(R.id.btn_play);
        mPlayerView.setOnClickListener(view -> clickPause());
        btn_back.setOnClickListener(view -> onBackPressed());
        seekBar = findViewById(R.id.seekBar);
        download = findViewById(R.id.download);
        download.setOnClickListener(view -> downloadVideo());
        time = findViewById(R.id.time);
        btn_back = findViewById(R.id.btn_back);


        if (path != null) {
            mPlayerView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mDuration = mediaPlayer.getDuration();
                    seekBar.setMax(mDuration);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer.start();
                                btn_play.setVisibility(View.INVISIBLE);
                                setupSeekBar();
                                startSeekUpdates();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.seekTo(0);
                            mp.start();
                            btn_play.setVisibility(View.INVISIBLE);
                        }
                    });

                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                            double ratio = percent / 100.0;
                            int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                            seekBar.setSecondaryProgress(bufferingLevel);
                        }
                    });
                }
            });
            mPlayerView.setVideoPath(path);
        }
    }

    private void downloadVideo() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        savedVideo(path, destinationFile);
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
                        FullViewVideoActivity.this,
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

    private void clickPause() {
        if (mPlayerView != null) {
            if (mPlayerView.isPlaying()) {
                mPlayerView.pause();
                btn_play.setVisibility(View.VISIBLE);
            } else {
                mPlayerView.start();
                btn_play.setVisibility(View.INVISIBLE);
                startSeekUpdates();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSeekBar() {
        mPlayerView.setOnTouchListener(null);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayerView != null) {
                    time.setText(formatTime(progress / 1000) + " / " + formatTime(mDuration / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = true;
                if (mPlayerView.isPlaying()) {
                    mPlayerView.pause();
                }
                mSeekHandler.removeCallbacks(mSeekRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTracking = false;
                if (mPlayerView != null) {
                    mPlayerView.seekTo(seekBar.getProgress());
                    mPlayerView.start();
                    btn_play.setVisibility(View.INVISIBLE);
                    startSeekUpdates();
                }
            }
        });
    }

    private void startSeekUpdates() {
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }

        mSeekRunnable = new Runnable() {
            @Override
            public void run() {
                if (mPlayerView != null && mPlayerView.isPlaying() && !isSeekBarTracking) {
                    int currentPosition = mPlayerView.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    time.setText(formatTime(currentPosition / 1000) + " / " + formatTime(mDuration / 1000));
                }
                mSeekHandler.postDelayed(this, 1000);
            }
        };
        mSeekHandler.post(mSeekRunnable);
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayerView != null) {
            mPlayerView.pause();
            btn_play.setVisibility(View.VISIBLE);
        }
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null && mPlayerView.isPlaying()) {
            startSeekUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSeekRunnable != null) {
            mSeekHandler.removeCallbacks(mSeekRunnable);
        }
        if (mPlayerView != null) {
            mPlayerView.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPlayerView != null) {
            mPlayerView.pause();
        }
        finish();
    }
}