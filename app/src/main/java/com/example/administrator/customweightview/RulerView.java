package com.example.administrator.customweightview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/10/17.
 */

public class RulerView extends View {
    private static final String TAG = "RulerView";

    private static final int WEIGHT = 5000;
    private static final int HEIGHT = 200;
    private static final int SCROLL = 2000;

    private Paint mPaint = new Paint();
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mTextsPaint;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        velocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mTextPaint = new Paint();
        mLinePaint = new Paint();
        mTextsPaint = new Paint();
        mTextsPaint.setAntiAlias(true);
        mTextsPaint.setTextSize(40);
        mTextsPaint.setColor(Color.RED);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setTextSize(200);
        mTextPaint.setAntiAlias(true);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.GREEN);
        mLinePaint.setStrokeWidth(8);
    }

    private float dis = 0;
    private float x;
    private float y;
    private float startX = 0;
    private int velocity;
    private VelocityTracker mTracker;
    private ObjectAnimator mAnimator;

    public void setDis(float dis){
        this.dis = dis;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx;
        float dy;
        int action = event.getAction();
        if (mTracker == null){
            mTracker = VelocityTracker.obtain();
        } else {
            mTracker.clear();
        }
        mTracker.addMovement(event);
        mTracker.computeCurrentVelocity(1000);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                dx = x - event.getX();
                dy = y - event.getY();
                if (Math.abs(mTracker.getXVelocity()) > velocity*20){
                    if (mTracker.getXVelocity() > 0){
                        mAnimator = ObjectAnimator.ofFloat(this, "dis", 0, -1000);
                        mAnimator.setRepeatCount(1);
                        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        mAnimator.start();
                    } else {
                        mAnimator = ObjectAnimator.ofFloat(this, "dis", 0, 1000);
                        mAnimator.setRepeatCount(1);
                        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        mAnimator.start();
                    }

                    return true;
                }
                if (Math.abs(dy) < Math.abs(dx)){
                    dis = dx/2;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() * 10 * 10;
        int height = getHeight()/2;
        int height1 = height - HEIGHT /2;
        int height2 = height - HEIGHT /4;
        int height3 = height + HEIGHT /4;
        float unit = (float) (width * 1000 / WEIGHT);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(5);
        canvas.drawLine(0, height1, width, height1, mPaint);

        startX += dis;

        if (startX < 0 && Math.abs(startX) > getWidth()/2){
            startX = - getWidth() / 2;
        }

        if (startX > 0 && Math.abs(startX) > width - getWidth()/2){
            startX = width - getWidth()/2;
        }

        for (int i = 0; i < WEIGHT; i++) {
            float pos = (float) (i * unit / 1E3) - startX;

            if (Math.abs(pos - getWidth() / 2) < (unit / 2 / 1E3)) {
                String text = "" + (i / 10) + "." + (i % 10);
                float w = mTextPaint.measureText(text, 0, text.length());
                canvas.drawText(text, getWidth() / 2 - w / 2, height - HEIGHT, mTextPaint);
            }
            if (i % 10 == 0) {
                canvas.drawLine(pos, height1, pos, height, mPaint);
                String text = "" + (i / 10);
                float w = mTextsPaint.measureText(text, 0, text.length());
                canvas.drawText(text, pos - w / 2, height3, mTextsPaint);
            } else {
                canvas.drawLine(pos, height1, pos, height2, mPaint);
            }
        }

        canvas.drawLine(getWidth()/2, height1, getWidth()/2, height1 + HEIGHT, mLinePaint);


    }
}
