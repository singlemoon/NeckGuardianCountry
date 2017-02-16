package com.neckguardian.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.BluetoothManage;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.activity.Mine.MineFragment;
import com.neckguardian.activity.Rank.RankFragment;
import com.neckguardian.activity.Recreation.RecreationFragment;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.T;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final int UART_PROFILE_CONNECTED = 123;
    public static final int UART_PROFILE_DISCONNECTED = 321;
    private static final int GET_BLE_DATA = 111;
    public static int mState = UART_PROFILE_DISCONNECTED;
    //四个需要添加的Fragment
    private static HomeFragment homeFragment = null;
    private final String TAG = "MainActivity";
    HomeFragment.MyHandler hfHandler = null;
    private FragmentManager fragmentManager = null;
    private AppManager appManager = null;   //管理器
    private BluetoothManage mBluetoothManage = null;
    private ServiceConnection mServiceConnection = null;
    private RecreationFragment recreationFragment = null;
    private RankFragment rankFragment = null;
    private MineFragment mineFragment = null;
    private LinearLayout tabHomeLayout = null;
    private ImageView tabHomeImage = null;
    private TextView tabHomeText = null;
    private LinearLayout tabRecreationLayout = null;
    private ImageView tabRecreationImage = null;
    private TextView tabRecreationText = null;
    private LinearLayout tabRankLayout = null;
    private ImageView tabRankImage = null;
    private TextView tabRankText = null;
    private LinearLayout tabMineLayout = null;
    private ImageView tabMineImage = null;
    private TextView tabMineText = null;
    private List<DataOfDay> dataList;
    private boolean firstTime = true;
    private Context context;
    private UartService mService = null;
    private boolean delayFlag = true;
    private Handler mHandler = new MyHandler();
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_CONNECT_MSG");
                        mService = mBluetoothManage.getmService();
                        mState = UART_PROFILE_CONNECTED;
                        hfHandler.sendEmptyMessage(HomeFragment.CHANGE_STATE);
                        mHandler.sendEmptyMessage(GET_BLE_DATA);
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        hfHandler.sendEmptyMessage(HomeFragment.CHANGE_STATE);
                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                if (mService != null) {
                    try {
                        mService.enableTXNotification();
                    } catch (Exception e) {
                        Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED" + e.toString());
                    }
                }
            }
            //*********************//

            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                if (mService != null) {
                    mService.disconnect();
                }
            }
        }
    };
    private long time;
    private long lastTime;

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    public static HomeFragment getHomeFragment() {
        return homeFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appManager = AppManager.getInstance();      //获取Activity管理器
        appManager.addActivity(this);               //将本Activity添加到管理器中
        mBluetoothManage = BluetoothManage.shareBluetoothManage();
        mServiceConnection = mBluetoothManage.getmServiceConnection();
        context = MainActivity.this;
        init();     //初始化
    }

    private void init() {
        tabHomeLayout = (LinearLayout) super.findViewById(R.id.tab_home_layout);
        tabHomeImage = (ImageView) super.findViewById(R.id.tab_home_image);
        tabHomeText = (TextView) super.findViewById(R.id.tab_home_text);

        tabRecreationLayout = (LinearLayout) super.findViewById(R.id.tab_recreation_layout);
        tabRecreationImage = (ImageView) super.findViewById(R.id.tab_recreation_image);
        tabRecreationText = (TextView) super.findViewById(R.id.tab_recreation_text);

        tabRankLayout = (LinearLayout) super.findViewById(R.id.tab_rank_layout);
        tabRankImage = (ImageView) super.findViewById(R.id.tab_rank_image);
        tabRankText = (TextView) super.findViewById(R.id.tab_rank_text);

        tabMineLayout = (LinearLayout) super.findViewById(R.id.tab_mine_layout);
        tabMineImage = (ImageView) super.findViewById(R.id.tab_mine_image);
        tabMineText = (TextView) super.findViewById(R.id.tab_mine_text);

        //设置监听
        tabHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChoiceItem(0);
            }
        });
        tabRecreationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChoiceItem(1);
            }
        });
        tabRankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChoiceItem(2);
            }
        });
        tabMineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChoiceItem(3);
            }
        });

        //获取launchActivity传来的数据
        dataList = (List<DataOfDay>) getIntent().getSerializableExtra("dataList");
        /*for (DataOfDay day : dataList) {
            Log.i(TAG + "day", day.toString());
        }*/

        //设默认显示的Fragment为HomeFragment
        setChoiceItem(0);
        service_init();

        hfHandler = new HomeFragment.MyHandler(homeFragment);
    }

    /**
     * 显示指定的Fragment
     *
     * @param what 要显示的Fragment的编码
     */
    private void setChoiceItem(int what) {
        clearChoose();          //回复所有的Tab状态
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);      //隐藏所有的Fragment

        switch (what) {
            case 0:
                tabHomeImage.setImageResource(R.mipmap.home_checked);
                tabHomeText.setTextColor(getResources().getColor(R.color.text_color_tab));
                if (homeFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.main_framelayout, homeFragment);
                    if (firstTime) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dataList", (Serializable) dataList);
                        homeFragment.setArguments(bundle);
                    }
                } else {    //如果homeFragment不为空，则直接显示
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                tabRecreationImage.setImageResource(R.mipmap.recreation_checked);
                tabRecreationText.setTextColor(getResources().getColor(R.color.text_color_tab));
                if (recreationFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    recreationFragment = new RecreationFragment();
                    transaction.add(R.id.main_framelayout, recreationFragment);
                } else {    //如果recreationFragment不为空，则直接显示
                    transaction.show(recreationFragment);
                }
                break;
            case 2:
                tabRankImage.setImageResource(R.mipmap.rank_checked);
                tabRankText.setTextColor(getResources().getColor(R.color.text_color_tab));
                if (rankFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    rankFragment = new RankFragment();
                    transaction.add(R.id.main_framelayout, rankFragment);
                } else {    //如果rankFragment不为空，则直接显示
                    transaction.show(rankFragment);
                }
                break;
            case 3:
                tabMineImage.setImageResource(R.mipmap.mine_checked);
                tabMineText.setTextColor(getResources().getColor(R.color.text_color_tab));
                if (mineFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    mineFragment = new MineFragment();
                    transaction.add(R.id.main_framelayout, mineFragment);
                } else {    //如果mainShippingFragment不为空，则直接显示
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 重置所有的选项
     */
    public void clearChoose() {

        tabHomeImage.setImageResource(R.mipmap.home_uncheck);
        tabHomeText.setTextColor(getResources().getColor(R.color.text_color_black));

        tabRecreationImage.setImageResource(R.mipmap.recreation_uncheck);
        tabRecreationText.setTextColor(getResources().getColor(R.color.text_color_black));

        tabRankImage.setImageResource(R.mipmap.rank_uncheck);
        tabRankText.setTextColor(getResources().getColor(R.color.text_color_black));

        tabMineImage.setImageResource(R.mipmap.mine_uncheck);
        tabMineText.setTextColor(getResources().getColor(R.color.text_color_black));
    }

    /**
     * 隐藏所有的Fragment避免造成Fragment混乱
     *
     * @param transaction //Fragment的转换器
     */

    private void hideFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (recreationFragment != null) {
            transaction.hide(recreationFragment);
        }
        if (rankFragment != null) {
            transaction.hide(rankFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        if (getIntent().getStringExtra("connect") != null) {
            if (getIntent().getStringExtra("connect").equals("connect")) {
                Log.i(TAG, "这里有没有输出");
                mBluetoothManage.connectDevide(context);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startActivity(startMain);
            T.showShort(context, "脖士正在后台运行");
        } else {
            exit();
        }
    }

    private void exit() {
        time = System.currentTimeMillis();
        if (time - lastTime > 2000) {
            T.showShort(context, getResources().getText(R.string.exit_app));
            lastTime = time;
        } else {
            appManager.appExit(context);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        if (mState == UART_PROFILE_DISCONNECTED) {
            if (mService != null) {
                mService.disconnect();
            }
        }
       /* if (!SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
            mBluetoothManage.connectDevide(context);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        homeFragment = null;
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
            unbindService(mServiceConnection);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        if (mService != null) {
            mService.stopSelf();
            mService = null;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_BLE_DATA:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String s = "O";
                            byte[] value;
                            try {
                                value = s.getBytes("UTF-8");
                                if (mState == UART_PROFILE_CONNECTED) {
                                    Thread.sleep(5000);
                                    mService.writeRXCharacteristic(value);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            s = "G";
                            try {
                                value = s.getBytes("UTF-8");
                                while (mState == UART_PROFILE_CONNECTED) {
                                    Thread.sleep(1000);
                                    mService.writeRXCharacteristic(value);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        }
    }
}
