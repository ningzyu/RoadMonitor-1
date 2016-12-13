package com.sxhxjy.defensemonitor.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.entity.RealTimeData;
import com.sxhxjy.defensemonitor.ui.main.ChartFullscreenActivity;

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
    public static int pointCount = 20;
    private static final int OFFSET = 65;
    private static final int OFFSET_LEGEND = 80;
    private static final int LEGEND_WIDTH= 70;
    private static final int LEGEND_HEIGHT = 35;

    private static final float OFFSET_SCALE = 8;
    private static final float SPLIT_TO = 5;
    private static float xSplitTo = 5;

    private static final int ALERT_VALUE = 100000000;
    private Random mRandom = new Random(47);
    private int xAxisLength, yAxisLength;
    private long xStart, xEnd;
    private long xStartRight, xEndRight;
    private float yStart, yEnd;
    private float yStartRight, yEndRight;
    private float firstPointX, nextPointX, firstPointY, nextPointY;

    private long BASE_TIME = System.currentTimeMillis();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private Date date = new Date();

    private Paint mPaint;
    private Path mPath = new Path();

    private PathEffect mPathEffect = new DashPathEffect(new float[] {8, 8}, 0);

    private ArrayList<MyLine> myLines = new ArrayList<>();
    private ArrayList<MyLine> myLinesRight = new ArrayList<>();

    private int offset = 0;

    private long globalIndex = 0;


    private RectF rectF = new RectF();
    public String yAxisName;
    public String yAxisNameRight;

    private NumberFormat numberFormat = NumberFormat.getInstance();
    private String emptyHint = "暂无数据";


    private boolean isBeingTouched = false;
    private float touchedX = -1;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private static boolean doubleTapHinted;
    private boolean chartInFullscreen;


    public static LineChartView lineChartView;
    private boolean mIsBeingDragged;
    public boolean mIsSimpleDraw = false;


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
                // resolve conflict
                if (!mIsBeingDragged && Math.abs(distanceY) - Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    mIsBeingDragged = true;
                }


                if (!isBeingTouched) {
                    if (++globalIndex % 2 == 0) { // reduce called times
                        if (distanceX > 5)
                            distanceX = 1;
                        else if (distanceX < -5)
                            distanceX = -1;
                        else distanceX = 0;

                        offset = offset - (int) distanceX;
//                        Log.e("test", "dx: " + distanceX + "offset:" + offset);
                        if (offset < 0)
                            offset = 0;
                        if (offset > myLines.get(0).points.size() - pointCount)
                            offset = myLines.get(0).points.size() - pointCount;
                    }
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                isBeingTouched = true;
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                invalidate();
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
//                offset = detector.getFocusX() / xAxisLength * myLines.get(0).points.size();
                offset = -5;
                if (offset < 0)
                    offset = 0;
                if (offset > myLines.get(0).points.size() - pointCount) offset = myLines.get(0).points.size() - pointCount;

                pointCount = (int) (pointCount + (1 - detector.getScaleFactor()) * 5);
                if (pointCount < 10) pointCount = 10;
                if (pointCount > myLines.get(0).points.size()) pointCount = myLines.get(0).points.size();
                Log.e("test", "p count: " + pointCount + "factor: " + detector.getScaleFactor());
                return super.onScale(detector);
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
                if (mList.size() == pointCount)
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

        if (myLines.isEmpty() && myLinesRight.isEmpty()) {
            mPaint.setTextSize(70);
            mPaint.setColor(Color.GRAY);
            float width = mPaint.measureText(emptyHint);
            canvas.drawText(emptyHint, getMeasuredWidth() / 2 - width / 2, getMeasuredHeight() / 2, mPaint);
            return;
        }

        canvas.translate(OFFSET + 10, getMeasuredHeight() - OFFSET - OFFSET_LEGEND);


        xStart = System.currentTimeMillis() * 2; // big enough
        xEnd = 0; // init !!!!!!!!
        yStart = 0;
        yEnd = -10000f;

        for (MyLine line : myLines) {
            if (line.points.size() - offset - pointCount < 0) continue;
            xEnd = Math.max(Collections.max(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorX).time, xEnd);
            xStart = Math.min(Collections.min(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorX).time, xStart);
            yEnd = Math.max(Collections.max(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorY).value, yEnd);
            yStart = Math.min(Collections.min(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorY).value, yStart);
        }

        // *RIGHT*
        xStartRight = System.currentTimeMillis() * 2;
        xEndRight = 0;
        yStartRight = 0;
        yEndRight = -10000f;
        for (MyLine line : myLinesRight) {
            xEndRight = Math.max(Collections.max(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorX).time, xEndRight);
            xStartRight = Math.min(Collections.min(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorX).time, xStartRight);
            yEndRight = Math.max(Collections.max(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorY).value, yEndRight);
            yStartRight = Math.min(Collections.min(line.points.subList(line.points.size() - offset - pointCount, line.points.size() - offset), comparatorY).value, yStartRight);
        }

        if (!myLinesRight.isEmpty()) {
            xEnd = Math.max(xEnd, xEndRight);
            xStart = Math.min(xStart, xStartRight);
        }



        // draw point and line
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        float minDistance = getMeasuredWidth();
        MyPoint minPoint = null;
        float simpleMinX = 0;
        boolean isRight = false;

        for (MyLine line : myLines) {
            for (MyPoint myPoint : line.points) {
                if (line.points.indexOf(myPoint) > line.points.size() - offset || line.points.indexOf(myPoint) < line.points.size() - offset - pointCount)
                    continue;

                if (line.points.size() - offset - pointCount < 0) continue;


                firstPointX = nextPointX;
                firstPointY = nextPointY;

                if (mIsSimpleDraw) // Simple draw x
                    nextPointX = (line.points.indexOf(myPoint) - (line.points.size() - offset - pointCount)) * xAxisLength / pointCount;
                else
                    nextPointX = (float) (((double) (myPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);

                nextPointY = -(float) (((double) (myPoint.value - yStart)) / (yEnd - yStart) * yAxisLength);

                mPaint.setColor(line.color);
                mPaint.setStrokeWidth(4);

                // draw line
                if (line.points.indexOf(myPoint) != line.points.size() - offset - pointCount) {// do not draw line when draw first point !
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
                        if (mIsSimpleDraw) simpleMinX = nextPointX;
                    }
                }
            }
        }

        //       *RIGHT*
        for (MyLine line : myLinesRight) {
            for (MyPoint myPoint : line.points) {
                if (line.points.indexOf(myPoint) > line.points.size() - offset || line.points.indexOf(myPoint) < line.points.size() - offset - pointCount)
                    continue;

                if (line.points.size() - offset - pointCount < 0) continue;

                firstPointX = nextPointX;
                firstPointY = nextPointY;
                nextPointX = (float) (((double) (myPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);
                nextPointY = -(float) (((double) (myPoint.value - yStartRight)) / (yEndRight - yStartRight) * yAxisLength);

                mPaint.setColor(line.color);
                mPaint.setStrokeWidth(4);

                // draw line
                if (line.points.indexOf(myPoint) != line.points.size() - offset - pointCount) {// do not draw line when draw first point !
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
                        isRight = true;
                    }
                }
            }
        }

        // draw point info
        if (isBeingTouched && minPoint != null) {
            mPaint.setColor(getResources().getColor(R.color.colorPrimary));
            mPaint.setStrokeWidth(14);
            float x, y;
            if (!isRight) {
                if (!mIsSimpleDraw)
                    x = (float) (((double) (minPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);
                else
                    x= simpleMinX;

                y = -(float) (((double) (minPoint.value - yStart)) / (yEnd - yStart) * yAxisLength);
            } else {
                x = (float) (((double) (minPoint.time - xStart)) / (xEnd - xStart) * xAxisLength);
                y = -(float) (((double) (minPoint.value - yStartRight)) / (yEndRight - yStartRight) * yAxisLength);
            }
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
            if (!isRight) {
                canvas.drawText(yAxisName + ": " + minPoint.value, x + offsetX, y - offsetY, mPaint);
            } else {
                canvas.drawText(yAxisNameRight + ": " + minPoint.value, x + offsetX, y - offsetY, mPaint);
            }
            canvas.drawText(dateFormat.format(date), x + offsetX, y - offsetY + 40, mPaint);

        }


        // draw x
        mPaint.setTextSize(20);
        mPaint.setColor(getResources().getColor(R.color.default_text_color));
        mPaint.setTextAlign(Paint.Align.CENTER);


        long xStartNew = 0;
        long xEndNew = 0;
        int drawBy = 0;

        if ((xEnd - xStart) <= 1000 * 3600 * 48) { // draw hour
            date.setTime(xStart);
            date.setHours(date.getHours() + 1);
            int startHour = date.getHours();
            date.setMinutes(0);
            date.setSeconds(0);
            xStartNew = date.getTime();

            date.setTime(xEnd);
            date.setHours(date.getHours());
            date.setMinutes(0);
            date.setSeconds(0);
            xEndNew = date.getTime();

            xSplitTo = date.getHours() - startHour;
            int dx = date.getHours() - startHour;
            if (xSplitTo < 0) {
                xSplitTo += 24;
                dx += 24;
            }

            if (xSplitTo > 10) {
                xSplitTo = 5;
                while (dx % xSplitTo != 0)
                    xSplitTo++;
            }


        } else  { // draw day
            drawBy = 1;

            date.setTime(xStart);
            date.setDate(date.getDate() + 1);
            int startDay = date.getDate();
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            xStartNew = date.getTime();

            date.setTime(xEnd);
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            xEndNew = date.getTime();

            xSplitTo = date.getDate() - startDay;
            int dx = date.getDate() - startDay;
            if (xSplitTo < 0) {
                xSplitTo += 24;
                dx += 24;
            }

            if (xSplitTo > 10) {
                xSplitTo = 5;
                while (dx % xSplitTo != 0)
                    xSplitTo++;
            }

        } /*else { // draw week
            drawBy = 2;


        }*/

        for (int j = 0; j <= xSplitTo; j++) {
            long x = xStartNew + (long) ((xEndNew - xStartNew) / xSplitTo * j);
            float xInView = (x - xStart) * 1f / (xEnd - xStart) * xAxisLength;
            date.setTime(x);
            mPaint.setStrokeWidth(1);
            if (!mIsSimpleDraw) {
                if (drawBy == 0)
                    canvas.drawText(date.getHours() + ": 00", xInView, OFFSET_SCALE * 4, mPaint);
                else
                    canvas.drawText((date.getMonth() + 1) + "-" + date.getDate(), xInView, OFFSET_SCALE * 4, mPaint);
            }
            mPaint.setStrokeWidth(2);
            canvas.drawLine(xInView, 0, xInView, -OFFSET_SCALE, mPaint);
            mPaint.setStrokeWidth(2);
        }

        canvas.drawLine(0, 0, xAxisLength, 0, mPaint); // x axis

        // draw y
        if (!myLines.isEmpty()) {
            mPaint.setColor(getResources().getColor(R.color.default_text_color));
            mPaint.setStrokeWidth(2);
            canvas.drawLine(0, 0, 0, -yAxisLength, mPaint);

            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(yStart + "", -OFFSET_SCALE, 0, mPaint);

            for (int j = 0; j < SPLIT_TO; j++) {
                float y = yStart + (yEnd - yStart) / SPLIT_TO * (j + 1);
                float yInView = (y - yStart) / (yEnd - yStart) * yAxisLength;
                yInView = -yInView; // reverse

                mPaint.setStrokeWidth(1);
                canvas.drawText(numberFormat.format(y) + "", -OFFSET_SCALE, yInView, mPaint);
                mPaint.setStrokeWidth(2);
                canvas.drawLine(0, yInView, OFFSET_SCALE, yInView, mPaint);
            }
        }

        // *RIGHT*

        if (!myLinesRight.isEmpty()) {
            mPaint.setColor(getResources().getColor(R.color.default_text_color));
            mPaint.setStrokeWidth(2);
            canvas.drawLine(xAxisLength, 0, xAxisLength, - yAxisLength, mPaint);

            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(yStart + "", - OFFSET_SCALE, 0, mPaint);

            for (int j = 0; j < SPLIT_TO; j++) {
                float y = yStartRight + (yEndRight - yStartRight) / SPLIT_TO * (j + 1);
                float yInView = (y - yStartRight) / (yEndRight - yStartRight) * yAxisLength;
                yInView = -yInView; // reverse

                mPaint.setStrokeWidth(1);
                canvas.drawText(numberFormat.format(y) + "", xAxisLength + OFFSET_SCALE, yInView, mPaint);
                mPaint.setStrokeWidth(2);
                canvas.drawLine(xAxisLength, yInView, xAxisLength - OFFSET_SCALE, yInView, mPaint);
            }
        }

        // draw yAxisName
        if (!myLines.isEmpty()) {
            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setStrokeWidth(1);
            canvas.drawText(yAxisName, -OFFSET_SCALE * 3, -yAxisLength - OFFSET / 2, mPaint);
        }

        // *RIGHT*
        if (!myLinesRight.isEmpty()) {
            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setStrokeWidth(1);
            canvas.drawText(yAxisNameRight, xAxisLength - OFFSET_SCALE * 6, -yAxisLength - OFFSET / 2, mPaint);
        }

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
        rectF.top = OFFSET;
        mPaint.setTextSize(30);
        for (MyLine myLine : myLines) {
            drawLegend(canvas, myLine, rectF);
        }

        // *RIGHT*
        for (MyLine myLine : myLinesRight) {
            drawLegend(canvas, myLine, rectF);
        }

    }

    private void drawLegend(Canvas canvas, MyLine myLine, RectF rectF) {
        mPaint.setColor(myLine.color);
        if (rectF.left + OFFSET_LEGEND+ mPaint.measureText(myLine.name) + 30 > xAxisLength) {
            rectF.top += OFFSET_LEGEND / 2;
            rectF.left = 0;
            rectF.bottom = rectF.top + OFFSET_LEGEND / 2 - 20;
            rectF.right = rectF.left + OFFSET_LEGEND;
            canvas.drawRoundRect(rectF, 2, 2, mPaint);
            mPaint.setColor(getResources().getColor(R.color.default_text_color));
            mPaint.setStrokeWidth(0.1f);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(myLine.name, rectF.right + 15, rectF.bottom, mPaint);
            rectF.left += rectF.width() + mPaint.measureText(myLine.name) + 30;
        } else {
            rectF.right = rectF.left + OFFSET_LEGEND;
            rectF.bottom = rectF.top + OFFSET_LEGEND / 2 - 20;
            canvas.drawRoundRect(rectF, 2, 2, mPaint);
            mPaint.setColor(getResources().getColor(R.color.default_text_color));
            mPaint.setStrokeWidth(0.1f);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(myLine.name, rectF.right + 15, rectF.bottom, mPaint);
            rectF.left += rectF.width() + mPaint.measureText(myLine.name) + 30;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!myLines.isEmpty()) {
            try {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                mIsBeingDragged = false;

                break;
        }
        invalidate();
        return true;
    }

    public void addPoints(ArrayList<MyPoint> points, String s, int color, boolean isRight) {
        if (!isRight)
            myLines.add(new MyLine(s, points, color)); // TODO cost memory
        else
            myLinesRight.add(new MyLine(s, points, color)); // TODO cost memory

        pointCount = myLines.get(0).points.size() / 2; // point count
        invalidate();
    }

    public ArrayList<MyPoint> convert(List<RealTimeData> list, boolean isRight) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            points.add(0, new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getX()));
        }
        if (!isRight)
             yAxisName = list.get(0).getXColName() + "/ " + list.get(0).getTypeUnit();
        else
             yAxisNameRight = list.get(0).getXColName() + "/ " + list.get(0).getTypeUnit();
        return points;
    }

    public  ArrayList<MyPoint> convertY(List<RealTimeData> list, boolean isRight) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            points.add(0, new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getY()));
        }
        if (!isRight)
            yAxisName = list.get(0).getYColName() + "/ " + list.get(0).getTypeUnit();
        else
            yAxisNameRight = list.get(0).getYColName() + "/ " + list.get(0).getTypeUnit();        return points;
    }

    public  ArrayList<MyPoint> convertZ(List<RealTimeData> list, boolean isRight) {

        ArrayList<MyPoint> points = new ArrayList<>();
        for (RealTimeData realTimeData : list) {
            points.add(0, new MyPoint(realTimeData.getSaveTime(), (float) realTimeData.getZ()));
        }
        if (!isRight)
            yAxisName = list.get(0).getZColName() + "/ " + list.get(0).getTypeUnit();
        else
            yAxisNameRight = list.get(0).getZColName() + "/ " + list.get(0).getTypeUnit();        return points;
    }

    public ArrayList<MyLine> getLines() {
        return myLines;
    }

    public ArrayList<MyLine> getLinesRight() {
        return myLinesRight;
    }

    public void setMyLines(ArrayList<MyLine> myLines) {
        this.myLines = myLines;
    }

    public void setMyLinesRight(ArrayList<MyLine> myLines) {
        this.myLinesRight = myLines;
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
