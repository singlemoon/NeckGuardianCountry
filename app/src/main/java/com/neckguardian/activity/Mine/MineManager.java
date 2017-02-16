package com.neckguardian.activity.Mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.neckguardian.R;
import com.neckguardian.activity.Mine.account_settings.NameModifyActivity;
import com.neckguardian.activity.login.LoginActivity;
import com.neckguardian.service.Utils.GlideLoader;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.SPPrivateUtils;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class MineManager extends AppCompatActivity {

    private TextView titleText = null;
    private LinearLayout modifyImg = null;
    private LinearLayout usernameMng = null;
    private TextView nickNameMng = null;
    private Button exitLogin = null;
    private RoundedImageView headImg = null;

    private ArrayList<String> path = new ArrayList<>();

    private Platform qq;
    private Platform weibo;
    private Context context;
    private AppManager mam = null; // Activity 管理器
    private final static String TAG = "MineManager";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_manager);

        titleText = (TextView) super.findViewById(R.id.title_text);
        modifyImg = (LinearLayout) super.findViewById(R.id.modify_img);
        usernameMng = (LinearLayout) super.findViewById(R.id.username_mng);
        nickNameMng = (TextView) super.findViewById(R.id.nick_name_mng);
        exitLogin = (Button) super.findViewById(R.id.exit_login);
        headImg = (RoundedImageView) super.findViewById(R.id.mine_manager_head_img);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = MineManager.this;

        initSDK();
        init();
    }

    private void initSDK() {
        ShareSDK.initSDK(this);

        qq = ShareSDK.getPlatform(QQ.NAME);
        weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
    }

    private void init() {
        titleText.setText("帐号管理");
        nickNameMng.setText(SPPrivateUtils.getString(context, State.nickName, "请输入你的昵称"));

        Glide.with(context)
                .load(SPPrivateUtils.get(context, State.headImg,State.headImg ))
                .centerCrop()
                .into(headImg);
        usernameMng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineManager.this, NameModifyActivity.class);
                startActivity(intent);
            }
        });

        exitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "退出，清除账号信息");
                weibo.removeAccount(true);
                qq.removeAccount(true);
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                exit();
                SPPrivateUtils.put(context, State.isLogin, false);
                SPPrivateUtils.put(context, State.nickName, "请点击登陆");
                DatabaseOperate dbo = new DatabaseOperate(context);
                dbo.deleteAllData();
            }
        });

        modifyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageConfig imageConfig
                        = new ImageConfig.Builder(
                        new GlideLoader())
                        .steepToolBarColor(getResources().getColor(R.color.yancy_lightbluea200))
                        .titleBgColor(getResources().getColor(R.color.yancy_lightbluea200))
                        .titleSubmitTextColor(getResources().getColor(R.color.white))
                        .titleTextColor(getResources().getColor(R.color.yancy_lightbluea200))
                        .singleSelect()
                        .crop()
                        .build();
                ImageSelector.open(MineManager.this, imageConfig);   // 开启图片选择器
//                mHandler.sendEmptyMessage(UPLOD_IMG);
            }
        });

    }

//    private static final int UPLOD_IMG = 111;
//    private static final int TOAST_1 = 222;
//    private static final int TOAST_2 = 333;
//    private static final int TOAST_3 = 444;
//    private Handler mHandler = new MyHandler(this);
//
//    private class MyHandler extends Handler {
//        private WeakReference<MineManager> mActivity;
//
//        public MyHandler(MineManager mineManager) {
//            mActivity = new WeakReference<MineManager>(mineManager);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            MineManager activity = mActivity.get();
//
//            switch (msg.what) {
//                case UPLOD_IMG:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            uploadHeadImg();
//                        }
//                    }).start();
//                    break;
//                case TOAST_1:
//                    T.showShort(activity, "您的意见我们已经收到，我们会根据您的意见做适当的修改");
//                    break;
//                case TOAST_2:
//                    T.showShort(activity, "发送失败，请检查网络");
//                    break;
//                case TOAST_3:
//                    T.showShort(activity, "您还没有填写意见, 那样我们就无法进行您想要的修改了");
//                default:
//                    break;
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            for (String path : pathList) {
                Log.i("ImagePathList", path);
            }

            path.clear();
            path.addAll(pathList);

            Glide.with(context)
                    .load(path.get(0))
                    .centerCrop()
                    .into(headImg);
            SPPrivateUtils.put(context,State.headImg,path.get(0));
        }
    }

//    private void initHeadImg(String path) {
//
//    }
//
//    private void uploadHeadImg() {
//        String imgPath = path.get(0);
//        File file = new File(imgPath);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        nickNameMng.setText(SPPrivateUtils.getString(context, State.nickName, "点击这里修改你的昵称"));
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
