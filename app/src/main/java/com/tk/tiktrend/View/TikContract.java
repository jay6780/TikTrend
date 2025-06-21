package com.tk.tiktrend.View;

import com.tk.tiktrend.ClassBean.TikTrend;

import java.util.List;

public interface TikContract {
    interface View {
        void showError(String message);
        void showLoading();
        void hideloading();
        void loadDataInList(TikTrend tikTrend);
    }

    interface Presenter {
        void loadTrend();
    }

}