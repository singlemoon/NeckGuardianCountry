package com.neckguardian.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.WindowManager;

import com.neckguardian.R;
import com.neckguardian.activity.Mine.SettingsActivity;
import com.neckguardian.activity.RemindActivity;
import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.VibratorUtil;

import java.io.IOException;

/**
 * Created by 孤月悬空 on 2016/2/05.
 */
public class RemindReceiver extends BroadcastReceiver {
    private static final String TAG = "RemindReceiver";
    MediaPlayer mediaPlayer = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
//        Log.i(TAG, "我们有没有收到广播");
        Intent show = new Intent(context, RemindActivity.class);
        show.putExtra("what", intent.getStringExtra("what"));
        show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(show);
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("警告");
        builder.setMessage("您的脖子已经很久没有活动了，请注意活动一下");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    VibratorUtil.cancel();
                }
                T.showShort(context, "请好好活动您僵硬的脖子，身体是革命的本钱啊");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();

        if (SPPrivateUtils.getBoolean(context, State.useBell, false) &&
                SPPrivateUtils.getBoolean(context, State.useShock, false)) {
            playRing(context);
            VibratorUtil.Vibrate(context, new long[]{2000, 500, 2000, 500}, true);
        } else if (SPPrivateUtils.getBoolean(context, State.useShock, false)) {
            VibratorUtil.Vibrate(context, 2000);
        } else if (SPPrivateUtils.getBoolean(context, State.useBell, false)) {
            playRing(context);
        }*/
    }

    private void playRing(final Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(context, SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring));

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Log.i("ChooseRingActivity" + SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring), "正在播放");
                } catch (IOException e) {
                    T.showShort(context, "播放失败");
                }
            }
        });
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.i("ChooseRingActivity" + SPPrivateUtils.getInt(context, State.bellId, R.raw.car_ring), "正在播放");
        } catch (IOException e) {
            T.showShort(context, "播放失败");
        }
    }
}
