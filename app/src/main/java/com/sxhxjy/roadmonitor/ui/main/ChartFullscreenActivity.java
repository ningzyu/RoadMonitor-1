package com.sxhxjy.roadmonitor.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseActivity;
import com.sxhxjy.roadmonitor.view.LineChartView;

import java.util.ArrayList;

/**
 * 2016/11/2
 *
 * @author Michael Zhao
 */

public class ChartFullscreenActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_full_screen_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        ArrayList<LineChartView.MyLine> lines = LineChartView.lineChartView.getLines();
        LineChartView lineChartView = (LineChartView) findViewById(R.id.chart);
        lineChartView.setMyLines(lines);
        lineChartView.yAxisName = LineChartView.lineChartView.yAxisName;
        lineChartView.invalidate();
    }
}
