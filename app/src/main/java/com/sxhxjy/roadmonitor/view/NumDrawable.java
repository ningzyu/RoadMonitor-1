package com.sxhxjy.roadmonitor.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 2016/11/21
 *
 * @author Michael Zhao
 */

public class NumDrawable extends Drawable {
    private Paint mPaint;
    private int mNum;

    public NumDrawable(int num) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNum = num;
    }

    @Override
    public void draw(Canvas canvas) {

        if (mNum == 0) return;

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getBounds().right+5, 0, 35f, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(45);
        mPaint.setStrokeWidth(2);
        final String s = "" + mNum;
        final float len = mPaint.measureText(s);
        canvas.drawText("" + mNum, getBounds().right+5 - len / 2, 15, mPaint);

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setNum(int num) {
        mNum = num;
        invalidateSelf();
    }
}
