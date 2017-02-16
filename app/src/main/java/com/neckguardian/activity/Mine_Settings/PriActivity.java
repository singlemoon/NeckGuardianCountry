package com.neckguardian.activity.Mine_Settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;

import java.lang.ref.WeakReference;

import me.xiaopan.switchbutton.SwitchButton;

public class PriActivity extends AppCompatActivity {

    private TextView titleText = null;
    private SwitchButton toCloud = null;
    private SwitchButton toAnother = null;
    private SwitchButton anonData = null;

    private AppManager mam = null; // Activity 管理器
    private Context context;
    private static final String TAG = "PriActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pri);

        titleText = (TextView) super.findViewById(R.id.title_text);
        toCloud = (SwitchButton) super.findViewById(R.id.to_cloud);
        toAnother = (SwitchButton) super.findViewById(R.id.to_another);
        anonData = (SwitchButton) super.findViewById(R.id.anon_data);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = PriActivity.this;

        init();
    }

    private void init() {
        titleText.setText("隐私设置");
        toCloud.setChecked(SPPrivateUtils.getBoolean(context, State.isToCloud, true));
        toAnother.setChecked(SPPrivateUtils.getBoolean(context, State.isToAnother, false));
        anonData.setChecked(SPPrivateUtils.getBoolean(context, State.isAnonData, false));

        toCloud.setOnCheckedChangeListener(new ToCloudListener());
        toAnother.setOnCheckedChangeListener(new ToAnotherListener());
        anonData.setOnCheckedChangeListener(new AnonDataListener());
    }

    public class ToCloudListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("本版本暂不支持将数据保存本地，您取消之后将会导致数据无法上传，下次登陆后您的数据将会为空，您还确定要取消吗？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SPPrivateUtils.put(context, State.isToCloud, false);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SPPrivateUtils.put(context, State.isToCloud, true);
                    mHandler.sendEmptyMessage(CANCEL_COULD);
                }
            });
            if (!isChecked) {
                builder.create().show();
            } else {
                SPPrivateUtils.put(context, State.isToCloud, true);
            }
        }
    }

    public class ToAnotherListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("本功能将会在V2.0开放，敬请期待");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SPPrivateUtils.put(context, State.isToAnother, false);
                    mHandler.sendEmptyMessage(CANCEL_ANOTHER);
                }
            });
            if (isChecked) {
                builder.create().show();
            }
        }
    }

    public class AnonDataListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mHandler.sendEmptyMessage(CANCEL_ANON);
        }
    }

    private static final int CANCEL_COULD = 111;
    private static final int CANCEL_ANOTHER = 222;
    private static final int CANCEL_ANON = 333;
    private Handler mHandler = new MyHandler(this);

    public class MyHandler extends Handler{
        private WeakReference<PriActivity> mActivity;

        public MyHandler(PriActivity priActivity){
            mActivity = new WeakReference<PriActivity>(priActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PriActivity activity = mActivity.get();

            switch (msg.what) {
                case CANCEL_COULD:
                    activity.toCloud.setChecked(true);
                    break;
                case CANCEL_ANOTHER:
                    activity.toAnother.setChecked(false);
                    break;
                case CANCEL_ANON:
                    SPPrivateUtils.put(context, State.isAnonData, true);
                    T.showShort(activity, "之后您反馈的消息将会以匿名用户为昵称");
                    break;
                default:
                    break;
            }
        }
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
