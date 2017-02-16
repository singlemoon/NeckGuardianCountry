package com.neckguardian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.ScreenShot;
import com.simo.utils.myView.GlideCircleTransform;
import com.simo.utils.myView.MyScrollView;
import com.simo.utils.myView.RainbowProgressView;

import java.io.File;
import java.io.FileOutputStream;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * 分享
 * Created by 孤月悬空 on 2016/1/13.
 */
public class ShareActivity extends BaseActivity {

    private ImageView qq = null;
    private ImageView qzone = null;
    private ImageView weixin = null;
    private ImageView weibo = null;
    private TextView shareDate = null;
    private TextView shareNickName = null;
    private TextView shareCurrent = null;
    private TextView shareTarget = null;
    private TextView shareLower = null;
    private TextView shareLeft = null;
    private TextView shareRight = null;
    private TextView shareAngle = null;
    private TextView shareAmount = null;
    private TextView shareEnergy = null;
    private RainbowProgressView shareProgress = null;
    private RoundedImageView shareHeadImg = null;
    private MyScrollView shareScroll = null;

    private AppManager appManager;      //Activity管理器
    private Context context;
    private String picName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        appManager = AppManager.getInstance();
        appManager.addActivity(this);
        context = ShareActivity.this;

        init();
    }

    private void init() {
        qq = (ImageView) super.findViewById(R.id.qq);
        qzone = (ImageView) super.findViewById(R.id.qzone);
        weixin = (ImageView) super.findViewById(R.id.weixin);
        weibo = (ImageView) super.findViewById(R.id.weibo);
        shareHeadImg = (RoundedImageView) super.findViewById(R.id.share_head_img);
        shareDate = (TextView) super.findViewById(R.id.share_date);
        shareNickName = (TextView) super.findViewById(R.id.share_nick_name);
        shareCurrent = (TextView) super.findViewById(R.id.share_current);
        shareTarget = (TextView) super.findViewById(R.id.share_target);
        shareLower = (TextView) super.findViewById(R.id.share_lower);
        shareLeft = (TextView) super.findViewById(R.id.share_left);
        shareRight = (TextView) super.findViewById(R.id.share_right);
        shareAngle = (TextView) super.findViewById(R.id.share_angle);
        shareAmount = (TextView) super.findViewById(R.id.share_amount);
        shareEnergy = (TextView) super.findViewById(R.id.share_energy);
        shareProgress = (RainbowProgressView) super.findViewById(R.id.share_progress);
        shareScroll = (MyScrollView) super.findViewById(R.id.share_Scroll);

        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            Glide.with(context)
                    .load(SPPrivateUtils.get(context, State.headImg, State.headImg))
                    .transform(new GlideCircleTransform(context))
                    .into(shareHeadImg);
        }

        getShareDay();

        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picName = ScreenShot.savePic(ScreenShot.getBitmapByView(shareScroll));
                Log.i("ShareActivity", picName);
                showShare(QQ.NAME);
            }
        });

        qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picName = ScreenShot.savePic(ScreenShot.compressImage(ScreenShot.getBitmapByView(shareScroll)));
                showShare(QZone.NAME);
            }
        });

//        weixin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetandSaveCurrentImage();
//                showShare(.NAME);
//            }
//        });

        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picName = ScreenShot.savePic(ScreenShot.compressImage(ScreenShot.getBitmapByView(shareScroll)));
                showShare(SinaWeibo.NAME);
            }
        });
    }

    private void getShareDay() {
        Intent intent = getIntent();
        DataOfDay shareDay = (DataOfDay) intent.getSerializableExtra("shareDay");
        shareDate.setText(shareDay.getDateNum());
        shareNickName.setText(SPPrivateUtils.getString(context, State.nickName, "请先登录"));
        shareCurrent.setText(String.valueOf(shareDay.getCurrentEnergy()));
        shareTarget.setText(String.valueOf(shareDay.getTargetEnergy() + ""));
        shareLower.setText(String.valueOf(shareDay.getLowerTime() + ""));
        shareLeft.setText(String.valueOf(shareDay.getLeftTime() + ""));
        shareRight.setText(String.valueOf(shareDay.getRightTime() + ""));
        shareAngle.setText(String.valueOf(shareDay.getExerciseAngle() + ""));
        shareAmount.setText(String.valueOf(shareDay.getExerciseAmount() + ""));
        shareEnergy.setText(String.valueOf(shareDay.getCurrentEnergy() + ""));
        shareProgress.setProgress(shareDay.getCurrentEnergy() * 1f / shareDay.getTargetEnergy() * 100);
//        shareProgress.setProgress(100);
    }

    private void showShare(String name) {
        OnekeyShare oks = new OnekeyShare();
        String SavePath = getSDCardPath() + "/BoShi/ScreenImage";
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share_to_where));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://7xq02m.com1.z0.glb.clouddn.com/1.jpg");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("一起来体验吧");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(picName);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://7xq02m.com1.z0.glb.clouddn.com/1.jpg");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://7xq02m.com1.z0.glb.clouddn.com/1.jpg");
        oks.setPlatform(name);
        oks.setSilent(true);
// 启动分享GUI
        oks.show(this);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.share_back_btn:
                exit();
                break;
            default:
                break;
        }
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
            String filepath = SavePath + "/Screen_1.png";
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
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);     //判断SDcard是否存在，不存在不进行操作
        String SDCardPath = null;
        if (flag) {
            SDCardPath =  Environment.getExternalStorageDirectory().getPath() + File.separator;
        }
        return SDCardPath;
    }

    /**
     * 退出
     */
    private void exit() {
        appManager.finishActivity(this);
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
