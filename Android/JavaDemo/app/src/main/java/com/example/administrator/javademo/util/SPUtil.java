package com.example.administrator.javademo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.administrator.javademo.bean.DownLoadBean;
import com.example.administrator.javademo.bean.NewsBean;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
/**
 * Created by hegeyang on 2017/3/25 0025 .
 */

public class SPUtil {
	private static final String SPNAME ="javaDemo" ;
	private static SharedPreferences sp;
	public static void putBoolean(String key, boolean value, Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key,value).commit();
	}

	public static boolean getBoolean(String key,Context context){
		if (sp==null) {
			sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
		}
		boolean b = sp.getBoolean(key, false);
		return b;
	}


	//写入数据
	public static void putString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	// 读取数据
	public static String getString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		return sp.getString(key, value);
	}

    //写入数据
    public static void putInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    // 读取数据
    public static int getInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        return sp.getInt(key, value);
    }

	public static void deleteSp(Context context){
		if (sp == null) {
			sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
		}
		sp.edit().clear().commit();
	}

    public static String[] getUid(Context context){
        String str = SPUtil.getString(context, AppConstants.SP_LOGIN, "");
        if(TextUtils.isEmpty(str)){
            return null;
        }
        String[] s=new String[5];
        try {
            JSONObject jsonObject = new JSONObject(str);
            String id = jsonObject.getString("id");
            String identifier=jsonObject.getString("identifier");
            String username=jsonObject.getString("username");
            String password=jsonObject.getString("password");
            String type=jsonObject.getString("type");
            s[0]=id;
            s[1]=identifier;
            s[2]=username;
            s[3]=password;
            s[4]=type;
        } catch (JSONException e) {
            LogUtil.e("登陆信息解析错误" + e.toString());
            return null;
        }
        return s;
    }

    public static List<NewsBean> getNewsList(Context context){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.SP_CACHE_NEWS, "");
        if(TextUtils.isEmpty(str)){
            return null;
        }
        return (List<NewsBean>) GsonUtil.parseJsonToList(str, new TypeToken<List<NewsBean>>() {
        }.getType());
    }

    public static void setNewsList(Context context,List<NewsBean> list){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.SP_CACHE_NEWS, "");
        LogUtil.e("str----"+str);
        String jsonList = "";
        if(TextUtils.isEmpty(str)){
            jsonList = GsonUtil.getGson().toJson(list);
            LogUtil.e("jsonList str-null----"+jsonList);
        }else{
            List<NewsBean> newsList = getNewsList(context);
            LogUtil.e("jsonList newsList.size()1----"+newsList.size());
            newsList.addAll(0,list);
            LogUtil.e("newsList.size()2----"+newsList.size());
            jsonList = GsonUtil.getGson().toJson(newsList);
            LogUtil.e("jsonList str-----"+jsonList);
        }
        sp.edit().putString(AppConstants.SP_CACHE_NEWS, jsonList).commit();
    }


    public static List<DownLoadBean> getVideoDownloadList(Context context){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.VIDEO_DOWNLOAD_LIST, "");
        if(TextUtils.isEmpty(str)){
            return null;
        }
        return (List<DownLoadBean>) GsonUtil.parseJsonToList(str, new TypeToken<List<DownLoadBean>>() {
        }.getType());
    }

    public static void setVideoDownloadList(Context context,DownLoadBean bean){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        String str = SPUtil.getString(context, AppConstants.VIDEO_DOWNLOAD_LIST, "");
        if(TextUtils.isEmpty(str)){
            String json = GsonUtil.getGson().toJson(bean);
            sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, "["+json+"]").commit();
            Toast.makeText(context, "添加到下载列表", Toast.LENGTH_SHORT).show();
        }else{
            List<DownLoadBean> videoDownloadList = getVideoDownloadList(context);
            int position = -1;
            for (int i = 0; i < videoDownloadList.size(); i++) {
                if (bean == null) {
                    if(videoDownloadList.get(i) == null){
                        //是这个
                        position = i;
                        break;
                    }
                }else if (bean.toString().equals(videoDownloadList.get(i).toString())){
                    position = i;
                    break;
                }
            }
            if (position != -1){
                //已存在
                Toast.makeText(context, "此视频已在下载列表", Toast.LENGTH_SHORT).show();
            }else{
                videoDownloadList.add(bean);
                String jsonList = GsonUtil.getGson().toJson(videoDownloadList);
                sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, jsonList).commit();
                Toast.makeText(context, "添加到下载列表", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void removeVideoDownloadList(Context context,DownLoadBean bean){
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, context.MODE_PRIVATE);
        }
        List<DownLoadBean> videoDownloadList = getVideoDownloadList(context);
        int position = -1;

        for (int i = 0; i < videoDownloadList.size(); i++) {
            if (bean == null) {
                if(videoDownloadList.get(i) == null){
                    //是这个
                    position = i;
                    break;
                }
            }else if (bean.toString().equals(videoDownloadList.get(i).toString())){
                position = i;
                break;
            }
        }
        if (position != -1){
            videoDownloadList.remove(position);
        }
        String jsonList = GsonUtil.getGson().toJson(videoDownloadList);
        sp.edit().putString(AppConstants.VIDEO_DOWNLOAD_LIST, jsonList).commit();

    }

}
