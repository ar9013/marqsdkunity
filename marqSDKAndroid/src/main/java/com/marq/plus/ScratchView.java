package com.marq.plus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;


class ScratchView extends View {
    
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path  mPath;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private int screenWidth, screenHeight;
    private int actionWidth, actionHeight;
    private int actionCoodX, actionCoodY;
    private Handler handler;
    
    public ScratchView(
            Context context, 
            Handler agent,
            int viewWidth, 
            int viewHeight, 
            int maskWidth, 
            int maskHeight, 
            Bitmap mask,
            int maskX,
            int maskY) {
        
        super(context);
        setFocusable(true);

        screenWidth = viewWidth;
        screenHeight = viewHeight;
        
        actionWidth = maskWidth;
        actionHeight = maskHeight;

        Bitmap bm = mask;
        
        //bm = setBitmapAlpha(bm, 255); 
        bm = scaleBitmapMaskScreen(bm, actionWidth, actionHeight);
        
        DebugLog.LOGE("cover bitmap width = ", Integer.toString(bm.getWidth()));
        DebugLog.LOGE("cover bitmap height = ", Integer.toString(bm.getHeight()));

        actionCoodX = maskX;
        actionCoodY = maskY; 
        
        actionCoodY = (screenHeight - bm.getHeight()) / 2;

        setCoverBitmap(bm);
        handler = agent;
    }
    
    private Bitmap setBitmapAlpha(Bitmap bm, int alpha) {
        int[] argb = new int[bm.getWidth() * bm.getHeight()];
        
        bm.getPixels(argb, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

        for (int i = 0; i < argb.length; i++) {

            argb[i] = ((alpha << 24) | (argb[i] & 0x00FFFFFF));
        }
        
        return Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
    }

    private Bitmap scaleBitmapMaskScreen(Bitmap src, int width, int height) {

        DebugLog.LOGE("SCREEN_W = ", Integer.toString(this.screenWidth));
        DebugLog.LOGE("SCREEN_H = ", Integer.toString(this.screenHeight));
        
        DebugLog.LOGE("MASK_W = ", Integer.toString(width));
        DebugLog.LOGE("MASK_H = ", Integer.toString(height));
        
        Matrix mx = new Matrix();
    
        float fScaleW = (float) screenWidth / (float) src.getWidth();
        float fScaleH = (float) screenHeight / (float) src.getHeight();    
        float fScale = fScaleW;
        
        if(fScaleW > fScaleH) {
            fScale = fScaleH;
        }
        //DebugLog.LOGE( "fScale", Float.toString(fScale));
        
        mx.setScale(fScale, fScale);
        
        Bitmap bmResized = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), mx, true);
        

        DebugLog.LOGE( "bmResized.height", Integer.toString(bmResized.getHeight()));
        DebugLog.LOGE( "bmResized.width", Integer.toString(bmResized.getWidth()));

        return bmResized;
    }

    private void setCoverBitmap(Bitmap bm) {
        // setting paint
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAntiAlias(true);
        
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(70);
        
        //set path
        mPath =  new Path();;

        // converting bitmap into mutable bitmap
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mBitmap);
        // drawXY will result on that Bitmap
        // be sure parameter is bm, not mBitmap
        //mCanvas.drawBitmap(bm, 0, 0, null);
        
        //float x = (float) (screenWidth - actionWidth) / 2;
        //float y = (float) (screenHeight - actionHeight) / 2;
        
        //mCanvas.drawBitmap(bm, x, y, null);
        
        mCanvas.drawBitmap(bm, actionCoodX, actionCoodY, null);
    }

   

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        mCanvas.drawPath(mPath, mPaint);
        super.onDraw(canvas);
    }
    
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }
    
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(
                    mX, 
                    mY, 
                    ((x + mX) / 2), 
                    ((y + mY) / 2));
            mX = x;
            mY = y;
        }
    }
    
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        Message msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        
        return true;
    }
}