package com.sxhxjy.defensemonitor.ui.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.adapter.RealTimeDataListAdapter;
import com.sxhxjy.defensemonitor.base.BaseActivity;
import com.sxhxjy.defensemonitor.base.BaseListFragment;
import com.sxhxjy.defensemonitor.base.HttpResponse;
import com.sxhxjy.defensemonitor.entity.RealTimeData;

import java.util.List;

import rx.Observable;

/**
 * 2016/9/26
 *
 * @author Michael Zhao
 */

public class RealTimeDataListActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        Fragment f = new RealTimeDataListFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.container, f).commit();
        initToolBar("实时数据", true);
    }

    public static class RealTimeDataListFragment extends BaseListFragment<RealTimeData> {

        @Override
        public Observable<HttpResponse<List<RealTimeData>>> getObservable() {
            mList.addAll((MonitorFragment.monitorFragment.mRealTimes));
            mAdapter.notifyDataSetChanged();
            return null;
//            return getHttpService().getRealTimeData(getArguments().getString("type"), System.currentTimeMillis() - 10000, System.currentTimeMillis());
        }

        @Override
        protected Class<RealTimeData> getItemClass() {
            return RealTimeData.class;
        }

        @Override
        protected void init() {
            mPullRefreshLoadLayout.enableRefresh(false);
        }

        @Override
        protected String getCacheKey() {
            return null;
        }

        @Override
        protected RecyclerView.Adapter getAdapter() {
            return new RealTimeDataListAdapter(this, mList);
        }
    }


}
