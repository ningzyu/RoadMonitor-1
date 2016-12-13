package com.sxhxjy.defensemonitor.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.view.LineChartView;

/**
 * 2016/11/2
 *
 * @author Michael Zhao
 */

public class ChartFullscreenActivity extends Activity {
    private static boolean hinted;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_full_screen_activity);

        LineChartView lineChartView = (LineChartView) findViewById(R.id.chart);
        lineChartView.setMyLines(LineChartView.lineChartView.getLines());
        lineChartView.setMyLinesRight(LineChartView.lineChartView.getLinesRight());

        lineChartView.yAxisName = LineChartView.lineChartView.yAxisName;
        lineChartView.yAxisNameRight = LineChartView.lineChartView.yAxisNameRight;
        lineChartView.setChartInFullscreen(true);
        lineChartView.mIsSimpleDraw = LineChartView.lineChartView.mIsSimpleDraw;
        lineChartView.invalidate();
        if (!hinted) {
            Toast.makeText(this, "亲~ 双击可以退出哦", Toast.LENGTH_SHORT).show();
            hinted = true;
        }
    }
}
