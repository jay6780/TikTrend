package com.tk.tiktrend.Presenter;


import com.tk.tiktrend.Api.Callback;
import com.tk.tiktrend.ClassBean.TikTrend;
import com.tk.tiktrend.Model.TikTrendTask;
import com.tk.tiktrend.View.TikContract;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class TikPresenter implements TikContract.Presenter {
    TikContract.View mView;

    public TikPresenter(TikContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void loadTrend() {
        mView.showLoading();
        TikTrendTask.getTiktokTrend(new Callback<TikTrend>() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }

            @Override
            public void returnResult(TikTrend tikTrend) {
                mView.hideloading();
                mView.loadDataInList(tikTrend);
            }

            @Override
            public void returnError(String message) {
                mView.hideloading();
                mView.showError(message);
            }
        });
    }
}