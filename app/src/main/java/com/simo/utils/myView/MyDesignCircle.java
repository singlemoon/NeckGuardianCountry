package com.simo.utils.myView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.neckguardian.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 孤月悬空 on 2016/1/19.
 */
public class MyDesignCircle extends View {
    private float radius;
    private float outRadius;
    private Context context;
    private Bitmap ball;
    private float ballAngle;
    private float values[];
    private List<Integer> newLineAngle;

    public MyDesignCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MyDesignCircle,
                0, 0);
        try {
            radius = a.getDimension(R.styleable.MyDesignCircle_radius, dp2px(75));
            ballAngle = a.getFloat(R.styleable.MyDesignCircle_ballAngle, 180);
        } finally {
            a.recycle();
        }
        initView();
    }

    private void initView() {
        newLineAngle = new ArrayList<>();
        ball = BitmapFactory.decodeResource(context.getResources(), R.mipmap.test_ball);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Paint paint = new Paint();

//        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(context.getResources().getColor(R.color.text_color_67_gray));
//        canvas.drawOval(new RectF(0, 0, 2 * (radius + 20), 2 * (radius + 20)), paint);

        for (int i = 0; i < 180; i++) {
            float angle = (float) (i * 2 / 180f * Math.PI);
            float x = (float) (radius + 15 + (radius + 15) * Math.sin(angle));
            float y = (float) (radius + 15 - (radius + 15) * Math.cos(angle));
//            Log.i("myDesignCircle", String.valueOf(Math.sin(Math.PI)));
//            Log.i("myDesignCircle", String.valueOf(Math.cos(Math.PI)));
            canvas.drawLine(radius + 15, radius + 15, x, y, paint);
        }

        paint.setColor(getResources().getColor(R.color.text_color_blue_2f));
        for (int i = 0; i < newLineAngle.size(); i++) {
            float angle = (float) (newLineAngle.get(i) / 180f * Math.PI);
            float x = (float) (radius + 15 + (radius + 15) * Math.sin(angle));
            float y = (float) (radius + 15 - (radius + 15) * Math.cos(angle));
            canvas.drawLine(radius + 15, radius + 15, x, y, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(context.getResources().getColor(R.color.white));
        RectF rect = new RectF(45, 45, radius * 1.9f, radius * 1.9f);
        canvas.drawOval(rect, paint);

        paint.setColor(context.getResources().getColor(R.color.dash_line_color));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(rect, paint);
//        for (int i = 0; i < 360; i++) {
        float ballRadian = (float) (ballAngle / 360f * Math.PI);
        float x = (float) (radius + 32 + (radius + 5 - ball.getWidth()) * Math.sin(ballRadian));
        float y = (float) (radius + 32 - (radius + 5 - ball.getWidth()) * Math.cos(ballRadian));
        canvas.drawBitmap(ball, x - ball.getWidth() / 2, y - ball.getHeight() / 2, null);
        if (!isLineRepeat() && newLineAngle.size() < 360) {
            newLineAngle.add((int) ballAngle);
        }
//        }
//        canvas.drawLine(40, 228, 416, 228, paint);
//        canvas.drawBitmap(ball, 228-ball.getWidth()/2, 40, null);
    }

    private boolean isLineRepeat() {
        for (int i = 0; i < newLineAngle.size(); i++) {
            if (newLineAngle.get(i) == (int) ballAngle) {
                return true;
            }
        }
        return false;
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

    private int dp2px(float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
