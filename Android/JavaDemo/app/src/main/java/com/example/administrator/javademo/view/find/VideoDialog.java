package com.example.administrator.javademo.view.find;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.activity.BaseActivity;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by 81521 on 2017/8/10.
 * 发现页面视频播放页面
 */

public class VideoDialog extends BaseActivity {
    @InjectView(R.id.videoView)
    VideoView mVideoView;
    @InjectView(R.id.iv_video_thumbnail)
    ImageView mIvVideoThumbnail;
    @InjectView(R.id.fl_video_group)
    FrameLayout mFlVideoGroup;
    @InjectView(R.id.video_dialog)
    LinearLayout mVideoDialog;
    //    private SurfaceVideoViewCreator surfaceVideoViewCreator;
    private String video_path;
    private String thumbImage_path;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_find_video;
    }

    @Override
    protected void initView() {
        video_path = getIntent().getStringExtra("video_path");

        thumbImage_path = getIntent().getStringExtra("thumbImage_path");
        LogUtil.e("播放页面=视频缩略图========" + thumbImage_path);
        if (TextUtils.isEmpty(video_path)) {
            return;
        }
        if (TextUtils.isEmpty(thumbImage_path)) {
            thumbImage_path = AppConstants.TOMCAT_URL + "javademo/default.jpg";
        }
        Picasso.with(this).load(thumbImage_path).fit().into(mIvVideoThumbnail);


        //设置视频
        mVideoView.setVideoPath(video_path);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mIvVideoThumbnail.setVisibility(View.GONE);
                mVideoView.start();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //停止播放
                mVideoView.stopPlayback();
                finish();
            }
        });

    }

    @Override
    protected void initData() {

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
