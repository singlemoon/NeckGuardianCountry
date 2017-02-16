package com.neckguardian.game.exercise;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.model.GameRecord;
import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.HomeFragment;
import com.ogaclejapan.arclayout.ArcLayout;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.KeepScreenOn;
import com.simo.utils.T;
import com.simo.utils.myView.RotatingBall;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 平衡大师
 * Created by 孤月悬空 on 2016/3/1.
 */
public class BalanceMaster extends AppCompatActivity {
    private ImageView imgLeft = null;
    private ImageView imgRight = null;
    private ProgressBar countDown = null;
    private MediaPlayer sounds = null;
    //    private ImageView yogaBall = null;
    private TextView titleText = null;
    //    private TextView history = null;
    private int score = 100;
    //    private ArcLayout.LayoutParams lp = null;
    private RotatingBall changeBall = null;

    private int[] left_imgs = new int[]{
            R.mipmap.yoga1_l, R.mipmap.yoga2_l, R.mipmap.yoga3_l, R.mipmap.yoga4_l, R.mipmap.yoga5_l, R.mipmap.yoga6_l
    };
    private int[] right_imgs = new int[]{
            R.mipmap.yoga1_r, R.mipmap.yoga2_r, R.mipmap.yoga3_r, R.mipmap.yoga4_r, R.mipmap.yoga5_r, R.mipmap.yoga6_r
    };
    private int[] soundArray = new int[]{
            R.raw.left_30, R.raw.right_30, R.raw.left_50, R.raw.right_50, R.raw.left_70, R.raw.left_70,
            R.raw.right_90, R.raw.right_90, R.raw.reset
    };
    private int[] targetAngle = new int[]{
            30, 50, 70, 90
    };

    private boolean isPlay = true;  //游戏运行的标记
    private boolean isPause = false;    //游戏暂停的标记

    private float screen_width = 0;
    private RelativeLayout.LayoutParams rightParams = null;
    private RelativeLayout.LayoutParams leftParams = null;
    private GameRecord mRecord = new GameRecord();

    private Context context = null;
    private static final String TAG = "BalanceMaster";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_balance_master);
        HomeFragment.isPlay = true;

        imgLeft = (ImageView) super.findViewById(R.id.img_left);
        imgRight = (ImageView) super.findViewById(R.id.img_right);
        countDown = (ProgressBar) super.findViewById(R.id.count_down);
//        yogaBall = (ImageView) super.findViewById(R.id.yoga_ball);
        titleText = (TextView) super.findViewById(R.id.title_text);
//        history = (TextView) super.findViewById(R.id.history);
        changeBall = (RotatingBall) super.findViewById(R.id.change_ball);
        context = BalanceMaster.this;
        titleText.setText("平衡大师");

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
//        history.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(BalanceMaster.this, GameHistory.class);
//                startActivity(intent);
//            }
//        });
//        lp = (ArcLayout.LayoutParams) yogaBall.getLayoutParams();
        leftParams = (RelativeLayout.LayoutParams) imgLeft.getLayoutParams();
        rightParams = (RelativeLayout.LayoutParams) imgRight.getLayoutParams();
        mHandler.sendEmptyMessage(NEXT_STEP);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("aaa hh:mm");
        mRecord.setShow_time(dateFormat.format(new Date()));
        mRecord.setGame_time(timeFormat.format(new Date()));
        mRecord.setGame_name(getResources().getString(R.string.balance_master));

        new Thread(new Runnable() {
            int count = 110;        //总游戏时间为96s

            @Override
            public void run() {
                while (isPlay) {
                    if (!isPause) {
                        count--;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = PROGRESS_CHANGE;
                    msg.arg1 = count;
                    mHandler.sendMessage(msg);
                    if (count == 0) {
                        mHandler.sendEmptyMessage(SHOW_DIALOG);
                        isPlay = false;
                    }
                }
            }
        }).start();

        service_init();
        KeepScreenOn.keepScreenOn(context, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                playRing(context, soundArray[0]);
            }
        });
    }

    private boolean isActivate[] = new boolean[]{
            true, true, true, true, true, true, true, true
    };  //用来控制动作

    private static final int START_ANIMATION = 111;
    private static final int PROGRESS_CHANGE = 222;
    private static final int NEXT_STEP = 333;
    private static final int SHOW_DIALOG = 444;
    private static final int ACTION = 555;
    private static final int CHANGE_BALLANGLE = 666;
    private MyHandler mHandler = new MyHandler();

    private class MyHandler extends Handler {

        int position[] = new int[]{
                2, 3, 4, 5
        };  //用来控制动画显示的位置

        int count = 0;
        int angle = 0;
        int animPage = 0;
        int direction = 0;      //判断方向 0 为下去，1 为上去
        private int millisecond = 0; //计时，每隔x秒切换一次动作

        Message next;

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_ANIMATION:
                    Log.i(TAG, "msg.arg2 = " + msg.arg2);
                    if (msg.arg2 % 2 != 0) {
                        if (imgRight.getVisibility() == View.INVISIBLE) {
                            imgRight.setVisibility(View.VISIBLE);
                        }
                        if (imgLeft.getVisibility() == View.VISIBLE) {
                            imgLeft.setVisibility(View.INVISIBLE);
                        }
                        imgRight.setImageResource(right_imgs[msg.arg1]);
                        /*switch (msg.arg1) {
                            case 0:
                                rightParams.height = (int) getRawSize(380);
                                break;
                            case 1:
                                rightParams.height = (int) getRawSize(373);
                                break;
                            case 2:
                                rightParams.height = (int) getRawSize(358);
                                break;
                            case 3:
                                rightParams.height = (int) getRawSize(334);
                                break;
                            case 4:
                                rightParams.height = (int) getRawSize(300);
                                break;
                            case 5:
                                rightParams.height = (int) getRawSize(263.5f);
                                break;
                            default:
                                break;
                            }*/
                    } else {
                        if (imgLeft.getVisibility() == View.INVISIBLE) {
                            imgLeft.setVisibility(View.VISIBLE);
                        }
                        if (imgRight.getVisibility() == View.VISIBLE) {
                            imgRight.setVisibility(View.INVISIBLE);
                        }
                        imgLeft.setImageResource(left_imgs[msg.arg1]);
                        /*switch (msg.arg1) {
                            case 0:
                                leftParams.height = (int) getRawSize(380);
                                break;
                            case 1:
                                leftParams.height = (int) getRawSize(373);
                                break;
                            case 2:
                                leftParams.height = (int) getRawSize(358);
                                break;
                            case 3:
                                leftParams.height = (int) getRawSize(334);
                                break;
                            case 4:
                                leftParams.height = (int) getRawSize(300);
                                break;
                            case 5:
                                leftParams.height = (int) getRawSize(263.5f);
                                break;
                            default:
                                break;
                        }*/
                    }
                    break;
                case PROGRESS_CHANGE:
                    countDown.setProgress(msg.arg1);
                    break;
                case NEXT_STEP:
                    if (count > 7) {
                        Log.i(TAG, "count到8了");
                        break;
                    }
                    for (boolean b : isActivate) {
                        Log.i(TAG, "isActivate[count] = " + b);
                    }
                    playRing(context, soundArray[count]);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isPlay && isActivate[count]) {
                                if (!isPause) {
                                    Log.e(TAG, "count = " + count);
                                    try {
                                        Thread.sleep(1000);
                                        millisecond += 1000;
                                        Log.i(TAG, "millisecond = " + millisecond);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    switch (count) {
                                        case 0:
                                        case 1:
                                            if (millisecond == 9000) {
                                                mHandler.sendEmptyMessage(NEXT_STEP);
                                                isActivate[count] = false;
                                                millisecond = -1000;
                                                animPage = 0;
                                                direction = 0;
                                                count++;
                                                if (second < 3) {
                                                    score = score - 12 + second;
                                                }
                                            } else {
                                                if (animPage == position[0]) {
                                                    direction = 1;
                                                } else if (animPage == 0) {
                                                    direction = 0;
                                                }
                                            }
                                            break;
                                        case 2:
                                        case 3:
                                            if (millisecond == 12000) {
                                                mHandler.sendEmptyMessage(NEXT_STEP);
                                                isActivate[count] = false;
                                                millisecond = -1000;
                                                animPage = 0;
                                                direction = 0;
                                                count++;
                                                if (second < 3) {
                                                    score = score - 12 + second;
                                                }
                                            } else {
                                                if (animPage == position[1]) {
                                                    direction = 1;
                                                } else if (animPage == 0) {
                                                    direction = 0;
                                                }
                                            }
                                            break;
                                        case 4:
                                        case 5:
                                            if (millisecond == 14000) {
                                                mHandler.sendEmptyMessage(NEXT_STEP);
                                                isActivate[count] = false;
                                                millisecond = -1000;
                                                animPage = 0;
                                                direction = 0;
                                                count++;
                                                if (second < 3) {
                                                    score = score - 12 + second;
                                                }
                                            } else {
                                                if (animPage == position[2]) {
                                                    direction = 1;
                                                } else if (animPage == 0) {
                                                    direction = 0;
                                                }
                                            }
                                            break;
                                        case 6:
                                        case 7:
                                            if (millisecond == 16000) {
                                                mHandler.sendEmptyMessage(NEXT_STEP);
                                                isActivate[count] = false;
                                                millisecond = -1000;
                                                animPage = 0;
                                                direction = 0;
                                                if (count < 7) {
                                                    count++;
                                                }
                                                if (second < 3) {
                                                    score = score - 12 + second;
                                                }
                                            } else {
                                                if (animPage == position[3]) {
                                                    direction = 1;
                                                } else if (animPage == 0) {
                                                    direction = 0;
                                                }
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    if (millisecond != -1000) {
                                        if (direction == 0) {
                                            animPage++;
                                        } else if (direction == 1) {
                                            animPage--;
                                        }
                                    }
                                    next = new Message();
                                    next.what = START_ANIMATION;
                                    next.arg1 = animPage;
                                    next.arg2 = count;
                                    mHandler.sendMessage(next);
                                    Log.e(TAG, "animPage = " + animPage);
                                    if (millisecond == -1000) {
                                        break;
                                    }
                                }
                            }
                        }
                    }).start();
                    break;
                case SHOW_DIALOG:
                    final AlertDialog alert = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.util_game, null);
                    LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                    TextView gameRank = (TextView) view.findViewById(R.id.game_rank);
                    TextView gameScore = (TextView) view.findViewById(R.id.game_score);
                    gameScore.setText(String.valueOf(score));
                    if (score >= 90) {
                        gameRank.setText(getResources().getString(R.string.game_rank_1));
                    } else if (score >= 80) {
                        gameRank.setText(getResources().getString(R.string.game_rank_2));
                    } else if (score >= 60) {
                        gameRank.setText(getResources().getString(R.string.game_rank_3));
                    } else {
                        gameRank.setText(getResources().getString(R.string.game_rank_4));
                    }
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.setView(view);
                    alert.show();
                    break;
                case CHANGE_BALLANGLE:
                    /*
                    changeBall.clearAnimation();
                    Animation animation;
                    switch (msg.arg1) {
                        case 1:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_left_45);
                            changeBall.setAnimation(animation);
                            break;
                        case 2:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_stay_45);
                            changeBall.setAnimation(animation);
                            break;
                        case 3:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_left_70);
                            changeBall.setAnimation(animation);
                            break;
                        case 4:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_stay_70);
                            changeBall.setAnimation(animation);
                            break;
                        case 5:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_back_left);
                            changeBall.setAnimation(animation);
                            break;
                        case 6:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_right);
                            changeBall.setAnimation(animation);
                            break;
                        case 7:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_right_stay);
                            changeBall.setAnimation(animation);
                            break;
                        case 8:
                            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_ball_back_right);
                            changeBall.setAnimation(animation);
                            break;
                        default:
                            break;
                    }*/
//                    lp.angle++;
                    changeBall.setBallAngle(angle);
                    angle++;
                    break;
                default:
                    break;
            }
        }

    }

    private void service_init() {
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private int second = 0;

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        int count = 0;
        int split0 = 0;
        int targetAngleNum = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValues = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      try {
                                          String text = new String(txValues, "UTF-8");
                                          String split[] = text.split("[.]");
                                          if (!text.contains("na") && !text.contains("ad")) {
                                              split0 = Integer.parseInt(split[0]);
//                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                              count++;
                                              switch (count) {
                                                  case 2:
                                                      if (split0 > 0) {
                                                          changeBall.setBallAngle(90 - split0);
                                                      } else {
                                                          changeBall.setBallAngle(-split0);
                                                      }
//                                        Log.i(TAG, "Case 2 split0 = " + split0);
//                                        Log.i(TAG, "judgeAble" + judgeAble);
                                                      //偏差在左右五度以内，开始计时
                                                      if (changeBall.getBallAngle() - targetAngle[targetAngleNum] < 5
                                                              && targetAngle[targetAngleNum] - changeBall.getBallAngle() < 5) {
                                                          second++;
                                                          if (second == 5) {
                                                              second = 0;
                                                          }
                                                      }
                                                      if (split0 > -88.5 && split0 < 0) {
                                                          Log.i(TAG, "左倾");
                                                      }
                                                      if (split0 < 88.5 && split0 > 0) {
                                                          Log.i(TAG, "右倾");
                                                      }
                                                      break;
                                                  case 3:
                                                      count = 0;
                                                      break;
                                                  default:
                                                      break;
                                              }
                                          }
                                      } catch (UnsupportedEncodingException e) {
                                          e.printStackTrace();
                                      }
                                  }
                              }

                );
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void playRing(final Context context, final int sound) {

        sounds = MediaPlayer.create(context, sound);

        if (sounds != null) {
            sounds.stop();
        }
        sounds.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (sounds != null) {
                    sounds.release();
                    sounds = null;
                }
                Log.i("ChooseRingActivity" + sound, "播放结束");
            }
        });
        try {
            sounds.prepare();
            sounds.start();
            Log.i("ChooseRingActivity" + sound, "正在播放");
        } catch (IOException e) {
            T.showShort(context, "播放失败");
        }
    }

    /**
     * 将宽高设为dip
     *
     * @param value
     * @return
     */
    public float getRawSize(float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                exit();
                break;
            default:
                break;
        }
    }

    private void exit() {
        isPlay = false;
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPlay = false;
    }

    @Override
    protected void onDestroy() {
        isPlay = false;
        super.onDestroy();
        if (sounds != null) {
            sounds.stop();
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(UARTStatusChangeReceiver);
        HomeFragment.isPlay = false;
        KeepScreenOn.keepScreenOn(context, false);
        DatabaseOperate databaseOperate = new DatabaseOperate(context);
        mRecord.setGame_use_time(countDown.getMax() - countDown.getProgress());
        mRecord.setCompleteness((int) ((countDown.getMax() - countDown.getProgress()) / (float) countDown.getMax() * 100));
        databaseOperate.insertGameRecord(mRecord);
    }
}

/*new Thread(new Runnable() {       //控制小球的动画
            int count = 0;

            @Override
            public void run() {
                while (isPlay) {
                    try {
                        Thread.sleep(1000);
                        count += 1000;

                        if (count == 2000) {    //球开始动
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 1;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 4000) {    //球暂停两秒
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 2;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 6000) {    //球再下去25度
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 3;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 7000) {     //停两秒
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 4;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 9000) {     //归零
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 5;
                            mHandler.sendMessage(msg);
                        }
                        *//*if (count == 11000){     //停两秒
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 5;
                            mHandler.sendMessage(msg);
                        }*//*
                        if (count == 13000) {    //向右
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 6;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 16000) {   //停两秒
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 7;
                            mHandler.sendMessage(msg);
                        }
                        if (count == 18000) {   //回归
                            Message msg = new Message();
                            msg.what = CHANGE_BALLANGLE;
                            msg.arg1 = 8;
                            mHandler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/


/*
        new Thread(new Runnable() {
            int i = 0;
            int direction = 0;

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        millisecond += 1000;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = START_ANIMATION;
                    if (millisecond <= 12000) {
                        msg.arg2 = 1;
                        if (millisecond >= 8000) {
                            direction = 2;
                            if (i > 0) {
                                i--;
                            } else {
                                i = 0;
                            }
                        }
                    } else {
                        msg.arg2 = 2;
                        if (millisecond == 12200) {
                            direction = 0;
                        }
                        if (millisecond >= 20000) {
                            direction = 2;
                            if (i > 0) {
                                i--;
                            } else {
                                i = 0;
                            }
                        }
                    }
                    msg.arg1 = i;
                    mHandler.sendMessage(msg);
                    if (direction == 0) {
                        i++;
                    } else if (direction == 1) {
                        i--;
                    }
                    if (i == 4) {
                        direction = 1;
                    } else if (i == 0) {
                        direction = 0;
                    }

                }
            }
        }).start();*/
/*
//    这里是改变球的角度的测试
    new Thread(new Runnable(){
        @Override
        public void run(){
                while(isPlay){
                    if(!isPause){
                        try{
                            Thread.sleep(50);
                            mHandler.sendEmptyMessage(CHANGE_BALLANGLE);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
        }
    }).start();
        */
