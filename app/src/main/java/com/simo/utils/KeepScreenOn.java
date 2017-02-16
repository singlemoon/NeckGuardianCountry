package com.simo.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * 保持屏幕常亮
 * Created by 孤月悬空 on 2016/3/4.
 */
public class KeepScreenOn {
    private static PowerManager.WakeLock wl;

    /**
     * 保持屏幕唤醒状态（即背景灯不熄灭）
     *
     * @param on 是否唤醒
     */
    public static void keepScreenOn(Context context, boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wl.acquire();
        } else {
            wl.release();
            wl = null;
        }
    }
}
