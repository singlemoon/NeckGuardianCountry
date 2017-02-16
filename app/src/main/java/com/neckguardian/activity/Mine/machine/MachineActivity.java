package com.neckguardian.activity.Mine.machine;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;

public class MachineActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 2;
    public static final String TAG = "MachineActivity";
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

    private TextView titleText = null;
    private TextView removeBound = null;
    private ConnectFragment connectFragment = null;
    private DisconnectFragment disconnectFragment = null;

    private AppManager mam = null; // Activity 管理器
    private Context context = null;
    private FragmentManager fragmentManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
        titleText = (TextView) super.findViewById(R.id.title_text);
        removeBound = (TextView) super.findViewById(R.id.remove_bound);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = MachineActivity.this;

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            mam.finishActivity(this);
            return;
        }
        init();
    }

    public void init() {
        titleText.setText("脖士");
        Log.i(TAG, "mState" + MainActivity.mState);
        if (MainActivity.mState == MainActivity.UART_PROFILE_DISCONNECTED) {
            setChoiceItem(0);
        } else if (MainActivity.mState == MainActivity.UART_PROFILE_CONNECTED) {
            setChoiceItem(1);
        }
        removeBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("解除绑定")
                            .setMessage("您确定要解除绑定吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SPPrivateUtils.put(context, State.isPairing, false);
                                    setChoiceItem(0);
                                    if (mService != null) {
                                        mService.disconnect();
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "取消了操作");
                                }
                            }).create();
                    dialog.show();
                } else {
                    T.showShort(context, "您还没有绑定设备");
                }
            }
        });
    }

    /**
     * 显示指定的Fragment
     *
     * @param what 要显示的Fragment的编码
     */
    private void setChoiceItem(int what) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);      //隐藏所有的Fragment

        switch (what) {
            case 0:
                if (connectFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    connectFragment = new ConnectFragment();
                    transaction.add(R.id.machine_frame, connectFragment);
                } else {    //如果homeFragment不为空，则直接显示
                    transaction.show(connectFragment);
                }
                break;
            case 1:
                if (disconnectFragment == null) {     //如果mainShippingFragment为空，则创建一个并添加到界面上
                    disconnectFragment = new DisconnectFragment();
                    transaction.add(R.id.machine_frame, disconnectFragment);
                } else {    //如果recreationFragment不为空，则直接显示
                    transaction.show(disconnectFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏所有的Fragment避免造成Fragment混乱
     *
     * @param transaction //Fragment的转换器
     */

    private void hideFragment(FragmentTransaction transaction) {
        if (connectFragment != null) {
            transaction.hide(connectFragment);
        }
        if (disconnectFragment != null) {
            transaction.hide(disconnectFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String s = "B";
        byte[] value;
        try {
            value = s.getBytes("UTF-8");
            Thread.sleep(1000);
            if (mService != null) {
                mService.writeRXCharacteristic(value);
                while (MainActivity.mState == MainActivity.UART_PROFILE_CONNECTED) {
                    mService.writeRXCharacteristic(value);
                    Thread.sleep(60000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "蓝牙已打开", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "蓝牙打开失败 ", Toast.LENGTH_SHORT).show();
                    mam.finishActivity(this);
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                back();
                break;
            default:
                break;
        }
    }

    /**
     * 退出
     */
    private void back() {
        mam.finishActivity(this);
    }
}
