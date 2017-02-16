package com.neckguardian.activity.pairing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.activity.MainActivity;
import com.simo.BaseActivity;
import com.simo.utils.myView.MyDesignCircle;

import java.io.File;
import java.io.FileOutputStream;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

/**
 * Created by 孤月悬空 on 2016/3/1.
 */
public class AdjustingThiActivity extends BaseActivity implements View.OnClickListener {
    private Button shareToQQ = null;
    private MyDesignCircle testCircleThird = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_adjusting_thi);

    }


    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        String SavePath = getSDCardPath() + "/BoShi/ScreenImage";
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share_to_where));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(SavePath + "/Screen_2.png");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.baidu.com");
        oks.setPlatform(QQ.NAME);
        oks.setSilent(true);
// 启动分享GUI
        oks.show(this);
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage() {
        //1.构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        String SavePath = getSDCardPath() + "/BoShi/ScreenImage";

        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath + "/Screen_2.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();

                Toast.makeText(this, "本图片文件已保存至SDCard/BoShi/ScreenImage/下", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
                Intent intent = new Intent(AdjustingThiActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                this.finish();
                break;
//            case R.id.searching:
//                intent = new Intent(PairingActivity.this, ConnectSuccessActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                  break;
            default:
                break;
        }
    }
}
