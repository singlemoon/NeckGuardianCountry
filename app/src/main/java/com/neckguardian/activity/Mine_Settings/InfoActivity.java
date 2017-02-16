package com.neckguardian.activity.Mine_Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.VibratorUtil;

import me.xiaopan.switchbutton.SwitchButton;

public class InfoActivity extends AppCompatActivity {

    private TextView titleText = null;
    private SwitchButton shock = null;
    private SwitchButton voice = null;
    private SwitchButton bell = null;
    private LinearLayout chooseRing = null;
    private TextView bellName = null;

    private AppManager mam = null; // Activity 管理器
    private Context context;
    private boolean shockCheck = false;
    private boolean voiceCheck = false;
    private boolean bellCheck = false;

    private final String TAG = "InfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        titleText = (TextView) super.findViewById(R.id.title_text);
        shock = (SwitchButton) super.findViewById(R.id.shock);
        voice = (SwitchButton) super.findViewById(R.id.voice);
        bell = (SwitchButton) super.findViewById(R.id.bell);
        chooseRing = (LinearLayout) super.findViewById(R.id.choose_ring);
        bellName = (TextView) super.findViewById(R.id.bell_name);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = InfoActivity.this;

        init();

    }

    private void init() {
        titleText.setText("通知设置");
        shock.setChecked(SPPrivateUtils.getBoolean(context, State.useShock, false));
        bell.setChecked(SPPrivateUtils.getBoolean(context, State.useBell, false));
        shockCheck = shock.isChecked();
        bellCheck = bell.isChecked();
        shock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i(TAG+"shock", String.valueOf(isChecked));
                shockCheck = isChecked;
                SPPrivateUtils.put(context, State.useShock, isChecked);
//                Log.i(TAG, String.valueOf(SPPrivateUtils.getBoolean(context, State.useShock, false)));
                if (bellCheck && shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并响铃");
                } else if (shockCheck&&voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并语音");
                }  else if (shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动");
                } else if (bellCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "响铃");
                } else if (voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "语音");
                }  else {
                    SPPrivateUtils.put(context, State.infoTint, "已关闭");
                }
                VibratorUtil.Vibrate(context, 500);
            }
        });

        bell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i(TAG+"bell", String.valueOf(isChecked));
                bellCheck = isChecked;
                if (bellCheck) {
                    voiceCheck = !bellCheck;
                    voice.setChecked(voiceCheck);
                }
                SPPrivateUtils.put(context, State.useBell, bellCheck);
                SPPrivateUtils.put(context, State.useVoice, voiceCheck);
//                Log.i(TAG, String.valueOf(SPPrivateUtils.getBoolean(context, State.useBell, false)));
                if (bellCheck && shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并响铃");
                } else if (shockCheck&&voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并语音");
                }  else if (shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动");
                } else if (bellCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "响铃");
                } else if (voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "语音");
                }  else {
                    SPPrivateUtils.put(context, State.infoTint, "已关闭");
                }
            }
        });

        voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                voiceCheck = isChecked;
                if (voiceCheck) {
                    bellCheck = !voiceCheck;
                    bell.setChecked(bellCheck);
                }
                SPPrivateUtils.put(context, State.useVoice, voiceCheck);
                SPPrivateUtils.put(context, State.useBell, bellCheck);
                if (bellCheck && shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并响铃");
                } else if (shockCheck&&voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动并语音");
                }  else if (shockCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "震动");
                } else if (bellCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "响铃");
                } else if (voiceCheck) {
                    SPPrivateUtils.put(context, State.infoTint, "语音");
                }  else {
                    SPPrivateUtils.put(context, State.infoTint, "已关闭");
                }
            }
        });

        chooseRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPPrivateUtils.getString(context, State.infoTint, "").equals("响铃")
                        || SPPrivateUtils.getString(context, State.infoTint, "").equals("震动并响铃")) {
                    Intent intent = new Intent(context, ChooseRingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                } else {
                    T.showShort(context, "你未选择响铃，不能选择铃声");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bellName.setText(SPPrivateUtils.getString(context, State.bellName, ""));
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
