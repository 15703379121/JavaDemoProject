package com.example.administrator.javademo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.TextBean;
import com.example.administrator.javademo.bean.UserBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.DialogUtil;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class PracticeSourceActivity extends Activity {

    private static final int FINISH_TEXT = 0;
    public static final int PRACTICE_SOURCE_CODE_RESULT = 6;
    public static final String PRACTICE_INFO_BEAN_LOCAL = "practice_info_bean_local";
    private List<TextBean> list = new ArrayList<>();
    private File file;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FINISH_TEXT:
                    LogUtil.e("搜索完拉");
                    if (!list.isEmpty()){
                        showList();
                    }else{
                        showText();
                    }
                    break;
            }
        }
    };

    private ListView lv_text;
    private TextView tv_tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);
        sdPermission(this);
        file = new File(AppConstants.FILE_PRACTICE);
        if (!file.exists()){
            file.mkdirs();
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                LogUtil.e("搜索文档");
                traverseFolder(file);
                handler.sendEmptyMessage(FINISH_TEXT);
            }
        }.start();
        initView();

    }

    private void initView() {
        lv_text = (ListView) findViewById(R.id.lv_text);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
    }

    private void showText() {
        lv_text.setVisibility(View.GONE);
        tv_tag.setVisibility(View.VISIBLE);
        tv_tag.setText("对不起，没有找到文件");
    }

    private void showList() {
        tv_tag.setVisibility(View.GONE);
        lv_text.setVisibility(View.VISIBLE);
        lv_text.setAdapter(new MyAdapter());
        lv_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = getIntent();
                intent.putExtra(PRACTICE_INFO_BEAN_LOCAL,list.get(i));
                setResult(PRACTICE_SOURCE_CODE_RESULT,intent);
                finish();
            }
        });
    }

    public void traverseFolder(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder(file2);
                    } else {
                        //获取并计算文件大小
                        long size = file2.length();
                        String t_size = "";
                        if (size <= 1024) {
                            t_size = size + "B";
                        } else if (size > 1024 && size <= 1024 * 1024) {
                            size /= 1024;
                            t_size = size + "KB";
                        } else {
                            size = size / (1024 * 1024);
                            t_size = size + "MB";
                        }
                        TextBean bean = new TextBean(t_size, file2.getName(), file2.getAbsolutePath());
                        list.add(bean);
                        /*if (file2.getName().endsWith(".txt")) {//格式为txt文件

                        }*/
                    }
                }
            }
        } else {
            LogUtil.e("文件不存在");
        }
    }

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
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
            if (view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(PracticeSourceActivity.this).inflate(R.layout.item_lv_info, null);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_file_name);
                holder.tv_path = (TextView) view.findViewById(R.id.tv_file_path);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_file_size);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.tv_name.setText(list.get(i).getFile_name()+"");
            holder.tv_path.setText(list.get(i).getFile_txt_path()+"");
            holder.tv_size.setText(list.get(i).getFile_size()+"");
            return view;
        }
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_path;
        TextView tv_size;
    }
    Object invokeVolumeList = null;
    Method getVolumeList= null;
    StorageManager mStorageManager;

    private void sdPermission(Context context) {
        mStorageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        try {
            getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            try {
                final Class<?>storageValumeClazz = Class.forName("android.os.storage.StorageVolume");
                final Method getPath= storageValumeClazz.getMethod("getPath");
                Method isRemovable = storageValumeClazz.getMethod("isRemovable");
                Method mGetState = null;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    try {
                        mGetState = storageValumeClazz.getMethod("getState");
                    } catch(NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    invokeVolumeList= getVolumeList.invoke(mStorageManager);
                }catch (Exception e) {
                }
                final int length = Array.getLength(invokeVolumeList);
                for(int i = 0; i<length ;i++) {
                    final Object storageValume= Array.get(invokeVolumeList, i);//
                    final String path =(String) getPath.invoke(storageValume);
                    final boolean removable =(Boolean) isRemovable.invoke(storageValume);
                    String state = null;
                    if (mGetState !=null) {
                        state = (String) mGetState.invoke(storageValume);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,"couldn't talkto MountService", e);
            }
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
