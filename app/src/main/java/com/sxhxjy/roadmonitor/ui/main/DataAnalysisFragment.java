package com.sxhxjy.roadmonitor.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseFragment;
import com.sxhxjy.roadmonitor.base.MySubscriber;
import com.sxhxjy.roadmonitor.entity.RealTimeData;
import com.sxhxjy.roadmonitor.entity.SimpleItem;
import com.sxhxjy.roadmonitor.util.ActivityUtil;
import com.sxhxjy.roadmonitor.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * 2016/9/26
 *
 * @author Michael Zhao
 */
public class DataAnalysisFragment extends BaseFragment {
    /**\
     * 数据分析——fragment页
     */
    private CountDownTimer mTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_analysis_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolBar(view, "数据分析", false);


        mToolbar.inflateMenu(R.menu.data_right);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), AddDataContrastActivity.class);
                startActivityForResult(intent, 1000);
                return true;
            }
        });

        ImageView left = (ImageView) view.findViewById(R.id.toolbar_left);
        left.setVisibility(View.VISIBLE);
        left.setImageResource(R.mipmap.data_correlation);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDataCorrelationActivity.class);
                startActivityForResult(intent, 1001);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // data contrast
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (mTimer != null)
                mTimer.cancel();
            LineChartView lineChartView = (LineChartView) getView().findViewById(R.id.chart);
            lineChartView.getLines().clear();
            mTimer = new CountDownTimer(100000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    getMessage(getHttpService().getRealTimeData(data.getStringExtra("code"), data.getLongExtra("start", 0), data.getLongExtra("end", System.currentTimeMillis())), new MySubscriber<List<RealTimeData>>() {
                        @Override
                        protected void onMyNext(List<RealTimeData> realTimeDatas) {
                            LineChartView lineChartView = (LineChartView) getView().findViewById(R.id.chart);
                            lineChartView.addPoints(lineChartView.convert(realTimeDatas), data.getStringExtra("title"), Color.MAGENTA);
                        }
                    });


                }

                @Override
                public void onFinish() {

                }
            };
            mTimer.start();
        }

        // data correlation

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            final ArrayList<SimpleItem> positionItems = (ArrayList<SimpleItem>) data.getSerializableExtra("positionItems");
            final ArrayList<SimpleItem> positionItemsCorrelation = (ArrayList<SimpleItem>) data.getSerializableExtra("positionItemsCorrelation");
            if (mTimer != null)
                mTimer.cancel();
            final LineChartView lineChartView =  (LineChartView) getView().findViewById(R.id.chart);
            mTimer = new CountDownTimer(30000, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    lineChartView.getLines().clear();
                    for (final SimpleItem item : positionItems) {
                        getMessage(getHttpService().getRealTimeData(item.getCode(), data.getLongExtra("start", 0), data.getLongExtra("end", System.currentTimeMillis())), new MySubscriber<List<RealTimeData>>() {
                            @Override
                            protected void onMyNext(List<RealTimeData> realTimeDatas) {
                                lineChartView.addPoints(lineChartView.convert(realTimeDatas), data.getStringExtra("title") + item.getTitle(), item.getColor());
                            }
                        });
                    }
                    for (final SimpleItem item : positionItemsCorrelation) {
                        getMessage(getHttpService().getRealTimeData(item.getCode(), data.getLongExtra("start", 0), data.getLongExtra("end", System.currentTimeMillis())), new MySubscriber<List<RealTimeData>>() {
                            @Override
                            protected void onMyNext(List<RealTimeData> realTimeDatas) {
                                lineChartView.addPoints(lineChartView.convert(realTimeDatas), data.getStringExtra("title")+ item.getTitle(), item.getColor());
                            }
                        });
                    }



                }

                @Override
                public void onFinish() {

                }
            };
            mTimer.start();


        }
    }

}
