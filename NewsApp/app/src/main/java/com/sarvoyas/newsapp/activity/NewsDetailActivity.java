package com.sarvoyas.newsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class NewsDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView sourceName, newsTitle, newsDescp, newsDate, newsView, labelSimilar;
    Button viewMore;
    ProgressBar progressBar;
    ImageView newsImage, sourceLogo;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    List<HomepageModel.News> news= new ArrayList<>();
    HomepageModel.News detailNews= null;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initViews();
        loadNewsDetails();
    }

    private void loadNewsDetails() {
        //calling our api
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("id", getIntent().getIntExtra("pid", 0)+"");
        Call<HomepageModel> call= apiInterface.getNewsDetailsById(params);
        call.enqueue(new Callback<HomepageModel>() {
            @Override
            public void onResponse(Call<HomepageModel> call, Response<HomepageModel> response) {
                detailNews= response.body().getNews().get(0);
                newsTitle.setText(detailNews.getTitle());
                newsDescp.setText(NewsAdapter.removeHtml(detailNews.getPostContent()));

                newsView.setText(detailNews.getViews()+" Views");

                if(detailNews.getImage().length() >=1){
                    Glide.with(NewsDetailActivity.this).load(detailNews.getImage()).into(newsImage);
                }else{
                    newsImage.setVisibility(View.GONE);
                }
                sourceName.setText(detailNews.getSource());
                if(detailNews.getSourceLogo().length() >=1 ){
                    Glide.with(NewsDetailActivity.this).load(detailNews.getSourceLogo()).into(sourceLogo);
                }
                viewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newsUrl="";
                        if(detailNews.getSourceUrl()!= null){
                          newsUrl= detailNews.getUrl();
                        }else{
                            newsUrl=detailNews.getSourceUrl();
                        }
                        Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl));
                        startActivity(browserIntent);
                    }
                });

                labelSimilar.setVisibility(View.VISIBLE);
                news.addAll(response.body().getNews());
                news.remove(0);
                recyclerView.setAdapter(newsAdapter);
            }

            @Override
            public void onFailure(Call<HomepageModel> call, Throwable t) {
                Toast.makeText(NewsDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initViews() {
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sourceName=findViewById(R.id.source_name);
        newsTitle= findViewById(R.id.news_title);
        newsDate=findViewById(R.id.news_date);
        newsDescp=findViewById(R.id.news_descp);
        newsView= findViewById(R.id.news_view);
        labelSimilar=findViewById(R.id.label_similar_news);
        viewMore= findViewById(R.id.view_more);
        progressBar=findViewById(R.id.progressbar);
        newsImage=findViewById(R.id.news_image);
        sourceLogo=findViewById(R.id.source_logo);
        recyclerView=findViewById(R.id.news_recy);


        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        newsAdapter= new NewsAdapter(this,news );

        adView= new AdView(this, "4021194884603578_4021237401265993", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(
                        NewsDetailActivity.this,
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.share){
            if(detailNews!= null){
                Intent intent= new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, detailNews.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, detailNews.getPostContent());
                startActivity(intent);
            }else{
                Toast.makeText(this, "News is not available!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}