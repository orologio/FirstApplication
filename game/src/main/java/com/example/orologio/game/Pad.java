package com.example.orologio.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by sekitakao on 2017/02/02.
 */
public class Pad implements DrawableItem{
    private final float mTop;
    private float mLeft;
    private final float mBottom;
    private float mRight;

    public Pad(float top, float bottom) {
        mTop = top;
        mBottom = bottom;
    }

    public void setLeftRight(float left, float right) {
        mLeft = left;
        mRight = right;
    }

    public float getTop(){
        return mTop;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }
}
