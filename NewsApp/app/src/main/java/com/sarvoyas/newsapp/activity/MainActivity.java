package com.sarvoyas.newsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.ads.AdRequest;
import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.adapter.GridAdapter;
import com.sarvoyas.newsapp.adapter.NewsAdapter;
import com.sarvoyas.newsapp.model.HomepageModel;
import com.sarvoyas.newsapp.rest.ApiClient;
import com.sarvoyas.newsapp.rest.ApiInterface;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SliderLayout sliderLayout;
    Toolbar toolbar;
    GridView gridView;
    GridAdapter gridAdapter;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    List<HomepageModel.News> news;
    List<HomepageModel.CategoryBotton> categoryBottons;

    int posts = 5;
    int page = 1;
    boolean isFromStart = true;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;

    private AdView adView;

    int backendAdsShown = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intiViews();
        page = 1;
        isFromStart = true;

        getHomeData();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    isFromStart = false;
                    progressBar.setVisibility(View.VISIBLE);
                    page++;
                    getHomeData();
                }
            }
        });
    }

    private void getHomeData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("posts", posts + "");
        Call<HomepageModel> call = apiInterface.getHomepageApi(params);
        call.enqueue(new Callback<HomepageModel>() {
            @Override
            public void onResponse(Call<HomepageModel> call, Response<HomepageModel> response) {

                updateDataToHomapage(response.body());
            }

            @Override
            public void onFailure(Call<HomepageModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateDataToHomapage(HomepageModel body) {

        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (isFromStart) {
            news.clear();
            categoryBottons.clear();
        }

        for (int i = 0; i < body.getBanners().size(); i++) {
            DefaultSliderView defaultSliderView = new DefaultSliderView(this);
            defaultSliderView.setRequestOption(new RequestOptions().centerCrop());
            defaultSliderView.image(body.getBanners().get(i).getImage());
            defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {

                    //TODO:handling click on image
                }
            });
            sliderLayout.addSlider(defaultSliderView);
        }
        sliderLayout.startAutoCycle();
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setDuration(4000);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

        int recentads = 0;

        int beforeNewsSize = news.size();

        for (int i = 0; i < body.getNews().size(); i++) {

            if ((i + 1) % 5 == 0) {
                if (body.getAds().size() > backendAdsShown) {
                    //backend ads
                    HomepageModel.Ad ad = body.getAds().get(backendAdsShown);
                    HomepageModel.News singleNews = new HomepageModel.News(ad.getPid(), ad.getTitle(), ad.getImage(), ad.getLink(), ad.getDescription());
                    news.add(singleNews);
                    backendAdsShown++;
                    singleNews.setAds(true);
                    singleNews.setAdsFromGoogle(false);
                } else {
                    //google ads
                    HomepageModel.News singleNews = new HomepageModel.News();
                    singleNews.setAdsFromGoogle(true);
                    singleNews.setAds(true);
                    news.add(singleNews);
                }
                recentads++;
            }
            news.add(body.getNews().get(i));
        }
        categoryBottons.addAll(body.getCategoryBotton());
        if (isFromStart) {
            recyclerView.setAdapter(newsAdapter);
            gridView.setAdapter(gridAdapter);
        } else {
            newsAdapter.notifyItemRangeChanged(beforeNewsSize, recentads + body.getNews().size());
        }
    }

    private void intiViews() {
        sliderLayout = findViewById(R.id.slider);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressBar = findViewById(R.id.progressbar);
        nestedScrollView = findViewById(R.id.nested);

        categoryBottons = new ArrayList<>();
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(this, categoryBottons);

        //fb ads
        adView = new AdView(this, "4021194884603578_4021237401265993", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(
                        MainActivity.this,
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




        recyclerView= findViewById(R.id.recy_news);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        news=new ArrayList<>();
        newsAdapter= new NewsAdapter(this, news);

        swipeRefreshLayout= findViewById(R.id.swipe);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.blue, R.color.green);
        swipeRefreshLayout.setOnRefreshListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("cid", categoryBottons.get(position).getCid());
                intent.putExtra("cname", categoryBottons.get(position).getCid());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.startAutoCycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.video){
            startActivity(new Intent(this, YoutubeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        isFromStart=true;
        page=1;
        getHomeData();
    }
}