package com.example.administrator.javademo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.TextBean;
import com.example.administrator.javademo.bean.VideoInfoBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.util.uploadfile.ProgressUIListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class AddVideoActivity extends BaseActivity {
    public static final int ADD_VIDEO_CODE_REQUEST = 0;
    @InjectView(R.id.tv_video_upload)
    TextView mTvVideoUpload;
    @InjectView(R.id.et_add_video_title)
    EditText mEtAddVideoTitle;
    @InjectView(R.id.et_add_video_info)
    EditText mEtAddVideoInfo;
    @InjectView(R.id.tv_add_video_submit)
    TextView mTvAddVideoSubmit;
    @InjectView(R.id.sb_add_video)
    SeekBar mSbAddVideo;
    private String videoPath;
    private ProgressDialog progressDialog;
    private String video_title;
    private String video_info;
    private int mCid;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final int MSG_FILE_EXEISTS = 5;
//    public static final int ADD_VIDEO_RESULT_CODE = 17;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //关闭加载对话框
            switch (msg.what) {
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(AddVideoActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(AddVideoActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(AddVideoActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FILE_EXEISTS:
                    Toast.makeText(AddVideoActivity.this, "已有同名文件，请更换文件名", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private int mClickPosition;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_video;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        mCid = intent.getIntExtra(VideoListActivity.CID,0);
        mClickPosition = intent.getIntExtra(MainActivity.CLICK_POSITION, MainActivity.POSITION_VIDEO);


        if (mClickPosition == MainActivity.POSITION_VIDEO){
            mTvVideoUpload.setText("选择视频");
            mEtAddVideoTitle.setHint("请输入视频标题");
            mEtAddVideoInfo.setHint("请输入视频详情");
        }else{
            mTvVideoUpload.setText("选择文档");
            mEtAddVideoTitle.setHint("请输入文档标题");
            mEtAddVideoInfo.setHint("请输入文档详情");
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        mTvVideoUpload.setOnClickListener(this);
        mTvAddVideoSubmit.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.tv_video_upload:
                Intent intent;
                if (mClickPosition == MainActivity.POSITION_VIDEO) {
                    intent = new Intent(AddVideoActivity.this, VideoSourceActivity.class);
                }else{
                    intent = new Intent(AddVideoActivity.this, PracticeSourceActivity.class);
//                    intent.putExtra(AppConstants.FILE_PATH,AppConstants.FILE_PRACTICE);
                }
                startActivityForResult(intent, ADD_VIDEO_CODE_REQUEST);
                break;
            case R.id.tv_add_video_submit:
                submit();
                break;
        }
    }
    private void submit() {
        if (TextUtils.isEmpty(videoPath)){
            Toast.makeText(this, "请选择上传文件", Toast.LENGTH_SHORT).show();
            return;
        }
        video_title = mEtAddVideoTitle.getText().toString().trim();
        video_info = mEtAddVideoInfo.getText().toString().trim();
        if (TextUtils.isEmpty(video_title) || TextUtils.isEmpty(video_info)){
            Toast.makeText(this, "请完善报名信息", Toast.LENGTH_SHORT).show();
            return;
        }

        //上传视频
        uploadVideo();

     //   progressDialog = DialogUtil.initProgress(this, "正在上传。。。");
    }

    private void uploadVideo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadMethod(videoPath);
                } catch (Exception e) {
                    LogUtil.e("上传出现了bug");
                }
            }
        });
        thread.start();
        thread.interrupt();
    }

    //上传文件方法
    private void UploadMethod(String path) throws IOException {
        String url = "";
        if (mClickPosition == MainActivity.POSITION_VIDEO){
            url = AppConstants.FILE_UPLOAD_VIDEO_URL;
        }else{
            url = AppConstants.FILE_UPLOAD_PRACTICE_URL;
        }
        String fileNameByPath = FileUtil.getFileNameByPath(videoPath);
        String suffix = FileUtil.getSuffix(fileNameByPath);
        OkHttpUtil.upLoadFileProgress(url, new File(path), suffix,new ProgressUIListener() {

            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
                LogUtil.e("onUIProgressStart:"+totalBytes);
                mTvVideoUpload.setText("正在上传");
            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                mSbAddVideo.setProgress((int) (100 * percent));

            }

            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                LogUtil.e("onUIProgressFinish:");
                mTvVideoUpload.setText("上传成功");

            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("上传失败");
                handler.sendEmptyMessage(MSG_WRITE_FAIL);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("result------------"+result);
                if (!TextUtils.isEmpty(result)){
                    if (AppConstants.FAIL.equals(result)){
                        handler.sendEmptyMessage(MSG_WRITE_FAIL);
                    }else if (AppConstants.FILE_EXISTS.equals(result)){
                        handler.sendEmptyMessage(MSG_FILE_EXEISTS);
                    }else if (result.startsWith("http://")){
                        submitInfo(result);
                    }
                }else {
                    handler.sendEmptyMessage(MSG_WRITE_FAIL);
                }

            }
        });

    }

    /**
     * 上传视频信息
     * @param videoNetPath
     */
    private void submitInfo( String videoNetPath) {
        HashMap<String, Object> map = new HashMap<>();
        String[] uid = SPUtil.getUid(this);
        int id = 0;
        if (uid != null){
            id = Integer.parseInt(uid[0]);
        }
        LogUtil.e("TID-----"+id);
        LogUtil.e("TITLE-----"+video_title);
        LogUtil.e("INFO-----"+video_info);
        LogUtil.e("CID-----"+mCid);
        LogUtil.e("URL-----"+videoNetPath);
        map.put(VideoListActivity.TID,id);
        map.put(VideoListActivity.TITLE, video_title);
        map.put(VideoListActivity.INFO, video_info);
        map.put(VideoListActivity.CID, mCid);
        map.put(VideoListActivity.URL,videoNetPath);
        String url = "";
        if (mClickPosition == MainActivity.POSITION_VIDEO) {
            url = AppConstants.VIDEO_SAVE_URL;
        }else{
            url = AppConstants.PRACTICE_SAVE_URL;
        }
        LogUtil.e("url--------"+url);
        netWritePost(url, map);
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
                    LogUtil.e("Video写操作------" + result);
                    if (!TextUtils.isEmpty(result) && AppConstants.SUCCESS.equals(result)) {
                        LogUtil.e("写好拉");
                        handler.sendEmptyMessage(MSG_WRITE_SUCCESS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_VIDEO_CODE_REQUEST && resultCode == VideoSourceActivity.VIDEO_SOURCE_CODE_RESULT){
            //传回将上传的视频
            if (data != null){
                VideoInfoBean videoInfoBean = (VideoInfoBean) data.getSerializableExtra(VideoSourceActivity.VIDEO_INFO_BEAN_LOCAL);
                if (videoInfoBean != null){
                    videoPath = videoInfoBean.getFilePath();
                    title = videoInfoBean.getTitle();
                }
            }
        }else if(requestCode == ADD_VIDEO_CODE_REQUEST && resultCode == PracticeSourceActivity.PRACTICE_SOURCE_CODE_RESULT){
            if (data != null){
                TextBean textInfoBean = (TextBean) data.getSerializableExtra(PracticeSourceActivity.PRACTICE_INFO_BEAN_LOCAL);
                if (textInfoBean != null){
                    videoPath = textInfoBean.getFile_txt_path();
                    title = textInfoBean.getFile_name();
                }
            }
        }
        if (videoPath != null) {
            android.util.Log.i("radish", "videoPath------------------" + videoPath);

            //  File file = new File(videoPath);

            //这里进行替换uri的获得方式
//                imageUri = FileProvider.getUriForFile(this,"com.ruiyihong.toyshop", file);

            //视频名称设置
            LogUtil.e("fileName----"+title);
            if (!TextUtils.isEmpty(FileUtil.getName(title))){

                mEtAddVideoTitle.setText(""+FileUtil.getName(title));
            }else{
                mEtAddVideoTitle.setText(""+title);
            }
            if (mSbAddVideo != null){
                //上传进度条
                mSbAddVideo.setMax(100);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
