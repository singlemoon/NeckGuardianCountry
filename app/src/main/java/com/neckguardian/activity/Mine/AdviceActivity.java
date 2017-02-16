package com.neckguardian.activity.Mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.service.AdviceService;
import com.simo.manager.AppManager;
import com.simo.utils.T;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class AdviceActivity extends AppCompatActivity {

    private TextView titleText = null;
    private EditText feedbackEdit = null;
    private Button submitBut = null;
    private AppManager mam = null; // Activity 管理器
    private final static String TAG = "AdviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        titleText = (TextView) super.findViewById(R.id.title_text);
        feedbackEdit = (EditText) super.findViewById(R.id.feedback_edit);
        submitBut = (Button) super.findViewById(R.id.submit_but);

        mam = AppManager.getInstance();
        mam.addActivity(this);

        init();
    }

    private void init() {
        titleText.setText("意见反馈");
        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(SEND_FEEDBACK);
            }
        });
    }

    private static final int SEND_FEEDBACK = 111;
    private static final int TOAST_1 = 222;
    private static final int TOAST_2 = 333;
    private static final int TOAST_3 = 444;
    private Handler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<AdviceActivity> mActivity;

        public MyHandler(AdviceActivity adviceActivity) {
            mActivity = new WeakReference<AdviceActivity>(adviceActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            AdviceActivity activity = mActivity.get();

            switch (msg.what) {
                case SEND_FEEDBACK:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            senFeedback();
                        }
                    }).start();
                    break;
                case TOAST_1:
                    T.showShort(activity, "您的意见我们已经收到，我们会根据您的意见做适当的修改");
                    break;
                case TOAST_2:
                    T.showShort(activity, "发送失败，请检查网络");
                    break;
                case TOAST_3:
                    T.showShort(activity, "您还没有填写意见, 那样我们就无法进行您想要的修改了");
                default:
                    break;
            }
        }
    }

    private void senFeedback() {
        String feedBackStr = feedbackEdit.getText().toString();
        if (TextUtils.isEmpty(feedBackStr)) {
            mHandler.sendEmptyMessage(TOAST_3);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("feedBackStr", feedBackStr);
            Log.i(TAG, "---------------- 开始上传 ----------------");
            String msg = new AdviceService().sendFeedback(map);
            if (msg == null) {
                mHandler.sendEmptyMessage(TOAST_2);
            } else if (msg.equals("success")) {
                mHandler.sendEmptyMessage(TOAST_1);
            } else if (msg.equals("no")) {
                Log.i(TAG, "上传失败，请检查代码");
            } else if (msg.equals("error")) {
                Log.i(TAG, "服务器发生了未知的错误，请修改");
            } else {
                mHandler.sendEmptyMessage(TOAST_2);
            }
        }
        Log.i(TAG, "---------------- 上传结束 ----------------");
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
