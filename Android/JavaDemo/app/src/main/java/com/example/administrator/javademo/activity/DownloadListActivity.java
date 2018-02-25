package com.example.administrator.javademo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.DownLoadBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileTypeUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.util.VideoUtil;
import com.yixia.camera.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

public class DownloadListActivity extends Activity implements View.OnClickListener{

    public static final int DOWNLOAD_LIST_REQUEST_CODE = 21;
    public static final String VIDEO_LOCAL_PATH = "video_local_path";
    private FrameLayout mFlDownloading;
    private ListView mLvDownloadList;
    private LinearLayout mLlDownloadList;
    private LinearLayout mLlDownloadListNull;
    private boolean mIsLoadingEmpty = false;
    private boolean mIsLoadedEmpty = false;
    private MyAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        initView();
//        initData();
        initEvent();
    }

    private void initView() {
        mLvDownloadList = (ListView) findViewById(R.id.lv_download_list);
        mFlDownloading = (FrameLayout) findViewById(R.id.fl_downloading);
        mLlDownloadList = (LinearLayout) findViewById(R.id.ll_download_list);
        mLlDownloadListNull = (LinearLayout) findViewById(R.id.ll_download_list_null);
    }
    private List<String > downloadedList;
    private void initData() {
        //正在下载中文件
        List<DownLoadBean> downloadList = SPUtil.getVideoDownloadList(this);
        if (downloadList!=null && downloadList.size() >0){
            mFlDownloading.setVisibility(View.VISIBLE);
        }else{
            mIsLoadingEmpty = true;
            mFlDownloading.setVisibility(View.GONE);
        }
        //已下载文件
        new Thread(){
            @Override
            public void run() {
                File loadedDir = new File(AppConstants.FILE_DOWN_VIDEO);
                String[] mLoadedFiles = loadedDir.list();
                downloadedList = new ArrayList<>();
                if (mLoadedFiles != null && mLoadedFiles.length > 0){
                    for (int i = 0; i < mLoadedFiles.length; i++) {
                        String filePath = AppConstants.FILE_DOWN_VIDEO + "/" + mLoadedFiles[i];
                        File file = new File(filePath);
                        if (!file.isDirectory()){
                            //  这是一个文件
                            if (FileTypeUtil.isVideo(filePath)){
                                //这是一个视频文件
                                downloadedList.add(mLoadedFiles[i]);
                            }
                        }
                    }
                }else{
                    mIsLoadedEmpty = true;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsLoadedEmpty) {
                            //初始化列表
                            mAdapter = new MyAdapter();
                            mLvDownloadList.setAdapter(mAdapter);
                        }

                        //如果既没有已下载文件也没有下载中文件则显示空
                        if (mIsLoadedEmpty && mIsLoadingEmpty){
                            mLlDownloadList.setVisibility(View.GONE);
                            mLlDownloadListNull.setVisibility(View.VISIBLE);
                        }else{
                            mLlDownloadList.setVisibility(View.VISIBLE);
                            mLlDownloadListNull.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();
    }

    private void initEvent() {
        mFlDownloading.setOnClickListener(this);
        mLvDownloadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转播放页面
                Intent intent = new Intent(DownloadListActivity.this, VideoLocalActivity.class);
                intent.putExtra(VIDEO_LOCAL_PATH,downloadedList.get(i));
                startActivityForResult(intent,DOWNLOAD_LIST_REQUEST_CODE);
            }
        });
        mLvDownloadList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadListActivity.this);
                TextView tv = new TextView(DownloadListActivity.this);
                tv.setText("删除");
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(30,10,30,10);
                tv.setBackgroundColor(Color.WHITE);
                builder.setView(tv);
                final AlertDialog dialog = builder.create();
                dialog.show();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //若是本地有文件则也删除本地文件
                        File file = new File(AppConstants.FILE_DOWN_VIDEO + "/" + downloadedList.get(position));
                        if (file.exists()){
                            file.delete();
                        }
                        //更新列表
                        updateList(position);
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }


    private void updateList(int position){
        downloadedList.remove(position);
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_downloading:
                startActivity(new Intent(this,DownloadActivity.class));
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return downloadedList.size();
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
            convertView = LayoutInflater.from(DownloadListActivity.this).inflate(R.layout.item_download_list,null);
            ImageView iv_bg = (ImageView) convertView.findViewById(R.id.iv_video_bg);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_file_name);
            /*Bitmap videoThumbnail = VideoUtil.getVideoThumbnail(AppConstants.FILE_DOWN_VIDEO + "/" + downloadedList.get(position));

            if (videoThumbnail != null) {
                iv_bg.setImageBitmap(videoThumbnail);
            }*/
            VideoUtil.setVideoLocalThumbnail(DownloadListActivity.this,AppConstants.FILE_DOWN_VIDEO + "/" + downloadedList.get(position),iv_bg);
            tv_name.setText(""+downloadedList.get(position));
            return convertView;
        }
    }
}
