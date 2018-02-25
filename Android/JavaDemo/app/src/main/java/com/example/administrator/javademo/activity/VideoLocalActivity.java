package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.PixelUtils;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2018/2/2 0002.
 */

public class VideoLocalActivity extends BaseActivity {
    @InjectView(R.id.videoView)
    VideoView mVideoView;
    @InjectView(R.id.iv_video_start)
    ImageView mIvStart;
    @InjectView(R.id.fl_video_group)
    FrameLayout mFlVideoGroup;
    private MediaController mController;
    private long mCurrentPosition = 0;
    private String mFilePath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_local;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mVideoView != null) {
            mVideoView.seekTo(mCurrentPosition);
            mVideoView.start();
        }
    }

    @Override
    protected void initView() {
        //设置无标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉系统通知栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        //调整mFlVideoGroup布局参数
        /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mFlVideoGroup.setLayoutParams(params);*/

        Vitamio.initialize(getApplicationContext());
        Intent intent = getIntent();
        if (!LibsChecker.checkVitamioLibs(this))
            return;

        mFilePath = AppConstants.FILE_DOWN_VIDEO + "/" + intent.getStringExtra(DownloadListActivity.VIDEO_LOCAL_PATH);
        LogUtil.e("videoPath----" + mFilePath);
        if (!new File(mFilePath).exists()) {
            return;
        }
        mController = new MediaController(this, true, mFlVideoGroup);
        //上来先隐藏controller
        mController.setVisibility(View.GONE);

    }

    @Override
    protected void initData() {
        init();

    }
    private void init() {

        //设置缩略图
//        VideoUtil.setThumbnail(this, mVideoBean.getUrl() + "", mIvThumbnail);

        //设置视频
        mVideoView.setVideoPath(mFilePath);
        mVideoView.requestFocus();
//        mIvThumbnail.setVisibility(View.GONE);
        mVideoView.setMediaController(mController);
        mIvStart.setVisibility(View.GONE);
    }

    @Override
    protected void initEvent() {
        Log.e("radish", "initEvent:");
        mIvStart.setOnClickListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                LogUtil.e("准备好了");
                mVideoView.start();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//                mediaPlayer.setPlaybackSpeed(1.0f);
                LogUtil.e("开始播放");
//                mVideoView.start();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //停止播放
                mVideoView.stopPlayback();
                mIvStart.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.iv_video_start:
                LogUtil.e("click----start");
                if (mVideoView != null) {
                    LogUtil.e("click----start");
                    mVideoView.setVideoPath(mFilePath);
                    mIvStart.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop");
        if (mVideoView != null) {
            //清除缓存
//            mVideoView.destroyDrawingCache();
            //停止播放
            mVideoView.pause();
            mCurrentPosition = mVideoView.getCurrentPosition();
//            mVideoView = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("onDestroy");
        if (mVideoView != null) {
            //清除缓存
            mVideoView.destroyDrawingCache();
            //停止播放
            mVideoView.stopPlayback();
            mVideoView = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

}
