package com.example.administrator.javademo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.example.administrator.javademo.R;
import com.example.administrator.javademo.activity.DownloadActivity;
import com.example.administrator.javademo.bean.DownLoadBean;
import com.example.administrator.javademo.listener.OnDownloadListener;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.FileUtil;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;

import java.io.File;

/**
 * Created by Administrator on 2018/2/10 0010.
 */

public class DownloadService extends Service {

    private boolean isDownLoading = false;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAIL = 1;
    private static final int MSG_DOWNLOADING = 2;
    private static final int MSG_PAUSED = 3;
    private static final int MSG_CANCEL = 4;
    private OkHttpUtil okHttpUtil = new OkHttpUtil();//下载
    private DownloadBinder mBinder = new DownloadBinder();//绑定
    private OnDownloadListener serviceListener;
    private DownLoadBean bean = new DownLoadBean();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SUCCESS:
                    isDownLoading = false;
                    getNotificationManager().notify(1,getNotification("下载完成",-1));
                    getNotificationManager().cancel(1);
                    new Thread(){
                        @Override
                        public void run() {
                            //把video/temp里的文件转到video
                            FileUtil.cutFile(bean.getFilePath(), AppConstants.FILE_DOWN_VIDEO+"/"+bean.getName()+bean.getSuffix());
                            //删除SP文件里的下载列表
                            SPUtil.removeVideoDownloadList(DownloadService.this,bean);
                            //若本地有图片也要删除
                            File filePic = new File(AppConstants.FILE_DOWN_VIDEO_THUMBNAIL+"/"+bean.getName()+".png");
                            if (filePic.exists()){
                                filePic.delete();
                            }
                        }
                    }.start();
                    if (serviceListener != null)
                        serviceListener.success();
                    Toast.makeText(DownloadService.this, "下载完成", Toast.LENGTH_SHORT).show();
            /*service kill 自杀*/
//                    DownloadService.this.stopSelf();
                    break;
                case MSG_FAIL:
                    isDownLoading = false;
                    getNotificationManager().notify(1,getNotification("下载失败",-1));
                    if (serviceListener != null)
                        serviceListener.fail();
                    Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DOWNLOADING:
                    getNotificationManager().notify(1,getNotification("下载中",mPosition));
                    if (serviceListener != null)
                        serviceListener.downloading(mPosition);
                    break;
                case MSG_PAUSED:
                    isDownLoading = false;
                    if (serviceListener != null)
                        serviceListener.paused();
                    break;
                case MSG_CANCEL:
                    isDownLoading = false;
                    if (serviceListener != null)
                        serviceListener.canceled();
                    break;
            }
        }
    };
    private int mPosition = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    public class DownloadBinder extends Binder {
        public void startDownload(DownLoadBean downLoadBean){
            isDownLoading = true;
            bean = downLoadBean;
            //是否已下载
            File loadedDir = new File(AppConstants.FILE_DOWN_VIDEO);
            String[] loadedFiles = loadedDir.list();
            if (loadedFiles != null && loadedFiles.length > 0){
                //轮询查看
                for (int i = 0; i < loadedFiles.length; i++) {
                    if (loadedFiles[i].equals(downLoadBean.getName()+downLoadBean.getSuffix())){
                        //此文件已下载
                        if (serviceListener != null){
                            serviceListener.success();
                            return;
                        }
                    }
                }
            }
            okHttpUtil = new OkHttpUtil();
            okHttpUtil.setPaused(false);
            okHttpUtil.setCanceled(false);
            okHttpUtil.download(bean.getUrl(),bean.getFilePath(),downloadListener);
            startForeground(1,getNotification("下载中...",0));
            Toast.makeText(DownloadService.this, "视频下载", Toast.LENGTH_SHORT).show();
        }

        public void pauseDownload(){
            isDownLoading = false;
            if(okHttpUtil != null){
                okHttpUtil.setPaused(true);
            }
        }

        public void cancelDownload(){
            isDownLoading = false;
            if(okHttpUtil != null){
                okHttpUtil.setCanceled(true);
            }
        }

        public boolean isDownLoading(){
            return isDownLoading;
        }

        public DownLoadBean getDownLoadBean(){
            return bean;
        }

        public void setDownloadListener(OnDownloadListener listener){
            serviceListener = listener;
        }
    }

    private OnDownloadListener downloadListener = new OnDownloadListener() {
        @Override
        public void success() {
            handler.sendEmptyMessage(MSG_SUCCESS);
        }

        @Override
        public void fail() {
            handler.sendEmptyMessage(MSG_FAIL);

        }

        @Override
        public void downloading(int position) {
            mPosition = position;
            handler.sendEmptyMessage(MSG_DOWNLOADING);
        }

        @Override
        public void paused() {

            handler.sendEmptyMessage(MSG_PAUSED);
        }

        @Override
        public void canceled() {

            handler.sendEmptyMessage(MSG_CANCEL);
        }
    };

    /**
     * 通知栏显示
     * @param title
     * @param progress
     * @return
     */
    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this,DownloadActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.icon_download)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_download))
                .setContentIntent(pi)
                .setContentTitle(title);
        if(progress > 0){
            builder.setContentText(progress+"%")
                    .setProgress(100,progress,false);
        }
        return builder.build();
    }


    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
}
