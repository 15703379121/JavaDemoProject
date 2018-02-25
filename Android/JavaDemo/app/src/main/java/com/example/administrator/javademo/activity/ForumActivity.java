package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.adapter.ForumRvAdapter;
import com.example.administrator.javademo.bean.InformationBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.view.FullyLinearLayoutManager;
import com.example.administrator.javademo.view.find.ImageDialog;
import com.example.administrator.javademo.view.find.VideoDialog;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/19 0019.
 */

public class ForumActivity extends BaseActivity {
    public static final int FORUM_REQUEST_CODE = 20;
    private static final String SP_CACHE_INFORMATION = "sp_cache_information";
    @InjectView(R.id.rv_forum_home)
    RecyclerView mRvForumHome;
    @InjectView(R.id.forum_float_button)
    FloatingActionButton mForumFloatButton;
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final int MSG_FIND_FAIL_LOADMORE = 5;
    private static final int MSG_FIND_SUCCESS_LOADMORE = 6;
    private static final int MSG_NET_FAIL_LOADMORE = 7;
    private static final int MSG_FIND_NOMORE_LOADMORE = 8;
    @InjectView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    private int mPage = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_SUCCESS:
                    mPage++;
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishRefresh(0);
                    }
                    listShow();
                    break;
                case MSG_FIND_FAIL:
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishRefresh(0);
                    }
                    Toast.makeText(ForumActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(ForumActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    netReadPost();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(ForumActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishRefresh(0);
                    }
                    Toast.makeText(ForumActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FIND_SUCCESS_LOADMORE:
                    mPage++;
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishLoadmore(0);
                    }
                    listShow();
                    break;
                case MSG_FIND_NOMORE_LOADMORE:
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishLoadmore(0);
                    }
                    Toast.makeText(ForumActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FIND_FAIL_LOADMORE:
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishLoadmore(0);
                    }
                    Toast.makeText(ForumActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL_LOADMORE:
                    if (mSmartRefreshLayout != null){
                        mSmartRefreshLayout.finishLoadmore(0);
                    }
                    Toast.makeText(ForumActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private List<InformationBean> mInformationList;
    private ForumRvAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forum;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //读取SP
        try {
            String result = SPUtil.getString(this, SP_CACHE_INFORMATION, "");
            mInformationList = (List<InformationBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<InformationBean>>() {
            }.getType());
            listShow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //读取数据库
        netReadPost();
    }

    private void netReadPost() {
        mPage = 0;
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", mPage);
        try {
            OkHttpUtil.postJson(AppConstants.INFORMATION_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Forum查寻------" + result);
                    if (!TextUtils.isEmpty(result)) {
                        LogUtil.e("查寻到拉");
                        mInformationList = (List<InformationBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<InformationBean>>() {
                        }.getType());
                        SPUtil.putString(ForumActivity.this, SP_CACHE_INFORMATION, result);
                        handler.sendEmptyMessage(MSG_FIND_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(MSG_FIND_FAIL);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }
    }

    private void listShow() {

        if (mInformationList != null && mRvForumHome != null) {
            if (mAdapter == null) {
                mRvForumHome.setLayoutManager(new FullyLinearLayoutManager(this));
                mAdapter = new ForumRvAdapter(this, mInformationList);
                mRvForumHome.setAdapter(mAdapter);

                mAdapter.setOnGvItemClickListener(new ForumRvAdapter.OnGvItemClickListener() {

                    @Override
                    public void onClickListener(View v, int position, int listPostion) {
                        //点击图片，放大显示图片
                        showImage(position, mInformationList.get(listPostion));
                    }
                });

                mAdapter.setOnVideoClickListener(new ForumRvAdapter.OnVideoClickListener() {
                    @Override
                    public void onClickListener(View v, int position, String picPath, String thumbImage_path) {

                        //跳转到视频播放
                        playVideo(picPath, thumbImage_path);
                    }
                });
            } else {
                mAdapter.setList(mInformationList);
            }

        }
    }

    /**
     * 播放视频
     *
     * @param picPath
     * @param thumbImage_path
     */
    private void playVideo(String picPath, String thumbImage_path) {
        if (picPath.endsWith(";")) {
            picPath = picPath.substring(0, picPath.length() - 1);
        }
        Intent intent = new Intent(this, VideoDialog.class);
        intent.putExtra("video_path", picPath);
        intent.putExtra("thumbImage_path", thumbImage_path);//缩略图路径
        intent.putExtra("useCache", true);
        startActivity(intent);
    }

    @Override
    protected void initEvent() {
        mForumFloatButton.setOnClickListener(this);

        //刷新与加载
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //刷新
                netReadPost();
//                refreshlayout.finishRefresh(2000);
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                if (mPage == 0){
                    //没有网或是没有加载过来呢
                    mSmartRefreshLayout.finishLoadmore(0);
                }else{
                    //访问网络
                    netReadPostLoad();
                }
//                refreshlayout.finishLoadmore(2000);
            }
        });
    }
    private void netReadPostLoad() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", mPage);
        try {
            OkHttpUtil.postJson(AppConstants.INFORMATION_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_FIND_FAIL_LOADMORE);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Forum查寻------" + result);
                    if (!TextUtils.isEmpty(result)) {
                        LogUtil.e("查寻到拉");
                        List<InformationBean> list = (List<InformationBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<InformationBean>>() {
                        }.getType());
                        if (list != null && list.size() > 0){
                            //有数据
                            mInformationList.addAll(list);
                            SPUtil.putString(ForumActivity.this, SP_CACHE_INFORMATION, GsonUtil.getGson().toJson(mInformationList));
                            handler.sendEmptyMessage(MSG_FIND_SUCCESS_LOADMORE);
                        }else{
                            //无数据拉
                            handler.sendEmptyMessage(MSG_FIND_NOMORE_LOADMORE);
                        }
                    } else {
                        handler.sendEmptyMessage(MSG_FIND_FAIL_LOADMORE);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(MSG_NET_FAIL_LOADMORE);
            e.printStackTrace();
        }
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.forum_float_button:
                //发布新文章
                Intent intent = new Intent(this, PublishActivity.class);
                startActivityForResult(intent, FORUM_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FORUM_REQUEST_CODE && resultCode == PublishActivity.PUBLIC_RESULT_CODE) {
            //TODO 发布说说后，更新说说列表
            //读取数据库
            netReadPost();
        }
    }

    /**
     * 放大显示图片
     */
    private void showImage(int position, InformationBean listBean) {
        final ImageDialog imageDialog = ImageDialog.getDialog(this);
        String pic = listBean.getUrl();
        if (pic.endsWith(";")) {
            pic = pic.substring(0, pic.length() - 1);
        }
        String[] split = pic.split(";");

        imageDialog.setImageData(split, position);

        imageDialog.setOnDialogClickListener(new ImageDialog.DialogItemClickListener() {
            @Override
            public void itemClick(int position) {
                imageDialog.dismiss();
            }
        });

//        imageDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageDialog.show();


    }
}
