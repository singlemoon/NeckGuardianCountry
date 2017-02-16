package com.neckguardian.game.exercise;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.model.GameRecord;
import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.activity.MainActivity;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.KeepScreenOn;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健脖操
 * Created by 孤月悬空 on 2016/2/29.
 */
public class NeckGym extends AppCompatActivity {

    private RelativeLayout showInLeft = null;     //左边区域
    private RelativeLayout showInRight = null;    //右边区域
    private TextView titleText = null;
    private TextView surplus = null;
    private TextView gameRight = null;
    private View judgeLine = null;  //当方块滑到这里时才能开始判断

    private List<Map<String, Object>> imgs = null;     //存放所有方块
    private HomeFragment.MyHandler hfHandler = null;    //消息通道

    private float screen_width;         //获取屏幕宽度
    private float screen_height;        //获取屏幕高度
    private boolean isPlay = true;      //是否正在运行
    private boolean isPause = false;    //是否处于暂停状态
    private int location = 234;

    private static final int LEFT = 997;    //图片出现在左
    private static final int RIGHT = 998;   //图片出现在右
    float judgeLineY;
    float judgeLineHeight;
    private int score = 0;

    private static final String TAG = "NeckGym";
    private Context context;
    private GameRecord mRecord = new GameRecord();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_neckgym);
        HomeFragment.isPlay = true;
        context = NeckGym.this;

        showInLeft = (RelativeLayout) super.findViewById(R.id.show_in_left);
        showInRight = (RelativeLayout) super.findViewById(R.id.show_in_right);
        titleText = (TextView) super.findViewById(R.id.title_text);
        surplus = (TextView) super.findViewById(R.id.surplus);
        gameRight = (TextView) super.findViewById(R.id.game_right);
        judgeLine = super.findViewById(R.id.judge_line);

        imgs = new ArrayList<>();
        hfHandler = new HomeFragment.MyHandler(MainActivity.getHomeFragment());

        createImg(RIGHT, 2);
        LocalBroadcastManager.getInstance(context).registerReceiver(neckStateReceiver, makeGattUpdateIntentFilter());
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();
        judgeLineY = getRawSize(480f);
        judgeLineHeight = getRawSize(25f);
        //设置背景常亮
        KeepScreenOn.keepScreenOn(context, true);
        titleText.setText("健脖操");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("aaa hh:mm");
        mRecord.setShow_time(dateFormat.format(new Date()));
        mRecord.setGame_time(timeFormat.format(new Date()));
        mRecord.setGame_name(getResources().getString(R.string.neck_gym));
        new Thread(new Runnable() {
            int count = 0;      //计时用，总游戏时间为60s

            @Override
            public void run() {
                Log.i(TAG, "screen_height = " + screen_height);
                while (isPlay) {
                    if (!isPause) {
                        mHandler.sendEmptyMessage(CHANGE_Y);
                    }
                    try {
                        Thread.sleep(50);
                        if (!isPause) {
                            count += 50;
                        }
                        if (count % 1000 == 0) {
                            mHandler.sendEmptyMessage(CHANGE_TIME);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    int[] imgTypes = new int[]{
            R.mipmap.neckgym_l_n,
            R.mipmap.neckgym_r_n
    };//出现的图片

    private void createImg(int position, long flag) {
        LinearLayout layout;
        LinearLayout.LayoutParams layoutParams;
        ImageView imageView;
        Map<String, Object> map;

        switch (position) {
            case RIGHT:
                map = new HashMap<>();
                layout = new LinearLayout(NeckGym.this);
                layoutParams = new LinearLayout.LayoutParams(
                        (int) getRawSize(100f), (int) getRawSize(100f));
                layout.setLayoutParams(layoutParams);
                layout.setGravity(Gravity.CENTER);
                layout.setY(getRawSize(100f));
                imageView = new ImageView(context);
                if (flag % 2 == 0) {
                    imageView.setImageResource(imgTypes[0]);
                    map.put("imgType", imgTypes[0]);

                } else {
                    imageView.setImageResource(imgTypes[1]);
                    map.put("imgType", imgTypes[1]);
                }
                layout.addView(imageView);
                map.put("layout", layout);
                map.put("imageView", imageView);
                imgs.add(map);
                showInRight.addView(layout);
                break;
            case LEFT:
                layout = new LinearLayout(NeckGym.this);
                map = new HashMap<>();
                layoutParams = new LinearLayout.LayoutParams(
                        (int) getRawSize(100f), (int) getRawSize(100f));
                layout.setLayoutParams(layoutParams);
                layout.setGravity(Gravity.CENTER);
                layout.setY(getRawSize(100f));
                imageView = new ImageView(context);
                if (flag % 2 == 0) {
                    imageView.setImageResource(imgTypes[0]);
                    map.put("imgType", imgTypes[0]);
                } else {
                    imageView.setImageResource(imgTypes[1]);
                    map.put("imgType", imgTypes[1]);
                }
                layout.addView(imageView);
                map.put("layout", layout);
                map.put("imageView", imageView);
                imgs.add(map);
                showInLeft.addView(layout);
                break;
            default:
                break;
        }
    }

    int count = 59;     //计时
    private static final int CHANGE_Y = 111;
    private static final int SHOW_DIALOG = 222;
    private static final int CHANGE_TIME = 333;
    private static final int CHANGE_SCORE = 444;
    private static final int CHANGE_TEXT = 555;
    private MyHandler mHandler = new MyHandler();

    private class MyHandler extends Handler {

        int speed = 10;
        long lastSecond = System.currentTimeMillis();
        int where = LEFT;
        int amount = 0;
        int rightPose = 0;
        DecimalFormat df = new DecimalFormat("00");

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (System.currentTimeMillis() - lastSecond > 3000) {
                if (amount < 20) {
                    if (where == LEFT) {
                        createImg(LEFT, 1);
                        where = RIGHT;
                    } else {
                        createImg(RIGHT, 2);
                        where = LEFT;
                    }
                }
//                Log.i(TAG, "有没有输出");

                amount++;
                lastSecond = System.currentTimeMillis();
            }
            switch (msg.what) {
                case CHANGE_Y:
                    int i = 0;
                    for (Map<String, Object> map : imgs) {
                        LinearLayout layout = (LinearLayout) map.get("layout");
                        layout.setY(layout.getY() + speed);
                        if (layout.getY() + layout.getHeight() > judgeLineY &&
                                layout.getY() < judgeLineY + judgeLineHeight) {
                            location = i;
                        } else if (layout.getY() > judgeLineY + judgeLineHeight) {
//                            Log.w(TAG, "layout.getY() = " + layout.getY());
//                            Log.w(TAG, "judge.getY() + judgeLine.getHeight() = " + judgeLine.getY() + judgeLine.getHeight());
                            if (map.containsKey("isTrue")) {
                                if (!(boolean) map.get("isTrue")) {
                                    if (((int) map.get("imgType")) == imgTypes[0]) {
                                        ((ImageView) map.get("imageView")).setImageResource(R.mipmap.neckgym_l_f);
                                    } else {
                                        ((ImageView) map.get("imageView")).setImageResource(R.mipmap.neckgym_r_f);
                                    }
                                }
                            } else {
                                if (((int) map.get("imgType")) == imgTypes[0]) {
//                                    Log.w(TAG, "难道是跑到这里来了");
                                    ((ImageView) map.get("imageView")).setImageResource(R.mipmap.neckgym_l_f);
                                } else {
//                                    Log.w(TAG, "我就看看你是不是到这里来了");
                                    ((ImageView) map.get("imageView")).setImageResource(R.mipmap.neckgym_r_f);
                                }
                            }
                        }
                        i++;
                    }
                    if (imgs.size() > 0) {
                        if (((LinearLayout) imgs.get(0).get("layout")).getY() == screen_height) {
                            imgs.remove(0);
                            if (imgs.size() == 0) {
                                isPlay = false;
                                mHandler.sendEmptyMessage(SHOW_DIALOG);
                            }
                            imgs.size();
                        }
                    }
                    break;
                case SHOW_DIALOG:
                    final AlertDialog alert = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.util_game, null);
                    TextView gameRank = (TextView) view.findViewById(R.id.game_rank);
                    TextView gameScore = (TextView) view.findViewById(R.id.game_score);
                    LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                    gameScore.setText(String.valueOf(score));
                    if (score >= 1800) {
                        gameRank.setText(getResources().getString(R.string.game_rank_1));
                    } else if (score >= 1600) {
                        gameRank.setText(getResources().getString(R.string.game_rank_2));
                    } else if (score >= 1200) {
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
                case CHANGE_TIME:
                    surplus.setText("剩余时间 0:" + df.format(count));
                    if (count > 0) {
                        count--;
                    }
                    break;
                case CHANGE_SCORE:
                    rightPose++;
                    gameRight.setText("正确姿势：" + rightPose);
                    break;
                case CHANGE_TEXT:
                    break;
                default:
                    break;
            }
        }
    }

    private BroadcastReceiver neckStateReceiver = new BroadcastReceiver() {

        int count = 0;
        int split0 = 0;
        int lastSplit0;
        Message msg;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String[] split = text.split("[.]");
//                            Log.i(TAG, "text" + text);
                            if (!text.contains("na") && !text.contains("ad")) {
                                split0 = Integer.parseInt(split[0]);
//                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                count++;
                                switch (count) {
                                    case 2:
//                                        Log.i(TAG, "Case 2 split0 = " + split0);
//                                        Log.i(TAG, "judgeAble" + judgeAble);
                                        /*Message msg= new Message();
                                        msg.what = CHANGE_TEXT;
                                        msg.obj = split0;
                                        msg.arg1 = 1;
                                        mHandler.sendMessage(msg);
                                        if (split0 > 0 && split0 < 90) {
                                            Log.i(TAG, "在左边");

                                            if (lastSplit0>0){
                                                if (split0 - lastSplit0 > 1) {
                                                    Log.i(TAG, "左往左");

                                                } else {
                                                    Log.i(TAG, "左往回");

                                                }
                                            } else if (lastSplit0 < 0) {
                                                Log.i(TAG, "右往左");

                                            }
                                        } else if (split0 < 0 && split0 > -90) {
                                            Log.i(TAG, "在右边");

                                            if (lastSplit0 > 0) {
                                                Log.i(TAG, "左往右");

                                            } else{
                                                if (lastSplit0 - split0 > 1) {
                                                    Log.i(TAG, "右往右");

                                                } else {
                                                    Log.i(TAG, "右往回");

                                                }
                                            }
                                        }*/
                                        if (split0 > -88.5 && split0 < 0) {
                                            Log.i(TAG, "左倾");
                                            if (location < imgs.size()) {
                                                if (((int) imgs.get(location).get("imgType")) == imgTypes[0]) {
//                                                    Log.i(TAG, "isTrue");
                                                    if (!imgs.get(location).containsKey("isTrue")) {
                                                        imgs.get(location).put("isTrue", true);
                                                        ((ImageView) imgs.get(location).get("imageView")).setImageResource(R.mipmap.neckgym_l_s);
                                                        score += 100;
                                                        mHandler.sendEmptyMessage(CHANGE_SCORE);
                                                    }
                                                } else {
                                                    if (!imgs.get(location).containsKey("isTrue")) {
                                                        imgs.get(location).put("isTrue", false);
                                                        ((ImageView) imgs.get(location).get("imageView")).setImageResource(R.mipmap.neckgym_r_f);
                                                    }
                                                }
                                            }
                                        }
                                        if (split0 < 88.5 && split0 > 0) {
                                            Log.i(TAG, "右倾");
                                            if (location < imgs.size()) {
                                                if (((int) imgs.get(location).get("imgType")) == imgTypes[1]) {
                                                    if (!imgs.get(location).containsKey("isTrue")) {
                                                        imgs.get(location).put("isTrue", true);
                                                        ((ImageView) imgs.get(location).get("imageView")).setImageResource(R.mipmap.neckgym_r_s);
                                                        score += 100;
                                                        mHandler.sendEmptyMessage(CHANGE_SCORE);
                                                    }
                                                } else {
                                                    if (!imgs.get(location).containsKey("isTrue")) {
                                                        imgs.get(location).put("isTrue", false);
                                                        ((ImageView) imgs.get(location).get("imageView")).setImageResource(R.mipmap.neckgym_l_f);
                                                    }
                                                }
                                            }
                                        }
                                        lastSplit0 = split0;
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
                });
            }

            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        MainActivity.mState = MainActivity.UART_PROFILE_DISCONNECTED;
                        hfHandler.sendEmptyMessage(HomeFragment.CHANGE_STATE);
                    }
                });
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlay = false;
        LocalBroadcastManager.getInstance(context).unregisterReceiver(neckStateReceiver);
        HomeFragment.isPlay = false;
        KeepScreenOn.keepScreenOn(context, false);
        DatabaseOperate databaseOperate = new DatabaseOperate(context);
        mRecord.setGame_use_time(60 - count);
        mRecord.setCompleteness((int) ((60-count) / 60f * 100));
        databaseOperate.insertGameRecord(mRecord);
    }
}
