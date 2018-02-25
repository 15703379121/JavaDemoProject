package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.PracticeBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileTypeUtil;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OpenFileUtil;
import com.example.administrator.javademo.util.SPUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2018/2/23 0023.
 */

public class DownloadPracticeActivity extends BaseActivity {
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.bt_video_add)
    Button mBtVideoAdd;
    @InjectView(R.id.tv_null)
    TextView mTvNull;
    @InjectView(R.id.ll_view_info)
    LinearLayout mLlViewInfo;
    @InjectView(R.id.lv_video_list)
    ListView mLvVideoList;
    private List<String> downloadedList;
    private boolean mIsLoadedEmpty;
    private MyAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        mBtVideoAdd.setVisibility(View.GONE);
        mTvTitle.setText("习题列表");
    }

    @Override
    protected void initData() {
        new Thread(){
            @Override
            public void run() {
                File loadedDir = new File(AppConstants.FILE_DOWN_PRACTICE);
                String[] mLoadedFiles = loadedDir.list();
                downloadedList = new ArrayList<>();
                if (mLoadedFiles != null && mLoadedFiles.length > 0){
                    for (int i = 0; i < mLoadedFiles.length; i++) {
                        String filePath = AppConstants.FILE_DOWN_PRACTICE + "/" + mLoadedFiles[i];
                        File file = new File(filePath);
                        if (!file.isDirectory()){
                            //  这是一个文件
                            downloadedList.add(filePath);
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
                            if (mAdapter == null) {
                                mAdapter = new MyAdapter();
                                mLvVideoList.setAdapter(mAdapter);
                            }else{
                                mAdapter.notifyDataSetChanged();
                            }
                        }else{
                            mLlViewInfo.setVisibility(View.VISIBLE);
                            mTvNull.setText("暂无相关习题");
                        }
                    }
                });
            }
        }.start();
    }
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return downloadedList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(DownloadPracticeActivity.this).inflate(R.layout.item_lv_video_list, null);
                holder.iv_bg = (ImageView) view.findViewById(R.id.iv_class_bg_item);
                holder.tv_title = (TextView) view.findViewById(R.id.tv_video_title);
                holder.tv_teacher = (TextView) view.findViewById(R.id.tv_video_teacher);
//                holder.iv_bg.setVisibility(View.GONE);
                holder.iv_bg.setBackgroundResource(R.mipmap.icon_practice);
                ViewGroup.LayoutParams layoutParams = holder.iv_bg.getLayoutParams();
                layoutParams.width = 40;
                layoutParams.height = 40;
                holder.iv_bg.setLayoutParams(layoutParams);
                holder.tv_teacher.setVisibility(View.GONE);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_title.setText(FileUtil.getFileNameByPath(downloadedList.get(i)));


            return view;
        }

        class ViewHolder {
            ImageView iv_bg;
            TextView tv_title;
            TextView tv_teacher;
        }
    }
    @Override
    protected void initEvent() {
        mLvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转打开
                String filePath = downloadedList.get(i);
//                fileType = "."+split[split.length - 1];
//                filePath = AppConstants.FILE_DOWN_PRACTICE + File.separator + downloadedList.get(i);
                LogUtil.e("filePath--------"+ filePath);
                File file = new File(filePath);
                if (file.exists()){
                    //打开它
                    openFile(filePath);
                }
            }
        });

        mLvVideoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //删除
                return true;
            }
        });
    }
    private void openFile(String filePath) {
        String fileName = FileUtil.getFileNameByPath(filePath);
        String fileType = FileUtil.getSuffix(fileName);
        LogUtil.e("fileType-------"+fileType);
        LogUtil.e("filePath-------"+filePath);
        try{
            Intent intent = null;
            if (".doc".equals(fileType) || ".docx".equals(fileType)){
                intent = OpenFileUtil.getWordFileIntent(filePath);
            }else if (".xls".equals(fileType) || ".xlsx".equals(fileType) || ".xlt".equals(fileType) || ".xlsm".equals(fileType)){
                OpenFileUtil.getExcelFileIntent(filePath);
            }else if (".ppt".equals(fileType) || ".pps".equals(fileType) || ".pptx".equals(fileType)){
                OpenFileUtil.getPPTFileIntent(filePath);
            }else if (".txt".equals(fileType)){
                OpenFileUtil.getTextFileIntent(filePath);
            }else if (".htm".equals(fileType) || ".html".equals(fileType)){
                OpenFileUtil.getHtmlFileIntent(filePath);
            }else if (".BMP".equals(fileType) || ".GIF".equals(fileType) || fileType.contains(".JPEG") ||
                    ".PNG".equals(fileType) || "SVG".equals(fileType) || ".PSD".equals(fileType) ||
                    ".TIFF".equals(fileType) ){
                OpenFileUtil.getImageFileIntent(filePath);
            }else if (".pdf".equals(fileType)){
                OpenFileUtil.getPdfFileIntent(filePath);
            }else {
                Toast.makeText(this, "请到文件管理器打开文件", Toast.LENGTH_SHORT).show();
            }
            if (intent != null)
                startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            //没有安装第三方的软件会提示
            Toast toast = Toast.makeText(this, "没有找到打开该文件的应用程序", Toast.LENGTH_SHORT);
            toast.show();
        }
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
