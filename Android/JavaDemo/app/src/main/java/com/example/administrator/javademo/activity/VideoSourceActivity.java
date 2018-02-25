package com.example.administrator.javademo.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.VideoInfoBean;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class VideoSourceActivity extends BaseActivity {

    public static final String VIDEO_INFO_BEAN_LOCAL = "video_info_bean_local";
    public static final int VIDEO_SOURCE_CODE_RESULT = 1;
    @InjectView(R.id.gv_local_video)
    GridView mGvLocalVideo;
    private List<VideoInfoBean> videoList;
    private Cursor cursor;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initUI();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_source;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //本地获取视频
        new Thread() {
            @Override
            public void run() {
                String[] thumbColumns = new String[]{
                        MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.VIDEO_ID
                };

                String[] mediaColumns = new String[]{
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.MIME_TYPE
                };
                //首先检索SDcard上所有的video
                cursor = VideoSourceActivity.this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);

                videoList = new ArrayList<VideoInfoBean>();

                if (cursor.moveToFirst()) {
                    do {
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        if (size > 0 && duration > 1000 && ".mp4".equals(path.substring(path.length() - 4))) {
                            //1秒以上的视频
                            VideoInfoBean info = new VideoInfoBean();
                            info.setSize(size);
                            info.setDuration(duration);
                            info.setFilePath(path);
                            info.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                            info.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                            //获取当前Video对应的Id，然后根据该ID获取其Thumb
                            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                            Cursor videoThumbnailCursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);

                            String thumbUri = "";
                            if (videoThumbnailCursor.moveToFirst()) {
                                thumbUri = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                            }

                            ContentResolver crThumb = getContentResolver();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 1;
                            info.setThumbPath(thumbUri);
                            //然后将其加入到videoList
                            videoList.add(info);
                        }
                    } while (cursor.moveToNext());
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {


        if (videoList != null) {
            mGvLocalVideo.setAdapter(new MyAdapter());
            mGvLocalVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = getIntent();
                    intent.putExtra(VIDEO_INFO_BEAN_LOCAL,videoList.get(i));
                    setResult(VIDEO_SOURCE_CODE_RESULT,intent);
                    finish();
                }
            });

        }
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

    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 6;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(VideoSourceActivity.this).inflate(R.layout.item_gv_local_video, null);
                holder.iv = (ImageView) view.findViewById(R.id.iv_bg);
                holder.tv = (TextView) view.findViewById(R.id.tv_time);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            String thumbUri = videoList.get(position).getThumbPath();
            LogUtil.e("thumbUri------------------"+thumbUri );
            if (!TextUtils.isEmpty(thumbUri)) {
                holder.iv.setImageURI(Uri.parse(thumbUri));
            }else{
                holder.iv.setImageResource(R.mipmap.icon);
            }

            LogUtil.e("duration------------------"+videoList.get(position).getDuration() );
            String time = StringUtil.formatDuration(videoList.get(position).getDuration());
            holder.tv.setText(time+"");

            return view;
        }
    }
    class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}
