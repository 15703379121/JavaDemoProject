package com.example.administrator.javademo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.activity.DownloadActivity;
import com.example.administrator.javademo.activity.MainActivity;
import com.example.administrator.javademo.activity.VideoActivity;
import com.example.administrator.javademo.activity.VideoListActivity;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			this.context = context;
			Bundle bundle = intent.getExtras();
			LogUtil.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				//进入了这个
				LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				LogUtil.d(TAG, "[MyReceiver] 用户点击打开了通知");

				//打开自定义的Activity
				Intent i = new Intent(context, MainActivity.class);
				i.putExtras(bundle);
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				LogUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				LogUtil.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					LogUtil.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					LogUtil.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//TODO send msg to VideoActivity
	private void processCustomMessage(Context context, Bundle bundle) {
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
		LogUtil.e("alert-----"+alert);
		Intent msgIntent = new Intent("android.intent.action.javademo.JPush");
//        msgIntent.putExtra(VideoActivity.VIDEO_ID, Integer.parseInt(video));
		if (!TextUtils.isEmpty(alert)) {
			String[] split = alert.split(":");
			LogUtil.e("split.length-----"+split.length);
			if (split.length == 3){
				notificationShow(split);
			}

		}
//        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//        LogUtil.e("JPush返回的alert---"+alert);
//        LogUtil.e("JPush返回的title---"+title);
//        LogUtil.e("JPush返回的extras---"+extras);
//        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//        LogUtil.e("JPush返回的message---"+message);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
	}

	private void notificationShow(String[] split) {
		NotificationManager nom = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(context,VideoActivity.class);
		intent.putExtra(VideoListActivity.VIDEO_POSITION,-1);
		int videoId = Integer.parseInt(split[2]);
		intent.putExtra(VideoActivity.VIDEO_ID,videoId);
		LogUtil.e("MyReceiver-----video_id---"+videoId);
		PendingIntent pi = PendingIntent.getActivity(context,0,intent,0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.mipmap.icon)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
				.setContentIntent(pi)
				.setContentTitle(split[0]+"")
				.setContentInfo(split[1]+"");
		nom.notify(2,builder.build());


/*
				Intent intent = new Intent(context, VideoActivity.class);
		intent.putExtra(VideoActivity.VIDEO_ID,Integer.parseInt(split[3]));
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		//注意：通知一定要有图标，没有设置图标的通知，通知不显示
		Notification notification = new Notification.Builder(context)
				.setContentTitle(split[0]+"")
				.setContentText(split[1]+"")
				.setSmallIcon(R.mipmap.icon)//设置图标
				.setContentIntent(pi)
				.build();
		nom.notify(1, notification);*/
	}


}
