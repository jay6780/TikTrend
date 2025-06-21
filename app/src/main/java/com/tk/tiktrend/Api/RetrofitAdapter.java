package com.tk.tiktrend.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {

    private static Retrofit retrofit;
    private static Gson gson;
    private static final String BASE_URL = "https://betadash-api-swordslush-production.up.railway.app/";

    public static synchronized Retrofit getInstance() {

        if (retrofit == null) {
            if (gson == null) {
                gson = new GsonBuilder().setLenient().create();
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

        }

        return retrofit;
    }

}