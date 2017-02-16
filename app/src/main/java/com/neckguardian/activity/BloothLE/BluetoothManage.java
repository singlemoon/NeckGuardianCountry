package com.neckguardian.activity.BloothLE;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;

/**
 * 蓝牙的管理器
 * Created by 孤月悬空 on 2016/2/22.
 */
public class BluetoothManage {

    private static BluetoothManage mBluetoothManage;

    private UartService mService = null;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("BluetoothManage", "蓝牙已连接");
            mService = ((UartService.LocalBinder) service).getService();
            if (!mService.initialize()) {
                Log.i("BluetoothManage", "蓝牙初始化失败");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private BluetoothManage() {
    }

    public static BluetoothManage shareBluetoothManage() {
        if (mBluetoothManage == null) {
            mBluetoothManage = new BluetoothManage();
        }
        return mBluetoothManage;
    }

    public boolean connectDevide(Context context) {
        String address = SPPrivateUtils.getString(context, State.BLEDEVICE, null);
        if (address == null) {
            return false;
        }
        if (mService != null) {
            return mService.connect(address);
        } else {
            return false;
        }
    }

    public void disConnectDevide() {
        mService.disconnect();
    }

    public UartService getmService() {
        return mService;
    }

    public ServiceConnection getmServiceConnection() {
        return mServiceConnection;
    }
}
