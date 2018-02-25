package com.example.administrator.javademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.javademo.R;
import com.example.administrator.javademo.util.AppConstants;
import com.example.administrator.javademo.util.LogUtil;
import com.example.administrator.javademo.util.MD5Util;
import com.example.administrator.javademo.util.OkHttpUtil;
import com.example.administrator.javademo.util.SPUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class LoginActivity extends BaseActivity {
    private static final int MSG_LOGIN_SUCCESS = 0;
    private static final int MSG_LOGIN_FAIL = 1;
    private static final int MSG_NET_FAIL = 2;
    @InjectView(R.id.et_login_admin)
    EditText mEtLoginAdmin;
    @InjectView(R.id.et_login_psd)
    EditText mEtLoginPsd;
    @InjectView(R.id.rb_login_student)
    RadioButton mRbLoginStudent;
    @InjectView(R.id.rb_login_teacher)
    RadioButton mRbLoginTeacher;
    @InjectView(R.id.rg_login)
    RadioGroup mRgLogin;
    @InjectView(R.id.tv_login_submit)
    TextView mTvLoginSubmit;
    private String mAdminType = AppConstants.USER_STUDENT;//0:学生            1:教师      2:教秘
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOGIN_SUCCESS:
                    //登录成功
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                    break;
                case MSG_LOGIN_FAIL:
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NET_FAIL:
                    Toast.makeText(LoginActivity.this, "访问网络失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        String login = SPUtil.getString(this, AppConstants.SP_LOGIN, "");

        if (!TextUtils.isEmpty(login)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        //登录监听
        mTvLoginSubmit.setOnClickListener(this);
        //radioGroup监听
        mRgLogin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id){
                    case R.id.rb_login_student:
                        mAdminType = AppConstants.USER_STUDENT;
                        mEtLoginAdmin.setHint("学号");
                        break;
                    case R.id.rb_login_teacher:
                        mAdminType = AppConstants.USER_TEACHER;
                        mEtLoginAdmin.setHint("职工号");
                        break;
                    case R.id.rb_login_admin:
                        mAdminType = AppConstants.USER_ADMIN;
                        mEtLoginAdmin.setHint("教秘号");
                        break;
                }
            }
        });
    }

    @Override
    protected void processClick(View v) throws IOException {
        switch (v.getId()){
            case R.id.tv_login_submit:
                loginSubmit();
                break;
        }
    }

    private void loginSubmit() {
        String admin = mEtLoginAdmin.getText().toString().trim();
        String psd = mEtLoginPsd.getText().toString().trim();
        if (TextUtils.isEmpty(admin) || TextUtils.isEmpty(psd)){
            Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.e("identifier----"+admin);
        LogUtil.e("password----"+psd);
        LogUtil.e("type----"+mAdminType);
        try {
//            String md5 = MD5Util.getMD5(psd);
            //登录验证（成功后写入sp）
            Map<String, Object> params = new HashMap<>();
            params.put("identifier", admin);
            params.put("password",psd);
            params.put("type",mAdminType);
            OkHttpUtil.postJson(AppConstants.USER_CHECK_LOGIN_URL, params, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e(""+result);
                    if (TextUtils.isEmpty(result)){
                        //登录失败
                        handler.sendEmptyMessage(MSG_LOGIN_FAIL);
                    }else{
                        //登录成功
                        SPUtil.putString(LoginActivity.this,AppConstants.SP_LOGIN,result);
                        handler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e("登录联网操作失败");
            handler.sendEmptyMessage(MSG_NET_FAIL);
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
