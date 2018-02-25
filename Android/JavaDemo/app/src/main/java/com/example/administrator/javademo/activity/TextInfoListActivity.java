package com.example.administrator.javademo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.TextBean;
import com.example.administrator.javademo.bean.UserBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.DialogUtil;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class TextInfoListActivity extends Activity {

    private static final int FINISH_TEXT = 0;
    private static final int MSG_SAVEALL_SUCCESS = 2;
    private static final int MSG_SAVEALL_FAIL = 3;
    private static final int MSG_NET_FAIL = 4;
    private static final int MSG_FILESPLIT_FAIL = 5;
    private List<TextBean> list = new ArrayList<>();
    private File file;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FINISH_TEXT:
                    if (list != null && list.size()>0){
                        showList();
                    }else{
                        showText();
                    }
                    break;
                case MSG_SAVEALL_SUCCESS:
                    //添加成功
                    mProgressDialog.dismiss();
                    Integer count = (Integer) (msg.obj);
                    if (count==0) {
                        Toast.makeText(TextInfoListActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(TextInfoListActivity.this, "含"+count+"条重复数据未添加", Toast.LENGTH_LONG).show();
                    }
                    break;
                case MSG_SAVEALL_FAIL:
                    //添加失败
                    mProgressDialog.dismiss();
                    Toast.makeText(TextInfoListActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    //访问网络失败
                    mProgressDialog.dismiss();
                    Toast.makeText(TextInfoListActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FILESPLIT_FAIL:
                    //文件切割结果为空
                    mProgressDialog.dismiss();
                    Toast.makeText(TextInfoListActivity.this, "文件格式不正确，请重新选择文件", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private ListView lv_text;
    private TextView tv_tag;
    private String mAdminType;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);
        sdPermission(this);
        file = new File(AppConstants.FILE_TXT);
        if (!file.exists()){
            file.mkdirs();
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                traverseFolder(file);
                handler.sendEmptyMessage(FINISH_TEXT);
            }
        }.start();
        initView();

    }

    private void initView() {
        mAdminType = getIntent().getStringExtra(MainActivity.INTENT_DATA_ADMIN_TYPE);
        lv_text = (ListView) findViewById(R.id.lv_text);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
    }

    private void showText() {
        tv_tag.setText("对不起，没有找到txt文件");
    }

    private void showList() {
        tv_tag.setVisibility(View.GONE);
        lv_text.setVisibility(View.VISIBLE);
        lv_text.setAdapter(new MyAdapter());
        lv_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder dialog =  new AlertDialog.Builder(TextInfoListActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定要要添加文本中的所有用户吗？");
                dialog.setIcon(R.mipmap.dialog_add);
                dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,  int i) {
                        dialogInterface.dismiss();
                        mProgressDialog = DialogUtil.initProgress(TextInfoListActivity.this,"正在处理中。。。");
                        //添加所有用户
                        //切割文件
                        List<UserBean> userList = FileUtil.splitFile(list.get(position).getFile_txt_path(), mAdminType);
                        if (!userList.isEmpty()){
                            Map<String,Object> map = new HashMap<>();
                            String json = GsonUtil.getGson().toJson(userList);
                            map.put("userList",json);
                            LogUtil.e("json----"+json);
                            try {
                                OkHttpUtil.postJson(AppConstants.USER_SAVE_ALL_URL, map, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        LogUtil.e("获取数据失败");
                                        handler.sendEmptyMessage(MSG_SAVEALL_FAIL);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result = OkHttpUtil.getResult(response);
                                        LogUtil.e("result-----"+result);
                                        if (!TextUtils.isEmpty(result)){
                                            if (result.contains(AppConstants.SUCCESS)){
                                                //返回类型：success-0;
                                                Message msg = Message.obtain();
                                                String[] split = result.split("-");
                                                if (split != null && split.length == 2){
                                                    LogUtil.e("保存成功");
                                                    msg.what = MSG_SAVEALL_SUCCESS;
                                                    LogUtil.e(split[1]);
                                                    LogUtil.e("count---"+Integer.parseInt(split[1]));
                                                    msg.obj = Integer.parseInt(split[1]);
                                                    handler.sendMessage(msg);
                                                }else{
                                                    handler.sendEmptyMessage(MSG_SAVEALL_FAIL);
                                                }
                                            }else{
                                                LogUtil.e("保存失败");
                                                handler.sendEmptyMessage(MSG_SAVEALL_FAIL);
                                            }
                                        }else{
                                            LogUtil.e("返回值为null");
                                            handler.sendEmptyMessage(MSG_SAVEALL_FAIL);
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                LogUtil.e("访问网络失败");
                                handler.sendEmptyMessage(MSG_NET_FAIL);
                                e.printStackTrace();
                            }
                        }else{
                            handler.sendEmptyMessage(MSG_FILESPLIT_FAIL);
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }


    public void traverseFolder(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder(file2);
                    } else {
                        if (file2.getName().endsWith(".txt")) {//格式为txt文件
                            //获取并计算文件大小
                            long size = file2.length();
                            String t_size = "";
                            if (size <= 1024) {
                                t_size = size + "B";
                            } else if (size > 1024 && size <= 1024 * 1024) {
                                size /= 1024;
                                t_size = size + "KB";
                            } else {
                                size = size / (1024 * 1024);
                                t_size = size + "MB";
                            }
                            TextBean bean = new TextBean(t_size, file2.getName(), file2.getAbsolutePath());
                            list.add(bean);
                        }
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    class MyAdapter extends BaseAdapter{
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
            ViewHolder holder;
            if (view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(TextInfoListActivity.this).inflate(R.layout.item_lv_info, null);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_file_name);
                holder.tv_path = (TextView) view.findViewById(R.id.tv_file_path);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_file_size);
                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            holder.tv_name.setText(list.get(i).getFile_name()+"");
            holder.tv_path.setText(list.get(i).getFile_txt_path()+"");
            holder.tv_size.setText(list.get(i).getFile_size()+"");
            return view;
        }
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_path;
        TextView tv_size;
    }
    Object invokeVolumeList = null;
    Method getVolumeList= null;
    StorageManager mStorageManager;

    private void sdPermission(Context context) {
        mStorageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        try {
            getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            try {
                final Class<?>storageValumeClazz = Class.forName("android.os.storage.StorageVolume");
                final Method getPath= storageValumeClazz.getMethod("getPath");
                Method isRemovable = storageValumeClazz.getMethod("isRemovable");
                Method mGetState = null;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    try {
                        mGetState = storageValumeClazz.getMethod("getState");
                    } catch(NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    invokeVolumeList= getVolumeList.invoke(mStorageManager);
                }catch (Exception e) {
                }
                final int length = Array.getLength(invokeVolumeList);
                for(int i = 0; i<length ;i++) {
                    final Object storageValume= Array.get(invokeVolumeList, i);//
                    final String path =(String) getPath.invoke(storageValume);
                    final boolean removable =(Boolean) isRemovable.invoke(storageValume);
                    String state = null;
                    if (mGetState !=null) {
                        state = (String) mGetState.invoke(storageValume);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG,"couldn't talkto MountService", e);
            }
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
