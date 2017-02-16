package com.neckguardian.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.User;
import com.neckguardian.R;
import com.neckguardian.activity.Home.HomeFragment;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.service.LoginService;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.PhoneUtils;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.UIUtils;
import com.simo.utils.myView.ProgressDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

/**
 * 登陆界面
 * Created by 孤月悬空 on 2015/12/29.
 */
public class LoginActivity extends BaseActivity {

    private ImageView weibo_btn = null;
    private ImageView qq_btn = null;
    private TextView sms_btn = null;

    private AppManager appManager = null;
    private Context context = null;
    private Intent intent = null;
    private PlatformDb platDB = null;
    private final static String TAG = "LoginActivity";

    private Platform weibo;
    private Platform qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appManager = AppManager.getInstance();
        appManager.addActivity(this);
        context = this;

        ShareSDK.initSDK(this);

        weibo_btn = (ImageView) super.findViewById(R.id.weibo_btn);
        qq_btn = (ImageView) super.findViewById(R.id.qq_btn);
        sms_btn = (TextView) super.findViewById(R.id.sms_btn);

        UIUtils.hideTitleBar(this, R.id.activity_login);
        init();
    }


    private void init() {
        initWeiBo();
        initQQ();

        weibo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick()) {
                    weibo.showUser(null);
                }
            }
        });

        qq_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick()) {
                    qq.showUser(null);
                }
            }
        });

//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "移除授权 清空缓存");
//                weibo.removeAccount(true);
//                qq.removeAccount(true);
//            }
//        });

        sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, SMSLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                appManager.finishActivity(LoginActivity.this);
            }
        });

    }

    private void initWeiBo() {
        weibo = ShareSDK.getPlatform(SinaWeibo.NAME);

        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                Log.i(TAG, "onComplete: ");
                if (action == Platform.ACTION_USER_INFOR) {
                    platDB = platform.getDb();
                    Log.i(TAG, "getToken: " + platDB.getToken());
                    Log.i(TAG, "getUserGender: " + platDB.getUserGender());
                    Log.i(TAG, "getUserIcon: " + platDB.getUserIcon());
                    Log.i(TAG, "getUserId: " + platDB.getUserId());
                    Log.i(TAG, "getUserName: " + platDB.getUserName());

                    if (TextUtils.isEmpty(platDB.getUserId())) {
                        weibo.authorize();
                    } else {
                        Log.i(TAG, "onComplete: 已登录");
                        mHandler.sendEmptyMessage(PROGRESS_SHOW);
                        mHandler.sendEmptyMessage(GET_SERVER_RESULT);
                    }
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                Log.i(TAG, "onError: ");
                mHandler.sendEmptyMessage(TOAST_2);
            }

            @Override
            public void onCancel(Platform platform, int action) {
                Log.i(TAG, "onCancel: ");
            }
        });

    }

    private void initQQ() {
        qq = ShareSDK.getPlatform(QQ.NAME);

        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                Log.i(TAG, "onComplete: ");
                if (action == Platform.ACTION_USER_INFOR) {
                    platDB = platform.getDb();

                    Log.i(TAG, "getToken: " + platDB.getToken());
                    Log.i(TAG, "getUserGender: " + platDB.getUserGender());
                    Log.i(TAG, "getUserIcon: " + platDB.getUserIcon());
                    Log.i(TAG, "getUserId: " + platDB.getUserId());
                    Log.i(TAG, "getUserName: " + platDB.getUserName());
                    if (TextUtils.isEmpty(platDB.getUserId())) {
                        qq.authorize();
                    } else {
                        Log.i(TAG, "onComplete: 已登录");
                        mHandler.sendEmptyMessage(PROGRESS_SHOW);
                        mHandler.sendEmptyMessage(GET_SERVER_RESULT);
                    }
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                Log.i(TAG, "onError: ");
                mHandler.sendEmptyMessage(TOAST_2);
            }

            @Override
            public void onCancel(Platform platform, int action) {
                Log.i(TAG, "onCancel: ");
            }
        });

    }

    private boolean isActivate = true; // 判断当前 Activity 是否处于激活状态
    private HomeFragment.MyHandler hfHandler = new HomeFragment.MyHandler(MainActivity.getHomeFragment());

    private void intentToMainActivity() {
        Log.w(TAG, "------------ 准备 登录 ------------");
        Map<String, String> map = new HashMap<>();

        if (platDB.getPlatformNname().equalsIgnoreCase("QQ")) {
            map.put("qq", platDB.getUserId());
        } else if (platDB.getPlatformNname().equalsIgnoreCase("SinaWeibo")) {
            map.put("weiBo", platDB.getUserId());
        }
        map.put("nickName", platDB.getUserName());
        map.put("deviceId", PhoneUtils.getPhoneSoleId(this));
        Map<String, String> msgMap = new LoginService().login(map);
        if (msgMap == null) {
            mHandler.sendEmptyMessage(PROGRESS_DISMISS);
            mHandler.sendEmptyMessage(TOAST_1);
            return;
        }
        String result = msgMap.get("msg");
        mHandler.sendEmptyMessage(PROGRESS_DISMISS);
        if (!isActivate) {
            Log.w(TAG, "改Activity已经被关闭， 暂不获取数据");
        } else if (result == null || result.equals("")) {
            Log.e(TAG, "系统异常，请稍后重试");
            mHandler.sendEmptyMessage(TOAST_1);
        } else if (result.equalsIgnoreCase("no")) {
            Log.w(TAG, "参数异常，请检查");
        } else if ("error".equalsIgnoreCase(result)) {
            Log.w(TAG, "服务器发生未知错误，请稍后重试");
            mHandler.sendEmptyMessage(TOAST_4);
        } else if ("success".equals(result)) {
            Log.i(TAG, platDB.getUserId() + "登陆成功");
            SPPrivateUtils.put(context, State.isLogin, true);
            saveUserData(msgMap.get("user"));
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            hfHandler.sendEmptyMessage(HomeFragment.GET_USER_DATE);
            appManager.finishActivity(LoginActivity.this);
        } else {
            Log.e(TAG, "网络异常， 请稍后重试");
            mHandler.sendEmptyMessage(TOAST_2);
        }
        Log.w(TAG, "------------ 登录 结束 ------------");
    }

    private void saveUserData(String userJson) {
        Gson gson = new Gson();

        User user = gson.fromJson(userJson, new TypeToken<User>() {
        }.getType());
//        Log.i(TAG, user.toString());
//        SPPrivateUtils.put(context, State.userId, user.getUserId());
//        SPPrivateUtils.put(context, State.nickName, user.getNickName());
        Log.i(TAG, platDB.getUserName());
        SPPrivateUtils.put(context, State.nickName, platDB.getUserName());
        SPPrivateUtils.put(context, State.headImg, platDB.getUserIcon());
        if (platDB.getPlatformNname().equalsIgnoreCase("QQ")) {
            SPPrivateUtils.put(context, State.isQQBinding, true);
        } else if (platDB.getPlatformNname().equalsIgnoreCase("SinaWeiBo")) {
            SPPrivateUtils.put(context, State.isSinaWeiBoBinding, true);
        }
    }

    private ProgressDialog progressDialog;
    private final static int PROGRESS_SHOW = 1;
    private final static int PROGRESS_DISMISS = 2;
    private final static int GET_SERVER_RESULT = 11;
    private final static int TOAST_1 = 101;
    private final static int TOAST_2 = 102;
    private final static int TOAST_3 = 103;
    private final static int TOAST_4 = 104;
    private final static int TOAST_5 = 105;
    private final static int SHOW_INFO_1 = 51;
    private Handler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final LoginActivity activity = mActivity.get();

            if (activity != null) {
                switch (msg.what) {
                    case PROGRESS_SHOW:
                        activity.progressDialog = new ProgressDialog.Builder(activity).content(activity.getText(R.string.login_ing).toString()).build();
                        activity.progressDialog.show();
                        break;
                    case PROGRESS_DISMISS:
                        activity.progressDialog.dismiss();
                        break;
                    case GET_SERVER_RESULT:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                activity.intentToMainActivity();
                            }
                        }).start();
                        break;
                    case TOAST_1:
                        T.showShort(activity, activity.getText(R.string.system_error));
                        break;
                    case TOAST_2:
                        T.showShort(activity, activity.getText(R.string.no_network));
                        break;
                    case TOAST_3:
                        T.showShort(activity, activity.getText(R.string.login_ban));
                        break;
                    case TOAST_4:
                        T.showShort(activity, "服务器发生未知错误，请稍后重试");
                        break;
                    case TOAST_5:
                        T.showShort(activity, "您的申请信息被驳回，请重新填写");
                        break;
                    case SHOW_INFO_1:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private final static long TIME = 2000;
    private static long lastClickTime;

    /**
     * 500毫秒限制点击一次
     */
    public synchronized static boolean isClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < TIME) {
            return false;
        }
        lastClickTime = time;
        return true;
    }
}
