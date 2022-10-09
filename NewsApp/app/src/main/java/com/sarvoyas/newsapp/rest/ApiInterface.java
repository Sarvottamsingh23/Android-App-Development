package com.sarvoyas.newsapp.rest;

import com.sarvoyas.newsapp.model.HomepageModel;
import com.sarvoyas.newsapp.model.OurYtModel;
import com.sarvoyas.newsapp.model.YtModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("homepage_api")
    Call<HomepageModel> getHomepageApi(@QueryMap Map<String,String> params);

    @GET("news_by_pid")
    Call<HomepageModel> getNewsDetailsById(@QueryMap Map<String,String> params);

    @GET("news_by_cid")
    Call<HomepageModel> getNewsByCId(@QueryMap Map<String,String> params);

    @GET("youtube")
    Call<OurYtModel> getYoutubeDataFromServer();

    @GET("https://www.googleapis.com/youtube/v3/activities")
    Call<YtModel> getYoutubeServerData(@QueryMap Map<String,String> params);


}
