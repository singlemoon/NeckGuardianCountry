package com.neckguardian.activity.Mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.BluetoothManage;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Mine.machine.MachineActivity;
import com.neckguardian.activity.login.LoginActivity;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.myView.GlideCircleTransform;
import com.simo.utils.myView.VerticalProgressBar;

import java.io.File;
import java.io.UnsupportedEncodingException;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

/**
 * “我的”的Fragment
 * Created by 孤月悬空 on 2015/12/23.
 */
public class MineFragment extends Fragment {

    private final static String TAG = "FragmentMine";
    private static final int GET_BLE_DATA = 111;
    private static final int PROGRESS_CHANGE = 222;
    private View mineView = null;
    private LinearLayout login = null;
    private TextView username = null;
    private RoundedImageView mine_head_img = null;
    private LinearLayout mine_health = null;
    private LinearLayout mine_health_analyze = null;
    private LinearLayout health_set = null;
    private LinearLayout mine_machine = null;
    private LinearLayout mine_settings = null;
    private LinearLayout mine_share = null;
    private LinearLayout mine_advice = null;
    private LinearLayout mine_about = null;
    private VerticalProgressBar mine_progress = null;
    private Intent intent = null;
    private Context context;
    private BluetoothManage mBluetoothManage;
    private Handler mHandler = new MyHandler();
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        Message msg;

        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            if (text.contains("%")) {
                                msg = new Message();
                                msg.what = PROGRESS_CHANGE;
                                msg.arg1 = Integer.parseInt(text.substring(0, text.length() - 1));
                                mHandler.sendMessage(msg);
                                Log.i(TAG, "text" + text);
                            }
                        } catch (NumberFormatException e) {
                            msg.arg1 = SPPrivateUtils.getInt(context, State.ELECTRIC_QUANTITY, 100);
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();

//        login = (Button) mineView.findViewById(R.id.login);
//        username = (TextView) mineView.findViewById(R.id.username);
        login = (LinearLayout) mineView.findViewById(R.id.login);
        username = (TextView) mineView.findViewById(R.id.username_mine);
        mine_head_img = (RoundedImageView) mineView.findViewById(R.id.mine_head_img);
        mine_health = (LinearLayout) mineView.findViewById(R.id.mine_health);
        mine_health_analyze = (LinearLayout) mineView.findViewById(R.id.mine_health_analyze);
        health_set = (LinearLayout) mineView.findViewById(R.id.health_set);
        mine_machine = (LinearLayout) mineView.findViewById(R.id.mine_machine);
        mine_settings = (LinearLayout) mineView.findViewById(R.id.mine_settings);
        mine_share = (LinearLayout) mineView.findViewById(R.id.mine_share);
        mine_advice = (LinearLayout) mineView.findViewById(R.id.mine_advice);
        mine_about = (LinearLayout) mineView.findViewById(R.id.mine_about);
        mine_progress = (VerticalProgressBar) mineView.findViewById(R.id.mine_progress);

        mBluetoothManage = BluetoothManage.shareBluetoothManage();

        init();     //初始化
    }

    private void init() {
        SPPrivateUtils.put(context, State.ELECTRIC_QUANTITY, 85);
        username.setText(SPPrivateUtils.getString(context, State.nickName, "请点击登陆"));
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            Glide.with(context)
                    .load(SPPrivateUtils.get(context, State.headImg, State.headImg))
                    .transform(new GlideCircleTransform(context))
                    .into(mine_head_img);
        } else {
            mine_head_img.setImageResource(R.mipmap.nologin_n);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
                    intent = new Intent(context, LoginActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    intent = new Intent(context, MineManager.class);
                    intent.putExtra(State.nickName, username.getText().toString());
                    startActivity(intent);
                }
            }
        });
        mine_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, MyHealthActivity.class);
                getActivity().startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });
        mine_health_analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, HealthAnalyzeActivity.class);
                getActivity().startActivity(intent);
            }
        });
        health_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, HealthSetActivity.class);
                getActivity().startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });
        mine_machine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, MachineActivity.class);
                getActivity().startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });
        mine_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
        mine_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(QQ.NAME);
            }
        });
        mine_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, AdviceActivity.class);
                startActivity(intent);
            }
        });
        mine_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, AboutUsActivity.class);
                startActivity(intent);
            }
        });
        service_init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mineView = inflater.inflate(R.layout.fragment_mine, container, false);   //设置需要显示的layout，把这个当做Activity来用
        return mineView;
    }

    private void service_init() {
        LocalBroadcastManager.getInstance(context).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "MainFragmentOnResume()");
        username.setText(SPPrivateUtils.getString(context, State.nickName, "点击这里登陆"));
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            if (SPPrivateUtils.getBoolean(context, State.isMobileBinding, false)) {
                mine_head_img.setImageResource(R.mipmap.nologin_n);
            } else {
                Glide.with(context)
                        .load(SPPrivateUtils.get(context, State.headImg, ""))
                        .transform(new GlideCircleTransform(context))
                        .into(mine_head_img);
            }
        } else {
            mine_head_img.setImageResource(R.mipmap.nologin_n);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "hidden" + hidden);
        if (!hidden) {
            username.setText(SPPrivateUtils.getString(context, State.nickName, "点击这里登陆"));
            if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
                if (SPPrivateUtils.getBoolean(context, State.isMobileBinding, false)) {
                    mine_head_img.setImageResource(R.mipmap.nologin_n);
                } else {
                    Glide.with(context)
                            .load(SPPrivateUtils.get(context, State.headImg, ""))
                            .transform(new GlideCircleTransform(context))
                            .into(mine_head_img);
                }
            } else {
                mine_head_img.setImageResource(R.mipmap.nologin_n);
            }
//            if (MainActivity.mState == MainActivity.UART_PROFILE_CONNECTED) {
            mHandler.sendEmptyMessage(GET_BLE_DATA);
//            }
            mHandler.sendEmptyMessage(PROGRESS_CHANGE);
//            if (SPPrivateUtils.getInt(context, State.ELECTRIC_QUANTITY, 100) <= 20) {
//                mine_progress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_style_small_warning));
//            } else {
//                mine_progress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_style_small));
//            }
        }
    }

    private void showShare(String name) {
        OnekeyShare oks = new OnekeyShare();
        String savePath = getSDCardPath() + "/BoShi/icon";
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share_to_where));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(Global.DOMAIN + Global.ENTRANCE_9);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("一起来体验吧");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(Global.DOMAIN + Global.ENTRANCE_9);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(Global.DOMAIN + Global.ENTRANCE_9);
        oks.setPlatform(name);
        oks.setSilent(true);
// 启动分享GUI
        oks.show(context);
    }

    private String getSDCardPath() {
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);     //判断SDcard是否存在，不存在不进行操作
        String SDCardPath = null;
        if (flag) {
            SDCardPath = Environment.getExternalStorageDirectory().getPath() + File.separator;
        }
        return SDCardPath;
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
                            String s = "B";
                            byte[] value;
                            try {
                                value = s.getBytes("UTF-8");
                                Thread.sleep(1000);
                                mBluetoothManage.getmService().writeRXCharacteristic(value);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case PROGRESS_CHANGE:
                    SPPrivateUtils.getInt(context, State.ELECTRIC_QUANTITY, 85);
                    mine_progress.setProgress(85);
                default:
                    break;
            }
        }
    }
}
