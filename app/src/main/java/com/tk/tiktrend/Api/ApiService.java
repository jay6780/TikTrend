package com.tk.tiktrend.Api;


import com.tk.tiktrend.ClassBean.TikTrend;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface ApiService {
    @GET("tiktrend")
    Observable<TikTrend> getTikTrend();
}
