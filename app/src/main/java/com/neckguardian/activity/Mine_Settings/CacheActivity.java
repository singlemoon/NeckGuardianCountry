package com.neckguardian.activity.Mine_Settings;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.neckguardian.R;
import com.simo.manager.AppManager;

import java.io.File;

public class CacheActivity extends AppCompatActivity {

    private TextView titleText = null;
    private AppManager mam = null; // Activity 管理器
    String SavePath = getSDCardPath()+"/BoShi/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);

        titleText = (TextView) super.findViewById(R.id.title_text);
        mam = AppManager.getInstance();
        mam.addActivity(this);

        init();

    }
    private void init() {
        titleText.setText("清除缓存");
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

    public  void deleteDir() {
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

    private String getSDCardPath(){
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(sdcardExist){
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
