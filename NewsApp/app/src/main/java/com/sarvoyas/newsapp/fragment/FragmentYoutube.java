package com.sarvoyas.newsapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.adapter.YoutubeAdapter;
import com.sarvoyas.newsapp.model.HomepageModel;
import com.sarvoyas.newsapp.model.YtModel;
import com.sarvoyas.newsapp.rest.ApiClient;
import com.sarvoyas.newsapp.rest.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentYoutube extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    Context context;
    String cid, pageToken="";
    YoutubeAdapter youtubeAdapter;
    List<YtModel.Item> items = new ArrayList<>();
    boolean isFromStart = true;
    boolean shouldFetchData = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        recyclerView = view.findViewById(R.id.video_recy);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        progressBar = view.findViewById(R.id.progressbar);
        youtubeAdapter = new YoutubeAdapter(context, items, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        cid = getArguments().getString("cid");
        loadData(pageToken);

        swipeRefreshLayout.setRefreshing(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int passVisibleCount = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                if ((passVisibleCount + visibleItemCount) == totalItemCount) {
                    if (shouldFetchData) {
                        shouldFetchData = false;
                        isFromStart = false;
                        progressBar.setVisibility(View.VISIBLE);
                        loadData(pageToken);
                    }
                }
            }
        });
        return view;
    }

    private void loadData(String nextPageToken) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("part", "snippet");
        params.put("channelId", cid);
        params.put("maxResults", "100");
        params.put("pageToken", nextPageToken);
        params.put("key", getString(R.string.google_api_key));
        Call<YtModel> call = apiInterface.getYoutubeServerData(params);
        call.enqueue(new Callback<YtModel>() {
            @Override
            public void onResponse(Call<YtModel> call, Response<YtModel> response) {
                int beforeSize = items.size();
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                items.addAll(response.body().getItems());
                if(isFromStart) {
                    recyclerView.setAdapter(youtubeAdapter);
                }else{
                    youtubeAdapter.notifyItemRangeInserted(beforeSize, response.body().getItems().size());
                }
                shouldFetchData=true;
            }

            @Override
            public void onFailure(Call<YtModel> call, Throwable t) {

                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
