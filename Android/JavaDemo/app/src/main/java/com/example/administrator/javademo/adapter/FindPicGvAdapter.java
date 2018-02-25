package com.example.administrator.javademo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.util.AppConstants;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridAdapter;

import java.util.List;

/**
 * Created by 李晓曼 on 2017/8/5.
 */

public class FindPicGvAdapter extends NineGridAdapter {

    public FindPicGvAdapter(Context context, List list) {
        super(context, list);

    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();

    }

    @Override
    public String getUrl(int position) {
        return getItem(position) == null ? null : ((String)getItem(position));

    }

    @Override
    public Object getItem(int position) {
        return (list == null) ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view) {
        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        Picasso.with(context).load(getUrl(i)).fit().centerInside().placeholder(R.mipmap.icon).error(R.mipmap.icon).into(iv);
        return iv;
    }
}