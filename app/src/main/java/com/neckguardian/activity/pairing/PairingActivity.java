package com.neckguardian.activity.pairing;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.BluetoothManage;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.UIUtils;

import java.lang.ref.WeakReference;

/**
 * 蓝牙配对的类
 * Created by 孤月悬空 on 2015/12/27.
 */
public class PairingActivity extends BaseActivity implements View.OnClickListener {

    private TextView searching = null;

    private Context context = null;         //上下文
    private AppManager appManager = null;   //Activity的管理器
    private Intent intent;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManage mBluetoothManage;
    private UartService mService;
    private ServiceConnection mServiceConnection;

    private boolean isConnect = false;      //判断蓝牙是否已连接
    private boolean isActivate = true;      //判断本Activity是否已经关闭
    private int count = 0;                  //用来计数，达到动画的效果
    private static final int REQUEST_ENABLE_BT = 2;
    public static final int UART_PROFILE_CONNECTED = 123;
    public static final int UART_PROFILE_DISCONNECTED = 321;

    private static final String TAG = "PairingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pairing);

        searching = (TextView) super.findViewById(R.id.searching);

        context = PairingActivity.this;
        appManager = AppManager.getInstance();      //获取Activity管理器
        appManager.addActivity(this);               //将此Activity添加到管理器中

//      判断你的设备是否支持BLE。然后你可以禁止与ble相关的特色功能。
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化蓝牙的适配器，只有API18及以上的手机支持， 通过蓝夜管理器获取蓝牙适配器的引用
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {   //如果蓝牙未打开，打开蓝牙
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        mBluetoothManage = BluetoothManage.shareBluetoothManage();
        mServiceConnection = mBluetoothManage.getmServiceConnection();
        mService = mBluetoothManage.getmService();

        UIUtils.steepToolBar(this);      //如果安卓版本为4.4以上， 则隐藏状态栏
        init();
    }

    private void init() {

        new Thread(new Runnable() {
            @Override
            public void run() { //线程控制文字的变化
                while (!isConnect) {
                    mHandler.sendEmptyMessage(CHANGE_TEXT);
//                    Log.i(TAG, String.valueOf(count));
                    count++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        service_init();
        scanLeDevice(mBluetoothAdapter.enable());
    }

    private void service_init() {
        if (!SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
            Intent intent = new Intent(this, UartService.class);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter());
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_CONNECT_MSG");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(context, ConnectSuccessActivity.class);
                                startActivity(intent);
                                appManager.finishActivity(PairingActivity.this);
                            }
                        }).start();
                    }
                });
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        return intentFilter;
    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {   //延迟发送，10秒钟如果没有搜索到蓝牙设备，弹出提示框，并且停止搜索
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mHandler.sendEmptyMessage(NOT_FOUND_DEVICE);
                }
            }, 10000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }).start();
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    //蓝牙回调，搜索到蓝牙时会被调用
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Log.i(TAG, device.getAddress());
//                                    Log.i(TAG, "deviceName" + device.getName());
                                                                if (device.getAddress().equals("E0:89:20:66:1A:99")) {
                                                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                                                    SPPrivateUtils.put(context, State.BLEDEVICE, device.getAddress());
                                                                    if (SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
                                                                        mBluetoothManage.connectDevide(context);
                                                                    } else {
                                                                        Intent intent = new Intent(context, ConnectSuccessActivity.class);
                                                                        startActivity(intent);
                                                                        appManager.finishActivity(PairingActivity.this);
                                                                    }
                                                                } else if (device.getAddress().equals("FB:2A:AE:15:88:DE")) {
                                                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                                                    SPPrivateUtils.put(context, State.BLEDEVICE, device.getAddress());
                                                                    mBluetoothManage.connectDevide(context);
                                                                } else if (device.getAddress().equals("FB:53:D2:1C:29:75")) {
                                                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                                                    SPPrivateUtils.put(context, State.BLEDEVICE, device.getAddress());
                                                                    mBluetoothManage.connectDevide(context);
                                                                } else if (device.getAddress().equals("E8:44:20:6C:F6:BD")) {
                                                                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                                                    SPPrivateUtils.put(context, State.BLEDEVICE, device.getAddress());
                                                                    mBluetoothManage.connectDevide(context);
                                                                }
                                                            }
                                                        }

                                          );
                                      }
                                  }

                    );
                }
            };
    private final int CHANGE_TEXT = 101;
    private final int NOT_FOUND_DEVICE = 102;

    private Handler mHandler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<PairingActivity> mActivity;

        private MyHandler(PairingActivity pairingActivity) {
            mActivity = new WeakReference<PairingActivity>(pairingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PairingActivity activity = mActivity.get();
            switch (msg.what) {
                case CHANGE_TEXT:
                    setTextAnim(count);
                    break;
                case NOT_FOUND_DEVICE:
                    if (isActivate) {
                        showAlert();
                    }
                default:
                    break;
            }
        }
    }

    private void showAlert() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("没有发现附近的设备，或者未打开，请稍后再试")
                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanLeDevice(true);
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PairingActivity.this.finish();
                    }
                }).create();

        dialog.show();
    }

    private void setTextAnim(int countNum) {
        switch (countNum) {
            case 0:
                searching.setText("搜索中   ");
                break;
            case 1:
                searching.setText("搜索中.  ");
                break;
            case 2:
                searching.setText("搜索中.. ");
                break;
            case 3:
                searching.setText("搜索中...");
                count = -1;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                intent = new Intent(PairingActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                appManager.finishActivity(this);
                break;
//            case R.id.searching:
//                intent = new Intent(PairingActivity.this, ConnectSuccessActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                  break;
            default:
                break;
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
                    scanLeDevice(true);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "蓝牙打开失败 ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivate = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        if (!SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
            unbindService(mServiceConnection);
        }
    }
}
