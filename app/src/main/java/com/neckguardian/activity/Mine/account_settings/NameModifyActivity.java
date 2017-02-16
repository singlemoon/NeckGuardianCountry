package com.neckguardian.activity.Mine.account_settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;

import java.lang.ref.WeakReference;

public class NameModifyActivity extends AppCompatActivity {

    private EditText nickNameEdit = null;
    private TextView titleText = null;
    private ImageView modify_cancel = null;
    private Button submitBut = null;

    private AppManager mam = null; // Activity 管理器
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_modify);

        titleText = (TextView) super.findViewById(R.id.title_text);
        nickNameEdit = (EditText) super.findViewById(R.id.nick_name_edit);
        modify_cancel = (ImageView) super.findViewById(R.id.modify_cancel);
        submitBut = (Button) super.findViewById(R.id.submit_but);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = NameModifyActivity.this;

        init();
    }

    private void init() {
        titleText.setText("修改昵称");
        nickNameEdit.setHint("请输入您的新昵称");
        modify_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(CLEAN_EDIT);
            }
        });

        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nickNameEdit.getText().toString())) {
                    T.showShort(context, "昵称不能为空");
                } else {
                    T.showShort(context, "昵称修改成功");
                    SPPrivateUtils.put(context, State.nickName, nickNameEdit.getText().toString());
                    exit();
                }
            }
        });
    }

    private final int CLEAN_EDIT = 101;
    private MyHandler mHandler = new MyHandler(this);

    public class MyHandler extends Handler{
        WeakReference<NameModifyActivity> mActivity = null;

        public MyHandler(NameModifyActivity nameModifyActivity) {
            mActivity = new WeakReference<NameModifyActivity>(nameModifyActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            NameModifyActivity activity = mActivity.get();

            switch (msg.what) {
                case CLEAN_EDIT:
                    activity.nickNameEdit.setText("");
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
