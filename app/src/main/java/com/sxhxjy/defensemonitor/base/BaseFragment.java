package com.sxhxjy.defensemonitor.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhxjy.defensemonitor.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Extends fragment to add some methods
 *
 * @author Michael Zhao
 */
public class BaseFragment extends Fragment {

    /**
     * If it is first to show, you should instantiate fragment in Fragment.onResume()
     */
    protected boolean isFirst = true;

    protected boolean isViewCreated = false;

    private ProgressDialog mProgressDialog;


    protected Toolbar mToolbar;
    //定义标题栏
    public void initToolBar(View view, String title, boolean canBack) {
        mToolbar = (Toolbar) view.findViewById(R.id.tool_bar);
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.setBackgroundResource(R.color.colorPrimary);//背景色
            TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            if (title != null && mTitle != null) mTitle.setText(title);
            mToolbar.setTitle("");
            if (canBack) {
                mToolbar.setNavigationIcon(R.mipmap.navigation_icon);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();

                    }
                });
            }

        }
    }


    public void showToastMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    public void showWaitingDialog(String message) {
        if (message == null) {
            message = "加载中...";
        }
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void dismissWaitingDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
    }

    /* load data when visible to user */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst && isViewCreated) {
            isFirst = false;
            loadOnce();
        }
        if (isVisibleToUser && isViewCreated)
            loadEveryTime();
    }


    protected void loadOnce() {
        Log.e("base fragment", "loadOnce called !!!");
    }

    protected void loadEveryTime() {
    }

    public HttpService getHttpService() {
        return MyApplication.getMyApplication().getHttpService();
    }

    public <T> void getMessage(Observable<HttpResponse<T>> observable, MySubscriber<T> mySubscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new HttpResponseFunc<T>((BaseActivity) getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }
}
