package com.example.administrator.javademo.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.adapter.CommentAdapter;
import com.example.administrator.javademo.adapter.RelaVideoAdapter;
import com.example.administrator.javademo.bean.CommentBean;
import com.example.administrator.javademo.bean.DownLoadBean;
import com.example.administrator.javademo.bean.VideoBean;
import com.example.administrator.javademo.bean.VoteBean;
import com.example.administrator.javademo.service.DownloadService;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.PixelUtils;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.util.ServiceUtil;
import com.example.administrator.javademo.util.VideoUtil;
import com.example.administrator.javademo.view.ListViewForScrollView;
import com.example.administrator.javademo.view.MoreTextView;
import com.example.administrator.javademo.view.MyScrollView;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/2 0002.
 */

public class VideoActivity extends BaseActivity {
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final int MSG_DATA_VOTE_SELECT = 5;
    private static final int MSG_DATA_VOTE_CANCEL = 6;
    private static final int MSG_WRITE_SUCCESS_SELECT = 7;
    private static final int MSG_WRITE_SUCCESS_CANCEL = 8;
    private static final int MSG_WRITE_SUCCESS_COMMENT = 9;
    private static final int MSG_FIND_SUCCESS_COMMENT = 10;
    private static final int MSG_FIND_VIDEO_ID_SUCCESS = 11;
    public static final String VIDEO_ID = "video_id";
    @InjectView(R.id.videoView)
    VideoView mVideoView;
    @InjectView(R.id.iv_video_thumbnail)
    ImageView mIvThumbnail;
    @InjectView(R.id.iv_video_start)
    ImageView mIvStart;
    @InjectView(R.id.fl_video_group)
    FrameLayout mFlVideoGroup;
    @InjectView(R.id.tv_video_title)
    TextView mTvVideoTitle;
    @InjectView(R.id.mtv_video)
    MoreTextView mMtvVideo;
    @InjectView(R.id.ib_collect)
    ImageButton mIbCollect;
    @InjectView(R.id.tv_relative_video)
    TextView mTvRelativeClass;
    @InjectView(R.id.rv_video)
    RecyclerView mRvVideo;
    @InjectView(R.id.msv)
    MyScrollView mMsv;
    @InjectView(R.id.ib_download)
    ImageButton mIbDownload;
    @InjectView(R.id.ib_dianzan)
    ImageButton mIbDianzan;
    @InjectView(R.id.tv_video_info)
    TextView mTvVideoInfo;
    @InjectView(R.id.tv_null)
    TextView tvNull;
    @InjectView(R.id.ll_pinglun_null)
    LinearLayout mLlPinglunNull;
    @InjectView(R.id.lv_video_pinglun)
    ListViewForScrollView mLvVideoPinglun;
    @InjectView(R.id.ll_view_info)
    LinearLayout llViewInfo;
    @InjectView(R.id.tv_vote)
    TextView mTvVote;
    @InjectView(R.id.ib_comment)
    ImageButton mIbComment;
    @InjectView(R.id.tv_comment)
    TextView mTvComment;
    private MediaController mController;
    private boolean mIsFullScreen;
    private List<View> mViews;
    private List<VideoBean> videoList = new ArrayList<>();
    private VideoBean mVideoBean = new VideoBean();
    private long mCurrentPosition = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_DATA_VOTE_SELECT:
                    //点赞
                    mIbDianzan.setSelected(true);
                    //点赞量
                    mTvVote.setText("赞 " + mVoteCount);
                    break;
                case MSG_DATA_VOTE_CANCEL:
                    //取消赞
                    mIbDianzan.setSelected(false);
                    //点赞量
                    mTvVote.setText("赞 " + mVoteCount);
                    break;
                case MSG_FIND_SUCCESS:
                    //设置相关视频
                    setRelaVideo();
                    break;
                case MSG_FIND_VIDEO_ID_SUCCESS:
                    init();
                    break;
                case MSG_FIND_FAIL:
                    Toast.makeText(VideoActivity.this, "访问目录失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(VideoActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    netReadPost();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(VideoActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(VideoActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS_COMMENT:
                    //评论完后，刷新评论列表
                    netReadPostComment();
                    break;
                case MSG_FIND_SUCCESS_COMMENT:
                    //刷新评论列表后，更新评论列表
                    if (mCommentAdapter == null){
                        mLlPinglunNull.setVisibility(View.GONE);
                        mCommentAdapter = new CommentAdapter(VideoActivity.this, mCommentList);
                        mLvVideoPinglun.setAdapter(mCommentAdapter);
                    }else {
                        mCommentAdapter.setList(mCommentList);
                        mCommentAdapter.notifyDataSetChanged();
                    }
                    //评论量
                    mTvComment.setText("评论 "+mCommentList.size());
                    break;
            }
        }
    };
    private List<CommentBean> mCommentList;
    private CommentAdapter mCommentAdapter;
    private int mVoteCount;

    private void netReadPostComment() {
        Map<String, Object> map = new HashMap<>();
        map.put("video", mVideoBean.getId());
        try {
            OkHttpUtil.postJson(AppConstants.COMMENT_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
//                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String mReadResult = OkHttpUtil.getResult(response);
                    LogUtil.e("读评论结果----"+mReadResult);
                    if (!TextUtils.isEmpty(mReadResult)) {
                        mCommentList = (List<CommentBean>) GsonUtil.parseJsonToList(mReadResult, new TypeToken<List<CommentBean>>() {
                        }.getType());
                        handler.sendEmptyMessage(MSG_FIND_SUCCESS_COMMENT);
//                        handler.sendEmptyMessage(MSG_FIND_SUCCESS);
                    } else {
//                        handler.sendEmptyMessage(MSG_FIND_FAIL);
                    }
                }
            });
        } catch (IOException e) {
//            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }
    }

    private int mCid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
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
        Vitamio.initialize(getApplicationContext());
        //开启并绑定服务
        initService();
        Intent intent = getIntent();
        int videoPosition = intent.getIntExtra(VideoListActivity.VIDEO_POSITION, -1);
        if (videoPosition == -1){
            int video_id = intent.getIntExtra(VideoActivity.VIDEO_ID,-1);
            LogUtil.e("videoActivity-----video_id---"+video_id);
            if (video_id != -1){
                netReadPostVideo(video_id);
                netReadPostVideoList(video_id);
            }
            //跳转过来的
            NotificationManager notificationManager =(NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(2);
        }else{
            //目录过来的
            String videoListJson = intent.getStringExtra(VideoListActivity.VIDEO_LIST);
            mCid = intent.getIntExtra(VideoListActivity.CATALOG, -1);
            if (!TextUtils.isEmpty(videoListJson)) {
                try {
                    videoList = (List<VideoBean>) GsonUtil.parseJsonToList(videoListJson, new TypeToken<List<VideoBean>>() {
                    }.getType());
                    mVideoBean = videoList.get(videoPosition);
                } catch (Exception e) {
                    Toast.makeText(this, "视频列表发生错误", Toast.LENGTH_SHORT).show();
                }
            }
            //初始化视频
            init();

            //设置相关视频
            setRelaVideo();
        }

        if (!LibsChecker.checkVitamioLibs(this))
            return;
        mController = new MediaController(this, true, mFlVideoGroup);
        //上来先隐藏controller
        mController.setVisibility(View.GONE);
        mIvStart.setOnClickListener(this);
        //保存除vitamio以外控件，以备全屏时对其屏蔽
        mViews = new ArrayList<>();
        mViews.add(mMsv);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT,
                PixelUtils.dip2px(this, 220));
        mFlVideoGroup.setLayoutParams(params);
    }

    private void netReadPostVideo(int video_id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("video",video_id);
        try {
            OkHttpUtil.postJson(AppConstants.VIDEO_FIND_ID_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("读取视频  result---"+result);
                    if (!TextUtils.isEmpty(result) && !TextUtils.equals(AppConstants.FAIL,result)){
                        //成功
                        mVideoBean = GsonUtil.parseJsonWithGson(result, VideoBean.class);
                        handler.sendEmptyMessage(MSG_FIND_VIDEO_ID_SUCCESS);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void netReadPostVideoList(final int video_id) {
        LogUtil.e("读取视频列表");
        HashMap<String, Object> map = new HashMap<>();
        map.put("video",video_id);
        try {
            OkHttpUtil.postJson(AppConstants.VIDEO_FIND_LIST_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("读取视频列表  result---"+result);
                    if (!TextUtils.isEmpty(result) && !TextUtils.equals(AppConstants.FAIL,result)){
                        //成功
                        videoList = (List<VideoBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<VideoBean>>() {
                        }.getType());
                        for (int i = 0; i < videoList.size(); i++) {
                            if(video_id == videoList.get(i).getId()){
                                mVideoBean = videoList.get(i);
                                mCid = mVideoBean.getCatalogId();
                                handler.sendEmptyMessage(MSG_FIND_SUCCESS);
                            }

                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        mIbCollect.setVisibility(View.GONE);

    }

    private void init() {
        //设置标题
        mTvVideoTitle.setText(mVideoBean.getTitle());

        //设置查看更多
        setMtvVideo();

        //设置缩略图
        VideoUtil.setThumbnail(this, mVideoBean.getUrl() + "", mIvThumbnail);

        //设置视频
        mVideoView.setVideoPath(mVideoBean.getUrl());
        mIvStart.setVisibility(View.GONE);

        //显示相关评论
        showRelaPinglun();

        //评论量
        mTvComment.setText("评论 "+mVideoBean.getCommentSize());

        mVoteCount = mVideoBean.getVoteSize();

        //初始化赞
        new Thread(){
            @Override
            public void run() {
                String[] uid = SPUtil.getUid(VideoActivity.this);
                if (uid != null){
                    int id = Integer.parseInt(uid[0]);
                    VoteBean voteByUser = mVideoBean.findVoteByUser(id);
                    if (voteByUser != null){
                        handler.sendEmptyMessage(MSG_DATA_VOTE_SELECT);
                    }else{
                        handler.sendEmptyMessage(MSG_DATA_VOTE_CANCEL);
                    }
                }
            }
        }.start();

        //更新网络数据

//        netReadPost();

    }
    /**
     * 按cid查找视频----初始化视频列表
     */
    public void netReadPost() {
        //查找视频
        Map<String, Object> map = new HashMap<>();
        map.put("cid", mCid);
        try {
            OkHttpUtil.postJson(AppConstants.VIDEO_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
//                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String mReadResult = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(mReadResult)) {
                        videoList = (List<VideoBean>) GsonUtil.parseJsonToList(mReadResult, new TypeToken<List<VideoBean>>() {
                        }.getType());
                    } else {
//                        handler.sendEmptyMessage(MSG_FIND_FAIL);
                    }
                }
            });
        } catch (IOException e) {
//            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }
    }
    private void showRelaPinglun() {
        mCommentList = mVideoBean.getComment();
        if (mCommentList != null && mCommentList.size() > 0) {
            mLlPinglunNull.setVisibility(View.GONE);
            mCommentAdapter = new CommentAdapter(this, mCommentList);
            mLvVideoPinglun.setAdapter(mCommentAdapter);
        } else {
            mLlPinglunNull.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置相关视频
     */
    private void setRelaVideo() {
        mRvVideo.setHasFixedSize(true);//设置固定大小
        mRvVideo.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        LinearLayoutManager mLayoutManage = new LinearLayoutManager(this);
        mLayoutManage.setOrientation(OrientationHelper.HORIZONTAL);//设置滚动方向，横向滚动
        mRvVideo.setLayoutManager(mLayoutManage);
        RelaVideoAdapter mAdapter = new RelaVideoAdapter(this, videoList);
        mRvVideo.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RelaVideoAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mVideoBean = videoList.get(position);
                init();
            }
        });
    }

    /**
     * 设置查看更多
     */
    private void setMtvVideo() {
        mMtvVideo.setText("" + mVideoBean.getInfo());
        mMtvVideo.refreshText();
    }

    @Override
    protected void initEvent() {
        mIvThumbnail.setVisibility(View.GONE);
        mVideoView.setMediaController(mController);
        mIvStart.setOnClickListener(this);
        mIbDianzan.setOnClickListener(this);
        mIbComment.setOnClickListener(this);
        mIbDownload.setOnClickListener(this);
        mTvVideoInfo.setOnClickListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
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

        //评论条目点击监听
        mLvVideoPinglun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //评论条目长按监听
        mLvVideoPinglun.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
                TextView tv = new TextView(VideoActivity.this);
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
                        //网络删除评论
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("video",mVideoBean.getId());
                        map.put("comment",mCommentList.get(position).getId());
                        netWritePostComment(AppConstants.COMMENT_DELETE_URL,map);
                        //更新列表
                        mCommentList.remove(position);
                        if (mCommentAdapter != null) {
                            mCommentAdapter.setList(mCommentList);
                            mCommentAdapter.notifyDataSetChanged();
                        }
                        //如果评论列表为空
                        if (mCommentList.size() == 0){
                            mLlPinglunNull.setVisibility(View.VISIBLE);
                        }
                        //评论量
                        mTvComment.setText("评论 "+mCommentList.size());
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.iv_video_start:
                if (mVideoView != null && mVideoBean != null) {
                    mVideoView.setVideoPath(mVideoBean.getUrl());
                    mIvStart.setVisibility(View.GONE);
                }
                break;
            case R.id.ib_download:
                //下载
                download();
                break;
            case R.id.ib_dianzan:
                //点赞
                updateVote();
                break;
            case R.id.ib_comment:
                //评论
                addComment();
                break;
            case R.id.ib_collect:
                //收藏
                break;
            case R.id.tv_video_info:
                //下载详情页面
                startActivity(new Intent(this, DownloadListActivity.class));
                break;
        }
    }

    /**
     * 添加评论
     */
    private void addComment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_comment, null);
        final EditText et_comment = (EditText) inflate.findViewById(R.id.et_comment);
        builder.setView(inflate);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
//        window.setContentView(R.layout.dialog_comment);//布局
        window.setGravity(Gravity.CENTER);
        inflate.findViewById(R.id.tv_comment_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = et_comment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(VideoActivity.this, "未填写评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("video",mVideoBean.getId());
                        String[] uid = SPUtil.getUid(VideoActivity.this);
                        if (uid != null){
                            int id = Integer.parseInt(uid[0]);
                            map.put("user",id);
                        }
                        map.put("content",comment);
                        netWritePostComment(AppConstants.COMMENT_SAVE_URL,map);
                    }
                }.start();
                dialog.dismiss();
            }
        });


    }

    private void netWritePostComment(String url, HashMap<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_WRITE_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Comment写操作------" + result);
                    if (!TextUtils.isEmpty(result) && AppConstants.SUCCESS.equals(result)) {
                        //评论成功
                        handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENT);
                    } else {
                        handler.sendEmptyMessage(MSG_WRITE_FAIL);
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }
    }

    /**
     * 点赞或取消赞
     */
    private void updateVote() {
        new Thread(){
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("video",mVideoBean.getId());
                String[] uid = SPUtil.getUid(VideoActivity.this);
                Integer id = Integer.parseInt(uid[0]);
                map.put("user",id);
                netWritePost(AppConstants.VOTE_UPDATE_URL,map);
            }
        }.start();
    }
    private void netWritePost(String url, Map<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_WRITE_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Vote写操作------" + result);
                    if (TextUtils.isEmpty(result) || AppConstants.FAIL.equals(result)) {
                        handler.sendEmptyMessage(MSG_WRITE_FAIL);
                    } else {
                        final String[] split = result.split(":");
                        if (split.length == 3){
                            mVoteCount = Integer.parseInt(split[2]);
                            if ("1".equals(split[1])){
                                //赞
                                handler.sendEmptyMessage(MSG_DATA_VOTE_SELECT);
                            }else{
                                //无赞
                                handler.sendEmptyMessage(MSG_DATA_VOTE_CANCEL);
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }
    }
    private void download() {
        String url = mVideoBean.getUrl();
        String name = mVideoBean.getTitle();
        String fileName = FileUtil.getFileNameByPath(url);
//        String name = FileUtil.getName(fileName);
        String suffix = FileUtil.getSuffix(fileName);
        //查看文件是否已下载
        //已下载文件
        File loadedDir = new File(AppConstants.FILE_DOWN_VIDEO);
        String[] loadedFiles = loadedDir.list();
        if (loadedFiles != null && loadedFiles.length > 0) {
            //轮询查看
            for (int i = 0; i < loadedFiles.length; i++) {
                if (loadedFiles[i].equals(name + suffix)) {
                    Toast.makeText(this, "此视频已下载", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        //保存要下载的文件URL
        DownLoadBean downLoadBean = new DownLoadBean(name, suffix, url, AppConstants.FILE_DOWN_VIDEO_TEMP + "/" + fileName);
        SPUtil.setVideoDownloadList(this, downLoadBean);
        //保存缩略图
        VideoUtil.saveNetVideoThumbnail(VideoActivity.this, url, name);
        //开始下载
        if (downloadBinder != null && !downloadBinder.isDownLoading()) {
            downloadBinder.startDownload(downLoadBean);
        }
        Toast.makeText(this, "已加入下载列表", Toast.LENGTH_SHORT).show();
    }

    //记得在activity中声明
    // android:screenOrientation="portrait" 强行设置为竖屏，关闭自动旋转屏幕
    //android:configChanges="orientation|keyboardHidden|screenLayout|screenSize"注册配置变化事件
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            mIsFullScreen = true;
            //去掉系统通知栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideViews(true);
            //调整mFlVideoGroup布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mFlVideoGroup.setLayoutParams(params);
            //原视频大小
//            public static final int VIDEO_LAYOUT_ORIGIN = 0;
            //最优选择，由于比例问题还是会离屏幕边缘有一点间距，所以最好把父View的背景设置为黑色会好一点
//            public static final int VIDEO_LAYOUT_SCALE = 1;
            //拉伸，可能导致变形
//            public static final int VIDEO_LAYOUT_STRETCH = 2;
            //会放大可能超出屏幕
//            public static final int VIDEO_LAYOUT_ZOOM = 3;
            //效果还是竖屏大小（字面意思是填充父View）
//            public static final int VIDEO_LAYOUT_FIT_PARENT = 4;
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        } else {
            mIsFullScreen = false;
            /*清除flag,恢复显示系统状态栏*/
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideViews(false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    PixelUtils.dip2px(this, 220));
            mFlVideoGroup.setLayoutParams(params);
        }
    }


    public void hideViews(boolean hide) {
        if (hide) {
            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setVisibility(View.GONE);
            }
        } else {
            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    //没有布局中没有设置返回键，只能响应硬件返回按钮，你可根据自己的意愿添加一个。若全屏就切换为小屏
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsFullScreen) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mController.setFullScreenIconState(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (connection != null) {
            unbindService(connection);
        }
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

    private DownloadService.DownloadBinder downloadBinder;

    //联接服务
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initService() {
        //开始下载
        Intent intent = new Intent(this, DownloadService.class);
        if (!ServiceUtil.isServiceRunning(this, "com.example.administrator.javademo.service.DownloadService")) {
            startService(intent);
        } else {
        }
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务
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
