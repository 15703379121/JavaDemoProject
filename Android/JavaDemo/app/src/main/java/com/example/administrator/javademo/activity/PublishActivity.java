package com.example.administrator.javademo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.activity.videoshootActivity.ShootMainActivity;
import com.example.administrator.javademo.adapter.PublishAdapter;
import com.example.administrator.javademo.bean.VideoInfoBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.DialogUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.PictureUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.util.VideoUtil;
import com.fire.photoselector.activity.PhotoSelectorActivity;
import com.fire.photoselector.models.PhotoSelectorSetting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hegeyang on 2017/8/5 0005 .
 */

public class PublishActivity extends BaseActivity {
    private static final int SELECT_VIDEO = 200;
    private static final int SHORT_VIDEO = 0;
    public static final int PUBLIC_RESULT_CODE = 38;

    @InjectView(R.id.tv_add_title)
    TextView tvAddTitle;
    @InjectView(R.id.ib_publish_back)
    ImageButton ibPublishBack;
    @InjectView(R.id.tv_publish_save)
    TextView tvPublishSave;
    @InjectView(R.id.et_publish_content)
    EditText etPublishContent;
    @InjectView(R.id.gv_public_addicon)
    GridView gvPublicAddicon;
    private static final int REQUEST_SELECT_PHOTO = 100;
    @InjectView(R.id.sv_publish)
    SurfaceView svPublish;
    private ArrayList<String> result = new ArrayList<>();
    private PublishAdapter publishAdapter;
    private android.app.AlertDialog chose_dialog;
    private MediaPlayer mp;
    private String mVideoPath = "";
    private int currentUploadType = -1;
    private static final int UPLOAD_TYPE_VIDEO = 1;//上传视频
    private static final int UPLOAD_TYPE_PHOTO = 0;//上传图片

    private static final String[] PERMISSIONS_CONTACT = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String mThumbnail = null;

    private Uri imageUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    private int getDataSize() {
        return result == null ? 0 : result.size();
    }

    @Override
    protected void initView() {
        publishAdapter = new PublishAdapter(this, result);
        gvPublicAddicon.setAdapter(publishAdapter);
        //publishAdapter.setList(result);
        gvPublicAddicon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == getDataSize()) {//点击“+”号位置添加图片
                    showIconDialog();

                } else {//点击图片删除
                    result.remove(i);
                    publishAdapter.setList(result);
                }
            }
        });
    }

    private void showIconDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.select_icon_dialog_find, null);
        chose_dialog = new android.app.AlertDialog.
                Builder(this).create();
        RelativeLayout rl_select_photo = (RelativeLayout) v.findViewById(R.id.rl_select_photo);
        RelativeLayout rl_select_video = (RelativeLayout) v.findViewById(R.id.rl_select_video);
        RelativeLayout rl_shot = (RelativeLayout) v.findViewById(R.id.rl_select_shot);
        RelativeLayout rl_shot_photo = (RelativeLayout) v.findViewById(R.id.rl_select_shot_photo);
        rl_select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择图片
                selectPhotos(6, 3);
                chose_dialog.dismiss();
            }
        });
        rl_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择视频
                selectVideo();
                chose_dialog.dismiss();
            }
        });
        rl_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄视频
                Intent intent = new Intent(PublishActivity.this, ShootMainActivity.class);
                startActivityForResult(intent, 0);
                chose_dialog.dismiss();
            }
        });
        rl_shot_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍摄相片
                choseImageFromCameraCapture();
                chose_dialog.dismiss();
            }
        });

        chose_dialog.setView(v);
        chose_dialog.show();
    }

    //private static final String IMAGE_FILE_NAME = System.currentTimeMillis() + ".png";
    private static final int CODE_CAMERA_REQUEST = 0xa1;

    private void choseImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            File file = new File(AppConstants.FILE_PUBLISH_PIC);
            if (!file.exists()) {
                file.mkdirs();
            }
            File image_file = new File(AppConstants.FILE_PUBLISH_PIC, getImageName());
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(this, "com.ruiyihong.toyshop", image_file);
            }else {
                imageUri = Uri.fromFile(image_file);
            }
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);

        }
    }

    private String getImageName(){
        return System.currentTimeMillis()+".png";
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    break;
                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initEvent() {
        ibPublishBack.setOnClickListener(this);
        tvPublishSave.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.tv_publish_save:
                //上传视频或图片
                LogUtil.e("currentUploadType");
                submitShuoshuo(currentUploadType+"");
                break;
            case R.id.ib_publish_back:
                //返回
                showExitDialog();
                break;
        }
    }

    /**
     * 发布说说
     *
     */
    private void submitShuoshuo(String type){

        String content = etPublishContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            //没有发表文字内容
            if (TextUtils.equals(type,"0") ){
                //有图片
                content = "分享图片";
            }else if (TextUtils.equals(type,"1")){
                //有视频
                content = "分享视频";
            }else {
                //没有图片或视频
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PublishActivity.this, "您没有发布任何内容", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        }
        ArrayList<String> list = new ArrayList<>();
        if (TextUtils.equals(type,"0")){
            //图片
            list = result;
        }else if(TextUtils.equals(type,"1")){
            //视频
            list.add(mVideoPath);
            list.add(mThumbnail);
        }
        final String uid = SPUtil.getUid(this)[0];
        LogUtil.e("发布消息----"+list.size()+";"+content+";"+type+";"+uid);
        mProgressDialog = DialogUtil.initProgress(this, "发布中");
        try {
            OkHttpUtil.upLoadFileMuti(AppConstants.INFORMATION_SAVE_URL,list,content,type,uid, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("发布说说=========" + result);
                    if (!TextUtils.isEmpty(result) && TextUtils.equals(AppConstants.SUCCESS,result)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                if (mProgressDialog != null){
                                    mProgressDialog.dismiss();
                                }
                                setResult(PUBLIC_RESULT_CODE,getIntent());
                                finish();
                            }
                        });
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectVideo() {
        Intent intent = new Intent(PublishActivity.this, VideoSourceActivity.class);
//        intent.putExtra(EnterVideoActivity.VIDEO_DURATION, 10 * 1000L);

        startActivityForResult(intent, SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_VIDEO:
                currentUploadType = UPLOAD_TYPE_VIDEO;
                if (resultCode == VideoSourceActivity.VIDEO_SOURCE_CODE_RESULT && data != null) {
                    VideoInfoBean videoInfoBean = (VideoInfoBean) data.getSerializableExtra(VideoSourceActivity.VIDEO_INFO_BEAN_LOCAL);
                    //final Bitmap videoThumbnail = new MyThumbnailUtils().createVideoThumbnail(videoPath, ScreenUtil.getScreenWidth(PublishActivity.this), (int) DensityUtil.dp2px(200));
                    Bitmap videoThumbnail = VideoUtil.getVideoLocalThumbnail(videoInfoBean.getFilePath());
                    String ThumbnailPath = saveVideoThumb(videoThumbnail);
                    LogUtil.e(videoInfoBean.getFilePath() + "==缩略图==" + videoThumbnail);
                    if (videoInfoBean.getFilePath() != null && new File(ThumbnailPath).exists()) {
                        videoData(videoInfoBean.getFilePath(), ThumbnailPath);
                    }
                }
                break;
            case REQUEST_SELECT_PHOTO:
                currentUploadType = UPLOAD_TYPE_PHOTO;
                if (resultCode == RESULT_OK) {
                    svPublish.setVisibility(View.GONE);
                    gvPublicAddicon.setVisibility(View.VISIBLE);
                    // result为照片绝对路径集合,isSelectedFullImage标识是否选择原图
                    LogUtil.e("Thread.currentThread().getName();"+Thread.currentThread().getName());
                    final ProgressDialog progressDialog = DialogUtil.initProgress(PublishActivity.this, "处理中");
                    new Thread(){
                        @Override
                        public void run() {
                            result= PictureUtil.compressPicList(data.getStringArrayListExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    publishAdapter.setList(result);
                                }
                            });
                        }
                    }.start();
                }
                break;
            case SHORT_VIDEO:
                currentUploadType = UPLOAD_TYPE_VIDEO;
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    Bitmap videoThumbnail = VideoUtil.getVideoLocalThumbnail(path);
                    String Thumbnail = saveVideoThumb(videoThumbnail);
                    if (path != null) {
                        videoData(path, Thumbnail);
                    }
                }
                break;case
            CODE_CAMERA_REQUEST:
                // 用户没有进行有效的设置操作，返回
                if (resultCode == RESULT_CANCELED) {
                    //Toast.makeText(Updata_meActivity.this, "取消了", Toast.LENGTH_LONG).show();
                    return;
                }
                LogUtil.e("Thread.currentThread().getName();"+Thread.currentThread().getName());
                final ProgressDialog progressDialog = DialogUtil.initProgress(PublishActivity.this, "处理中");
                if (hasSdcard()) {
                    new Thread(){
                        @Override
                        public void run() {
                            LogUtil.e("拍摄图片路径---"+imageUri.getPath());
                            final String path = PictureUtil.compressPic(imageUri.getPath());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentUploadType = UPLOAD_TYPE_PHOTO;
                                    svPublish.setVisibility(View.GONE);
                                    gvPublicAddicon.setVisibility(View.VISIBLE);
                                    //result.add(tempFile.getPath());
                                    result.add(path);
                                    progressDialog.dismiss();
                                    publishAdapter.setList(result);
                                }
                            });
                        }
                    }.start();
                } else {
                    Toast.makeText(this, "没有SDCard", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 保存方法
     */
    private String saveVideoThumb(Bitmap thumb) {
        String thumbPath = AppConstants.FILE_PUBLISH_PIC +"/" + System.currentTimeMillis() + ".jpg";
        File img = new File(thumbPath);
        if (!img.exists()) {
            try {
                img.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(thumbPath);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return thumbPath;
    }

    private void videoData(String videoPath, String thumbnail) {
        this.mVideoPath = videoPath;
        this.mThumbnail = thumbnail;
        //获取视频，显示
        svPublish.setVisibility(View.VISIBLE);
        gvPublicAddicon.setVisibility(View.GONE);

        svPublish.getHolder().setKeepScreenOn(true);
        svPublish.getHolder().addCallback(new SurfaceViewLis());
        video_play(0, videoPath);

    }

    /**
     * 开始播放 回显视频
     *
     * @param msec 播放初始位置
     */
    protected void video_play(final int msec, final String videoPath) {

        LogUtil.e("视频路径=================" + videoPath);

        // 获取视频文件地址
        try {
            mp = new MediaPlayer();
            //设置音频流类型
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.e("准备好了，播放");
                    mp.start();
                    // 按照初始位置播放
                    mp.seekTo(msec);

                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    mp.reset();
                    video_play(0, videoPath);
                }
            });

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 发生错误重新播放
                    mp.reset();
                    video_play(0, videoPath);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mp.reset();
            try {
                //设置视屏文件图像的显示参数
                mp.setDisplay(holder);

                mp.setDataSource(mVideoPath);
                mp.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mp != null) {
                mp.stop();
                //释放资源
                mp.release();
            }
        }

    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否取消发布？");
        builder.setNegativeButton("确定取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setPositiveButton("继续保持", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 选择图片页面
     *
     * @param sum
     * @param columnCount
     */
    private void selectPhotos(int sum, int columnCount) {
        PhotoSelectorSetting.MAX_PHOTO_SUM = sum;
        PhotoSelectorSetting.COLUMN_COUNT = columnCount;
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorSetting.LAST_MODIFIED_LIST, result);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
    }


}
