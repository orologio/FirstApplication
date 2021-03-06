package com.example.orologio.game;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sekitakao on 2017/01/10.
 */

public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener{
    volatile private boolean mIsRunnable;
    volatile private float mTouchedX;
    volatile private float mTouchedY;
    private Thread mThread;

    public GameView(Context context){
        super(context);
        setSurfaceTextureListener(this);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }

    public void start(){
        mThread = new Thread(new Runnable(){
            @Override
            public void run(){
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);

                while(true){
                    long startTime = System.currentTimeMillis();
                    synchronized(GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);

                        float padLeft = mTouchedX-mPadHalfWidth;
                        float padRight = mTouchedX+mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        //mPad.draw(canvas, paint);
                        /*
                        canvas.drawCircle(mTouchedX, mTouchedY, 50, paint);
                        */
                        mBall.move();
                        float ballTop = mBall.getY()-mBallRadius;
                        float ballLeft = mBall.getX()-mBallRadius;
                        float ballBottom = mBall.getY()+mBallRadius;
                        float ballRight = mBall.getX()+mBallRadius;
                        if(ballLeft<0 && mBall.getSpeedX()<0 || ballRight>=getWidth() && mBall.getSpeedX()>0){
                            mBall.setSpeedX(-mBall.getSpeedX());
                        }
                        if(ballTop<0){
                            mBall.setSpeedY(-mBall.getSpeedY());
                        }
                        if(ballTop > getHeight()){
                            if(mLife > 0){
                                mLife--;
                                mBall.restart();
                            }
                        }

                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);
                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                        }
                        if(topBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedY());
                            topBlock.collision();
                        }
                        if(rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                        }
                        if(bottomBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedY());
                            bottomBlock.collision();
                        }

                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();
                        if(ballBottom>padTop && ballBottom-ballSpeedY<padTop && padLeft<ballRight && padRight>ballLeft){
                            if(ballSpeedY<mBlockHeight/3){
                                ballSpeedY *= -1.05f;
                            }else{
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX()+(mBall.getX()-mTouchedX)/10;
                            if(ballSpeedX > mBlockWidth/5){
                                ballSpeedX = mBlockWidth/5;
                            }
                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);
                        }

                        for (DrawableItem item : mBlockList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);

                        long sleepTime = 16-(System.currentTimeMillis()-startTime);
                        if(sleepTime>0){
                            try{
                                Thread.sleep(sleepTime);
                            }catch(InterruptedException e){
                            }
                        }
                    }
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }
    public void stop() {
        mIsRunnable = false;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);

    }


    private ArrayList<DrawableItem> mBlockList;

    private Pad mPad;
    private float mPadHalfWidth;

    private Ball mBall;
    private float mBallRadius;

    private float mBlockWidth;
    private  float mBlockHeight;
    static final int BLOCK_COUNT = 100;

    private int mLife;

    public void readyObjects(int width, int height){
        mBlockWidth = width/10;
        mBlockHeight = height/20;
        mBlockList = new ArrayList<DrawableItem>();
        for(int i = 0; i < BLOCK_COUNT; i++){
            float blockTop = i/10*mBlockHeight;
            float blockLeft = i%10*mBlockWidth;
            float blockBottom = blockTop+mBlockHeight;
            float blockRight = blockLeft+mBlockWidth;
            mBlockList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }

        mPad = new Pad(height*0.8f, height*0.85f);
        mBlockList.add(mPad);
        mPadHalfWidth = width / 10;

        mBallRadius = width<height ? width/40 : height/40;
        mBall = new Ball(mBallRadius, width/2, height*0.75f);
        mBlockList.add(mBall);

        mLife = 5;
    }


    private Block getBlock(float x, float y){
        int index = (int)(x/mBlockWidth)+(int)(y/mBlockHeight)*10;
        if(0 <=index && index<BLOCK_COUNT){
            Block block = (Block)mBlockList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
    }


    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }
}
