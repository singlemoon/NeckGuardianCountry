package com.neckguardian.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.UIUtils;
import com.simo.utils.VibratorUtil;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by 孤月悬空 on 2016/2/24.
 */
public class RemindActivity extends BaseActivity {

    private TextView timePicker = null;
    private RippleBackground rippleBackground = null;
    private ImageView centerImage = null;

    private static final String TAG = "RemindReceiver";
    private MediaPlayer mediaPlayer = null;
    private Context context = null;
    private static final int VOICE = 998;
    private static final int RING = 889;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_remind);

        HomeFragment.isRemind = true;

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wl.alpha = 0.6f;
        window.setAttributes(wl);

        context = RemindActivity.this;
        timePicker = (TextView) super.findViewById(R.id.time_picker);
        centerImage = (ImageView) super.findViewById(R.id.centerImage);
        rippleBackground = (RippleBackground) super.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        UIUtils.hideTitleBar(this, R.id.activity_remind);

        if (SPPrivateUtils.getBoolean(context, State.useBell, false) &&
                SPPrivateUtils.getBoolean(context, State.useShock, false)) {
            playRing(context, RING);
            VibratorUtil.Vibrate(context, new long[]{1000, 500, 1000, 500}, true);
        } else if (SPPrivateUtils.getBoolean(context, State.useVoice, false) &&
                SPPrivateUtils.getBoolean(context, State.useShock, false)) {
            playRing(context, VOICE);
            VibratorUtil.Vibrate(context, new long[]{1000, 500, 1000, 500}, true);
        } else if (SPPrivateUtils.getBoolean(context, State.useShock, false)) {
            VibratorUtil.Vibrate(context, new long[]{1000, 500, 1000, 500}, true);
        } else if (SPPrivateUtils.getBoolean(context, State.useBell, false)) {
            playRing(context, RING);
        } else if (SPPrivateUtils.getBoolean(context, State.useVoice, false)) {
            playRing(context, VOICE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 61; i++) {
                    try {
                        Thread.sleep(1000);
                        mHandler.sendEmptyMessage(CHANGE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static final int CHANGE_TIME = 111;
    private Handler mHandler = new MyHandler();

    private class MyHandler extends Handler {
        int count = 59;
        DecimalFormat df = new DecimalFormat("00");

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_TIME:
                    if (count >= 0) {
                        timePicker.setText("0:" + df.format(count));
                        count--;
                    } else {
                        RemindActivity.this.finish();
                    }
                    break;
            }
        }
    }

    private void playRing(final Context context, int what) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Log.i(TAG, "sppBell" + SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring));

        if (what == VOICE) {
            if (getIntent().getStringExtra("what").equals("wrong_sitting")) {
                mediaPlayer = MediaPlayer.create(context, SPPrivateUtils.getInt(context, State.useVoice, R.raw.wrong_pose));
            } else if (getIntent().getStringExtra("what").equals("long_time")){
                mediaPlayer = MediaPlayer.create(context, SPPrivateUtils.getInt(context, State.useVoice, R.raw.long_time));
            }
        } else if (what == RING) {
            mediaPlayer = MediaPlayer.create(context, SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring));
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                Log.i("ChooseRingActivity" + SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring), "正在播放");
            }
        });
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.i("ChooseRingActivity" + SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring), "正在播放");
        } catch (IOException e) {
            T.showShort(context, "播放失败");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        VibratorUtil.cancel();
        HomeFragment.isRemind = false;
    }
}
