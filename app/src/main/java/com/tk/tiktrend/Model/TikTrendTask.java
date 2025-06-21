package com.tk.tiktrend.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tk.tiktrend.Api.Callback;
import com.tk.tiktrend.Api.NetworkingUtils;
import com.tk.tiktrend.ClassBean.TikTrend;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TikTrendTask {
    public static void getTiktokTrend(final Callback<TikTrend> callback) {
        NetworkingUtils.getTikTrendInstance()
                .getTikTrend()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TikTrend>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(TikTrend data) {
                        boolean isDev = false;
                        if(isDev){
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            String prettyJsonString = gson.toJson(data);
                            Log.d("Response", "value: " + prettyJsonString);
                        }
                        callback.returnResult(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error","value: "+e.getMessage());
                        callback.returnError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }
}