package com.simo;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mobstat.StatService;

/**
 * 自定义Activity，用来防止字体变大
 * Created by 孤月悬空 on 2016/1/10.
 */
public class BaseActivity extends AppCompatActivity {
    private final static String TAG = "BaseActivity";


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 防止字体变大
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
