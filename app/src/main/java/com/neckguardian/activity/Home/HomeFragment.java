package com.neckguardian.activity.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.Adapter.CalendarAdapter;
import com.neckguardian.activity.Home.Adapter.CalendarAdapter.ViewHolder;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.activity.Mine.machine.MachineActivity;
import com.neckguardian.activity.ShareActivity;
import com.neckguardian.service.HomeService;
import com.neckguardian.sign.State;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.FixedSpeedScroller;
import com.simo.utils.NetUtils;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.UIUtils;
import com.simo.utils.myView.MyScrollView;
import com.simo.utils.myView.ProgressDialog;
import com.simo.utils.myView.RainbowProgressView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页的Fragment
 * Created by 孤月悬空 on 2015/12/23.
 */
public class HomeFragment extends Fragment {

    private View homeView = null;
    private RecyclerView recyclerCal = null;
    private CalendarAdapter calendarAdapter = null;
    private ViewPager progressPager = null;
    private MyPagerAdapter pagerAdapter = null;
    private TextView currentEnergy = null;
    private TextView targetEnergy = null;
    private RainbowProgressView progressBar = null;
    private TextView dateNum = null;
    private TextView exerciseAngle = null;
    private TextView exerciseAmount = null;
    private TextView energyConsumption = null;
    private TextView lowerTime = null;
    private TextView rightTime = null;
    private TextView leftTime = null;
    private ImageView homeShare = null;
    private RainbowProgressView rainbow = null;
    private ImageView blurImg = null;

    private List<Map<String, Object>> dataList = null;
    private List<Map<String, Object>> historyMemory = null;
    private List<Map<String, Object>> exerciseEnergy = null;
    private List<View> viewList;
    private List<DataOfDay> getList = null;
    private List<TextView> boshiStates = null;
    private List<TextView> clickToConns = null;
    private List<LinearLayout> showMores = null;
    private Context context;
    private DataOfDay dataOfDay;
    private DatabaseOperate databaseOperate;
    private SimpleDateFormat format = null;
    private View pagerView = null;

    private int index = 0;
    private int mState = 0;
    private boolean[] flags;
    private boolean isActivate = true;
    private boolean isMoving = false;
    private FixedSpeedScroller mScroller;
    private final static String TAG = "HomeFragment";
    public static boolean isPlay = false;
    public static boolean isRemind = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        recyclerCal = (RecyclerView) homeView.findViewById(R.id.recycler_cal);
        progressPager = (ViewPager) homeView.findViewById(R.id.progress_pager);
        homeShare = (ImageView) homeView.findViewById(R.id.home_share);
        dateNum = (TextView) homeView.findViewById(R.id.date_num);

        dataOfDay = new DataOfDay();
        mState = MainActivity.mState;
        Log.i(TAG, "mState" + mState);
        databaseOperate = new DatabaseOperate(context);
        boshiStates = new ArrayList<>();
        clickToConns = new ArrayList<>();
        showMores = new ArrayList<>();

        format = new SimpleDateFormat("MM-dd");
        LocalBroadcastManager.getInstance(context).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());

        UIUtils.hideTitleBar(this.getActivity(), R.id.fragment_home_layout);   //如果安卓系统在4.4以上，隐藏状态栏
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10000);
//                    Message msg = new Message();
//                    msg.what = REMIND;
//                    msg.arg1 = WRONG_SITTING;
//                    mHandler.sendMessage(msg);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        init();
    }

    private void init() {
        homeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
                    DataOfDay shareDay = getShareDay();
                    Intent intent = new Intent(context, ShareActivity.class);
                    intent.putExtra("shareDay", shareDay);
                    startActivity(intent);
                } else {
                    T.showShort(context, "您还没有登陆");
                }
            }
        });
        if (!isLogin()) {
            mHandler.sendEmptyMessage(TOAST_1);
        }
        dataList = new ArrayList<>();
        historyMemory = new ArrayList<>();
        exerciseEnergy = new ArrayList<>();
        viewList = new ArrayList<>();
        getUserDate();

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(context, historyMemory, new CalendarAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(int position) {
                if (position == dataList.size() - 1) {
                    dateNum.setText("今天");
                } else {
                    dateNum.setText((String) dataList.get(position).get("dateNum"));
                }
                progressPager.setCurrentItem(position);
                calendarAdapter.notifyDataSetChanged();
            }
        });
        recyclerCal.setAdapter(calendarAdapter);

        pagerAdapter = new MyPagerAdapter(initViewList(exerciseEnergy), context);
        progressPager.setAdapter(pagerAdapter);

        if (historyMemory.size() > 7) {
            recyclerCal.scrollToPosition(historyMemory.size() - 7);
            progressPager.setCurrentItem(historyMemory.size() - 1);
        } else {
            recyclerCal.scrollToPosition(historyMemory.size() - 1);
            progressPager.setCurrentItem(historyMemory.size() - 1);
        }
        progressPager.addOnPageChangeListener(new pagerListener());
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(context, new AccelerateInterpolator());
            mField.set(progressPager, mScroller);
            mScroller.setmDuration(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        flags = new boolean[exerciseEnergy.size()];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = true;
        }
        mHandler.sendEmptyMessage(CHANGE_STATE);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < 100; i++) {
                    Message msg = Message.obtain();
                    msg.what = TEST_TEXT;
                    msg.arg1 = i*300;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
//        mHandler.sendEmptyMessage(REMIND);
    }

    private class pagerListener implements ViewPager.OnPageChangeListener {
        int position;
        int lastPosition = historyMemory.size() - 1;
        ViewHolder viewHolder;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            this.position = position;
        }

        @Override
        public void onPageSelected(int position) {
            HomeFragment.this.index = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                viewHolder = (ViewHolder) recyclerCal.findViewHolderForLayoutPosition(position);
//                    lastHolder = (ViewHolder) recyclerCal.findViewHolderForLayoutPosition(lastPosition);
//
//                    lastHolder.getDateNum().setTextColor(getResources().getColor(R.color.text_color_black));
//                    lastHolder.getDatePro().setBackgroundResource(R.color.white);
                for (boolean bool : flags) {
                    Log.i(TAG, "flag = " + bool);
                }
                if (lastPosition > position) {
                    recyclerCal.scrollToPosition(position - 1);
                } else {
                    recyclerCal.scrollToPosition(position + 1);
                }
                lastPosition = position;
                calendarAdapter.setLastPosition(lastPosition);

                viewHolder.getDateNum().setTextColor(getResources().getColor(R.color.white));
                viewHolder.getDateNum().setBackgroundResource(R.drawable.bg_item_cal);
                pagerView = progressPager.findViewWithTag(position);

                if (flags[position]) {
                    flags[position] = false;
                    rainbow = (RainbowProgressView) pagerView.findViewById(R.id.progress_bar);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            float percent = rainbow.getProgress();
                            Log.i(TAG, "first progress=" + rainbow.getProgress() + "");

                            for (int i = 0; i <= percent; i++) {
                                Log.i(TAG + " progress=", rainbow.getProgress() + "");
                                try {
                                    Message msg = new Message();
                                    msg.what = PROGRESS_ANIMATION;
                                    msg.arg1 = i;
                                    msg.arg2 = i;
                                    mHandler.sendMessage(msg);
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }

                if (position == dataList.size() - 1) {
                    dateNum.setText("今天");
                } else {
                    dateNum.setText((String) dataList.get(position).get("dateNum"));
                }
                calendarAdapter.notifyDataSetChanged();
//                    calendarAdapter.setLastHolder(lastHolder);
            }
        }
    }


    private boolean firstReceiver = true;   //连接蓝牙收到的第一个可用信息
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        int count = 0;
        Message msg = null;
        Map<String, Integer> map = null;
        int[] millisecond = new int[]{
                0, 0, 0
        };
        int[] second = new int[]{
                0, 0, 0
        };
        int[] minute = new int[]{
                0, 0, 0
        };
        int[] last_second = new int[]{
                0, 0, 0
        };
        int[] last_angle = new int[]{
                0, 0, 0
        };
        int changeAngle = 0;

        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();

            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        second[0] = 0;
                        second[1] = 0;
                        second[2] = 0;
                        MainActivity.mState = MainActivity.UART_PROFILE_DISCONNECTED;
                        mHandler.sendEmptyMessage(CHANGE_STATE);
                    }
                });
            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            String text = null;
                            String split = null;
                            try {
                                text = new String(txValue, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (firstReceiver) {
                                split = text.split("[.]")[0];
                            } else {
                                split = text.substring(0, 2);
                            }
                            int split0 = Integer.parseInt(split);
//                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            count++;
//                                Log.i(TAG, "Math.abs() = " + Math.abs(split0 - last_angle[0]));
                            switch (count) {
                                case 0:
                                    if (firstReceiver) {
                                        leftTime.setText(String.valueOf(split0 +
                                                SPPrivateUtils.getInt(context, State.leftTime, 0)));
                                    }
                                case 1:
                                    if (firstReceiver) {
                                        rightTime.setText(String.valueOf(split0 +
                                                SPPrivateUtils.getInt(context, State.rightTiem, 0)));
                                    } else {
                                        Log.i(TAG, "second[0] = " + second[0]);
                                        Log.i(TAG, "last_second[0]" + last_second[0]);
                                        if (Math.abs(split0 - last_angle[0]) > 5) {
                                            isMoving = true;
                                            last_second[0] = second[0];
                                            if (last_angle[0] > 0 && split0 < 0) {
                                                changeAngle = (90 - last_angle[0]) + (90 + split0);
                                            } else if (last_angle[0] < 0 && split0 > 0) {
                                                changeAngle = (90 - split0) + (90 + last_angle[0]);
                                            } else if (last_angle[0] == 0) {
                                                changeAngle = 5;
                                            } else {
                                                changeAngle = Math.abs(split0 - last_angle[0]);
                                            }
                                            last_angle[0] = split0;
//                                            Log.i(TAG, "case 1");
                                        } else {
                                            isMoving = false;
                                        }
                                        if (split0 < 60 && split0 > 0) {
//                                            Log.i(TAG, "低头");
//                                            Log.i(TAG, "millisecond = " + millisecond[0]);
//                                            Log.i(TAG, "second[0] = " + second[0]);
//                                            Log.i(TAG, "minute = " + minute[0]);
                                            if (second[0] == 60) {
                                                second[0] = 0;
                                                minute[0] += 1;
                                                SPPrivateUtils.put(context, State.lowerTime, minute[0]);
                                                map = new HashMap<String, Integer>();
                                                map.put("lowerTime", minute[0]);
                                                msg = new Message();
                                                msg.what = CHANGE_LOWER;
                                                mHandler.sendMessage(msg);
                                            } else {
                                                if (!isPlay) {
                                                    second[0] += 1;
                                                    if (!isRemind) {
                                                        if (second[0] - last_second[0] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)
                                                                || last_second[0] - second[0] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)) {
                                                            Message msg = new Message();
                                                            msg.what = REMIND;
                                                            msg.arg1 = WRONG_SITTING;
                                                            mHandler.sendMessage(msg);
                                                            last_second[0] = second[0];
                                                        }
                                                    } else {
                                                        last_second[0] = second[0];
                                                    }
                                                } else {
                                                    last_second[0] = second[0];
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case 2:
//                                        Log.i(TAG, "Case 2 split0 = " + split0);
                                    if (firstReceiver) {
                                        lowerTime.setText(String.valueOf(split0 +
                                                SPPrivateUtils.getInt(context, State.lowerTime, 0)));
                                        firstReceiver = false;
                                    } else {
                                        if (Math.abs(split0 - last_angle[1]) > 5 || Math.abs(split0 - last_angle[2]) > 5) {
                                            isMoving = true;
                                            last_second[1] = second[1];
                                            last_second[2] = second[2];
                                            if (Math.abs(split0 - last_angle[1]) > 5) {
                                                if (last_angle[1] == 0) {
                                                    changeAngle = 5;
                                                } else {
                                                    changeAngle = (Math.abs(split0 - last_angle[1]));
                                                }
                                                last_angle[1] = split0;
                                            } else if (Math.abs(split0 - last_angle[2]) > 5) {
                                                if (last_angle[2] == 0) {
                                                    changeAngle = 5;
                                                } else {
                                                    changeAngle = (Math.abs(split0 - last_angle[2]));
                                                }
                                                last_angle[2] = split0;
                                            }
                                        } else {
                                            isMoving = false;
                                        }
                                        if (split0 > -86 && split0 < 0) {
                                            Log.i(TAG, "左倾");
//                                            Log.i(TAG, "second[1] = " + second[1]);
                                            if (second[1] == 60) {
                                                second[1] = 0;
                                                minute[1] += 1;
                                                SPPrivateUtils.put(context, State.leftTime, minute[0]);
                                                map = new HashMap<String, Integer>();
                                                map.put("leftTime", minute[1]);
                                                msg = new Message();
                                                msg.what = CHANGE_LEFT;
                                                mHandler.sendMessage(msg);
                                            } else {
                                                if (!isPlay) {
                                                    second[1] += 1;
                                                    if (!isRemind) {
                                                        if (second[1] - last_second[1] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)
                                                                || last_second[1] - second[1] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)) {
                                                            Message msg = new Message();
                                                            msg.what = REMIND;
                                                            msg.arg1 = WRONG_SITTING;
                                                            mHandler.sendMessage(msg);
                                                            last_second[1] = second[1];
                                                        }
                                                    } else {
                                                        last_second[1] = second[1];
                                                    }
                                                } else {
                                                    last_second[1] = second[1];
                                                }
                                            }
                                        }
                                        if (split0 < 86 && split0 > 0) {
                                            Log.i(TAG, "右倾");
//                                            Log.i(TAG, "second[2] = " + second[2]);
                                            if (second[2] == 60) {
                                                second[2] = 0;
                                                minute[2] += 1;
                                                SPPrivateUtils.put(context, State.rightTiem, minute[0]);
                                                map = new HashMap<String, Integer>();
                                                map.put("rightTime", minute[2]);
                                                msg = new Message();
                                                msg.what = CHANGE_RIGHT;
                                                mHandler.sendMessage(msg);
                                            } else {
                                                if (!isPlay) {
                                                    second[2] += 1;
                                                    if (!isRemind) {
                                                        if (second[2] - last_second[2] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)
                                                                || last_second[2] - second[2] == SPPrivateUtils.getInt(context, State.wrongSetting, 30)) {
                                                            Message msg = new Message();
                                                            msg.what = REMIND;
                                                            msg.arg1 = WRONG_SITTING;
                                                            mHandler.sendMessage(msg);
                                                            last_second[2] = second[2];
                                                        }
                                                    } else {
                                                        last_second[2] = second[2];
                                                    }
                                                } else {
                                                    last_second[2] = second[2];
                                                }
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    if (changeAngle > 5) {
                                        msg = new Message();
                                        msg.what = CHANGE_OTHER;
                                        msg.arg1 = changeAngle;
                                        mHandler.sendMessage(msg);
                                    }
                                    changeAngle = 0;
                                    count = 0;
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    };

    private void resetNeckTime() {

    }

    private List<View> initViewList(List<Map<String, Object>> mapList) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < mapList.size(); i++) {
            View view = mLayoutInflater.inflate(R.layout.item_progress, null);
            TextView currentEnergy = (TextView) view.findViewById(R.id.current_energy);
            TextView targetEnergy = (TextView) view.findViewById(R.id.target_energy);
            final RainbowProgressView progressBar = (RainbowProgressView) view.findViewById(R.id.progress_bar);
            TextView exerciseAngle = (TextView) view.findViewById(R.id.exercise_angle);
            TextView exerciseAmount = (TextView) view.findViewById(R.id.exercise_amount);
            TextView energyConsumption = (TextView) view.findViewById(R.id.energy_consumption);
            TextView lowerTime = (TextView) view.findViewById(R.id.lower_time);
            TextView leftTime = (TextView) view.findViewById(R.id.left_time);
            TextView rightTime = (TextView) view.findViewById(R.id.right_time);
            final LinearLayout showMore = (LinearLayout) view.findViewById(R.id.show_more);
            MyScrollView scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
            final TextView boshiState = (TextView) view.findViewById(R.id.boshi_state);
            ImageView turnToPrev = (ImageView) view.findViewById(R.id.turn_to_prev);
            ImageView turnToNext = (ImageView) view.findViewById(R.id.turn_to_next);
            TextView clickToConn = (TextView) view.findViewById(R.id.click_to_conn);
            ImageView blurImage = (ImageView) view.findViewById(R.id.blur_img);

            int currentData = (int) mapList.get(i).get("currentEnergy");
            int tarGetData = (int) mapList.get(i).get("targetEnergy");
            int angleData = (int) mapList.get(i).get("exerciseAngle");
            int amountData = (int) mapList.get(i).get("exerciseAmount");
            int consumptionData = (int) mapList.get(i).get("energyConsumption");
            int lowerData = (int) mapList.get(i).get("lowerTime");
            int rightData = (int) mapList.get(i).get("rightTime");
            int leftData = (int) mapList.get(i).get("leftTime");

            currentEnergy.setText(String.valueOf(currentData));
            targetEnergy.setText(String.valueOf(tarGetData));
            float result = currentData * 1f / tarGetData;
            progressBar.setProgress(result * 100);
            if (progressBar.getProgress() > 100) {
                blurImage.setVisibility(View.VISIBLE);
            } else {
                blurImage.setVisibility(View.INVISIBLE);
            }
            exerciseAngle.setText(String.valueOf(angleData));
            exerciseAmount.setText(String.valueOf(amountData));
            energyConsumption.setText(String.valueOf(consumptionData));
            lowerTime.setText(String.valueOf(lowerData));
            rightTime.setText(String.valueOf(rightData));
            leftTime.setText(String.valueOf(leftData));
            scrollView.setScrollViewListener(new MyScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                    float alpha = 1 - y / 2.27f / 100f;
                    int padding = y / 37;
                    float size = y / 77.6f;
//                        Log.i(TAG + " y", y + "");
//                        Log.i(TAG + "alpha", alpha + "");
//                        Log.i(TAG + " X", showMore.getX() + "");
                    boshiState.setPadding(0, padding, 0, 0);
                    boshiState.setTextSize(size + 18);
                    showMore.setAlpha(alpha);
                    showMore.setPadding(0, padding, 0, 0);
                }
            });

            boshiStates.add(boshiState);
            clickToConns.add(clickToConn);
            showMores.add(showMore);

            turnToPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressPager.setCurrentItem(progressPager.getCurrentItem() - 1);
                }
            });
            turnToNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressPager.setCurrentItem(progressPager.getCurrentItem() + 1);
                }
            });
            clickToConn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent connIntent = new Intent(context, MachineActivity.class);
                    startActivity(connIntent);
                }
            });
            if (mapList.size() == 1) {
                turnToNext.setVisibility(View.GONE);
                turnToPrev.setVisibility(View.GONE);
            } else if (i == 0) {
                turnToPrev.setVisibility(View.GONE);
            } else if (i == mapList.size() - 1) {
                turnToNext.setVisibility(View.GONE);
            }

            viewList.add(view);
        }
        return viewList;
    }

    private class MyPagerAdapter extends PagerAdapter {

        List<View> views = null;
        Context pagerCon;
        LayoutInflater mLayoutInflater;

        public MyPagerAdapter(List<View> views, Context pagerCon) {
            this.views = views;
            this.pagerCon = pagerCon;
            mLayoutInflater = LayoutInflater.from(pagerCon);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            views.get(position).setTag(position);
//            Log.i(TAG, "position = " + position);
            container.addView(views.get(position));

            if (position == views.size() - 1) {
                currentEnergy = (TextView) views.get(position).findViewById(R.id.current_energy);
                targetEnergy = (TextView) views.get(position).findViewById(R.id.target_energy);
                progressBar = (RainbowProgressView) views.get(position).findViewById(R.id.progress_bar);
                blurImg = (ImageView) views.get(position).findViewById(R.id.blur_img);
                exerciseAngle = (TextView) views.get(position).findViewById(R.id.exercise_angle);
                exerciseAmount = (TextView) views.get(position).findViewById(R.id.exercise_amount);
                energyConsumption = (TextView) views.get(position).findViewById(R.id.energy_consumption);
                lowerTime = (TextView) views.get(position).findViewById(R.id.lower_time);
                leftTime = (TextView) views.get(position).findViewById(R.id.left_time);
                rightTime = (TextView) views.get(position).findViewById(R.id.right_time);
                pagerView = views.get(position);
                /*if (fitst == 888) {
                    currentEnergy.setText("5235");
                    targetEnergy.setText("30000");
                    progressBar.setProgress(17.4f);
                    exerciseAngle.setText("793");
                    exerciseAmount.setText("7333");
                    energyConsumption.setText("99");
                    lowerTime.setText("5");
                    rightTime.setText("5");
                    leftTime.setText("4");
                    fitst = 999;
                }*/
                /*if (fitst == 88) {
                    Log.i(TAG, "zheliyoumeiyou");
                    currentEnergy.setText("6195");
                    targetEnergy.setText("30000");
                    progressBar.setProgress(23.8f);
                    exerciseAngle.setText("793");
                    exerciseAmount.setText("7333");
                    energyConsumption.setText("99");
                    lowerTime.setText("1");
                    rightTime.setText("5");
                    leftTime.setText("4");
                    fitst = 999;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (int i=0; i< 2; i++) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message msg = new Message();
                                msg.what = TEST_TEXT;
                                msg.arg1 = i;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }).start();
                }*/
            }
            return views.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            View view = (View) object;
            int currentPage = index;
            if (currentPage == view.getTag()) {
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }
    }

    private ProgressDialog progressDialog = null;

    private static final int SHOW_USER_DATE = 101;
    private static final int UPLOAD_DATA = 102;
    private static final int TEST_TEXT = 103;
    private static final int TOAST_1 = 104;
    private static final int TOAST_2 = 105;
    private static final int SHOW_NOTHING = 106;
    private static final int PROGRESS_SHOW = 107;
    private static final int PROGRESS_DISMISS = 108;
    private static final int REMIND = 109;
    public static final int CHANGE_STATE = 110;
    private static final int PROGRESS_ANIMATION = 111;
    public static final int GET_USER_DATE = 112;
    private static final int CHANGE_DATA = 113;
    private static final int CHANGE_LOWER = 114;
    private static final int CHANGE_LEFT = 115;
    private static final int CHANGE_RIGHT = 116;
    private static final int CHANGE_OTHER = 117;
    private static final int WRONG_SITTING = 118;
    private static final int LONG_TIME = 119;

    private Handler mHandler = new MyHandler(HomeFragment.this);

    public static class MyHandler extends Handler {

        private WeakReference<HomeFragment> mFragment;

        private int counts[] = new int[]{
                0, 0, 0, 0
        };
        int changedAngle = 0;
        int currentData = 0;
        int angleData = 0;
        int energyData = 0;

        public MyHandler(HomeFragment fragment) {
            mFragment = new WeakReference<HomeFragment>(fragment);
        }

        Map<String, Integer> map = null;
        boolean firstTime = true;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final HomeFragment fragment = mFragment.get();
            switch (msg.what) {
                case UPLOAD_DATA:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (fragment.isActivate) {
                                try {
                                    Thread.sleep(60000);
                                    fragment.uploadDate();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    break;
                case TEST_TEXT:
                    break;
                case SHOW_NOTHING:
//                    Log.i("ShowNothing", "is that used");
                    break;
                case PROGRESS_SHOW:
                    fragment.progressDialog = new ProgressDialog.Builder(fragment.context).content(fragment.getText(R.string.load).toString()).build();
                    fragment.progressDialog.show();
                    break;
                case PROGRESS_DISMISS:
                    fragment.progressDialog.dismiss();
                    break;
                case TOAST_1:
                    T.showShort(fragment.context, "请先登录");
                    break;
                case TOAST_2:
                    T.showShort(fragment.context, "无网络连接");
                    break;
                case REMIND:
                    if (msg.arg1 == WRONG_SITTING) {
                        if (!isPlay) {
                            if (!isRemind) {
                                Intent intent = new Intent("com.simo.receiver.LONG_TIME_REMIND");
                                intent.putExtra("what", "wrong_sitting");
                                fragment.getActivity().sendBroadcast(intent);
                            }
                        }
                    } else {
                        new Thread(new Runnable() {
                            int count = SPPrivateUtils.getInt(fragment.context, State.remind_time, 15) * 60;

                            @Override
                            public void run() {
                                while (fragment.isActivate) {
                                    if (!fragment.isMoving) {
                                        if (!isPlay) {
                                            if (!isRemind) {
                                                count--;
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                if (count == 0) {
                                                    Intent intent = new Intent("com.simo.receiver.LONG_TIME_REMIND");
                                                    intent.putExtra("what", "long_time");
                                                    fragment.getActivity().sendBroadcast(intent);
                                                }
                                            }
                                        }
                                    } else {
                                        count = SPPrivateUtils.getInt(fragment.context, State.remind_time, 15) * 60;
                                    }
                                }
                                fragment.getActivity().sendBroadcast(new Intent("com.simo.receiver.LONG_TIME_REMIND"));
                            }
                        }).start();
                    }
                    break;
                case CHANGE_STATE:
                    fragment.mState = MainActivity.mState;
                    if (fragment.mState == MainActivity.UART_PROFILE_CONNECTED) {
                        for (int i = 0; i < fragment.boshiStates.size(); i++) {
                            fragment.boshiStates.get(i).setVisibility(View.VISIBLE);
                            fragment.showMores.get(i).setVisibility(View.VISIBLE);
                        }
                        for (TextView conn : fragment.clickToConns) {
                            conn.setVisibility(View.INVISIBLE);
                        }
                        Log.i(TAG, "已连接");
                    } else {
                        for (int i = 0; i < fragment.boshiStates.size(); i++) {
                            fragment.boshiStates.get(i).setVisibility(View.INVISIBLE);
                            fragment.showMores.get(i).setVisibility(View.INVISIBLE);
                        }
                        for (TextView conn : fragment.clickToConns) {
                            conn.setVisibility(View.VISIBLE);
                        }
                        Log.i(TAG, "未连接");
                    }
                    break;
                case PROGRESS_ANIMATION:
                    fragment.rainbow.setProgress(msg.arg1);
                    break;
                case GET_USER_DATE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.getUserDate_2();
                        }
                    }).start();
                    break;
                case SHOW_USER_DATE:
                    fragment.showUserDate();
                    break;
                case CHANGE_LEFT:
                    fragment.leftTime.setText(1 +
                            Integer.parseInt(fragment.leftTime.getText().toString()));
                    break;
                case CHANGE_RIGHT:
                    fragment.rightTime.setText(1 +
                            Integer.parseInt(fragment.rightTime.getText().toString()));
                    break;
                case CHANGE_LOWER:
                    fragment.lowerTime.setText(1 +
                            Integer.parseInt(fragment.lowerTime.getText().toString()));
                    break;
                case CHANGE_OTHER:
                    Log.i(TAG, "msg.arg1 = " + msg.arg1);
                    changedAngle = msg.arg1;
                    currentData = Integer.parseInt(fragment.currentEnergy.getText().toString());
                    angleData = Integer.parseInt(fragment.currentEnergy.getText().toString());
                    energyData = Integer.parseInt(fragment.currentEnergy.getText().toString());

                    fragment.currentEnergy.setText(String.valueOf(currentData + (changedAngle / 10 + 1)));
                    float progress = Float.parseFloat(fragment.currentEnergy.getText().toString()) * 1f
                            / SPPrivateUtils.getInt(fragment.context, State.targetEnergy, 30000) * 100;
                    fragment.progressBar.setProgress(progress);
                    Log.i(TAG, "" + progress);
                    if (progress > 100) {
                        fragment.blurImg.setVisibility(View.VISIBLE);
                    }
                    fragment.exerciseAngle.setText(String.valueOf(angleData + changedAngle));
                    fragment.exerciseAmount.setText(Integer.parseInt(fragment.currentEnergy.getText().toString()) / 4 + "");
                    fragment.energyConsumption.setText(String.valueOf(energyData + changedAngle * 3));
                    firstTime = false;
                    break;
                default:
                    break;
            }
        }

    }

    private void showUserDate() {
        if (historyMemory != null) {
            historyMemory.clear();
            historyMemory.addAll(dataList);
        }
        if (exerciseEnergy != null) {
            exerciseEnergy.clear();
            exerciseEnergy.addAll(dataList);
            if (viewList != null) {
                viewList.clear();
                initViewList(exerciseEnergy);
                flags = new boolean[viewList.size()];
                for (int i = 0; i < exerciseEnergy.size(); i++) {
                    flags[i] = true;
                }
                mHandler.sendEmptyMessage(CHANGE_STATE);
            }
        }

        calendarAdapter.notifyDataSetChanged();
        pagerAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessage(PROGRESS_DISMISS);
        mHandler.sendEmptyMessage(UPLOAD_DATA);
    }

    private void getUserDate_2() {
        mHandler.sendEmptyMessage(PROGRESS_SHOW);
        if (NetUtils.isConnected(context, true)) {
            getList = new HomeService().getUserDate(SPPrivateUtils.getString(context, State.userId, "1"));
            if (getList != null) {
                databaseOperate.deleteAllData();
                dataList.clear();
                for (int i = 0; i < getList.size(); i++) {
                    DataOfDay dataOfDay = getList.get(i);

                    Log.i(TAG, dataOfDay.toString());

                    Map<String, Object> map = new HashMap<>();
                    map.put("currentEnergy", dataOfDay.getCurrentEnergy());
                    map.put("targetEnergy", dataOfDay.getTargetEnergy());
                    map.put("dateNum", dataOfDay.getDateNum());
                    map.put("exerciseAngle", dataOfDay.getExerciseAngle());
                    map.put("exerciseAmount", dataOfDay.getExerciseAmount());
                    map.put("energyConsumption", dataOfDay.getEnergyConsumption());
                    map.put("lowerTime", dataOfDay.getLowerTime());
                    map.put("leftTime", dataOfDay.getLeftTime());
                    map.put("rightTime", dataOfDay.getRightTime());

                    dataList.add(map);
                    databaseOperate.insertDataOfDay(dataOfDay);
                }
                if (!format.format(new Date()).equals(dataList.get(dataList.size() - 1).get("dateNum"))) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("currentEnergy", 0);
                    map.put("targetEnergy", SPPrivateUtils.getInt(context, State.targetEnergy, 30000));
                    map.put("dateNum", (Calendar.getInstance().get(Calendar.MONTH) + 1)
                            + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    map.put("exerciseAngle", 0);
                    map.put("exerciseAmount", 0);
                    map.put("energyConsumption", 0);
                    map.put("lowerTime", 0);
                    map.put("leftTime", 0);
                    map.put("rightTime", 0);
                    dataList.add(map);
                    dataOfDay.setCurrentEnergy(0);
                    dataOfDay.setTargetEnergy(SPPrivateUtils.getInt(context, State.targetEnergy, 30000));
                    dataOfDay.setDateNum(format.format(new Date()));
                    dataOfDay.setExerciseAngle(0);
                    dataOfDay.setExerciseAmount(0);
                    dataOfDay.setEnergyConsumption(0);
                    dataOfDay.setLowerTime(0);
                    dataOfDay.setLowerTime(0);
                    dataOfDay.setRightTime(0);
//            Log.i(TAG, dataOfDay.toString());
                    databaseOperate.insertDataOfDay(dataOfDay);
                }
            }
        }
//            Log.i(TAG + "spp", SPPrivateUtils.getInt(context, State.dayOfYear, Calendar.DAY_OF_YEAR) + "");
//            Log.i(TAG + "cal", Calendar.DAY_OF_YEAR + "");

        mHandler.sendEmptyMessage(SHOW_USER_DATE);
        mHandler.sendEmptyMessage(PROGRESS_DISMISS);
    }

    private void uploadDate() {

        dataOfDay.setCurrentEnergy(Integer.parseInt(currentEnergy.getText().toString()));
        dataOfDay.setTargetEnergy(Integer.parseInt(targetEnergy.getText().toString()));
        dataOfDay.setDateNum(format.format(new Date()));
        dataOfDay.setExerciseAngle(Integer.parseInt(exerciseAngle.getText().toString()));
        dataOfDay.setExerciseAmount(Integer.parseInt(exerciseAmount.getText().toString()));
        dataOfDay.setEnergyConsumption(Integer.parseInt(energyConsumption.getText().toString()));
        dataOfDay.setLowerTime(Integer.parseInt(lowerTime.getText().toString()));
        dataOfDay.setLeftTime(Integer.parseInt(leftTime.getText().toString()));
        dataOfDay.setRightTime(Integer.parseInt(rightTime.getText().toString()));

        Log.w(TAG, "---------------- 开始将数据保存到数据库 ----------------");
        Log.i(TAG, dataOfDay.toString());
        if (databaseOperate.queryHistory() != null || databaseOperate.queryHistory().size() == 0) {
            databaseOperate.updateDataOfDay(dataOfDay);
        }
        Log.w(TAG, "---------------- 数据库修改完毕 ----------------");
    }

    private void getUserDate() {
        mHandler.sendEmptyMessage(PROGRESS_SHOW);
        if (getList != null && getList.size() > 0) {
            for (int i = 0; i < getList.size(); i++) {
                DataOfDay dataOfDay = getList.get(i);
                Map<String, Object> map = new HashMap<>();
                map.put("currentEnergy", dataOfDay.getCurrentEnergy());
                map.put("targetEnergy", dataOfDay.getTargetEnergy());
                map.put("dateNum", dataOfDay.getDateNum());
                map.put("exerciseAngle", dataOfDay.getExerciseAngle());
                map.put("exerciseAmount", dataOfDay.getExerciseAmount());
                map.put("energyConsumption", dataOfDay.getEnergyConsumption());
                map.put("lowerTime", dataOfDay.getLowerTime());
                map.put("leftTime", dataOfDay.getLeftTime());
                map.put("rightTime", dataOfDay.getRightTime());

                dataList.add(map);
            }
            DataOfDay day = databaseOperate.queryLastDay();
//            Log.i(TAG, "dateNum" + day.getDateNum());
//            Log.i(TAG, "date" + format.format(new Date()));
            if (!day.getDateNum().equals(format.format(new Date()))) {
                insertOneDay();
            }
        } else {
            insertOneDay();
        }
        historyMemory.addAll(dataList);
        exerciseEnergy.addAll(dataList);
        mHandler.sendEmptyMessage(PROGRESS_DISMISS);
        mHandler.sendEmptyMessage(UPLOAD_DATA);
    }

    private void insertOneDay() {
        Map<String, Object> map = new HashMap<>();
        map.put("currentEnergy", 0);
        map.put("targetEnergy", SPPrivateUtils.getInt(context, State.targetEnergy, 30000));
        map.put("dateNum", format.format(new Date()));
        map.put("exerciseAngle", 0);
        map.put("exerciseAmount", 0);
        map.put("energyConsumption", 0);
        map.put("lowerTime", 0);
        map.put("leftTime", 0);
        map.put("rightTime", 0);
        dataList.add(map);
        dataOfDay.setCurrentEnergy(0);
        dataOfDay.setTargetEnergy(SPPrivateUtils.getInt(context, State.targetEnergy, 30000));
        dataOfDay.setDateNum(format.format(new Date()));
        dataOfDay.setExerciseAngle(0);
        dataOfDay.setExerciseAmount(0);
        dataOfDay.setEnergyConsumption(0);
        dataOfDay.setLowerTime(0);
        dataOfDay.setLeftTime(0);
        dataOfDay.setRightTime(0);
//            Log.i(TAG, dataOfDay.toString());
        databaseOperate.insertDataOfDay(dataOfDay);
    }

    private DataOfDay getShareDay() {

        TextView shareCurrent = (TextView) pagerView.findViewById(R.id.current_energy);
        TextView shareTarget = (TextView) pagerView.findViewById(R.id.target_energy);
        TextView shareAngle = (TextView) pagerView.findViewById(R.id.exercise_angle);
        TextView shareAmount = (TextView) pagerView.findViewById(R.id.exercise_amount);
        TextView shareEnergy = (TextView) pagerView.findViewById(R.id.energy_consumption);
        TextView shareLower = (TextView) pagerView.findViewById(R.id.lower_time);
        TextView shareLeft = (TextView) pagerView.findViewById(R.id.left_time);
        TextView shareRight = (TextView) pagerView.findViewById(R.id.right_time);
        DataOfDay shareDay = new DataOfDay();

        shareDay.setCurrentEnergy(Integer.parseInt(shareCurrent.getText().toString()));
        shareDay.setTargetEnergy(Integer.parseInt(shareTarget.getText().toString()));
        shareDay.setExerciseAngle(Integer.parseInt(shareAngle.getText().toString()));
        shareDay.setExerciseAmount(Integer.parseInt(shareAmount.getText().toString()));
        shareDay.setEnergyConsumption(Integer.parseInt(shareEnergy.getText().toString()));
        shareDay.setLowerTime(Integer.parseInt(shareLower.getText().toString()));
        shareDay.setLeftTime(Integer.parseInt(shareLeft.getText().toString()));
        shareDay.setRightTime(Integer.parseInt(shareRight.getText().toString()));

        String[] day = dateNum.getText().toString().split("-");
        if (day.length > 1) {
            shareDay.setDateNum(day[0] + "月" + day[1] + "日");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
            shareDay.setDateNum(simpleDateFormat.format(new Date()));
        }

        return shareDay;
    }

    /*private void changeDate(int i) {
        currentEnergy.setText(i + "");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home, container, false);       //设置需要显示的layout，把这个当做Activity来用
        Bundle bundle = getArguments();
        getList = (List<DataOfDay>) bundle.getSerializable("dataList");
        if (getList != null) {
            Log.i(TAG, "getList");
        }
        /*for (DataOfDay day : getList) {
            Log.i(TAG + "day", day.toString());
        }*/
        return homeView;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        isActivate = false;
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put("userId", SPPrivateUtils.getString(context, State.userId, ""));
        dateMap.put("nickName", "葫芦娃");
        dateMap.put("currentEnergy", Integer.parseInt(currentEnergy.getText().toString()));
        dateMap.put("targetEnergy", Integer.parseInt(targetEnergy.getText().toString()));
        dateMap.put("dateNum", format.format(new Date()));
        dateMap.put("exerciseAngle", Integer.parseInt(exerciseAngle.getText().toString()));
        dateMap.put("exerciseAmount", Integer.parseInt(exerciseAmount.getText().toString()));
        dateMap.put("energyConsumption", Integer.parseInt(energyConsumption.getText().toString()));
        dateMap.put("lowerTime", Integer.parseInt(lowerTime.getText().toString()));
        dateMap.put("leftTime", Integer.parseInt(leftTime.getText().toString()));
        dateMap.put("rightTime", Integer.parseInt(rightTime.getText().toString()));
        try {
            uploadDate();
        } catch (Exception e) {
            Log.i(TAG, "数据没有保存");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (targetEnergy != null) {
            targetEnergy.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 30000) + "");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isNetEnable() {
        if (NetUtils.isConnected(context, true)) {
            if (SPPrivateUtils.getBoolean(context, State.useWifi, false)
                    && NetUtils.isWifi(context)) {
                return true;
            } else if (SPPrivateUtils.getBoolean(context, State.useAllNet, true)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isLogin() {
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            return true;
        } else {
            return false;
        }
    }
}
