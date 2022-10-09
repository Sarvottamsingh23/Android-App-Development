package com.sarvoyas.newsapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sarvoyas.newsapp.R;
import com.sarvoyas.newsapp.model.HomepageModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    Context context;
    List<HomepageModel.CategoryBotton> categoryBottons;

    public  GridAdapter(Context context, List<HomepageModel.CategoryBotton> categoryBottons){
    this.context=context;
    layoutInflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    this.categoryBottons=categoryBottons;
    }

    @Override
    public int getCount() {
        return categoryBottons.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryBottons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder= null;
        if(view==null){
            view= layoutInflater.inflate(R.layout.item_category, null);
            holder= new ViewHolder();
            holder.circleImageView= view.findViewById(R.id.category_image);
            holder.categoryName= view.findViewById(R.id.category_name);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        holder.categoryName.setText(categoryBottons.get(i).getName());
        Glide.with(context).load(categoryBottons.get(i).getImage()).into(holder.circleImageView);

        if(categoryBottons.get(i).getColor() != null ) {

            holder.circleImageView.setCircleBackgroundColor(Color.parseColor(categoryBottons.get(i).getColor()));
            holder.circleImageView.setBorderColor(Color.parseColor(categoryBottons.get(i).getColor()));
        }

        return view;
    }
    static  class ViewHolder{
        CircleImageView circleImageView;
        TextView categoryName;
    }

}
