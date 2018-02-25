package com.example.administrator.javademo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.CommentBean;
import com.example.administrator.javademo.util.FileUtil;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2018/2/18 0018.
 */

public class CommentAdapter extends BaseAdapter {

    List<CommentBean> list;
    Context context;

    public CommentAdapter(Context context,List<CommentBean> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_comment,null);
            viewHolder.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommentBean commentBean = list.get(position);
        viewHolder.tv_user.setText(commentBean.getUser().getUsername()+":");
        viewHolder.tv_content.setText(commentBean.getContent());
        Timestamp timestamp = new Timestamp(commentBean.getCreateTime());
        String time = FileUtil.getName(timestamp.toString());
        viewHolder.tv_time.setText(time+"");
        return convertView;
    }

    class ViewHolder{
        TextView tv_user;
        TextView tv_time;
        TextView tv_content;
    }

    public void setList(List<CommentBean> list){
        this.list = list;
    }
}
