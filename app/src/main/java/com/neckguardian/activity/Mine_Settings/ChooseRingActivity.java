package com.neckguardian.activity.Mine_Settings;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.neckguardian.R;
import com.neckguardian.activity.Mine_Settings.Adapter.MusicAdapter;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;

import java.io.IOException;

public class ChooseRingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = null;
    private RecyclerView recyclerRing = null;
    private MusicAdapter adapter = null;

    private AppManager mam;
    private Context context;
    private String[] ringList = {"QQ飞车音乐", "htc闹铃", "起床闹铃"};
    private int[] ringIdList = {R.raw.car_ring, R.raw.htc_ring, R.raw.wake_up_ring};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_choose_ring);

        recyclerRing = (RecyclerView) super.findViewById(R.id.ring_list);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = ChooseRingActivity.this;

        init();
    }

    private void init() {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerRing.setLayoutManager(manager);
        adapter = new MusicAdapter(context, ringList, new MusicAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(int position) {
                SPPrivateUtils.put(context, State.bellId, ringIdList[position]);
                adapter.notifyDataSetChanged();
                playRing(position);
            }
        });
        recyclerRing.setAdapter(adapter);
    }

    private void playRing(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(context, ringIdList[position]);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            SPPrivateUtils.put(context, State.bellName, ringList[position]);

            Log.i("ChooseRingActivity"+ringList[position], "正在播放");
        } catch (IOException e) {
            T.showShort(context, "播放失败");
        }
    }


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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
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
