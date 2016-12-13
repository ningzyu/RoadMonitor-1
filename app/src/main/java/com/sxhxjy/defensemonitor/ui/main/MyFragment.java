package com.sxhxjy.defensemonitor.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.BaseFragment;

/**
 * 2016/9/26
 *
 * @author Michael Zhao
 */

public class MyFragment extends BaseFragment {
    /**\
     * 我的——fragment页
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolBar(view, "我的", false);
    }
}
