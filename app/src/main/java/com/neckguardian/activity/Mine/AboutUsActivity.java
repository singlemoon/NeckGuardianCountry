package com.neckguardian.activity.Mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.neckguardian.R;
import com.simo.manager.AppManager;

public class AboutUsActivity extends AppCompatActivity {

    private TextView titleText = null;
    private AppManager mam = null; // Activity 管理器
    private final static String TAG = "AboutUsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        titleText = (TextView) super.findViewById(R.id.title_text);
        mam = AppManager.getInstance();
        mam.addActivity(this);

        init();
    }

    private void init() {
        titleText.setText("关于我们");
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
