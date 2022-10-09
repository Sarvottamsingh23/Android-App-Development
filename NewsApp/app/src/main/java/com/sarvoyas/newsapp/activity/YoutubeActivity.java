package com.sarvoyas.newsapp.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.adapter.ViewpagerAdapter;
import com.sarvoyas.newsapp.model.OurYtModel;
import com.sarvoyas.newsapp.rest.ApiClient;
import com.sarvoyas.newsapp.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class YoutubeActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewpagerAdapter viewpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        initViews();
    }

    private void initViews() {
        toolbar= findViewById(R.id.toolbar);
        viewPager= findViewById(R.id.view_pager);
        tabLayout= findViewById(R.id.tablayout);

        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Youtube Videos");
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getYoutubeData();

    }

    private void getYoutubeData() {
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        Call<OurYtModel> call= apiInterface.getYoutubeDataFromServer();
        call.enqueue(new Callback<OurYtModel>() {
            @Override
            public void onResponse(Call<OurYtModel> call, Response<OurYtModel> response) {
                viewpagerAdapter= new ViewpagerAdapter(getSupportFragmentManager(), response.body(), YoutubeActivity.this);
                viewPager.setAdapter(viewpagerAdapter);
            }

            @Override
            public void onFailure(Call<OurYtModel> call, Throwable t) {

                Toast.makeText(YoutubeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}