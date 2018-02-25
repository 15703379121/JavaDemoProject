package com.example.administrator.javademo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.VideoBean;
import com.example.administrator.javademo.util.VideoUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class RelaVideoAdapter extends RecyclerView.Adapter<RelaVideoAdapter.MyViewHolder> {
    private final List<VideoBean> list;
    private final Context context;
    private onRecyclerViewItemClickListener itemClickListener;

    public RelaVideoAdapter(Context context, List<VideoBean> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_video,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        VideoBean bean = list.get(position);
        VideoUtil.setThumbnail(context,bean.getUrl()+"",holder.iv_item_bg);
        holder.tv_item.setText(bean.getTitle()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener!=null){
                    itemClickListener.onItemClick(view,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView iv_item_bg;
        private final TextView tv_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_item_bg = (ImageView) itemView.findViewById(R.id.iv_class_bg_item);
            tv_item = (TextView) itemView.findViewById(R.id.tv_class_bg_buttom);

        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;

    }


    /**条目点击事件的监听器*/
    public  interface onRecyclerViewItemClickListener {

        void onItemClick(View v,int position);
    }
}
