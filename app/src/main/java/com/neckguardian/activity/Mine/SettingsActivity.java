package com.neckguardian.activity.Mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.activity.Mine_Settings.InfoActivity;
import com.neckguardian.activity.Mine_Settings.NetActivity;
import com.neckguardian.activity.Mine_Settings.PriActivity;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.FileSizeUtils;
import com.simo.utils.SPPrivateUtils;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private TextView titleText = null;
    private Intent intent;
    private AppManager mam = null; // Activity 管理器
    private Context context;
    private final static String TAG = "SettingsActivity";
    String SavePath = getSDCardPath() + "/BoShi/ScreenImage/";

    private LinearLayout settings_info = null;
    private LinearLayout settings_pri = null;
    private LinearLayout settings_net = null;
    private LinearLayout settings_cache = null;
    private TextView infoHint = null;
    private TextView netHint = null;
    private TextView cacheHint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = SettingsActivity.this;
        titleText = (TextView) super.findViewById(R.id.title_text);
        settings_info = (LinearLayout) super.findViewById(R.id.settings_info);
        settings_pri = (LinearLayout) super.findViewById(R.id.settings_pri);
        settings_net = (LinearLayout) super.findViewById(R.id.settings_net);
        settings_cache = (LinearLayout) super.findViewById(R.id.settings_cache);
        infoHint = (TextView) super.findViewById(R.id.info_hint);
        netHint = (TextView) super.findViewById(R.id.net_hint);
        cacheHint = (TextView) super.findViewById(R.id.cache_hint);

        mam = AppManager.getInstance();
        mam.addActivity(this);

        init();
    }

    private void init() {
        titleText.setText("设置");
        infoHint.setText(SPPrivateUtils.getString(context, State.infoTint, "已关闭"));
        netHint.setText(SPPrivateUtils.getString(context, State.netTint, "使用WiFi和移动网络"));
        SPPrivateUtils.put(context, State.cacheHint, FileSizeUtils.getAutoFileOrFilesSize(getSDCardPath() + "/Boshi"));
        cacheHint.setText(SPPrivateUtils.getString(context, State.cacheHint, "0M"));

        settings_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, InfoActivity.class);
                startActivity(intent);
            }
        });
        settings_pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, PriActivity.class);
                startActivity(intent);
            }
        });
        settings_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, NetActivity.class);
                startActivity(intent);
            }
        });
        settings_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("是否清除缓存")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDir();
                                Toast.makeText(context, "成功清除缓存", Toast.LENGTH_SHORT).show();
                                SPPrivateUtils.put(context, State.cacheHint, FileSizeUtils.getAutoFileOrFilesSize(getSDCardPath() + "/Boshi"));
                                cacheHint.setText(SPPrivateUtils.getString(context, State.cacheHint, "0B"));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        infoHint.setText(SPPrivateUtils.getString(context, State.infoTint, "已关闭"));
        netHint.setText(SPPrivateUtils.getString(context, State.netTint, "使用WiFi和移动网络"));
        cacheHint.setText(SPPrivateUtils.getString(context, State.cacheHint, "0B"));
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

    //删除文件夹和文件夹里面的文件
    public void deleteDir() {
        File dir = new File(SavePath);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
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
