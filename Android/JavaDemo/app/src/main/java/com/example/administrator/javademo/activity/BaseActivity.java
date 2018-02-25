package com.example.administrator.javademo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.mApplication;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/7/5.
 * Activity基类
 */

public abstract class BaseActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //初始化ButterKinfe
        ButterKnife.inject(this);
        initView();
        initData();
        initEvent();
        regComButton();
        LogUtil.e("BaseActivity-------newsShow()");
        newsShow();
    }

    /**
     * 展示新消息
     */
    protected void newsShow() {
        HashMap<String, Object> map = new HashMap<>();
        String[] user = SPUtil.getUid(this);
        if (user != null) {
            map.put("user", Integer.parseInt(user[0]));
            try {
                OkHttpUtil.postJson(AppConstants.NEWS_FIND_SIZE_URL, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = OkHttpUtil.getResult(response);
                        if (!TextUtils.isEmpty(result) && result.startsWith(AppConstants.SUCCESS)){
                            String[] split = result.split(":");
                            if (split != null && split.length == 2){
                                mApplication.mNewsSize = split[1];
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**获取布局id*/
    protected abstract int getLayoutId();

    /**初始化view*/
    protected abstract void initView();

    /**初始化数据*/
    protected abstract void initData();

    /**初始化事件*/
    protected abstract void initEvent();

    /**
     * 将所有页面都有的共性按钮的点击事件在这里注册
     */
    private void regComButton() {
        View view = findViewById(R.id.back);
        if (view!=null){
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //在多个页面都存在的点击，统一在此处处理
            case R.id.back:
                finish();
                break;
            default:
                try {
                    processClick(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 在baseActivity中没有处理的点击事件，在此处处理
     */
    protected abstract void processClick(View v) throws IOException;

}
