package com.example.administrator.javademo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.adapter.FindPicGvAdapter;
import com.example.administrator.javademo.bean.CommentinfoBean;
import com.example.administrator.javademo.bean.InformationBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.view.find.FindPlView;
import com.example.administrator.javademo.view.find.ImageDialog;
import com.example.administrator.javademo.view.find.VideoDialog;
import com.fire.photoselector.utils.ScreenUtil;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridlayout;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/24 0024.
 */

public class InformationActivity extends BaseActivity {
    public static final String PARENT_ID = "parent_id";
    public static final String PARENT_TYPE = "parent_type";
    private static final int MSG_FIND_INFORMATION_SUCCESS = 0;
    private static final int MSG_INFO_DELETE = 1;
    @InjectView(R.id.tv_find_hottuijian_item_name)
    TextView tv_name_item;
    @InjectView(R.id.tv_find_hottuijian_item_time)
    TextView tv_time_item;
    @InjectView(R.id.ll_top)
    LinearLayout ll_top;
    @InjectView(R.id.tv_item_content)
    TextView tv_content_item;
    @InjectView(R.id.gv_find_hottuijian_item)
    NineGridlayout gv_item;
    @InjectView(R.id.video_find_hottuijian_item)
    ImageView vv_item;
    @InjectView(R.id.iv_find_play)
    ImageView iv_find_play;
    @InjectView(R.id.rl_video)
    RelativeLayout rl_video;
    @InjectView(R.id.iv_pinglun)
    ImageView iv_pinglun;
    @InjectView(R.id.tv_pinglun)
    TextView tv_pinglun;
    @InjectView(R.id.ll_pinglun)
    LinearLayout ll_pinglun;
    @InjectView(R.id.iv_dianzan)
    ImageView iv_dianzan;
    @InjectView(R.id.tv_dianzan)
    TextView tv_dianzan;
    @InjectView(R.id.ll_dianzan)
    LinearLayout ll_dianzan;
    @InjectView(R.id.fl_find_pinglun)
    FrameLayout fl_pinglun;
    @InjectView(R.id.find_item_delete)
    ImageView tv_delete;
    @InjectView(R.id.ll_infomation)
    RelativeLayout llInfomation;
    @InjectView(R.id.tv_null)
    TextView tvNull;
    @InjectView(R.id.ll_view_info)
    LinearLayout llViewInfo;
    private int my_uid;
    private InformationBean informationBean;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_INFORMATION_SUCCESS:
                    dataShow();
                    break;
                case MSG_INFO_DELETE:
                    //内容已被删除
                    llInfomation.setVisibility(View.GONE);
                    llViewInfo.setVisibility(View.VISIBLE);
                    tvNull.setText("相关内容已被删除");
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_information;
    }

    @Override
    protected void initView() {
        if (SPUtil.getUid(this) != null) {
            String s = SPUtil.getUid(this)[0];
            my_uid = Integer.parseInt(s);
        }
        Intent intent = getIntent();
        int parent_id = intent.getIntExtra(PARENT_ID, -1);
        int parent_type = intent.getIntExtra(PARENT_TYPE, -1);
        HashMap<String, Object> map = new HashMap<>();
        String url = "";
        if (parent_type == 2 || parent_type == 3) {
            llInfomation.setVisibility(View.VISIBLE);
            llViewInfo.setVisibility(View.GONE);
            url = AppConstants.INFORMATION_FIND_COMMENTINFO_URL;
            map.put("commentinfo", parent_id);
            netReadPost(url, map);
        } else if (parent_type == 4) {
            llInfomation.setVisibility(View.VISIBLE);
            llViewInfo.setVisibility(View.GONE);
            url = AppConstants.INFORMATION_FIND_COMMENT_SECOND_URL;
            map.put("commentSecond", parent_id);
            netReadPost(url, map);
        }else{
            llInfomation.setVisibility(View.GONE);
            llViewInfo.setVisibility(View.VISIBLE);
            tvNull.setText("相关内容已被删除");
        }
    }

    private void netReadPost(String url, HashMap<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (!TextUtils.isEmpty(result) && !TextUtils.equals(AppConstants.FAIL, result)) {
                        //成功
                        informationBean = GsonUtil.parseJsonWithGson(result, InformationBean.class);
                        handler.sendEmptyMessage(MSG_FIND_INFORMATION_SUCCESS);
                    } else {
                        //原内容已被删除
                        handler.sendEmptyMessage(MSG_INFO_DELETE);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * 放大显示图片
     */
    private void showImage(int position, InformationBean listBean) {
        final ImageDialog imageDialog = ImageDialog.getDialog(this);
        String pic = listBean.getUrl();
        if (pic.endsWith(";")) {
            pic = pic.substring(0, pic.length() - 1);
        }
        String[] split = pic.split(";");

        imageDialog.setImageData(split, position);

        imageDialog.setOnDialogClickListener(new ImageDialog.DialogItemClickListener() {
            @Override
            public void itemClick(int position) {
                imageDialog.dismiss();
            }
        });

//        imageDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageDialog.show();
    }


    /**
     * 播放视频
     *
     * @param picPath
     * @param thumbImage_path
     */
    private void playVideo(String picPath, String thumbImage_path) {
        if (picPath.endsWith(";")) {
            picPath = picPath.substring(0, picPath.length() - 1);
        }
        Intent intent = new Intent(this, VideoDialog.class);
        intent.putExtra("video_path", picPath);
        intent.putExtra("thumbImage_path", thumbImage_path);//缩略图路径
        intent.putExtra("useCache", true);
        startActivity(intent);
    }

    /**
     * 点赞
     */
    private void postZan(final InformationBean bean, final ImageView iv_dianzan, final TextView tv_dianzan) throws IOException {

        //TODO 点赞
        final HashMap<String, Object> map = new HashMap<>();
        map.put("information", bean.getId());
        map.put("user", my_uid);
        OkHttpUtil.postJson(AppConstants.VOTE_INFORMATION_UPDATE_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("点赞=================" + result);
                if (TextUtils.isEmpty(result) || !TextUtils.equals(AppConstants.FAIL, result)) {
                    //成功
                    final String[] split = result.split(":");
                    if (split.length == 3) {
                        //split[0] = success ,split[1] = state(0无赞,1有赞), split[2] = voteinfoSize 点赞量
                        boolean flag = false;
                        if (TextUtils.equals("0", split[1])) {
                            //无赞
                            flag = false;
                        } else if (TextUtils.equals("1", split[1])) {
                            //赞
                            flag = true;
                        }
                        final boolean finalFlag = flag;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_dianzan.setSelected(finalFlag);
                                tv_dianzan.setText("点赞 (" + split[2] + ")");
                            }
                        });
                    }
                }
            }
        });

    }

    private void dataShow() {
        int tid = informationBean.getId();//热门评论的id
        //是否是自己的说说
        final int informationUid = informationBean.getUser().getId();

        if (my_uid == informationUid) {
            tv_delete.setVisibility(View.VISIBLE);
        } else {
            tv_delete.setVisibility(View.INVISIBLE);
        }
        final List<CommentinfoBean> commentinfoList = informationBean.getCommentinfo();

        //回显是否点赞
        informationBean.voteinfoByUserShow(this, my_uid, iv_dianzan);
        //其它设置
        tv_content_item.setText(informationBean.getInfo());//发布内容
        tv_name_item.setText(informationBean.getUser().getUsername());//用户昵称
        Timestamp timestamp = new Timestamp(informationBean.getCreateTime());
        tv_time_item.setText(FileUtil.getName(timestamp.toString()));//发布时间
        tv_pinglun.setText("评论 (" + informationBean.getCommentinfoSize() + ")");//评论
        tv_dianzan.setText("点赞 (" + informationBean.getVoteinfoSize() + ")");//点赞

        int type = informationBean.getType();
        String url = informationBean.getUrl();
        String[] split;
        switch (type) {
            case -1:
                //纯文本
                gv_item.setVisibility(View.GONE);
                rl_video.setVisibility(View.GONE);
                break;
            case 0:
                //图片
                gv_item.setVisibility(View.VISIBLE);
                rl_video.setVisibility(View.GONE);
                //是图片
                if (url.endsWith(";")) {
                    url = url.substring(0, url.length() - 1);
                }
                split = url.split(";");
                List<String> imageList = Arrays.asList(split);
                FindPicGvAdapter adapter = new FindPicGvAdapter(this, imageList);
                gv_item.setAdapter(adapter);
                gv_item.setDefaultWidth(ScreenUtil.dp2px(this, 300));
                gv_item.setDefaultHeight(ScreenUtil.dp2px(this, 200));
                gv_item.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //点击图片，放大显示图片
                        showImage(position, informationBean);
                    }
                });

                break;
            case 1:
                //视频
                gv_item.setVisibility(View.GONE);
                rl_video.setVisibility(View.VISIBLE);
                //获取视频缩略图
                if (url.endsWith(";")) {
                    url = url.substring(0, url.length() - 1);
                }
                split = url.split(";");
                final String s1 = split[0];
                String s2 = "";
                if (split.length > 1) {
                    s2 = split[1];
                }
                if (s1.endsWith(".PNG") || s1.endsWith(".png") || s1.endsWith(".JPG") || s1.endsWith(".jpg")) {
                    //s1是图片，s2是视频
                    Picasso.with(this).load(s1).fit().into(vv_item);
                    // videoPath = s2;
                    final String finalS = s2;
                    rl_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //跳转到视频播放
                            playVideo(finalS, s1);
                        }
                    });

                } else {
                    //s2是图片，s1是视频
                    Picasso.with(this).load(s2).fit().into(vv_item);
                    // videoPath = s1;
                    final String finalS1 = s2;
                    rl_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //跳转到视频播放
                            playVideo(s1, finalS1);
                        }
                    });
                }
                break;
        }


        /**点赞*/
        ll_dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    postZan(informationBean, iv_dianzan, tv_dianzan);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /**评论*/
        FindPlView findPlView = new FindPlView(this, informationBean.getCommentinfo(), informationBean.getId(), my_uid + "", tv_pinglun, informationBean);
        fl_pinglun.addView(findPlView.mRootView);

        /**
         * 删除说说
         */
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);
                builder.setMessage("是否删除");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            deleteShuoshuo(informationBean.getId(), informationBean.getUser().getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    /**
     * 删除说说
     *
     * @param informationId
     * @param uid
     */
    private void deleteShuoshuo(int informationId, int uid) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("information", informationId);
//        map.put("uid", uid);
        OkHttpUtil.postJson(AppConstants.INFORMATION_DELETE_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("delete shuoshuo----" + result);
                if (!TextUtils.isEmpty(result) && TextUtils.equals(AppConstants.SUCCESS, result)) {
                    //成功
                    //刷新页面
                    handler.sendEmptyMessage(MSG_INFO_DELETE);
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
