package com.example.administrator.javademo.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import com.example.administrator.javademo.activity.VideoActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.ThumbnailUtils;
import io.vov.vitamio.provider.MediaStore;

/**
 * Created by Administrator on 2018/2/6 0006.
 */

public class VideoUtil {

    /**
     * 设置缩略图
     */
    public static void setThumbnail(final Context context, final String url, final ImageView mIvThumbnail) {
        new Thread() {
            @Override
            public void run() {
                //设置缩略图,Vitamio提供的工具类。
                final Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                        context, url
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                LogUtil.e("videoThumbnail--------"+videoThumbnail);
                if (videoThumbnail != null) {
                    mIvThumbnail.post(new Runnable() {
                        @Override
                        public void run() {
                            mIvThumbnail.setImageBitmap(videoThumbnail);
                        }
                    });
                }
            }
        }.start();
    }
    /**
     * 获取本地视频缩略图
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoLocalThumbnail(final String videoPath) {
        try{

            MediaMetadataRetriever media =new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            return media.getFrameAtTime();
        }catch (Exception e){

        }
        return null;
    }
    /**
     * 获取本地视频缩略图
     * @param videoPath
     * @return
     */
    public static void setVideoLocalThumbnail(final Activity activity, final String videoPath, final ImageView imageView) {
        try{
            new Thread(){
                @Override
                public void run() {
                    final Bitmap bitmap = getVideoLocalThumbnail(videoPath);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }.start();
        }catch (Exception e){

        }
    }

    /**
     * 获取本地图片
     * @param picPath
     * @return
     */
    public static Bitmap getLocalPic(String picPath){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(picPath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean savePic(Bitmap btImage,String filePath,String fileName){
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {
            File dirFile  = new File(filePath);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(filePath, fileName+".png");// 在SDcard的目录下创建图片文,以当前时间为其命名

            try {
                FileOutputStream out = new FileOutputStream(file);
                btImage.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public static void saveNetVideoThumbnail(final Context context, final String url, final String name) {
        try {
            new Thread() {
                @Override
                public void run() {
                    //设置缩略图,Vitamio提供的工具类。
                    final Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(
                            context, url
                            , MediaStore.Video.Thumbnails.MINI_KIND);
                    LogUtil.e("videoThumbnail--------"+videoThumbnail);
                    if (videoThumbnail != null) {
                        savePic(videoThumbnail, AppConstants.FILE_DOWN_VIDEO_THUMBNAIL,name);
                    }
                }
            }.start();

        } catch (Exception ex) {
            // Assume this is a corrupt video file
        }
    }
}
