package com.example.administrator.javademo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.NewsBean;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.VideoUtil;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2018/2/23 0023.
 */

public class NewsRvAdapter extends RecyclerView.Adapter<NewsRvAdapter.MyViewHolder> {

    private List<NewsBean> list;
    private final Context context;
    private OnItemClickListener listener;

    public NewsRvAdapter(Context context, List<NewsBean> list){
        this.list = list;
        this.context = context;
    }

    @Override
    public NewsRvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_news,parent,false));
    }

    @Override
    public void onBindViewHolder(NewsRvAdapter.MyViewHolder holder, final int position) {
        NewsBean newsBean = list.get(position);
        holder.tv_name1.setText(newsBean.getUser().getUsername());
        String operate1 = "";
        String operate2 = "";
        switch (newsBean.getType()){
            //类型 0,comment; 1,vote; 2,commentinfo; 3voteinfo; 4,commentSecond
            case 0:
                operate1 = "评论了";
                operate2 = "的视频";
                break;
            case 1:
                operate1 = "点赞了";
                operate2 = "的视频";
                break;
            case 2:
                operate1 = "评论了";
                operate2 = "的论坛";
                break;
            case 3:
                operate1 = "点赞了";
                operate2 = "的论坛";
                break;
            case 4:
                operate1 = "评论了";
                operate2 = "的评论";
                break;
        }
        holder.tv_operate1.setText(operate1);
        holder.tv_operate2.setText(operate2);
        holder.tv_content.setText(newsBean.getContent()+"");
        holder.tv_time.setText(""+FileUtil.getName((new Timestamp(newsBean.getCreateTime()).toString())));
        if (!TextUtils.isEmpty(newsBean.getPicUrl())){
            holder.iv_news.setVisibility(View.VISIBLE);
            if (newsBean.getType() == 0 || newsBean.getType() == 1){
                //视频
                VideoUtil.setThumbnail(context,newsBean.getPicUrl(),holder.iv_news);
            }else {
                //图片
                Picasso.with(context).load(newsBean.getPicUrl()).placeholder(R.mipmap.icon).error(R.mipmap.icon).into(holder.iv_news);
            }
        }else{
            holder.iv_news.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onItemClickListener(position,v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_name1;
        private final TextView tv_name2;
        private final TextView tv_operate1;
        private final TextView tv_operate2;
        private final TextView tv_content;
        private final TextView tv_time;
        private final ImageView iv_news;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name1 = (TextView) itemView.findViewById(R.id.tv_pname1);
            tv_name2 = (TextView) itemView.findViewById(R.id.tv_pname2);
            tv_operate1 = (TextView) itemView.findViewById(R.id.tv_operate1);
            tv_operate2 = (TextView) itemView.findViewById(R.id.tv_operate2);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            iv_news = (ImageView) itemView.findViewById(R.id.iv_news);

        }
    }

    public void setList(List<NewsBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClickListener(int position,View view);
    }
}
