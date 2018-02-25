package com.example.administrator.javademo.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.CatalogBean;
import com.example.administrator.javademo.bean.VideoBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.util.VideoUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/3 0003.
 */

public class VideoListActivity extends BaseActivity {
    private static final int VIDEO_LIST_REQUEST_CODE = 2;
    private static final int VIDEO_LIST_REQUEST_CODE_ADD = 3;
    public static final String VIDEO_POSITION = "video_position";
    public static final String VIDEO_LIST = "video_list";
    @InjectView(R.id.tv_null)
    TextView mTvNull;
    @InjectView(R.id.ll_view_info)
    LinearLayout mLlViewInfo;
    @InjectView(R.id.lv_video_list)
    ListView mLvVideoList;
    public static final String VIDEO = "video";
    public static final String TITLE = "title";
    public static final String INFO = "info";
    public static final String URL = "url";
    public static final String CID = "cid";
    public static final String TID = "tid";
    public static final String CATALOG = "catalog";
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    List<VideoBean> videoList = new ArrayList();
    @InjectView(R.id.bt_video_add)
    Button mBtVideoAdd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_SUCCESS:
                    if (videoList != null) {
                        setList();
                    } else {
                        mLvVideoList.setVisibility(View.GONE);
                        mLlViewInfo.setVisibility(View.VISIBLE);
                        mTvNull.setText("视频列表为空");
                    }
                    break;
                case MSG_FIND_FAIL:
                    Toast.makeText(VideoListActivity.this, "访问目录失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(VideoListActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    netReadPost();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(VideoListActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(VideoListActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private CatalogBean catalog;
    private String mReadResult;
    private String mAdminType;

    private void setList() {
        mLlViewInfo.setVisibility(View.GONE);
        mLvVideoList.setVisibility(View.VISIBLE);
        if (mAdapter == null)
            mAdapter = new MyAdapter();
        mLvVideoList.setAdapter(mAdapter);
        mLvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
                intent.putExtra(VIDEO_POSITION, i);
                intent.putExtra(VIDEO_LIST, mReadResult);
                intent.putExtra(VideoListActivity.CATALOG,mCid);
                startActivityForResult(intent, VIDEO_LIST_REQUEST_CODE);
            }
        });

        mLvVideoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(AppConstants.USER_TEACHER.equals(mAdminType)){
                    String[] user = SPUtil.getUid(VideoListActivity.this);
                    if (user != null){
                        String uid = user[0];
                        if (TextUtils.equals(uid,videoList.get(position).getId()+"")) {
                            //教师删除
                            setDelOrUpdateDialog(position);
                        }
                    }
                }
                return true;
            }
        });
    }

    /**
     * 删除
     * @param position
     */
    private void setDelOrUpdateDialog(final int position) {
        AlertDialog.Builder   mBuilder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_catalog_del_update, null);
        inflate.findViewById(R.id.bt_dialog_update).setVisibility(View.GONE);
        mBuilder.setView(inflate);
        final AlertDialog mDelOrUpdateDialog = mBuilder.create();
        inflate.findViewById(R.id.bt_dialog_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
//                String json = GsonUtil.getGson().toJson(videoList.get(position));
                map.put(VideoListActivity.VIDEO,videoList.get(position).getId());
                map.put(VideoListActivity.CATALOG,mCid);
                netWritePost(AppConstants.VIDEO_DELETE_URL,map);
                mDelOrUpdateDialog.dismiss();
            }
        });
        mDelOrUpdateDialog.show();
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
    private MyAdapter mAdapter;
    private Integer mCid;
    private int mClickPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        //添加按钮是否可见
        String[] uid = SPUtil.getUid(this);
        if (uid != null){
            mAdminType = uid[4];
            if(AppConstants.USER_STUDENT.equals(mAdminType)){
                //学生无添加按钮
                mBtVideoAdd.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (catalog != null) {
            mCid = catalog.getId();
            //初始化视频列表
            netReadPost();
        }
    }

    @Override
    protected void initData() {
        //按cid查找视频
        Intent intent = getIntent();
        String json = intent.getStringExtra(CatalogActivity.CATALOG_BEAN);
        catalog = GsonUtil.parseJsonWithGson(json, CatalogBean.class);
//        catalog = (CatalogBean) intent.getSerializableExtra(CatalogActivity.CATALOG_BEAN);
        mClickPosition = intent.getIntExtra(MainActivity.CLICK_POSITION, MainActivity.POSITION_VIDEO);
        /*if (catalog != null) {
            mCid = catalog.getId();
            //初始化视频列表
            netReadPost();
        }
*/
//        mLlViewInfo.setVisibility(View.VISIBLE);
//        mTvNull.setText("目录列表为空");
//        mLvVideoList.setAdapter(new MyAdapter());

    }

    @Override
    protected void initEvent() {
        mBtVideoAdd.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.bt_video_add:
                //添加视频
                Intent intent = new Intent(this, AddVideoActivity.class);
                intent.putExtra(CID,mCid);
                intent.putExtra(MainActivity.CLICK_POSITION,mClickPosition);
                startActivityForResult(intent,VIDEO_LIST_REQUEST_CODE_ADD);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    /**
     * 按cid查找视频----初始化视频列表
     */
    public void netReadPost() {
        //查找视频
        Map<String, Object> map = new HashMap<>();
        map.put(CID, mCid);
        try {
            OkHttpUtil.postJson(AppConstants.VIDEO_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mReadResult = OkHttpUtil.getResult(response);
                    LogUtil.e("Video查寻------" + mReadResult);
                    if (!TextUtils.isEmpty(mReadResult)) {
                        LogUtil.e("查寻到拉");
                        videoList = (List<VideoBean>) GsonUtil.parseJsonToList(mReadResult, new TypeToken<List<VideoBean>>() {
                        }.getType());
                        handler.sendEmptyMessage(MSG_FIND_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(MSG_FIND_FAIL);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_LIST_REQUEST_CODE_ADD) {
            if (catalog != null) {
                netReadPost();
            }
        }
    }

    class MyAdapter extends BaseAdapter {

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
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(VideoListActivity.this).inflate(R.layout.item_lv_video_list, null);
                holder.iv_bg = (ImageView) view.findViewById(R.id.iv_class_bg_item);
                holder.tv_title = (TextView) view.findViewById(R.id.tv_video_title);
                holder.tv_teacher = (TextView) view.findViewById(R.id.tv_video_teacher);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            VideoBean videoBean = videoList.get(i);
            VideoUtil.setThumbnail(VideoListActivity.this,videoBean.getUrl(),holder.iv_bg);
            holder.tv_title.setText(videoBean.getTitle());
            holder.tv_teacher.setText(videoBean.getUser().getUsername());



            return view;
        }
    }

    class ViewHolder {
        ImageView iv_bg;
        TextView tv_title;
        TextView tv_teacher;
    }
}
