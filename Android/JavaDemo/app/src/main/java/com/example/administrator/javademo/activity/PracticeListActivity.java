package com.example.administrator.javademo.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.CatalogBean;
import com.example.administrator.javademo.bean.PracticeBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.OpenFileUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class PracticeListActivity extends BaseActivity {
    private static final int PRACTICE_LIST_REQUEST_CODE = 2;
    private static final int PRACTICE_LIST_REQUEST_CODE_ADD = 3;
    @InjectView(R.id.tv_null)
    TextView mTvNull;
    @InjectView(R.id.ll_view_info)
    LinearLayout mLlViewInfo;
    @InjectView(R.id.lv_video_list)
    ListView mLvVideoList;
    public static final String PRACTICE = "practice";
    public static final String TITLE = "title";
    public static final String INFO = "info";
    public static final String URL = "url";
    public static final String CID = "cid";
    public static final String TID = "tid";
    private static final String CATALOG = "catalog";
    private static final int MSG_FIND_SUCCESS = 0;
    private static final int MSG_FIND_FAIL = 1;
    private static final int MSG_WRITE_SUCCESS = 2;
    private static final int MSG_WRITE_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final int MSG_DOWNLOADING = 5;
    private static final int MSG_DOWNLOAD_FINISH = 6;
    private static final int MSG_DOWNLOAD_FAIL = 7;
    List<PracticeBean> practiceList = new ArrayList();
    @InjectView(R.id.bt_video_add)
    Button mBtVideoAdd;
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_SUCCESS:
                    if (practiceList != null) {
                        setList();
                    } else {
                        mLvVideoList.setVisibility(View.GONE);
                        mLlViewInfo.setVisibility(View.VISIBLE);
                        mTvNull.setText("习题列表为空");
                    }
                    break;
                case MSG_FIND_FAIL:
                    Toast.makeText(PracticeListActivity.this, "访问目录失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WRITE_SUCCESS:
                    //重新访问数据库
                    Toast.makeText(PracticeListActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    netReadPost();
                    break;
                case MSG_WRITE_FAIL:
                    Toast.makeText(PracticeListActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(PracticeListActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DOWNLOADING:
                    int progressPosition = (int) msg.obj;
                    mProgressDownload.setProgress(progressPosition);
                    mTvProgressPosition.setText(progressPosition+"%");
                    break;
                case MSG_DOWNLOAD_FINISH:
                    mTvProgressPosition.setText("下载完成");
                    Toast.makeText(PracticeListActivity.this, "下载文件完成", Toast.LENGTH_SHORT).show();
                    mDownLoadDialog.dismiss();
                    //打开文件
                    openFile();
                    break;
                case MSG_DOWNLOAD_FAIL:
                    mTvProgressPosition.setText("下载失败");
                    Toast.makeText(PracticeListActivity.this, "下载文件失败", Toast.LENGTH_SHORT).show();
                    mDownLoadDialog.dismiss();
                    break;

            }
        }
    };

    private CatalogBean catalog;
    private String mAdminType;
    private TextView mTvProgressPosition;
    private ProgressBar mProgressDownload;
    private AlertDialog mDownLoadDialog;
    private String filePath;
    private String fileType;


    private void openFile() {
        LogUtil.e("fileType-------"+fileType);
        LogUtil.e("filePath-------"+filePath);
        try{
            Intent intent = null;
            if (".doc".equals(fileType) || ".docx".equals(fileType)){
                intent = OpenFileUtil.getWordFileIntent(filePath);
            }else if (".xls".equals(fileType) || ".xlsx".equals(fileType) || ".xlt".equals(fileType) || ".xlsm".equals(fileType)){
                OpenFileUtil.getExcelFileIntent(filePath);
            }else if (".ppt".equals(fileType) || ".pps".equals(fileType) || ".pptx".equals(fileType)){
                OpenFileUtil.getPPTFileIntent(filePath);
            }else if (".txt".equals(fileType)){
                OpenFileUtil.getTextFileIntent(filePath);
            }else if (".htm".equals(fileType) || ".html".equals(fileType)){
                OpenFileUtil.getHtmlFileIntent(filePath);
            }else if (".BMP".equals(fileType) || ".GIF".equals(fileType) || fileType.contains(".JPEG") ||
                    ".PNG".equals(fileType) || "SVG".equals(fileType) || ".PSD".equals(fileType) ||
                    ".TIFF".equals(fileType) ){
                OpenFileUtil.getImageFileIntent(filePath);
            }else if (".pdf".equals(fileType)){
                OpenFileUtil.getPdfFileIntent(filePath);
            }else {
                Toast.makeText(PracticeListActivity.this, "请到文件管理器打开文件", Toast.LENGTH_SHORT).show();
            }
            if (intent != null)
                startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            //没有安装第三方的软件会提示
            Toast toast = Toast.makeText(PracticeListActivity.this, "没有找到打开该文件的应用程序", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private void setList() {
        mLlViewInfo.setVisibility(View.GONE);
        mLvVideoList.setVisibility(View.VISIBLE);
        if (mAdapter == null)
            mAdapter = new MyAdapter();
        mLvVideoList.setAdapter(mAdapter);
        mLvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转打开
                PracticeBean practiceBean = practiceList.get(i);

                String[] split = practiceBean.getUrl().split("\\.");
                fileType = "."+split[split.length - 1];
                filePath = AppConstants.FILE_DOWN_PRACTICE + File.separator + practiceBean.getTitle() + fileType;
                LogUtil.e("filePath--------"+ filePath);
                File file = new File(filePath);
                if (!file.exists()){
                    File filePractice = new File(AppConstants.FILE_DOWN_PRACTICE);
                    if (!filePractice.exists()){
                        LogUtil.e("没文件，新建文件----");
                        filePractice.mkdirs();
                    }
                    //下载它
                    downLoadDialogShow(practiceBean.getUrl(),filePath);
                }else{
                    //打开它
                    openFile();
                }
            }
        });

        mLvVideoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(AppConstants.USER_TEACHER.equals(mAdminType)){
                    String[] user = SPUtil.getUid(PracticeListActivity.this);
                    if (user != null){
                        String uid = user[0];
                        if (TextUtils.equals(uid,practiceList.get(position).getId()+"")) {
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
     * 下载对话框
     * @param url
     * @param filePath
     */
    private void downLoadDialogShow(String url, String filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_download, null);
        builder.setView(inflate);
        mTvProgressPosition = (TextView) inflate.findViewById(R.id.tv_progress_position);
        mProgressDownload = (ProgressBar) inflate.findViewById(R.id.progress_download);
        mDownLoadDialog = builder.create();
        mDownLoadDialog.show();
        LogUtil.e("url----"+url);
        downLoad(url,filePath);
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
//                String json = GsonUtil.getGson().toJson(practiceList.get(position));
                map.put(PracticeListActivity.PRACTICE,practiceList.get(position).getId());
                map.put(PracticeListActivity.CATALOG,mCid);
                netWritePost(AppConstants.PRACTICE_DELETE_URL,map);
                mDelOrUpdateDialog.dismiss();
            }
        });
        mDelOrUpdateDialog.show();
    }
    private MyAdapter mAdapter;
    private Integer mCid;
    private int mClickPosition;
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
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        mTvTitle.setText("习题列表");

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
    protected void initData() {
        //按cid查找习题
        Intent intent = getIntent();
        String json = intent.getStringExtra(CatalogActivity.CATALOG_BEAN);
        catalog = GsonUtil.parseJsonWithGson(json, CatalogBean.class);
//        catalog = (CatalogBean) intent.getSerializableExtra(CatalogActivity.CATALOG_BEAN);
        mClickPosition = intent.getIntExtra(MainActivity.CLICK_POSITION, MainActivity.POSITION_VIDEO);
        if (catalog != null) {
            mCid = catalog.getId();
            //初始化习题列表
            netReadPost();
        }

//        mLvVideoList.setAdapter(new MyAdapter());

    }

    @Override
    protected void initEvent() {
        mBtVideoAdd.setOnClickListener(this);
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()) {
            case R.id.bt_video_add:
                //添加视频
                Intent intent = new Intent(this, AddVideoActivity.class);
                intent.putExtra(CID, mCid);
                intent.putExtra(MainActivity.CLICK_POSITION, mClickPosition);
                startActivityForResult(intent, PRACTICE_LIST_REQUEST_CODE_ADD);
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
            OkHttpUtil.postJson(AppConstants.PRACTICE_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_FIND_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("Practice查寻------" + result);
                    if (!TextUtils.isEmpty(result)) {
                        LogUtil.e("查寻到拉");
                        practiceList = (List<PracticeBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<PracticeBean>>() {
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
        if (requestCode == PRACTICE_LIST_REQUEST_CODE_ADD){
            if (catalog != null){
                netReadPost();
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return practiceList.size();
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
                view = LayoutInflater.from(PracticeListActivity.this).inflate(R.layout.item_lv_video_list, null);
                holder.iv_bg = (ImageView) view.findViewById(R.id.iv_class_bg_item);
                holder.tv_title = (TextView) view.findViewById(R.id.tv_video_title);
                holder.tv_teacher = (TextView) view.findViewById(R.id.tv_video_teacher);
                holder.iv_bg.setBackgroundResource(R.mipmap.icon_practice);
                ViewGroup.LayoutParams layoutParams = holder.iv_bg.getLayoutParams();
                layoutParams.width = 40;
                layoutParams.height = 40;
                holder.iv_bg.setLayoutParams(layoutParams);
//                holder.iv_bg.setVisibility(View.GONE);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_title.setText(practiceList.get(i).getTitle());
            holder.tv_teacher.setText(practiceList.get(i).getUser().getUsername());


            return view;
        }
    }

    class ViewHolder {
        ImageView iv_bg;
        TextView tv_title;
        TextView tv_teacher;
    }

    /**
     * 从服务器下载文件
     */
    public void downLoad(final String urlPath, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    java.net.URL url = new URL(urlPath);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "utf-8");
                    con.setRequestMethod("GET");
                    int length = con.getContentLength();
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
//                    String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JavaDemo";
                            File file = new File(filePath);
                            fileOutputStream = new FileOutputStream(file);//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            long count=0;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                count += ch;
                                int progressPosition = (int) (((float) count / length) * 100);
                                Message msg = Message.obtain();
                                msg.what = MSG_DOWNLOADING;
                                msg.obj = progressPosition;
                                handler.sendMessage(msg);
                            }
                            handler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        }
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(MSG_DOWNLOAD_FAIL);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
