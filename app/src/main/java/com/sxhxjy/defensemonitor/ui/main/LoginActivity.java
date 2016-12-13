package com.sxhxjy.defensemonitor.ui.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.BaseActivity;
import com.sxhxjy.defensemonitor.base.CacheManager;
import com.sxhxjy.defensemonitor.base.MyApplication;
import com.sxhxjy.defensemonitor.base.MySubscriber;
import com.sxhxjy.defensemonitor.entity.LoginData;
import com.sxhxjy.defensemonitor.util.ActivityUtil;
import com.sxhxjy.defensemonitor.util.MD5_X;
import com.sxhxjy.defensemonitor.util.Utils;


/**
 * Log in
 *
 * adb push C:\Users\dell\Desktop\yiwu\trunk\Ewuexchange\app\app-debug.apk /data/local/tmp/com.efulink.ewuexchange
 *
 * adb shell pm install -r "/data/local/tmp/com.efulink.ewuexchange"
 *
 * @author Michael Zhao
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 登录页
     */
    public ImageView imageViewQQ;
    public ImageView imageViewWechat;
    public ImageView imageViewWeibo;
    private Context mContext;
    private Button mLogin;
    private TextView mReg, mForget;
    private EditText mUser, mPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        initView();
    }

    private void initView() {
        mContext= this;
        mUser = (EditText) findViewById(R.id.user);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(this);
    }

    private boolean isPhoneValid() {
        if (Utils.checkPhone(mUser.getText().toString())) {
            return true;
        } else {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mUser.setError("手机号不正确");
                    mUser.requestFocus();
                }
            }, 300); // must delay or focus change TOO FAST !!!!!!
            // what the fuck !!!!!!

            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login:
                    getMessage(getHttpService()
                            .login(mUser.getText().toString(), MD5_X.md5(mPassword.getText().toString().getBytes()).toLowerCase()),
                            new MySubscriber<LoginData>() {
                        @Override
                        public void onMyNext(LoginData loginData) {
                            showToastMsg("登录成功");
                            CacheManager.getInstance().set("login", new Gson().toJson(loginData));
                            MyApplication.getMyApplication().getSharedPreference().edit().putString("uid", loginData.getUser().getId()).apply();
                            MyApplication.getMyApplication().getSharedPreference().edit().putString("gid", loginData.getUser().getGid()).apply();
                            ActivityUtil.startActivityForResult(LoginActivity.this, StationListActivity.class, null, 111);
                            finish();
                        }
                    });

                break;
        }
    }



    private boolean isValid() {
        if (Utils.checkPhone(mUser.getText().toString()))
            if (Utils.checkPwd(mPassword.getText().toString()))
                return true;
            else {
                mPassword.setError("请输入合法的密码");
                mPassword.requestFocus();
                return false;
            }
        else {
            mUser.setError("请输入正确的手机号");
            mUser.requestFocus();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        MyApplication.getMyApplication().exit();
    }

}
