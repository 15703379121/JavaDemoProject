package com.example.administrator.javademo.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Administrator on 2018/2/12 0012.
 */

public class ServiceUtil {
    public static boolean isServiceRunning(Context context, String ServicePackageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ServicePackageName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
