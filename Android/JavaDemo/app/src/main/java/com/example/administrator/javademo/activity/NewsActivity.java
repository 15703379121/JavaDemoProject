package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.adapter.ForumRvAdapter;
import com.example.administrator.javademo.adapter.NewsRvAdapter;
import com.example.administrator.javademo.bean.InformationBean;
import com.example.administrator.javademo.bean.NewsBean;
import com.example.administrator.javademo.bean.VideoBean;
import com.example.administrator.javademo.mApplication;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.view.FullyLinearLayoutManager;
import com.google.gson.reflect.TypeToken;
import com.yixia.camera.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/23 0023.
 */

public class NewsActivity extends BaseActivity {
    @InjectView(R.id.rv_news)
    RecyclerView mRvNews;
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_NET_FAIL = 4;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_SUCCESS:
                    //更新列表
                    listShow();
                    break;
                case MSG_FIND_FAIL:
                    Toast.makeText(NewsActivity.this, "访问目录失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(NewsActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private List<NewsBean> mNewsList;
    private NewsRvAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        //读取SP
        listShow();

        //获取网络新数据
        netReadPost();

        //更改网络新数据状态
        netWritePost();
    }

    private void listShow() {
        try{
            mNewsList = SPUtil.getNewsList(this);
            Log.e("mNewsList.size()----"+mNewsList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        if (mNewsList != null && mRvNews != null) {
            if (mAdapter == null) {
                mRvNews.setLayoutManager(new FullyLinearLayoutManager(this));
                mAdapter = new NewsRvAdapter(this, mNewsList);
                mRvNews.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new NewsRvAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position, View view) {
                        //点击事件
                        NewsBean newsBean = mNewsList.get(position);
                        LogUtil.e("newsBean---"+newsBean);
                        Integer parentId = newsBean.getParentId();
                        Integer type = newsBean.getType();
                        Intent intent = null;
                        switch (type){
                            //类型 0,comment; 1,vote; 2,commentinfo; 3voteinfo; 4,commentSecond
                            case 0:
                                intent = new Intent(NewsActivity.this,VideoActivity.class);
                                intent.putExtra(VideoActivity.VIDEO_ID,parentId);
                                startActivityForResult(intent,31);
                                break;
                            case 1:
                                intent = new Intent(NewsActivity.this,VideoActivity.class);
                                intent.putExtra(VideoActivity.VIDEO_ID,parentId);
                                startActivityForResult(intent,32);
                                break;
                            case 2:
                                intent = new Intent(NewsActivity.this,InformationActivity.class);
                                intent.putExtra(InformationActivity.PARENT_TYPE,type);
                                intent.putExtra(InformationActivity.PARENT_ID,parentId);
                                startActivityForResult(intent,33);
                                break;
                            case 3:
                                intent = new Intent(NewsActivity.this,InformationActivity.class);
                                intent.putExtra(InformationActivity.PARENT_TYPE,type);
                                intent.putExtra(InformationActivity.PARENT_ID,parentId);
                                LogUtil.e("PARENT_TYPE----"+type);
                                LogUtil.e("PARENT_ID----"+parentId);
                                startActivityForResult(intent,33);
                                break;
                            case 4:
                                intent = new Intent(NewsActivity.this,InformationActivity.class);
                                intent.putExtra(InformationActivity.PARENT_TYPE,type);
                                intent.putExtra(InformationActivity.PARENT_ID,parentId);
                                startActivityForResult(intent,33);

                                break;
                        }

                    }
                });
            }else{
                mAdapter.setList(mNewsList);
            }

        }
    }

    private void netWritePost() {
        HashMap<String, Object> map = new HashMap<>();
        String[] user = SPUtil.getUid(this);
        if (user != null){
            map.put("user",Integer.parseInt(user[0]));
            try {
                OkHttpUtil.postJson(AppConstants.NEWS_UPDATE_URL, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = OkHttpUtil.getResult(response);
                        LogUtil.e("result-更新消息状态 ---"+result);
                        if (TextUtils.isEmpty(result) || TextUtils.equals(AppConstants.FAIL,result)){
                            //更新消息状态失败
                            handler.sendEmptyMessage(MSG_FIND_FAIL);

                        }else{
                            //更新消息状态成功
                            mApplication.mNewsSize = "0";
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void netReadPost() {
        HashMap<String,Object> map = new HashMap<>();
        String[] user = SPUtil.getUid(this);
        if (user != null){
            map.put("user",Integer.parseInt(user[0]));
            try {
                OkHttpUtil.postJson(AppConstants.NEWS_FIND_LIST_URL, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(MSG_FIND_FAIL);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = OkHttpUtil.getResult(response);
                        LogUtil.e("result-获取新消息 ---"+result);
                        if (TextUtils.isEmpty(result) || TextUtils.equals(AppConstants.FAIL,result)){
                            //获取消息失败
                            handler.sendEmptyMessage(MSG_FIND_FAIL);

                        }else{
                            //获取消息成功
                            List<NewsBean> list = (List<NewsBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<NewsBean>>() {
                            }.getType());
                            SPUtil.setNewsList(NewsActivity.this,list);
                            handler.sendEmptyMessage(MSG_FIND_SUCCESS);

                        }
                    }
                });
            } catch (IOException e) {
                handler.sendEmptyMessage(MSG_NET_FAIL);
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void processClick(View v) throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
