package com.example.administrator.javademo.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.ProjectBean;
import com.example.administrator.javademo.mApplication;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;

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

public class MainActivity extends BaseActivity {

    public static final int REQUEST_CODE = 10;
    public static final String INTENT_DATA_ADMIN_TYPE = "intent_data_admin_type";
    private static final int MSG_DELETE_SUCCESS = 0;
    private static final int MSG_DELETE_FAIL = 1;
    private static final int MSG_NET_FAIL = 2;
    public static final String CLICK_POSITION = "click_position";
    public static final int POSITION_VIDEO = 0;
    public static final int POSITION_PRACTICE = 1;
    List<ProjectBean> list = new ArrayList<>();
    @InjectView(R.id.tv_main_title)
    TextView mTvMainTitle;
    @InjectView(R.id.iv_item_gv_main1)
    ImageView mIvItemGvMain1;
    @InjectView(R.id.tv_item_gv_main1)
    TextView mTvItemGvMain1;
    @InjectView(R.id.rl_1)
    RelativeLayout mRl1;
    @InjectView(R.id.iv_item_gv_main2)
    ImageView mIvItemGvMain2;
    @InjectView(R.id.tv_item_gv_main2)
    TextView mTvItemGvMain2;
    @InjectView(R.id.rl_2)
    RelativeLayout mRl2;
    @InjectView(R.id.iv_item_gv_main3)
    ImageView mIvItemGvMain3;
    @InjectView(R.id.tv_item_gv_main3)
    TextView mTvItemGvMain3;
    @InjectView(R.id.rl_3)
    RelativeLayout mRl3;
    @InjectView(R.id.tv_news)
    TextView mTvNews;
    @InjectView(R.id.iv_item_gv_main4)
    ImageView mIvItemGvMain4;
    @InjectView(R.id.tv_item_gv_main4)
    TextView mTvItemGvMain4;
    @InjectView(R.id.rl_4)
    RelativeLayout mRl4;
    @InjectView(R.id.iv_item_gv_main5)
    ImageView mIvItemGvMain5;
    @InjectView(R.id.tv_item_gv_main5)
    TextView mTvItemGvMain5;
    @InjectView(R.id.rl_5)
    RelativeLayout mRl5;
    private String mAdminType;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELETE_SUCCESS:
                    Toast.makeText(MainActivity.this, "删除用户成功", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DELETE_FAIL:
                    Toast.makeText(MainActivity.this, "删除用户失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(MainActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private EditText mEtDeldialog;
    private AlertDialog delDialog;
    private int mClickPosition;
    private AlertDialog.Builder mProjectDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //主题
        String[] str = SPUtil.getUid(this);
        if (str == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        mAdminType = str[4];
        if (TextUtils.isEmpty(mAdminType)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (AppConstants.USER_ADMIN.equals(mAdminType)) {
            mRl5.setVisibility(View.VISIBLE);
            mTvNews.setVisibility(View.GONE);
            mTvItemGvMain1.setText("批量添加学生信息");
            mTvItemGvMain2.setText("批量删除学生信息");
            mTvItemGvMain3.setText("批量添加教师信息");
            mTvItemGvMain4.setText("批量删除教师信息");
            mTvItemGvMain5.setText("我的");
            mIvItemGvMain1.setBackgroundResource(R.mipmap.icon_add);
            mIvItemGvMain2.setBackgroundResource(R.mipmap.icon_delete);
            mIvItemGvMain3.setBackgroundResource(R.mipmap.icon_add);
            mIvItemGvMain4.setBackgroundResource(R.mipmap.icon_delete);
            mIvItemGvMain5.setBackgroundResource(R.mipmap.icon_mine);
        } else {
            mRl5.setVisibility(View.GONE);
            mTvItemGvMain1.setText("Java视频");
            mTvItemGvMain2.setText("练习巩固");
            mTvItemGvMain3.setText("Java论坛");
            mTvItemGvMain4.setText("我的");
            mIvItemGvMain1.setBackgroundResource(R.mipmap.icon_video);
            mIvItemGvMain2.setBackgroundResource(R.mipmap.icon_practice);
            mIvItemGvMain3.setBackgroundResource(R.mipmap.icon_discuss);
            mIvItemGvMain4.setBackgroundResource(R.mipmap.icon_mine);
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
    protected void onResume() {
        super.onResume();
        //用户处理

        //新消息处理
        if (mTvNews!=null && !AppConstants.USER_ADMIN.equals(mAdminType)){
            if (!TextUtils.equals("0", mApplication.mNewsSize) ){
                //有新消息
                mTvNews.setVisibility(View.VISIBLE);
                mTvNews.setText(mApplication.mNewsSize+"");
            }else{
                //没有新消息
                mTvNews.setVisibility(View.GONE);
            }
            newsShow();
        }
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
                            final String[] split = result.split(":");
                            if (split != null && split.length == 2){
                                mApplication.mNewsSize = split[1];
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!TextUtils.equals("0",split[1]) ){
                                            //有新消息
                                            mTvNews.setVisibility(View.VISIBLE);
                                            mTvNews.setText(mApplication.mNewsSize+"");
                                        }else{
                                            //没有新消息
                                            mTvNews.setVisibility(View.GONE);
                                        }
                                    }
                                });
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
    protected void initEvent() {
        mRl1.setOnClickListener(this);
        mRl2.setOnClickListener(this);
        mRl3.setOnClickListener(this);
        mRl4.setOnClickListener(this);
        mRl5.setOnClickListener(this);
    }

    /**
     * 科目对话框
     */
    private void projectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_projcet, null);
        mProjectDialog = builder.setView(inflate);
        mProjectDialog.show();
        inflate.findViewById(R.id.iv_projcet_java).setOnClickListener(this);
        inflate.findViewById(R.id.iv_projcet_jsp).setOnClickListener(this);
        inflate.findViewById(R.id.iv_projcet_javaweb).setOnClickListener(this);
    }

    /**
     * 管理员批量删除用户
     */
    private void deleteUser() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user_delete, null);
        mEtDeldialog = (EditText) view.findViewById(R.id.et_deldialog);
        builder.setView(view);
        delDialog = builder.create();
        view.findViewById(R.id.bt_deldialog_submit).setOnClickListener(this);
        view.findViewById(R.id.bt_deldialog_cancel).setOnClickListener(this);
        delDialog.show();

    }

    /**
     * @param v
     * @throws IOException
     */
    @Override
    protected void processClick(View v) throws IOException {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rl_1:
                if (AppConstants.USER_ADMIN.equals(mAdminType)) {
                    //添加学生
                    intent = new Intent(MainActivity.this, TextInfoListActivity.class);
                    intent.putExtra(INTENT_DATA_ADMIN_TYPE, AppConstants.USER_STUDENT);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
                }else{
                    //Java视频
                    mClickPosition = POSITION_VIDEO;
                    projectDialog();
                }
                break;
            case R.id.rl_2:
                if (AppConstants.USER_ADMIN.equals(mAdminType)) {
                    //删除学生
                    deleteUser();
                }else{
                    //练习巩固
                    mClickPosition = POSITION_PRACTICE;
                    projectDialog();
                }
                break;
            case R.id.rl_3:
                if (AppConstants.USER_ADMIN.equals(mAdminType)) {
                    //添加教师
                    intent = new Intent(MainActivity.this, TextInfoListActivity.class);
                    intent.putExtra(INTENT_DATA_ADMIN_TYPE, AppConstants.USER_TEACHER);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
                }else{
                    //java论坛
                    startActivity(new Intent(MainActivity.this, ForumActivity.class));
                }
                break;
            case R.id.rl_4:
                if (AppConstants.USER_ADMIN.equals(mAdminType)) {
                    //删除教师
                    deleteUser();
                }else{
                    //我的
                    startActivity(new Intent(MainActivity.this, MineActivity.class));
                }
                break;
            case R.id.rl_5:
                if (AppConstants.USER_ADMIN.equals(mAdminType)) {
                    //我的
                    startActivity(new Intent(MainActivity.this, MineActivity.class));
                }
                break;
            case R.id.bt_deldialog_submit:
                //删除用户对话框---确定
                delDialogListener();
                break;
            case R.id.bt_deldialog_cancel:
                //删除用户对话框---取消
                delDialog.dismiss();
                break;
            case R.id.iv_projcet_java:
                if (mProjectDialog != null)
                    projectListener(AppConstants.PROJECT_JAVA);
                break;
            case R.id.iv_projcet_jsp:
                if (mProjectDialog != null)
                    projectListener(AppConstants.PROJECT_JSP);
                break;
            case R.id.iv_projcet_javaweb:
                if (mProjectDialog != null)
                    projectListener(AppConstants.PROJECT_JAVAWEB);
                break;
        }
    }

    private void projectListener(Integer type) {
        Intent intent = new Intent(this, CatalogActivity.class);
        intent.putExtra(AppConstants.PROJECT_TYPE, type);
        intent.putExtra(CLICK_POSITION, mClickPosition);
        startActivityForResult(intent, 0);

    }

    private void delDialogListener() {
        final String identifier = mEtDeldialog.getText().toString().trim();
        if (TextUtils.isEmpty(identifier)) {
            Toast.makeText(MainActivity.this, "编号不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //删除提示框
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(MainActivity.this);
        infoDialog.setIcon(R.mipmap.dialog_delete);
        infoDialog.setTitle("提示");
        infoDialog.setMessage("真的要删除编号以。。。开头的所有用户么？");
        infoDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                delDialog.dismiss();
                //删除数据库操作
                Map<String, String> map = new HashMap<>();
                map.put("identifier", identifier);
                try {
                    OkHttpUtil.postString(AppConstants.USER_DELETE_ALL_URL, map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //删除失败
                            handler.sendEmptyMessage(MSG_DELETE_FAIL);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = OkHttpUtil.getResult(response);
                            LogUtil.e("result-----" + result);
                            if (!TextUtils.isEmpty(result)) {
                                if (AppConstants.SUCCESS.equals(result)) {
                                    //返回成功
                                    handler.sendEmptyMessage(MSG_DELETE_SUCCESS);
                                } else {
                                    //返回失败
                                    handler.sendEmptyMessage(MSG_DELETE_FAIL);
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    LogUtil.e("登录联网操作失败");
                    handler.sendEmptyMessage(MSG_NET_FAIL);
                    e.printStackTrace();
                }
            }
        });
        infoDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        infoDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
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
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_gv_main, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item_gv_main);
            TextView tv = (TextView) view.findViewById(R.id.tv_item_gv_main);
            TextView tv_news = (TextView) view.findViewById(R.id.tv_news);
            if (i == list.size() - 1 && !TextUtils.equals("0", mApplication.mNewsSize)) {
                //有新消息
                tv_news.setVisibility(View.VISIBLE);
                tv_news.setText(mApplication.mNewsSize + "");
            } else {
                //没有新消息
                tv_news.setVisibility(View.GONE);
            }
            iv.setBackgroundResource(list.get(i).getImg());
            tv.setText(list.get(i).getTitle());
            return view;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProjectDialog != null){
            mProjectDialog = null;
        }
    }
}
