package com.example.administrator.javademo.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.administrator.javademo.R;

/**
 * Created by Administrator on 2018/1/30 0030.
 */

public class DialogUtil {

    public static ProgressDialog initProgress(Context context,String message){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(R.style.MaterialDialog);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

}
