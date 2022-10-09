package com.sarvoyas.newsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.adapter.NewsAdapter;
import com.sarvoyas.newsapp.model.HomepageModel;
import com.sarvoyas.newsapp.rest.ApiClient;
import com.sarvoyas.newsapp.rest.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;

    ProgressBar progressBar;

    int page=1;
    int posts=10;

    NewsAdapter newsAdapter;
    List<HomepageModel.News> news= new ArrayList<>();

    boolean isStartFirst=true, shouldFetchData=true;
    int backendAdsShown=0;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView= findViewById(R.id.news_recy);
        toolbar=findViewById(R.id.toolbar);
        swipeRefreshLayout= findViewById(R.id.swipe);
        progressBar= findViewById(R.id.progressbar);

        adView= new AdView(this, "4021194884603578_4021237401265993", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(
                        CategoryActivity.this,
                        "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };

        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());



        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(this);

        final  LinearLayoutManager linearLayoutManager=  new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getIntent().getStringExtra("cname"));
        newsAdapter= new NewsAdapter(this, news);
        isStartFirst= true;
        page=1;
        getCategoryData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int passVisibleCount = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                if ((passVisibleCount + visibleItemCount) == totalItemCount) {
                    if (shouldFetchData) {
                        shouldFetchData = false;
                        isStartFirst = false;
                        progressBar.setVisibility(View.VISIBLE);
                        page++;
                        getCategoryData();
                    }
                }
            }
        });
    }

    private void getCategoryData() {
        //call our api
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("page", page+"");
        params.put("posts", posts+"");
        params.put("cid", getIntent().getIntExtra("cid", 0)+"");
        Call<HomepageModel> call= apiInterface.getNewsByCId(params);
        call.enqueue(new Callback<HomepageModel>() {
            @Override
            public void onResponse(Call<HomepageModel> call, Response<HomepageModel> response) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                int beforeSize= news.size();
                if (isStartFirst){
                    news.clear();
                }
                int recentads=0;

                for(int i=0; i<response.body().getNews().size(); i++){

                    if ((i + 1) % 5 == 0) {
                        if(response.body().getAds().size() > backendAdsShown){
                            //backend ads
                            HomepageModel.Ad ad= response.body().getAds().get(backendAdsShown);
                            HomepageModel.News singleNews= new HomepageModel.News(ad.getPid(), ad.getTitle(), ad.getImage(), ad.getLink(), ad.getDescription());
                            news.add(singleNews);
                            backendAdsShown++;
                            singleNews.setAds(true);
                            singleNews.setAdsFromGoogle(false);
                        }else{
                            //google ads
                            HomepageModel.News singleNews= new HomepageModel.News();
                            singleNews.setAdsFromGoogle(true);
                            singleNews.setAds(true);
                            news.add(singleNews);
                        }
                        recentads++;
                    }
                    news.add(response.body().getNews().get(i)) ;
                }
                if (isStartFirst){
                    recyclerView.setAdapter(newsAdapter);
                }else{
                    newsAdapter.notifyItemRangeInserted(beforeSize,recentads+response.body().getNews().size());
                }
                shouldFetchData=true;
            }

            @Override
            public void onFailure(Call<HomepageModel> call, Throwable t) {
                Toast.makeText(CategoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        isStartFirst=true;
        page=1;
        shouldFetchData=true;
        getCategoryData();
    }
}