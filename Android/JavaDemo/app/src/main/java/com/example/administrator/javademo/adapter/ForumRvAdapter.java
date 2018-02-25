package com.example.administrator.javademo.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.bean.CommentinfoBean;
import com.example.administrator.javademo.bean.InformationBean;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;
import com.example.administrator.javademo.view.find.FindPlView;
import com.fire.photoselector.utils.ScreenUtil;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridlayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 李晓曼 on 2017/8/5.
 */

public class ForumRvAdapter extends RecyclerView.Adapter<ForumRvAdapter.MyViewHolder> {

    private final Activity context;
    private List<InformationBean> list;

    private OnVideoClickListener itemListener;

    private OnGvItemClickListener gvItemListener;
    private int my_uid;
    private HashMap<Integer, Boolean> is_comment = new HashMap<>();//每个条目评论展开的情况
//    private HashMap<Integer, Integer> zanCountMap = new HashMap<>();//每个条目点赞展开的情况

    public ForumRvAdapter(Activity context, List<InformationBean> list) {
        this.context = context;
        this.list = list;
        String s = "";
        if (SPUtil.getUid(context) != null) {
            s = SPUtil.getUid(context)[0];
        }
        my_uid = 0;
        if (!TextUtils.isEmpty(s)) {
            my_uid = Integer.parseInt(s);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rv_forum, null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int listPostion = position;
        final InformationBean informationBean = list.get(position);
        int tid = informationBean.getId();//热门评论的id
        //是否是自己的说说
        int informationUid = informationBean.getUser().getId();

        if (my_uid == informationUid) {
            holder.tv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.tv_delete.setVisibility(View.INVISIBLE);
        }
        //初始化评论
        for (int i = 0; i < list.size(); i++) {
            is_comment.put(i, false);
        }
        final List<CommentinfoBean> commentinfoList = informationBean.getCommentinfo();

        //回显是否点赞
        informationBean.voteinfoByUserShow(context,my_uid,holder.iv_dianzan);

        // 回显是否收藏
       /* for (int i = 0; i < collectList.size(); i++) {
            if (collectList.get(i).getSign() == 0) {
                if (collectList.get(i).getWid() == tid) {
                    holder.iv_shoucang.setSelected(true);
                    break;
                }
            }
        }*/
        //其它设置
        holder.tv_content_item.setText(informationBean.getInfo());//发布内容
        holder.tv_name_item.setText(informationBean.getUser().getUsername());//用户昵称
        Timestamp timestamp = new Timestamp(informationBean.getCreateTime());
        holder.tv_time_item.setText(FileUtil.getName(timestamp.toString()) );//发布时间
        holder.tv_pinglun.setText("评论 (" + informationBean.getCommentinfoSize() + ")");//评论
        holder.tv_dianzan.setText("点赞 (" + informationBean.getVoteinfoSize() + ")");//点赞

        int type = informationBean.getType();
        String url = informationBean.getUrl();
        String[] split;
        switch (type){
            case -1:
                //纯文本
                holder.gv_item.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.GONE);
                break;
            case 0:
                //图片
                holder.gv_item.setVisibility(View.VISIBLE);
                holder.rl_video.setVisibility(View.GONE);
                //是图片
                if (url.endsWith(";")) {
                    url = url.substring(0, url.length() - 1);
                }
                split = url.split(";");
                List<String> imageList = Arrays.asList(split);
                FindPicGvAdapter adapter = new FindPicGvAdapter(context, imageList);
                holder.gv_item.setAdapter(adapter);
                holder.gv_item.setDefaultWidth(ScreenUtil.dp2px(context, 300));
                holder.gv_item.setDefaultHeight(ScreenUtil.dp2px(context, 200));
                holder.gv_item.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if (gvItemListener != null) {
                            gvItemListener.onClickListener(view, position, listPostion);
                        }
                    }
                });

                break;
            case 1:
                //视频
                holder.gv_item.setVisibility(View.GONE);
                holder.rl_video.setVisibility(View.VISIBLE);
                //获取视频缩略图
                if (url.endsWith(";")) {
                    url = url.substring(0, url.length() - 1);
                }
                split = url.split(";");
                final String s1 = split[0];
                String s2 = "";
                if (split.length>1){
                    s2 = split[1];
                }
                if (s1.endsWith(".PNG") ||s1.endsWith(".png") ||s1.endsWith(".JPG")||s1.endsWith(".jpg")){
                    //s1是图片，s2是视频
                    Picasso.with(context).load(s1).fit().into(holder.vv_item);
                    // videoPath = s2;
                    final String finalS = s2;
                    holder.rl_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemListener != null) {

                                itemListener.onClickListener(view, position, finalS,s1);
                            }
                        }
                    });

                }else{
                    //s2是图片，s1是视频
                    Picasso.with(context).load(s2).fit().into(holder.vv_item);
                    // videoPath = s1;
                    final String finalS1 = s2;
                    holder.rl_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemListener != null) {
                                itemListener.onClickListener(view, position, s1, finalS1);
                            }
                        }
                    });
                }
                break;
        }


        /**点赞*/
        holder.ll_dianzan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    postZan(informationBean, holder.iv_dianzan, holder.tv_dianzan);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        /**收藏*/
        /*holder.ll_shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    postCollect(informationBean, holder.iv_shoucang, holder.tv_shoucang);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        /**评论*/
        holder.ll_pinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //展示评论列表
                if (is_comment.get(position)) {
                    //展开----收回
                    holder.fl_pinglun.removeAllViews();
                    is_comment.put(position, false);
                } else {
                    is_comment.put(position, true);
                    String[] uids = SPUtil.getUid(context);
                    String uid = "";
                    if (uids != null) {
                        uid = uids[0];
                    }
                    FindPlView findPlView = new FindPlView(context,list.get(position).getCommentinfo(), list.get(position).getId(), uid, holder.tv_pinglun, informationBean);
                    holder.fl_pinglun.addView(findPlView.mRootView);
                }
            }
        });
        /**
         * 删除说说
         */
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("是否删除");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            deleteShuoshuo(position, list.get(position).getId(), list.get(position).getUser().getId());
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
     * @param position
     * @param informationId
     * @param uid
     */
    private void deleteShuoshuo(final int position, int informationId, int uid) throws IOException {
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
                LogUtil.e("delete shuoshuo----"+result);
                if (!TextUtils.isEmpty(result) && TextUtils.equals(AppConstants.SUCCESS,result)) {
                    //成功
                    //刷新页面
                    list.remove(position);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_name_item; //用户名
        private final TextView tv_content_item;//内容
        private final NineGridlayout gv_item;//图片
        private final ImageView vv_item; //视频
        private final TextView tv_time_item; // 发布时间
        private final LinearLayout ll_pinglun;
        private final LinearLayout ll_dianzan;
        private final LinearLayout ll_shoucang;
        private final TextView tv_pinglun;
        private final TextView tv_dianzan;
        private final TextView tv_shoucang;
        private final ImageView tv_delete;
        private final ImageView iv_pinglun;
        private final ImageView iv_shoucang;
        private final ImageView iv_dianzan;
        private final FrameLayout fl_pinglun;
        private final RelativeLayout rl_video;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name_item = (TextView) itemView.findViewById(R.id.tv_find_hottuijian_item_name);
            tv_time_item = (TextView) itemView.findViewById(R.id.tv_find_hottuijian_item_time);
            tv_content_item = (TextView) itemView.findViewById(R.id.tv_item_content);
            gv_item = (NineGridlayout) itemView.findViewById(R.id.gv_find_hottuijian_item);
            vv_item = (ImageView) itemView.findViewById(R.id.video_find_hottuijian_item);
            ll_pinglun = (LinearLayout) itemView.findViewById(R.id.ll_pinglun);
            ll_dianzan = (LinearLayout) itemView.findViewById(R.id.ll_dianzan);
            ll_shoucang = (LinearLayout) itemView.findViewById(R.id.ll_shoucang);
            tv_pinglun = (TextView) itemView.findViewById(R.id.tv_pinglun);
            tv_shoucang = (TextView) itemView.findViewById(R.id.tv_shoucang);
            tv_dianzan = (TextView) itemView.findViewById(R.id.tv_dianzan);
            iv_pinglun = (ImageView) itemView.findViewById(R.id.iv_pinglun);
            iv_shoucang = (ImageView) itemView.findViewById(R.id.iv_shoucang);
            iv_dianzan = (ImageView) itemView.findViewById(R.id.iv_dianzan);
            fl_pinglun = (FrameLayout) itemView.findViewById(R.id.fl_find_pinglun);
            rl_video = (RelativeLayout) itemView.findViewById(R.id.rl_video);
            tv_delete = (ImageView) itemView.findViewById(R.id.find_item_delete);

            iv_shoucang.setVisibility(View.GONE);
            tv_shoucang.setVisibility(View.GONE);
        }
    }

    public void setOnVideoClickListener(OnVideoClickListener itemListener) {
        this.itemListener = itemListener;
    }


    public interface OnVideoClickListener {
        void onClickListener(View v, int position, String picPath, String finalS1);
    }

    /**
     * 九宫格图片条目点击事件
     */
    public void setOnGvItemClickListener(OnGvItemClickListener itemListener) {
        this.gvItemListener = itemListener;
    }


    public interface OnGvItemClickListener {
        void onClickListener(View v, int position, int listPostion);
    }


    /**
     * 点赞
     */
    private void postZan(final InformationBean bean, final ImageView iv_dianzan, final TextView tv_dianzan) throws IOException {
        String[] uid = SPUtil.getUid(context);
        if (uid == null) {
            Toast.makeText(context, "请登录后操作", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO 点赞
        final HashMap<String, Object> map = new HashMap<>();
        map.put("information", bean.getId());
        map.put("user", Integer.parseInt(uid[0]));
        OkHttpUtil.postJson(AppConstants.VOTE_INFORMATION_UPDATE_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("点赞=================" + result);
                if (TextUtils.isEmpty(result) || !TextUtils.equals(AppConstants.FAIL,result)) {
                    //成功
                    final String[] split = result.split(":");
                    if (split.length == 3){
                        //split[0] = success ,split[1] = state(0无赞,1有赞), split[2] = voteinfoSize 点赞量
                        boolean flag = false;
                        if (TextUtils.equals("0",split[1])){
                            //无赞
                            flag = false;
                        }else if (TextUtils.equals("1",split[1])){
                            //赞
                            flag = true;
                        }
                        final boolean finalFlag = flag;
                        context.runOnUiThread(new Runnable() {
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

    /**
     * 收藏
     *  @param bean
     * @param iv_shoucang
     * @param tv_shoucang
     */
    /*private void postCollect(final InformationBean bean, final ImageView iv_shoucang, final TextView tv_shoucang) throws IOException {
        *//*String[] uid = SPUtil.getUid(context);
        if (uid == null) {
            Toast.makeText(context, "请登录后操作", Toast.LENGTH_SHORT).show();
            return;
        }
        final HashMap<String, String> map = new HashMap<>();
        map.put("wid", bean.getId() + "");//说说id
        map.put("uid", uid[0]);//用户id
        map.put("sign", "0");//标记 0 是热门推荐收藏

        OkHttpUtil.postString(AppConstants.COLLECT_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e(response + "==热门推荐收藏===" + result);
                if (result != null) {
                    try {
                        JSONObject object = new JSONObject(result);
                        final String msg = object.getString("msg");
                        if (!TextUtils.isEmpty(msg) && msg.contains("成功")) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    int id = bean.getId();
//                                    Integer count = zanCountMap.get(id);
                                    if (msg.contains("取消")) {
                                        //取消收藏
                                        iv_shoucang.setSelected(false);

                                    } else {
                                        //收藏
                                        iv_shoucang.setSelected(true);
                                    }
                                }
                            });
                        }
                        if (!TextUtils.isEmpty(msg) && msg.contains("失败")) {
                            ToastHelper.getInstance().displayToastShort("收藏失败，请稍后再试");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastHelper.getInstance().displayToastShort("收藏失败，请稍后再试");
                        }
                    });
                }
            }
        });*//*
    }*/

    public void setList(List<InformationBean> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
