package com.sxhxjy.defensemonitor.ui.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.adapter.AlertDetailListAdapter;
import com.sxhxjy.defensemonitor.base.BaseActivity;
import com.sxhxjy.defensemonitor.base.BaseListFragment;
import com.sxhxjy.defensemonitor.base.HttpResponse;
import com.sxhxjy.defensemonitor.entity.AlertData;

import java.util.List;

import rx.Observable;

/**
 * 2016/10/8
 *
 * @author Michael Zhao
 */

public class AlertDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        Fragment fragment =  new AlertDetailFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", getIntent().getSerializableExtra("data"));
        fragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment).commit();
        initToolBar("警告详情", true);
    }

    public static class AlertDetailFragment extends BaseListFragment<AlertData> {

        @Override
        public Observable<HttpResponse<List<AlertData>>> getObservable() {
            AlertData alertData = (AlertData) getArguments().getSerializable("data");
            return getHttpService().getAlertDataDetail(alertData.getId(), alertData.getUserId(), alertData.getConfirmMsg(), alertData.getStime(), alertData.getEtime(), alertData.getLevelId(), alertData.getTypeId(), alertData.getGenerationReason(), alertData.getAlarmContent(), alertData.getConfirmTime(), alertData.getConfirmInfo());
        }

        @Override
        protected Class<AlertData> getItemClass() {
            return AlertData.class;
        }

        @Override
        protected void init() {

        }

        @Override
        protected String getCacheKey() {
            return null;
        }

        @Override
        protected RecyclerView.Adapter getAdapter() {
            return new AlertDetailListAdapter(this, mList);
        }
    }


}