package com.example.orologio.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by sekitakao on 2017/02/03.
 */

public class Ball implements DrawableItem{
    private float mX;
    private float mY;
    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;

    private final float mInitialSpeedX;
    private final float mInitialSpeedY;
    private final float mInitialX;
    private final float mInitialY;

    public float getSpeedX(){
        return mSpeedX;
    }

    public float getSpeedY(){
        return mSpeedY;
    }

    public float getX(){
        return mX;
    }

    public float getY(){
        return mY;
    }

    public void setSpeedX(float speedX){
        mSpeedX = speedX;
    }

    public void setSpeedY(float speedY){
        mSpeedY = speedY;
    }

    public Ball(float radius, float initialX, float initialY){
        mRadius = radius;
        mSpeedX = radius;
        mSpeedY = -radius;
        mX = initialX;
        mY = initialY;

        mInitialSpeedX = mSpeedX*2;
        mInitialSpeedY = mSpeedY*2;
        mInitialX = mX;
        mInitialY = mY;
    }

    public void move(){
        mX += mSpeedX;
        mY += mSpeedY;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX, mY, mRadius, paint);
    }

    public void restart(){
        mX = mInitialX;
        mY = mInitialY;
        mSpeedX = mInitialSpeedX*((float)Math.random()-0.5f);
        mSpeedY = mInitialSpeedY;
    }
}
