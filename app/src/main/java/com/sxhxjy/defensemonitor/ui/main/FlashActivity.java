package com.sxhxjy.defensemonitor.ui.main;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.CacheManager;
import com.sxhxjy.defensemonitor.util.ActivityUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity will be showed when app is launching
 *
 * @author Michael Zhao
 */
public class FlashActivity extends Activity {
    /**
     * 广告页，第一次进入
     */
    private final long DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_flash);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(CacheManager.getInstance().get("login"))) {
                    ActivityUtil.startActivityForResult(FlashActivity.this, LoginActivity.class, null, -100, android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    ActivityUtil.startActivityForResult(FlashActivity.this, MainActivity.class, null, -100, android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        }, DELAY);
    }
}
