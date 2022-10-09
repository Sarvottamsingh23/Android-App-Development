package com.sarvoyas.newsapp.rest;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.1.104/newsapp/wp-json/api/";


    private static Retrofit retrofit= null;

    public  static  Retrofit getApiClient(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        if(retrofit == null){
            retrofit= new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
