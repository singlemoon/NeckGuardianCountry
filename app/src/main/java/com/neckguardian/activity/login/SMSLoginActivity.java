package com.neckguardian.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.User;
import com.neckguardian.R;
import com.neckguardian.service.LoginService;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.manager.AppManager;
import com.simo.utils.NetUtils;
import com.simo.utils.PhoneUtils;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.UIUtils;
import com.simo.utils.myView.ProgressDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 短信验证登陆界面
 * Created by 孤月悬空 on 2016/1/10.
 */
public class  SMSLoginActivity extends BaseActivity {
    private TextView titleText = null;
    private EditText mobileEdit = null;
    private EditText verificationCodeEdit = null;
    private Button verificationCodeBut = null;
    private Button loginBut = null;

    private AppManager appManager = null;   //Activity管理器
    private Context context = null;

    private final static String TAG = "SMSLoginActivity";   //标记

    private boolean isSend = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms);

        titleText = (TextView) super.findViewById(R.id.title_text);
        mobileEdit = (EditText) super.findViewById(R.id.mobile_edit);
        verificationCodeEdit = (EditText) super.findViewById(R.id.verification_code_edit);
        verificationCodeBut = (Button) super.findViewById(R.id.verification_code_but);
        loginBut = (Button) super.findViewById(R.id.login_but);

        context = SMSLoginActivity.this;
        appManager = AppManager.getInstance();
        appManager.addActivity(this);

        initSMSSDK();
        UIUtils.steepToolBar(this);     //如果系统的安卓版本在4.4以上，则隐藏状态栏

        init();
    }

    private void init() {
        titleText.setText("手机登陆");

        mobileEdit.addTextChangedListener(new TextWatcher() {
            CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() == 11 && isSend) {
                    verifyPhone();
                } else {
                    verificationCodeBut.setBackgroundResource(R.drawable.hollow_unchecked_1);
                    verificationCodeBut.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
            }
        });

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick()) {
                    if (TextUtils.isEmpty(mobileEdit.getText().toString()) || mobileEdit.getText().toString().length() < 11) {
                        T.showShort(context, getResources().getText(R.string.mobile_hint));
                    } else if (TextUtils.isEmpty(verificationCodeEdit.getText().toString())) {
                        T.showShort(context, getResources().getText(R.string.verification_code_hint));
                    } else if (NetUtils.isConnected(context, true)) {
                        mHandler.sendEmptyMessage(PROGRESS_SHOW);
                        mHandler.sendEmptyMessage(GET_SERVER_RESULT);
//                    mHandler.sendEmptyMessage(PROGRESS_SHOW);
                    SMSSDK.submitVerificationCode("86", mobileEdit.getText().toString(), verificationCodeEdit.getText().toString());
                    }
                }
            }
        });
    }

    private void verifyPhone() {
        verificationCodeBut.setBackgroundResource(R.drawable.hollow_checked);
        verificationCodeBut.setTextColor(getResources().getColor(R.color.bg_title));
        verificationCodeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetUtils.isConnected(context, true)) {
                    verificationCodeBut.setBackgroundResource(R.drawable.hollow_unchecked_1);
                    verificationCodeBut.setTextColor(getResources().getColor(R.color.text_color_gray));
                    verificationCodeBut.setClickable(false);
                    isSend = false;
                    SMSSDK.getVerificationCode("86", mobileEdit.getText().toString());

                    new CountDownTimer(1000 * 60, 1000) {  // 倒计时 60 秒后可以重新获取验证码
                        @Override
                        public void onTick(long millisUntilFinished) {
                            String s = "重新获取(" + (millisUntilFinished / 1000) + ")";
                            verificationCodeBut.setText(s);
                        }

                        @Override
                        public void onFinish() {
                            verificationCodeBut.setClickable(true);
                            isSend = true;
                            verificationCodeBut.setBackgroundResource(R.drawable.hollow_checked);
                            verificationCodeBut.setTextColor(getResources().getColor(R.color.bg_title));
                            verificationCodeBut.setText(R.string.get_verification_code);
                        }
                    }.start();
                }
            }
        });
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
    private final static int SMSSID = 0;
    private Handler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<SMSLoginActivity> mActivity;

        public MyHandler(SMSLoginActivity activity) {
            mActivity = new WeakReference<SMSLoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SMSLoginActivity activity = mActivity.get();

            if (activity != null) {
                switch (msg.what) {
                    case SMSSID:
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        Log.i(TAG + "event", event + "");
                        Log.i(TAG + "result", result + "");

                        if (result == SMSSDK.RESULT_COMPLETE) {
//                            短信注册成功后，返回MainActivity
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {   //提交验证码成功
                                Log.i(TAG, "验证成功");
                                sendEmptyMessage(GET_SERVER_RESULT);
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {   //发送验证码成功
                                Log.i(TAG, "验证码已经发送");
                            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {   //返回支持发送验证码的国家列表
                                Log.i(TAG, "获取国家列表成功");
                            }
                        } else {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                T.showShort(activity, "验证码获取失败");
                            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                sendEmptyMessage(PROGRESS_DISMISS);
                                T.showShort(activity, "验证码错误");
                            }
                        }
                        break;
                    case PROGRESS_SHOW:
                        activity.progressDialog = new ProgressDialog.Builder(activity)
                                .content(getResources().getText(R.string.login_ing).toString()).build();
                        activity.progressDialog.show();
                        break;
                    case PROGRESS_DISMISS:
                        activity.progressDialog.dismiss();
                        break;
                    case GET_SERVER_RESULT:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                activity.getServerResult();
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

    private boolean isActivate = true; // 判断当前 Activity 是否处于激活状态

    private void getServerResult() {
        Log.w(TAG, "------------ 准备 登录 ------------");
        Map<String, String> map = new HashMap<>();
        map.put("tele", mobileEdit.getText().toString());
        map.put("nickName", mobileEdit.getText().toString());
        map.put("deviceId", PhoneUtils.getPhoneSoleId(this));

        Map<String, String> msgMap = new LoginService().login(map);
        if (msgMap == null) {
            mHandler.sendEmptyMessage(PROGRESS_DISMISS);
            mHandler.sendEmptyMessage(TOAST_1);
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
            Log.i(TAG, mobileEdit.getText().toString() + "登陆成功");
            SPPrivateUtils.put(context, State.isLogin, true);
            saveUserData(msgMap.get("user"));
            this.finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            appManager.finishActivity(this);
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

//        SPPrivateUtils.put(context, State.userId, user.getUserId());
//        SPPrivateUtils.put(context, State.nickName, user.getNickName());
        Log.i(TAG, mobileEdit.getText().toString());
        SPPrivateUtils.put(context, State.nickName, mobileEdit.getText().toString());
        SPPrivateUtils.put(context, State.isMobileBinding, true);
    }

    private EventHandler eh = null;

    /**
     * 初始化短信SDK
     */
    private void initSMSSDK() {
        SMSSDK.initSDK(context, State.SMSSDK_APPKEY, State.SMSSDK_APPSECRET);

        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    private final static long TIME = 500;
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
