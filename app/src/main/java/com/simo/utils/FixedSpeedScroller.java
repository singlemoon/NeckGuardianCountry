package com.simo.utils;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 控制Viewpager滑动速度的类
 * Created by 孤月悬空 on 2016/2/20.
 */
public class FixedSpeedScroller extends Scroller {
    private int mDuration = 1500;
    public FixedSpeedScroller(Context context) {
        super(context);
    }
    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
    public void setmDuration(int time){
        mDuration = time;
    }
    public int getmDuration(){
        return mDuration;
    }

}