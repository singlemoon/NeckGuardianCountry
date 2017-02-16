package com.neckguardian.activity.pairing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.neckguardian.R;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.activity.Mine.machine.MachineActivity;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;

/**
 * 连接成功跳转
 * Created by 孤月悬空 on 2016/1/17.
 */
public class ConnectSuccessActivity extends BaseActivity {
    private Context context = null;
    private ImageView sweepLight = null;
    private Button makeSure = null;
    private String TAG = "ConnectSuccessActivity";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_success);
        context = ConnectSuccessActivity.this;

        sweepLight = (ImageView) super.findViewById(R.id.sweep_light);
        makeSure = (Button) super.findViewById(R.id.make_sure);
        makeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
//                if (!SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
//                    SPPrivateUtils.put(context,State.startUseTime, System.currentTimeMillis() );
//                    SPPrivateUtils.put(context, State.isPairing, true);
//                    intent.putExtra("connect", "connect");
//                }
//                Intent intent = new Intent(context, AdjustingFirActivity.class);
                if (!SPPrivateUtils.getBoolean(context, State.isPairing, false)) {
                    SPPrivateUtils.put(context,State.startUseTime, System.currentTimeMillis() );
                    SPPrivateUtils.put(context, State.isPairing, true);
                    intent.putExtra("connect", "connect");
                }
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                AppManager manager = AppManager.getInstance();
//                manager.finishActivity(MachineActivity.class);
                ConnectSuccessActivity.this.finish();
            }
        });
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            sweepLight.startAnimation(operatingAnim);
        }
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                break;
        }
    }
}
