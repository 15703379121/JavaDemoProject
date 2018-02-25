package com.example.administrator.javademo.view.find;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.activity.BaseActivity;
import com.example.administrator.javademo.activity.VideoActivity;
import com.example.administrator.javademo.bean.CatalogBean;
import com.example.administrator.javademo.bean.CommentSecondBean;
import com.example.administrator.javademo.bean.CommentinfoBean;
import com.example.administrator.javademo.bean.InformationBean;
import com.example.administrator.javademo.bean.UserBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.GsonUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.view.ListViewForScrollView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/8/10.
 * 发现的评论布局
 */

public class FindPlView {

    private static final int COMMENT_LIST_DATA = 0;
//    private static final int MSG_SEND_COMMENT = 1;
    private static final int DELETE_COMMENT = 2;
    private static final int DELETE_COMMENT_FAIL = 3;
//    private static final int MSG_FIND_SUCCESS = 4;
//    private static final int MSG_FIND_FAIL = 5;
    private static final int MSG_WRITE_SUCCESS = 6;
    private static final int MSG_WRITE_FAIL = 7;
    private static final int MSG_NET_FAIL = 8;
//    private static final int MSG_WRITE_SUCCESS_COMMENT_SECOND = 9;
//    private static final int MSG_WRITE_SUCCESS_COMMENTINFO = 10;
    private final Activity mContext;
    private final int informationId;//热门推荐id
    private final Integer mUid;//用户昵称
    private final TextView tv_pinglun;
    private final InformationBean listBean;
    public View mRootView;
    private RecyclerView rv_pinglun;
    private EditText et_find_pinglun;
    private TextView tv_send;
    private List<CommentinfoBean> commentList;
    private CommentAdapter commentAdapter;
    private PopupWindow pop;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case COMMENT_LIST_DATA:
                   parseCommentData();
                   break;
//               case MSG_SEND_COMMENT:
//                   if (commentAdapter==null){
//                       commentAdapter = new CommentAdapter();
//                       rv_pinglun.setLayoutManager(new LinearLayoutManager(mContext));
//                       rv_pinglun.setAdapter(commentAdapter);
//                       //清空edittext
//                       et_find_pinglun.setText("");
//                       //刷新评论数量
//                       tv_pinglun.setText("评论 ("+(listBean.getCommentinfoSize()+1)+")");
//                   }else{
//                       commentAdapter.notifyDataSetChanged();
//                       //清空edittext
//                       et_find_pinglun.setText("");
//                       //刷新评论数量
//                       tv_pinglun.setText("评论 ("+(listBean.getCommentinfoSize()+1)+")");
//                   }
//                   break;
//               case DELETE_COMMENT:
//                   int position = msg.arg1;
//                   commentList.remove(position);
//                   commentAdapter.notifyDataSetChanged();
//                   //刷新评论数量
//                   int i = listBean.getCommentinfoSize() - 1;
//                   if (i<0){
//                       i=0;
//                   }
//                   tv_pinglun.setText("评论 ("+i+")");
//                   if (pop!=null){
//                       pop.dismiss();
//                   }
//                   break;
//               case DELETE_COMMENT_FAIL:
//                   Toast.makeText(mContext, "删除失败，请稍后再试", Toast.LENGTH_SHORT).show();
//                   break;
//               case MSG_FIND_FAIL:
//                   Toast.makeText(mContext, "访问目录失败", Toast.LENGTH_SHORT).show();
//                   break;
               case MSG_WRITE_SUCCESS:
                   et_find_pinglun.setText("");
                   //重新访问数据库
//                   Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                   netReadPost();
                   break;
               case MSG_WRITE_FAIL:
                   Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                   break;
               case MSG_NET_FAIL:
                   Toast.makeText(mContext, "访问网络失败", Toast.LENGTH_SHORT).show();
                   break;
//               case MSG_WRITE_SUCCESS_COMMENT_SECOND:
//                   Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
//                   //TODO 更新二次评论列表
//                   //获取网络评论
//                   netReadPost();
//
//                   break;
//               case MSG_WRITE_SUCCESS_COMMENTINFO:
//                   Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
//                   //TODO 更新评论列表
//                   //获取网络评论
//                   netReadPost();
//                   break;
           }
        }
    };
    public FindPlView(Activity context, List<CommentinfoBean> list,int informationId, String pname, TextView tv_pinglun, InformationBean listBean){
        this.mContext = context;
        this.commentList = list;
        this.informationId = informationId;
        this.mUid = Integer.parseInt(pname);
        this.tv_pinglun = tv_pinglun;
        this.listBean = listBean;
        initView();
        initData();
        initEvent();

    }
    private void initView(){
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.find_pinglun, null);
        rv_pinglun = (RecyclerView) mRootView.findViewById(R.id.rv_find_pinglun);
        et_find_pinglun = (EditText) mRootView.findViewById(R.id.et_find_pinglun);
        tv_send = (TextView) mRootView.findViewById(R.id.tv_send);

    }
    private void initData(){
        //TODO 初始化评论列表
        //本地评论
        parseCommentData();
        //获取网络评论
        netReadPost();


    }

    private void netReadPost() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("information",informationId);
        try {
            OkHttpUtil.postJson(AppConstants.COMMENT_INFORMATION_FIND_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("评论列表======"+result);
                    if (!TextUtils.isEmpty(result) && !TextUtils.equals(AppConstants.FAIL,result)){
                        //评论列表
                        commentList = (List<CommentinfoBean>) GsonUtil.parseJsonToList(result, new TypeToken<List<CommentinfoBean>>() {
                        }.getType());
                        handler.sendEmptyMessage(COMMENT_LIST_DATA);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCommentData() {
        if (commentList!=null ) {
            //刷新评论数量
            tv_pinglun.setText("评论 ("+(commentList.size())+")");
            if(commentList.size()>0){
                if (commentAdapter == null) {
                    rv_pinglun.setLayoutManager(new LinearLayoutManager(mContext));
                    commentAdapter = new CommentAdapter();
                    rv_pinglun.setAdapter(commentAdapter);
                }else{
                    commentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void initEvent() {
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加评论
                String info = et_find_pinglun.getText().toString().trim();
                if (TextUtils.isEmpty(info)){
                    Toast.makeText(mContext, "未添加评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("information",informationId);
                map.put("user",mUid);
                map.put("info",info);
                netWritePostComment(AppConstants.COMMENT_INFORMATION_SAVE_URL,map);
            }
        });
    }

    /**
     * 发送评论
     */
//    private void sendComment() {
//        final String comment = et_find_pinglun.getText().toString().trim();
//
//        //TODO 发送评论的map
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("tid",informationId+"");
//        map.put("uid",mUid);
//        map.put("pcontent",comment);
//        try {
//            OkHttpUtil.postJson(AppConstants.COMMENT_INFORMATION_SAVE_URL, map, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//
//                }
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    //评论的数据
//                    /*String result = OkHttpUtil.getResult(response);
//                    LogUtil.e("发表评论======"+result);
//                    if (result!=null){
//                        try {
//                            JSONObject object = new JSONObject(result);
//                            String msg = object.getString("msg");
//                            int id = object.getInt("id");
//                            if (msg!=null && msg.contains("成功")){
//                                FindCommentBean.DataBean dataBean = new FindCommentBean.DataBean();
//                                dataBean.setPcontent(comment);
//                                dataBean.setUid(SPUtil.getUid(mContext)[0]);//uid
//
//                                dataBean.setId(id);//该条评论的id
//                                //SPUtil
//                                dataBean.setPname(SPUtil.getUid(mContext)[2]);//昵称
//                                dataBean.setTid(mTid);
//                                if (commentList==null) {
//                                    commentList = new ArrayList<>();
//                                }
//                                commentList.add(0,dataBean);
//                                //刷新界面
//                                handler.sendEmptyMessage(MSG_SEND_COMMENT);
//                            }else{
//                                ToastHelper.getInstance().displayToastShort("评论失败，请稍后再试");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }*/
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 添加评论
     */
    private void addComment(final String url, final HashMap<String,Object> map) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment, null);
        final EditText et_comment = (EditText) inflate.findViewById(R.id.et_comment);
        builder.setView(inflate);
        final android.app.AlertDialog dialog = builder.create();
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
                    Toast.makeText(mContext, "未填写评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put("info",comment);
                netWritePostComment(url,map);
                dialog.dismiss();
            }
        });


    }
    private void netWritePostComment(final String url, HashMap<String, Object> map) {
        try {
            OkHttpUtil.postJson(url, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_WRITE_FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e(url+"-----Comment写操作------" + result);
                    if (!TextUtils.isEmpty(result) && AppConstants.SUCCESS.equals(result)) {
                        //评论成功
                        handler.sendEmptyMessage(MSG_WRITE_SUCCESS);
//                        if(TextUtils.equals(AppConstants.COMMENT_SECOND_SAVE_URL,url)){
//                            //二次评论
//                            handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENT_SECOND);
//                        }else if(TextUtils.equals(AppConstants.COMMENT_INFORMATION_SAVE_URL,url)){
//                            //说说评论
//                            handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENTINFO);
//                        }
//                        handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENT);
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
    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment_list, parent,false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final CommentinfoBean commentinfoBean = commentList.get(position);
            holder.tv_pname.setText(commentinfoBean.getUser().getUsername()+":");
            holder.tv_content.setText(commentinfoBean.getContent());
            Timestamp timestamp = new Timestamp(commentinfoBean.getCreateTime());
            holder.tv_time.setText(FileUtil.getName(timestamp.toString())+"");

            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加评论
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("commentinfo",commentinfoBean.getId());
                    map.put("uSend",mUid);
                    map.put("uReceive",commentinfoBean.getUser().getId());
                    addComment(AppConstants.COMMENT_SECOND_SAVE_URL,map);
                }
            });

            holder.ll_comment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    UserBean userBean = commentinfoBean.getUser();
                    if (userBean.getId() == mUid){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("是否删除");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //删除评论
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("information",informationId);
                                map.put("commentinfo",commentinfoBean.getId());
                                try {
                                    OkHttpUtil.postJson(AppConstants.COMMENT_INFORMATION_DELETE_URL, map, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            handler.sendEmptyMessage(MSG_WRITE_FAIL);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (TextUtils.equals(AppConstants.SUCCESS,OkHttpUtil.getResult(response))){
                                                //成功
//                                                handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENTINFO);

                                                handler.sendEmptyMessage(MSG_WRITE_SUCCESS);
                                            }else{
                                                handler.sendEmptyMessage(MSG_WRITE_FAIL);
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    handler.sendEmptyMessage(MSG_NET_FAIL);
                                    e.printStackTrace();
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    }
                    return true;
                }
            });
            final List<CommentSecondBean> commentSecondList = commentinfoBean.getCommentSecond();
            holder.lv_comment_second.setAdapter(new CommentSecondAdapter(commentSecondList));

            /**
             * 点击添加回复
             */
            holder.lv_comment_second.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //回复给谁
                    UserBean userBean = commentSecondList.get(position).getuSend();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("commentinfo",commentinfoBean.getId());
                    map.put("uSend",mUid);
                    map.put("uReceive",userBean.getId());
                    addComment(AppConstants.COMMENT_SECOND_SAVE_URL,map);
                }
            });

            /**
             * 长按删除评论
             */
            holder.lv_comment_second.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    UserBean userBean = commentSecondList.get(position).getuSend();
                    if (userBean.getId() == mUid){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("是否删除");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //删除评论
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("commentinfo",commentinfoBean.getId());
                                map.put("commentSecond",commentSecondList.get(position).getId());
                                try {
                                    OkHttpUtil.postJson(AppConstants.COMMENT_SECOND_DELETE_URL, map, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            handler.sendEmptyMessage(MSG_WRITE_FAIL);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (TextUtils.equals(AppConstants.SUCCESS,OkHttpUtil.getResult(response))){
                                                //成功
//                                                handler.sendEmptyMessage(MSG_WRITE_SUCCESS_COMMENT_SECOND);

                                                handler.sendEmptyMessage(MSG_WRITE_SUCCESS);
                                            }else{
                                                handler.sendEmptyMessage(MSG_WRITE_FAIL);
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    handler.sendEmptyMessage(MSG_NET_FAIL);
                                    e.printStackTrace();
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_pname;
            private TextView tv_content;
            private LinearLayout ll_comment;
            private final ListViewForScrollView lv_comment_second;
            private final TextView tv_time;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_pname = (TextView) itemView.findViewById(R.id.tv_pname);
                tv_content = (TextView) itemView.findViewById(R.id.tv_content);
                tv_time = (TextView) itemView.findViewById(R.id.tv_time);
                ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
                lv_comment_second = (ListViewForScrollView) itemView.findViewById(R.id.lv_comment_second);
            }
        }
    }

    class CommentSecondAdapter extends BaseAdapter{


        private final List<CommentSecondBean> commentSecondList;

        public CommentSecondAdapter(List<CommentSecondBean> commentSecondList) {
            this.commentSecondList = commentSecondList;
        }

        @Override
        public int getCount() {
            return commentSecondList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentSecondHolder holder = null;
            if (convertView == null){
                holder = new CommentSecondHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment_second_list, null);
                holder.tv_name1 = (TextView) convertView.findViewById(R.id.tv_pname1);
                holder.tv_name2 = (TextView) convertView.findViewById(R.id.tv_pname2);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            }else{
                holder = (CommentSecondHolder) convertView.getTag();
            }
            CommentSecondBean commentSecondBean = commentSecondList.get(position);
            holder.tv_name1.setText(commentSecondBean.getuSend().getUsername());
            holder.tv_name2.setText(commentSecondBean.getuReceive().getUsername()+":");
            Timestamp timestamp = new Timestamp(commentSecondBean.getCreateTime());
            holder.tv_time.setText(FileUtil.getName(timestamp.toString())+"");
            holder.tv_content.setText(commentSecondBean.getContent());
            return convertView;
        }

        class CommentSecondHolder{
            TextView tv_name1;
            TextView tv_name2;
            TextView tv_time;
            TextView tv_content;
        }
    }

//    private void showDelePop(View view, final int position, final int id) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage("是否删除您的该条评论？");
//        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                try {
//                       deleteComment(position,id);
//                   } catch (IOException e) {
//                       e.printStackTrace();
//                   }
//            }
//        });
//        builder.setPositiveButton("取消",null);
//        builder.show();
//    }
    private void deleteComment(final int position, int id) throws IOException {
        //TODO 删除列表Map
        HashMap<String, Object> map = new HashMap<>();
        LogUtil.e("删除评论==uid"+mUid);
        map.put("pid",id+"");
        map.put("uid",mUid);
        OkHttpUtil.postJson(AppConstants.COMMENT_INFORMATION_DELETE_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("删除评论==="+result);
                if (result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String msg = jsonObject.getString("msg");
                        Message message = Message.obtain();
                        if (msg.contains("成功")){
                            message.what = DELETE_COMMENT;
                            message.arg1 = position;
                        }else{
                            message.what = DELETE_COMMENT_FAIL;
                        }
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
