package com.neckguardian.activity.pairing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.activity.BloothLE.BluetoothManage;
import com.neckguardian.activity.BloothLE.UartService;
import com.neckguardian.activity.MainActivity;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.utils.HardwareUtil;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.myView.MyDesignCircle;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

/**
 * 校验界面2
 * Created by 孤月悬空 on 2016/1/20.
 */
public class AdjustingSecActivity extends BaseActivity {
    private Intent intent;
    private Button startTest = null;
    private MyDesignCircle testCircle = null;
    private TextView complete = null;
    private TextView changeText = null;

    private Context context = null;
    private boolean isActivate = true;
    private boolean isStart = false;

    private static final String TAG = "AdjustingSecActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_adjusting_sec);

        context = AdjustingSecActivity.this;

        startTest = (Button) super.findViewById(R.id.start_test);
        changeText = (TextView) super.findViewById(R.id.changeText);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTest.getText().toString().equals("开始校验")) {
//                    isStart = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 360; i > 180; i -= 2) {
                                Message msg = new Message();
                                msg.what = CHANGE_BALL_ANGLE;
                                msg.arg1 = i;
                                mHandler.sendMessage(msg);
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            for (int i = 180; i < 540; i += 2) {
                                Message msg = new Message();
                                msg.what = CHANGE_BALL_ANGLE;
                                msg.arg1 = i;
                                mHandler.sendMessage(msg);
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            mHandler.sendEmptyMessage(CHANGE_TEXT);
                        }
                    }).start();
                } else if (startTest.getText().toString().equals("分享到QQ")) {
                    GetandSaveCurrentImage();
                    showShare();
                } else if (startTest.getText().toString().equals("完成")) {
                    intent = new Intent(AdjustingSecActivity.this, MainActivity.class);
                    intent.putExtra("connect", "connect");
                    startActivity(intent);
                    AdjustingSecActivity.this.finish();
                }
            }
        });
        testCircle = (MyDesignCircle) super.findViewById(R.id.test_circle);


//        service_init();
    }

    private int lastAngle = 0;
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        private int count = 0;
        private double x;
        private double y;
        private double z;
        private HardwareUtil hardwareUtil;

        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                if (isStart) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                count++;
                                int angle = 0;
                                String text = new String(txValue, "UTF-8");
                                String split[] = text.split("[.]");
                                if (!text.equals("nan")) {
                                    String msg = split[0] + "." + split[1].substring(0, 1);
//                                    Log.i(TAG, "msg = " + msg);
                                    switch (count) {
                                        case 1:
                                            z = Double.parseDouble(msg);
                                            break;
                                        case 2:
                                            y = Double.parseDouble(msg);
                                            break;
                                        case 3:
                                            count = 0;
                                            x = Double.parseDouble(msg);
                                            hardwareUtil = new HardwareUtil(z, y, x);
                                            angle = hardwareUtil.toCoordinate();
                                            Log.i(TAG, "toCoordiante = " + angle);
                                            Message message = new Message();
                                            message.what = CHANGE_BALL_ANGLE;
                                            message.arg1 = angle;
                                            mHandler.sendMessage(message);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    });
                }
            }
        }
    };

    private void service_init() {
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private static final int CHANGE_BALL_ANGLE = 111;
    private static final int CHANGE_ANIMOTION = 222;
    private static final int CHANGE_TEXT = 444;
    private MyHandler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        WeakReference<AdjustingSecActivity> mActivity = null;
        float count = 180f;
        int angle = 0;
        int angleI = 0;

        public MyHandler(AdjustingSecActivity adjustingSecActivity) {
            mActivity = new WeakReference<AdjustingSecActivity>(adjustingSecActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final AdjustingSecActivity activity = mActivity.get();
            switch (msg.what) {
                case CHANGE_BALL_ANGLE:
                    angle = msg.arg1;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < 10; i++) {
//                                try {
//                                    angleI = i + 1;
//                                    mHandler.sendEmptyMessage(CHANGE_ANIMOTION);
//                                    Thread.sleep(35);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            lastAngle = angle;
//                        }
//                    }).start();
                    testCircle.setBallAngle(angle);
//                    Log.i(TAG + "bal", count + "");
                    break;
                case CHANGE_ANIMOTION:
                    testCircle.setBallAngle((angle - lastAngle) / 10f * angleI + lastAngle);
                    Log.i(TAG, "ballAngle" + testCircle.getBallAngle());
                    break;
                case CHANGE_TEXT:
                    changeText.setText("校验完成，开始使用吧");
                    startTest.setText("完成");
                    SPPrivateUtils.put(context, State.isFirstTime, false);
                    break;
                default:
                    break;
            }
        }
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

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                this.finish();
                break;
            default:
                break;
        }
    }

}
