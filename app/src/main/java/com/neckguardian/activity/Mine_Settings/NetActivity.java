package com.neckguardian.activity.Mine_Settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;

import java.lang.ref.WeakReference;


public class NetActivity extends AppCompatActivity {

    private TextView titleText = null;
    private LinearLayout only_wifi;
    private LinearLayout all_net;
    private RadioButton wifi;
    private RadioButton net;

    private AppManager mam = null; // Activity 管理器
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);

        titleText = (TextView) super.findViewById(R.id.title_text);
        only_wifi = (LinearLayout) super.findViewById(R.id.only_wifi);
        all_net = (LinearLayout) super.findViewById(R.id.all_net);
        wifi = (RadioButton) super.findViewById(R.id.wifi);
        net = (RadioButton) super.findViewById(R.id.net);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = NetActivity.this;

        init();

    }

    private void init() {
        titleText.setText("网络设置");
        wifi.setChecked(SPPrivateUtils.getBoolean(context, State.useWifi, false));
        net.setChecked(SPPrivateUtils.getBoolean(context, State.useAllNet, true));

        only_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(CLICK1);
            }
        });
        all_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(CLICK2);
            }
        });
    }

    private final static int CLICK1 = 1;
    private final static int CLICK2 = 2;

    private Handler mHandler = new MyHandler(NetActivity.this);

    public class MyHandler extends Handler {
        private WeakReference<NetActivity> nActivity;

        public MyHandler(NetActivity netActivity) {
            nActivity = new WeakReference<NetActivity>(netActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NetActivity netActivity = nActivity.get();
            switch (msg.what) {
                case CLICK1:
                    netActivity.wifi.setChecked(true);
                    netActivity.net.setChecked(false);
                    SPPrivateUtils.put(context, State.useWifi, true);
                    SPPrivateUtils.put(context, State.useAllNet, false);
                    SPPrivateUtils.put(context, State.netTint, "仅使用WIFI");
                    break;
                case CLICK2:
                    netActivity.net.setChecked(true);
                    netActivity.wifi.setChecked(false);
                    SPPrivateUtils.put(context, State.useWifi, false);
                    SPPrivateUtils.put(context, State.useAllNet, true);
                    SPPrivateUtils.put(context, State.netTint, "使用WIFI和移动网络");
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 右上返回按钮的监听
     * @param view  被点击的控件
     */
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                exit();
                break;
            default:
                break;
        }
    }

    /**
     * 退出
     */
    private void exit() {
        mam.finishActivity(this);
    }

    /**
     * 回退键监听
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return true;
    }
}
