package com.simo.utils.myView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

import com.neckguardian.R;

/**
 * 绕中心旋转的球
 * Created by 孤月悬空 on 2016/1/19.
 */
public class RotatingBall extends View {
    private float radius;
    private Context context;
    private Bitmap ball;
    private float ballAngle;

    public RotatingBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RotatingBall,
                0, 0);
        try {
            ballAngle = a.getFloat(R.styleable.RotatingBall_ball_angle, 0);
        } finally {
            a.recycle();
        }
        initView();
    }

    private void initView() {
        ball = BitmapFactory.decodeResource(context.getResources(), R.mipmap.yoga_ball);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        radius = getWidth() / 2;
        float ballRadian = (float) (ballAngle / 180f * Math.PI);
//        float x = (float) (radius + 32 + (radius + 5 - ball.getWidth()) * Math.sin(ballRadian));
        float x = (float) (radius + (radius - ball.getWidth()) * Math.sin(ballRadian));
        float y = (float) (radius - (radius - ball.getWidth()) * Math.cos(ballRadian));
        canvas.drawBitmap(ball, x - ball.getWidth() / 2, y - ball.getHeight() / 2, null);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
        requestLayout();
    }

    public float getBallAngle() {
        return ballAngle;
    }

    public void setBallAngle(float ballAngle) {
        this.ballAngle = ballAngle;
        invalidate();
        requestLayout();
    }
}
