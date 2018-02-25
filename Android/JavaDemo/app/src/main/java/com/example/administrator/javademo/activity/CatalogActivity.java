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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.CatalogBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/31 0031.
 */

public class CatalogActivity extends BaseActivity {
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final String CATALOG = "catalog";
    private static final String PROJECT = "project";
    private static final String TAG = "tag";
    private static final String TITLE = "title";
    public static final String CATALOG_BEAN = "catalog_bean";
    @InjectView(R.id.lv_catalog)
    ListView mLvCatalog;
    @InjectView(R.id.bt_catalog_add)
    Button mBtCatalogAdd;
    @InjectView(R.id.tv_null)
    TextView mTvNull;
    @InjectView(R.id.ll_view_info)
    LinearLayout mLlViewInfo;
    private List<CatalogBean> catalogList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_SUCCESS:
                    if (catalogList != null) {
                        mLlViewInfo.setVisibility(View.GONE);
                        mLvCatalog.setVisibility(View.VISIBLE);
                        if (mAdapter == null)
                            mAdapter = new MyAdapter();
                        mLvCatalog.setAdapter(mAdapter);
                    } else {
                        mLvCatalog.setVisibility(View.GONE);
                        mLlViewInfo.setVisibility(View.VISIBLE);
                        mTvNull.setText("目录列表为空");
                    }
                    break;
                case MSG_FIND_FAIL:
                    Toast.makeText(CatalogActivity.this, "访问目录失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(CatalogActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    netReadPost();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(CatalogActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(CatalogActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private View mCatalogDialogView;
    private AlertDialog mAddDialog;
    private int mProject;
    private MyAdapter mAdapter;
    private AlertDialog mDelOrUpdateDialog;
    private int mDelOrUpdatePosition;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mUpdateDialog;
    private View mUpdateDialogView;
    private EditText mEtUpdateDialog;
    private int mClickPosition;
    private String mUserType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catalog;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mProject = intent.getIntExtra(AppConstants.PROJECT_TYPE, AppConstants.PROJECT_JAVA);
        mClickPosition = intent.getIntExtra(MainActivity.CLICK_POSITION, MainActivity.POSITION_VIDEO);
        String[] user = SPUtil.getUid(this);
        if (user != null) {
            mUserType = user[4];
            if (AppConstants.USER_STUDENT.equals(mUserType)) {
                //学生用户---没有添加目录功能
                mBtCatalogAdd.setVisibility(View.GONE);
            }
        }
        netReadPost();
    }

    private void netReadPost() {
        Map<String, Object> map = new HashMap<>();
        map.put(PROJECT, mProject);
        try {
            OkHttpUtil.postJson(AppConstants.CATALOG_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Catalog查寻------" + result);
                    if (!TextUtils.isEmpty(result)) {
                        LogUtil.e("查寻到拉");
                        catalogList = (List<CatalogBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<CatalogBean>>() {
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
                    LogUtil.e("Catalog写操作------" + result);
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
    protected void initEvent() {
        //添加目录
        mBtCatalogAdd.setOnClickListener(this);
        //list item长按
        mLvCatalog.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                mDelOrUpdatePosition = position;
                LogUtil.e("mUserType===="+mUserType);
                if (AppConstants.USER_TEACHER.equals(mUserType)) {
                    //教师用户---删改目录功能
                    String[] user = SPUtil.getUid(CatalogActivity.this);
                    if (user != null){
                        String uid = user[0];
                        if (TextUtils.equals(uid,catalogList.get(position).getId()+"")) {
                            setDelOrUpdateDialog();
                        }
                    }
                }
                return true;
            }
        });
        //list item点击事件
        mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
                Intent intent = null;
                if (mClickPosition == MainActivity.POSITION_VIDEO) {
                    //视频页面
                    intent = new Intent(CatalogActivity.this, VideoListActivity.class);
                } else if (mClickPosition == MainActivity.POSITION_PRACTICE) {
                    //练习题页面
                    intent = new Intent(CatalogActivity.this, PracticeListActivity.class);
                }
                if (intent != null) {
                    CatalogBean catalogBean = catalogList.get(i);
                    String json = GsonUtil.getJson(catalogBean);
                    intent.putExtra(CATALOG_BEAN, json);
                    intent.putExtra(MainActivity.CLICK_POSITION,mClickPosition);
                    startActivityForResult(intent,0);
                }

            }
        });
    }

    private void setDelOrUpdateDialog() {
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(this);
        }
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_catalog_del_update, null);
        inflate.findViewById(R.id.bt_dialog_del).setOnClickListener(this);
        inflate.findViewById(R.id.bt_dialog_update).setOnClickListener(this);
        mBuilder.setView(inflate);
        mDelOrUpdateDialog = mBuilder.create();
        mDelOrUpdateDialog.show();
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.bt_catalog_add:
                //添加目录
                catalogAddDialog();
                break;
            case R.id.bt_dialog_submit:
                //确定添加目录
                addDialogSubmit();
                break;
            case R.id.bt_dialog_cancel:
                //取消添加目录
                mAddDialog.dismiss();
                break;
            case R.id.bt_dialog_del:
                //删除目录
                setDelOrUpdate(catalogList.get(mDelOrUpdatePosition), AppConstants.CATALOG_DELETE_URL);
                break;
            case R.id.bt_dialog_update:
                //更新目录
                updateDialog();
                break;
            case R.id.bt_deldialog_submit:
                //确认更新目录
                updateSubmit();
                break;
            case R.id.bt_deldialog_cancel:
                //取消更新目录
                mUpdateDialog.dismiss();
                break;
        }
    }

    private void updateSubmit() {
        String et_update = mEtUpdateDialog.getText().toString().trim();
        if (TextUtils.isEmpty(et_update)) {
            Toast.makeText(this, "目录不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mUpdateDialog.dismiss();
        CatalogBean catalogBean = catalogList.get(mDelOrUpdatePosition);
        catalogBean.setTitle(et_update);
        setDelOrUpdate(catalogBean, AppConstants.CATALOG_UPDATE_URL);
    }

    private void updateDialog() {
        //更新
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(this);
        }
        mUpdateDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_delete, null);
        TextView tv_title = (TextView) mUpdateDialogView.findViewById(R.id.tv_dialog_title);
        TextView tv_info = (TextView) mUpdateDialogView.findViewById(R.id.tv_dialog_info);
        mEtUpdateDialog = (EditText) mUpdateDialogView.findViewById(R.id.et_deldialog);
        tv_title.setText("修改目录名称");
        tv_info.setText("");
        mEtUpdateDialog.setText("" + catalogList.get(mDelOrUpdatePosition).getTitle());
        mBuilder.setView(mUpdateDialogView);
        mUpdateDialogView.findViewById(R.id.bt_deldialog_submit).setOnClickListener(this);
        mUpdateDialogView.findViewById(R.id.bt_deldialog_cancel).setOnClickListener(this);
        mUpdateDialog = mBuilder.create();
        mUpdateDialog.show();
    }

    private void setDelOrUpdate(CatalogBean catalogBean, String url) {
        mDelOrUpdateDialog.dismiss();
        Map<String, Object> map = new HashMap<>();
        String json = GsonUtil.getGson().toJson(catalogBean);
        map.put(CATALOG, json);
        netWritePost(url, map);
    }

    /**
     * 确认添加目录
     */
    private void addDialogSubmit() {
        EditText et_tag = (EditText) mCatalogDialogView.findViewById(R.id.et_catalog_dialog_tag);
        EditText et_title = (EditText) mCatalogDialogView.findViewById(R.id.et_catalog_dialog_title);
        String tag = et_tag.getText().toString().trim();
        String title = et_title.getText().toString().trim();
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(title)) {
            Toast.makeText(this, "信息不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mAddDialog.dismiss();
        Map<String, Object> map = new HashMap<>();
        map.put(PROJECT, mProject);
        map.put(TAG, tag);
        map.put(TITLE, title);
        netWritePost(AppConstants.CATALOG_SAVE_URL, map);
    }

    private void catalogAddDialog() {
        if (mBuilder == null) {
            LogUtil.e("创建builder");
            mBuilder = new AlertDialog.Builder(this);
        }
        mCatalogDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_catalog_add, null);
        mCatalogDialogView.findViewById(R.id.bt_dialog_submit).setOnClickListener(this);
        mCatalogDialogView.findViewById(R.id.bt_dialog_cancel).setOnClickListener(this);
        mBuilder.setView(mCatalogDialogView);
        mAddDialog = mBuilder.create();
        mAddDialog.show();
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
            return catalogList.size();
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(CatalogActivity.this).inflate(R.layout.item_lv_catalog, null);
                holder.tv = (TextView) view.findViewById(R.id.tv_item_catalog);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String str;
            CatalogBean catalogBean = catalogList.get(position);
            int tag = catalogBean.getTag();
            if (tag < 10) {
                str = "第" + tag + "章    ";
            } else {
                str = "第" + tag + "章   ";
            }
            holder.tv.setText(str + catalogBean.getTitle());
            return view;
        }
    }

    class ViewHolder {
        TextView tv;
    }
}

        /*switch (projectType){
            case AppConstants.PROJECT_JAVA:
                catalogs = new String[]{"Java概述","Java语言基础","数组与字符串","类与对象",
                        "继承、抽象类和接口","Java的I/O流","异常处理"};
                break;
            case AppConstants.PROJECT_JSP:
                catalogs = new String[]{"JSP概述","JSP页面与JSP标记","Tag文件与Tag标记",
                        "JSP内置对象","ISP中的文件操作","JSP中使用数据库","JSP与.Javabean",
                        "Java Servlet基础","MVC模式"};
                break;
            case AppConstants.PROJECT_JAVAWEB:
                catalogs = new String[]{" 初入Struts2","控制层与配置文件","Struts2文件的上传与下载",
                        "Struts2的数据校验与国际化","Struts2标签库","初入Hibernate","Hibernate核心技能",
                        "Hibernate的检索","Spring基础","三大框架整合"};
                break;
        }*/