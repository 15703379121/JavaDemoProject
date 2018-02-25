package com.example.administrator.javademo;

import android.app.Application;

import com.example.administrator.javademo.util.AppConstants;
import com.yixia.camera.VCamera;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

//import cn.jpush.android.api.JPushInterface;

/**
 * Created by Burt on 2017/7/6 0006.
 */

public class mApplication extends Application {

    public static String mNewsSize = "0";
    @Override
    public void onCreate() {
        super.onCreate();
        File file = new File(AppConstants.FILE_PUBLISH_VIDEO);
        if(!file.exists()) file.mkdirs();
        //设置视频缓存路径
        VCamera.setVideoCachePath(AppConstants.FILE_PUBLISH_VIDEO);

        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);

        // 初始化拍摄SDK，必须
        VCamera.initialize(this);


        //极光推送
	    JPushInterface.init(getApplicationContext());
	    JPushInterface.setDebugMode(true);
    }
}
