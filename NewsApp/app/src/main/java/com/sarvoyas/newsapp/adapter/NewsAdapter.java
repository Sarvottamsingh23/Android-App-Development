package com.sarvoyas.newsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.activity.NewsDetailActivity;
import com.sarvoyas.newsapp.model.HomepageModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<HomepageModel.News> news;
    int image_left = 1;
    int image_top = 2;
    int ads=3;


    public NewsAdapter(Context context, List<HomepageModel.News> news) {
        this.context = context;
        this.news = news;
    }

    @Override
    public int getItemViewType(int position) {

        if (news.get(position).isAds()){
            return  ads;
        }else{
            if ((position + 1) % 8 == 0 || position == 0) {
                return image_top;
            } else {
                return image_left;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == image_left) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
            return new ViewHolder(v);
        } else if(viewType==ads){
            View v = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
            return  new AdsViewHolder(v);
        }else {
            View v = LayoutInflater.from(context).inflate(R.layout.item_news_image, parent, false);
            return new ViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomepageModel.News singleNews = news.get(holder.getAdapterPosition());

        if(singleNews.isAds()){
           //load advertisement from google or our backend
            AdsViewHolder adsViewHolder= (AdsViewHolder) holder;
            if (singleNews.isAdsFromGoogle()){
                //then ,load ad from google
                adsViewHolder.adView.setVisibility(View.VISIBLE);
                adsViewHolder.relativeLayout.setVisibility(View.GONE);

                AdView adView= adsViewHolder.adView;
                adView.loadAd(new AdRequest.Builder().build());
            }else{
                //load ads from backend
                adsViewHolder.adView.setVisibility(View.GONE);
                adsViewHolder.relativeLayout.setVisibility(View.VISIBLE);

                adsViewHolder.adsTitle.setText(singleNews.getTitle());
                adsViewHolder.adsDescp.setText(singleNews.getPostContent());
                Glide.with(context).load(singleNews.getImage()).into(adsViewHolder.adsImage);

            }
        }else{
            //load news
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.newsTitle.setText(removeHtml(singleNews.getTitle()).trim());
            viewHolder.newsDescp.setText(removeHtml(singleNews.getPostContent()).trim());

            if (singleNews.getSource() != null) {
                viewHolder.newsSource.setText(singleNews.getSource());
            }
            if(singleNews.getImage().length()<=1){
                viewHolder.newsImage.setVisibility(View.GONE);
            }else{
                viewHolder.newsImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(singleNews.getImage()).into(viewHolder.newsImage);
            }
            viewHolder.newsView.setText(singleNews.getViews()+" Views");
        }

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View holder;
        ImageView newsImage;
        TextView newsTitle, newsDescp, newsDate, newsSource, newsView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            holder = itemView;
            newsImage = holder.findViewById(R.id.news_image);
            newsTitle = holder.findViewById(R.id.news_title);
            newsDescp = holder.findViewById(R.id.news_descp);
            newsDate = holder.findViewById(R.id.news_date);
            newsSource = holder.findViewById(R.id.news_source);
            newsView = holder.findViewById(R.id.news_view);
            holder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent= new Intent(context, NewsDetailActivity.class);
            intent.putExtra("pid", news.get(getAdapterPosition()).getPid());
            context.startActivity(intent);
        }
    }
    public static String removeHtml(String html) {
        html = html.replaceAll("<(.*?)\\>", " ");
        html = html.replaceAll("<(.*?)\\\n", " ");
        html = html.replaceFirst("(.*?)\\>", " ");
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&amp;", " ");
        return html;
    }

    public class AdsViewHolder extends  RecyclerView.ViewHolder{
        View holder;
        TextView adsTitle, adsDescp;
        ImageView adsImage;
        AdView adView;
        RelativeLayout relativeLayout;


        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);

            holder= itemView;
            adsTitle=holder.findViewById(R.id.ads_title);
            adsDescp=holder.findViewById(R.id.ads_descp);
            adsImage=holder.findViewById(R.id.ads_image);
            adView=holder.findViewById(R.id.adView);
            relativeLayout= holder.findViewById(R.id.custom_ads_wrapper);
        }
    }

}























