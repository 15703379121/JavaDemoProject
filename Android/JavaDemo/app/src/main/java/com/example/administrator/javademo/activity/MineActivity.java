package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.mApplication;
import com.example.administrator.javademo.util.SPUtil;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2018/2/13 0013.
 */

public class MineActivity extends BaseActivity {
    @InjectView(R.id.ll_mine_video)
    LinearLayout mLlMineVideo;
    @InjectView(R.id.ll_mine_practice)
    LinearLayout mLlMinePractice;
    @InjectView(R.id.ll_mine_update_psd)
    LinearLayout mLlMineUpdatePsd;
    @InjectView(R.id.ll_mine_checkout)
    LinearLayout mLlMineCheckout;
    @InjectView(R.id.tv_identifier)
    TextView mTvIdentifier;
    @InjectView(R.id.tv_username)
    TextView mTvUsername;
    @InjectView(R.id.tv_news)
    TextView mTvNews;
    @InjectView(R.id.ll_mine_news)
    LinearLayout mLlMineNews;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTvNews!=null){
            if (!TextUtils.equals("0", mApplication.mNewsSize) ){
                //有新消息
                mTvNews.setVisibility(View.VISIBLE);
                mTvNews.setText(mApplication.mNewsSize+"");
            }else{
                //没有新消息
                mTvNews.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData() {
        String[] uid = SPUtil.getUid(this);
        if (uid != null) {
            mTvIdentifier.setText("账号 " + uid[1]);
            mTvUsername.setText("姓名 " + uid[2]);
        } else {
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));
//            finish();
        }
        if (!TextUtils.equals("0", mApplication.mNewsSize) ){
            //有新消息
            mTvNews.setVisibility(View.VISIBLE);
            mTvNews.setText(mApplication.mNewsSize+"");
        }else{
            //没有新消息
            mTvNews.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initEvent() {
        mLlMineVideo.setOnClickListener(this);
        mLlMinePractice.setOnClickListener(this);
        mLlMineUpdatePsd.setOnClickListener(this);
        mLlMineCheckout.setOnClickListener(this);
        mLlMineNews.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ll_mine_video:
                intent = new Intent(this, DownloadListActivity.class);
                break;
            case R.id.ll_mine_practice:
                intent = new Intent(this, DownloadPracticeActivity.class);
                break;
            case R.id.ll_mine_update_psd:
                //修改密码
                break;
            case R.id.ll_mine_checkout:
                //退出登录
                signOut();
                break;
            case R.id.ll_mine_news:
                intent = new Intent(this,NewsActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    /**
     * 退出登录
     */
    private void signOut() {
        SPUtil.deleteSp(this);
        startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
