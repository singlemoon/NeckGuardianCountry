package com.neckguardian.activity.Mine.machine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.BluetoothManage;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.activity.pairing.AdjustingFirActivity;
import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;

import java.text.DecimalFormat;

/**
 * 断开连接
 * Created by 孤月悬空 on 2016/2/22.
 */
public class DisconnectFragment extends Fragment {
    private View disconnectView = null;
    private Button myTestBtn = null;
    private Button disconnectBtn = null;
    private ProgressBar disconnectPro = null;
    private TextView batteryAvailable = null;
    private TextView batteryUsed = null;

    private BluetoothManage mBluetoothManage = null;
    private UartService mService = null;

    private Context context = null;
    private boolean isActivate = true;
    private static final String TAG = "DisconnectFragment";

    private DecimalFormat format = new DecimalFormat("00");

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myTestBtn = (Button) disconnectView.findViewById(R.id.my_test_btn);
        disconnectBtn = (Button) disconnectView.findViewById(R.id.disconnect_btn);
        disconnectPro = (ProgressBar) disconnectView.findViewById(R.id.disconnect_pro);
        batteryAvailable = (TextView) disconnectView.findViewById(R.id.battery_available);
        batteryUsed = (TextView) disconnectView.findViewById(R.id.battery_used);

        context = this.getActivity();
        mBluetoothManage = BluetoothManage.shareBluetoothManage();
        mService = mBluetoothManage.getmService();
        init();
    }

    private void init() {

        myTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(context, "该功能暂未开发");

//                Intent intent = new Intent(context, AdjustingFirActivity.class);
//                startActivity(intent);
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.disconnect();
                }
                MainActivity.mState = MainActivity.UART_PROFILE_DISCONNECTED;
                HomeFragment.MyHandler hfHandler = new HomeFragment.MyHandler(MainActivity.getHomeFragment());
                hfHandler.sendEmptyMessage(HomeFragment.CHANGE_STATE);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.machine_frame, new ConnectFragment());
                ft.commit();
                T.showShort(context, "您的设备已断开");
            }
        });
    }

    private static final int PROGRESS_CHANGE = 111;
    private static final int USED_CHANGE = 333;
    private MyHandler mHandler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS_CHANGE:
                    int availableDay = (int) ((long) msg.obj / 1000f / 36 / 36) / 24;
                    int availableHour = (int) ((long) msg.arg1 / 1000f / 36 / 36) % 24;
                    String text = "预计可用" + format.format(availableDay) + "天" + format.format(availableHour) + "小时";
                    disconnectPro.setProgress(100 - (int) (availableHour * 1f / 168));
                    availableSetText(text);
                    break;
                case USED_CHANGE:
                    usedSetText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    public void availableSetText(String text) {
        SpannableString msp = new SpannableString(text);
        msp.setSpan(new AbsoluteSizeSpan(18, true), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(10, true), 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(18, true), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(10, true), 9, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        batteryAvailable.setText(msp);
        batteryAvailable.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void usedSetText(String text) {
        SpannableString msp2 = new SpannableString(text);
        msp2.setSpan(new AbsoluteSizeSpan(24, true), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp2.setSpan(new AbsoluteSizeSpan(12, true), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp2.setSpan(new AbsoluteSizeSpan(24, true), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp2.setSpan(new AbsoluteSizeSpan(12, true), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp2.setSpan(new AbsoluteSizeSpan(24, true), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp2.setSpan(new AbsoluteSizeSpan(12, true), 9, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        batteryUsed.setText(msp2);
        batteryUsed.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        Message msg1 = new Message();
        msg1.what = PROGRESS_CHANGE;
        msg1.obj = System.currentTimeMillis() - SPPrivateUtils.getLong(context, State.startUseTime, System.currentTimeMillis());
        mHandler.sendMessage(msg1);
        long currentTime = System.currentTimeMillis();
        long startUseTime = SPPrivateUtils.getLong(context, State.startUseTime, System.currentTimeMillis());
        long usedMinute = (currentTime - startUseTime) / 1000 / 60 % 60;
        long usedHour = (currentTime - startUseTime) / 1000 / 60 / 60 % 24;
        long usedDay = (currentTime - startUseTime) / 1000 / 60 / 60 / 24;
        String text = format.format(usedDay) + "天"
                + format.format(usedHour) + "小时"
                + format.format(usedMinute) + "分钟";
        Message msg2 = new Message();
        msg2.what = USED_CHANGE;
        msg2.obj = text;
        mHandler.sendMessage(msg2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        disconnectView = inflater.inflate(R.layout.fragment_disconnect, container, false);       //设置需要显示的layout，把这个当做Activity来用
        return disconnectView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActivate = false;
    }
}
