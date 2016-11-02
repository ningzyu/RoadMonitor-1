package com.sxhxjy.roadmonitor.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureStore;
import android.gesture.GestureStroke;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseActivity;
import com.sxhxjy.roadmonitor.entity.RealTimeData;
import com.sxhxjy.roadmonitor.ui.main.ChartFullscreenActivity;
import com.sxhxjy.roadmonitor.ui.main.MainActivity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 2016/9/19
 *
 * @author Michael Zhao
 */
public class LineChartView extends View {
    private static final int DELAY = 1000;
    private static final int POINTS_COUNT = 24;
    private static final int OFFSET = 60;
    private static final int OFFSET_LEGEND = 70;
    private static final int LEGEND_WIDTH= 70;
    private static final int LEGEND_HEIGHT = 35;

    private static final float OFFSET_SCALE = 8;
    private static final float SPLIT_TO = 5;
    private static final float X_SPLIT_TO = 5;

    private static final int ALERT_VALUE = 1000;
    private Random mRandom = new Random(47);
    private int xAxisLength, yAxisLength;
    private long xStart, xEnd;
    private float yStart, yEnd;
    private float firstPointX, nextPointX, firstPointY, nextPointY;

    private long BASE_TIME = System.currentTimeMillis();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private Date date = new Date();

    private int[] colors = {Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};



    private Paint mPaint;
    private Path mPath = new Path();

    private PathEffect mPathEffect = new DashPathEffect(new float[] {8, 8}, 0);

    private ArrayList<MyLine> myLines = new ArrayList<>();


    private RectF rectF = new RectF();
    public String yAxisName;

    private NumberFormat numberFormat = NumberFormat.getInstance();
    private String emptyHint = "暂无数据";


    private boolean isBeingTouched = false;
    private float touchedX = -1;
    private GestureDetector gestureDetector;
    private static boolean doubleTapHinted;
    private boolean chartInFullscreen;


    public static LineChartView lineChartView;
    private boolean mIsBeingDraged;


    public LineChartView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (chartInFullscreen) {
                    ((Activity) context).finish();
                    return true;
                }

                Intent intent = new Intent(context, ChartFullscreenActivity.class);
                lineChartView = LineChartView.this;
                context.startActivity(intent);
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!mIsBeingDraged && Math.abs(distanceY) - Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    mIsBeingDraged = true;
                }
                return true;
            }
        });

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPath.setFillType(Path.FillType.WINDING);
        numberFormat.setMaximumFractionDigits(2);

        // fake data
        /*new CountDownTimer(1000000, DELAY) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mList.size() == POINTS_COUNT)
                    mList.remove(0);
                mList.add(new MyPoint(System.currentTimeMillis(), mRandom.nextInt(100)));

                invalidate();
            }

            @Override
            public void onFinish() {

            }
        }.start();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xAxisLength = getMeasuredWidth() - 2 * OFFSET;
        yAxisLength = getMeasuredHeight() - 2 * OFFSET - OFFSET_LEGEND;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.white));

        if (myLines.isEmpty()) {
            mPaint.setTextSize(70);
            mPaint.setColor(Color.GRAY);
            float width = mPaint.measureText(emptyHint);
            canvas.drawText(emptyHint, getMeasuredWidth() / 2 - width / 2, getMeasuredHeight() / 2, mPaint);
            return;
        }

        canvas.translate(OFFSET + 10, getMeasuredHeight() - OFFSET - OFFSET_LEGEND);


        xStart = System.currentTimeMillis() + 1000*3600*60;
        yEnd = -10000f;

        for (MyLine line : myLines) {
            xEnd = Math.max(Collections.max(line.points, comparatorX).time, xEnd);
            xStart = Math.min(Collections.min(line.points, comparatorX).time, xStart);
            yEnd = Math.max(Collections.max(line.points, comparatorY).value, yEnd);
            yStart = Math.min(Collections.min(line.points, comparatorY).value, yStart);
        }


        // draw point and line
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        float minDistance = getMeasuredWidth();
        MyPoint minPoint = null;

        for (MyLine line : myLines) {
            for (MyPoint myPoint : line.points) {
                firstPointX = nextPointX;
                firstPointY = nextPointY;
                nextPointX = (float) (((double) (myPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);
                nextPointY = -(float) (((double) (myPoint.value - yStart)) / (yEnd - yStart) * yAxisLength);

                mPaint.setColor(line.color);
                mPaint.setStrokeWidth(4);

                // draw line
                if (line.points.indexOf(myPoint) != 0) {// do not draw line when draw first point !
                    // first point is bad because of equals last next point
                    canvas.drawLine(firstPointX, firstPointY, nextPointX, nextPointY, mPaint);
                }

                mPaint.setStrokeWidth(8);

                if (myPoint.value > ALERT_VALUE) {
                    mPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
                }

                // draw point
                canvas.drawPoint(nextPointX, nextPointY, mPaint);

                // calculate min
                if (isBeingTouched) {
                    float d = Math.abs(touchedX - nextPointX);
                    if (d < minDistance) {
                        minDistance = d;
                        minPoint = myPoint;
                    }
                }
            }
        }

        // draw point info
        if (isBeingTouched && minPoint != null) {
            mPaint.setColor(getResources().getColor(R.color.colorPrimary));
            mPaint.setStrokeWidth(14);
            float x = (float) (((double) (minPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);
            float y = -(float) (((double) (minPoint.value - yStart)) / (yEnd - yStart) * yAxisLength);
            canvas.drawPoint(x, y, mPaint);

            mPaint.setTextSize(24);
            mPaint.setColor(Color.MAGENTA);
            mPaint.setStrokeWidth(1);
            int offsetX = 50;
            int offsetY = 50;
            if (x > xAxisLength / 2)
                offsetX = - offsetX * 5;
            if (- y > yAxisLength / 2)
                offsetY = - offsetY;

            date.setTime(minPoint.time);
            canvas.drawText(yAxisName + ": " + minPoint.value, x + offsetX, y - offsetY, mPaint);
            canvas.drawText(dateFormat.format(date), x + offsetX, y - offsetY + 40, mPaint);

        }


        // draw x
        mPaint.setTextSize(20);
        mPaint.setColor(getResources().getColor(R.color.default_text_color));
        mPaint.setTextAlign(Paint.Align.CENTER);
        for (int j = 0; j <= X_SPLIT_TO; j++) {
            long x = xStart + (long) ((xEnd - xStart) / X_SPLIT_TO * j);
            float xInView = (x - xStart) * 1f / (xEnd - xStart) * xAxisLength;
            date.setTime(x);
            mPaint.setStrokeWidth(1);
            canvas.drawText(date.getHours() + ": 00", xInView, OFFSET_SCALE * 4, mPaint);
            mPaint.setStrokeWidth(2);
            canvas.drawLine(xInView, 0, xInView, -OFFSET_SCALE, mPaint);
            mPaint.setStrokeWidth(2);
        }

        // draw y
        mPaint.setColor(getResources().getColor(R.color.default_text_color));
        mPaint.setStrokeWidth(2);
        canvas.drawLine(0, 0, 0, - yAxisLength, mPaint);
        canvas.drawLine(0, 0, xAxisLength, 0, mPaint);

        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(yStart + "", - OFFSET_SCALE, 0, mPaint);

        for (int j = 0; j < SPLIT_TO; j++) {
            float y = yStart + (yEnd - yStart) / SPLIT_TO * (j + 1);
            float yInView = (y - yStart) / (yEnd - yStart) * yAxisLength;
            yInView = -yInView; // reverse

            mPaint.setStrokeWidth(1);
            canvas.drawText(numberFormat.format(y) + "", - OFFSET_SCALE, yInView, mPaint);
            mPaint.setStrokeWidth(2);
            canvas.drawLine(0, yInView, OFFSET_SCALE, yInView, mPaint);
        }

        // draw yAxisName
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStrokeWidth(1);
        canvas.drawText(yAxisName, - OFFSET_SCALE * 3, -yAxisLength - OFFSET / 2, mPaint);

        // draw alert line
        mPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        mPaint.setPathEffect(mPathEffect);
        mPath.reset();
        float alertY = - (float) (((double)(ALERT_VALUE - yStart)) / (yEnd - yStart) * yAxisLength);
        mPath.moveTo(0, alertY);
        mPath.lineTo(xAxisLength, alertY);

        canvas.drawPath(mPath, mPaint);
        mPaint.setPathEffect(null);

        mPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(ALERT_VALUE + "", xAxisLength + OFFSET_SCALE, alertY - OFFSET_SCALE, mPaint);

        // draw legend
        rectF.setEmpty();
        mPaint.setTextSize(30);
        for (MyLine myLine : myLines) {
            mPaint.setColor(myLine.color);
            rectF.top = OFFSET;
            rectF.bottom = rectF.top + OFFSET_LEGEND / 2 - 20;
            rectF.right = rectF.left + OFFSET_LEGEND;
            canvas.drawRoundRect(rectF, 2, 2, mPaint);
            rectF.left += rectF.width() + OFFSET_LEGEND * 4;
            mPaint.setColor(getResources().getColor(R.color.default_text_color));
            mPaint.setStrokeWidth(0.1f);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(myLine.name, rectF.right + 15, rectF.bottom, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBeingTouched = true;
                if (!doubleTapHinted) {
                    Toast.makeText(getContext(), "可双击放大", Toast.LENGTH_SHORT).show();
                    doubleTapHinted = true;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                touchedX = event.getX() - OFFSET - 10; // in canvas
                break;

            case MotionEvent.ACTION_MOVE:
                touchedX = event.getX() - OFFSET - 10; // in canvas
                break;

            default:
                isBeingTouched = false;
                mIsBeingDraged = false;

                break;
        }
        invalidate();
        return true;
    }

    public void addPoints(ArrayList<MyPoint> points, String s, int color) {
        myLines.add(new MyLine(s, points, color)); // TODO cost memory
        invalidate();
    }

    public  ArrayList<MyPoint> convert(List<RealTimeData> list) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            if (points.size() == POINTS_COUNT)
                points.remove(0);
            points.add(new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getX()));
        }
        yAxisName = list.get(0).getXColName() + "/ " + list.get(0).getTypeUnit();
        return points;
    }

    public  ArrayList<MyPoint> convertY(List<RealTimeData> list) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            if (points.size() == POINTS_COUNT)
                points.remove(0);
            points.add(new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getY()));
        }
        yAxisName = list.get(0).getYColName() + "/ " + list.get(0).getTypeUnit();
        return points;
    }

    public  ArrayList<MyPoint> convertZ(List<RealTimeData> list) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            if (points.size() == POINTS_COUNT)
                points.remove(0);
            points.add(new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getZ()));
        }
        yAxisName = list.get(0).getZColName() + "/ " + list.get(0).getTypeUnit();
        return points;
    }

    public ArrayList<MyLine> getLines() {
        return myLines;
    }

    public void setMyLines(ArrayList<MyLine> myLines) {
        this.myLines = myLines;
    }

    public boolean isChartInFullscreen() {
        return chartInFullscreen;
    }

    public void setChartInFullscreen(boolean chartInFullscreen) {
        this.chartInFullscreen = chartInFullscreen;
    }

    private Comparator<MyPoint> comparatorX =  new Comparator<MyPoint>() {
        @Override
        public int compare(MyPoint lhs, MyPoint rhs) {
            return (int) (lhs.time - rhs.time);
        }
    };

    private Comparator<MyPoint> comparatorY =  new Comparator<MyPoint>() {
        @Override
        public int compare(MyPoint lhs, MyPoint rhs) {
            if (lhs.value - rhs.value > 0)
                return 1;
            if (lhs.value - rhs.value < 0)
                return -1;
            if (lhs.value - rhs.value == 0)
                return 0;
            return 0;
        }
    };


    public static class MyPoint implements Serializable {
        MyPoint(long time, float value) {
            this.time = time;
            this.value = value;
        }

        long time;
        float value;

    }

    public static class MyLine implements Serializable {
        MyLine(String name, ArrayList<MyPoint> points, int color) {
            this.name = name;
            this.points = points;
            this.color = color;
        }

        String name;
        int color;
        ArrayList<MyPoint> points;
    }
}
