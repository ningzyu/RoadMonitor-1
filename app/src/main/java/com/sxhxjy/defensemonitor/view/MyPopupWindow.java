package com.sxhxjy.defensemonitor.view;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.BaseActivity;


/**
 * 2016/6/11
 *
 * @author Michael Zhao
 */
public class MyPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {
    /**
     * 弹出窗口
     */
    protected BaseActivity mActivity;
    protected View view;

    public MyPopupWindow(BaseActivity activity, int viewId) {
        super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mActivity = activity;
        setAnimationStyle(R.style.popup_window_anim);
        setFocusable(true);
        setOutsideTouchable(true);
        //have to set background
        setBackgroundDrawable(mActivity.getResources().getDrawable(android.R.color.white));
        setOnDismissListener(this);

        view = activity.getLayoutInflater().inflate(viewId, null);

        setContentView(view);
    }


    @Override
    public void onDismiss() {
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 1;
        mActivity.getWindow().setAttributes(params);
    }

    public void show() {
        show(mActivity.findViewById(android.R.id.content));
    }

    public void show(View parentView) {
        showAtLocation(parentView, Gravity.RIGHT, 0, 0);
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = 0.6f;
        mActivity.getWindow().setAttributes(params);
    }
}
