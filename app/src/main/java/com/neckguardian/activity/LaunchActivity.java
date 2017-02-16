package com.neckguardian.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.activity.pairing.PairingActivity;
import com.neckguardian.service.HomeService;
import com.neckguardian.sign.State;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.NetUtils;
import com.simo.utils.SPPrivateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 打开App时显示的Activity
 * Created by 孤月悬空 on 2015/12/27.
 */
public class LaunchActivity extends Activity {

    private static final int TIME = 200;
    private final static String TAG = "LaunchActivity";
    private List<DataOfDay> dataList = null;    //用来存储数据
    private Intent intent;
    private Context context;
    private DatabaseOperate databaseOperate;    //数据库操作类
    private HomeService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_launch);

        context = LaunchActivity.this;
//        intent = new Intent(LaunchActivity.this, AdjustingSecActivity.class);
//        startActivity(intent);
//        LaunchActivity.this.finish();
//
        init();
    }

    private void init() {
        if (SPPrivateUtils.getBoolean(context, State.isFirstTime, true)) {     //如果是第一次打开本App，跳转到引导界面
            mHandler.sendEmptyMessage(SHOW_GUIDANCEACTIVITY);
            Log.i(TAG, "出现了没有");
        } else {
            if (SPPrivateUtils.getBoolean(context, State.isPairing, true)) {   //如果已经配对，开始从数据库获取数据
                databaseOperate = new DatabaseOperate(context);
//            insertVirtualData();      //添加模拟数据
//            databaseOperate.deleteAllData();
                dataList = databaseOperate.queryHistory();
                for (DataOfDay day : dataList) {
                    Log.i(TAG, day.toString());
                }
                Log.i(TAG + "list", dataList.size() + "");
                if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {     //如果已经登陆， 则向服务器请求数据
//                Log.i(TAG, "login");
                    if (NetUtils.isConnected(context, true)) {      //如果网络已连接， 则开始获取存在服务器中的数据，并且更新今天的数据
//                    Log.i(TAG, "netConnect");
                        mHandler.sendEmptyMessage(GET_DATA_OF_DAY);
                        mHandler.sendEmptyMessage(UPDATE);
                    }
                }
                mHandler.sendEmptyMessageDelayed(SHOW_MAINACTIVITY, TIME);      //无论有没有登陆都会跳转到主页
            } else {        //未配对，则跳转到配对界面
                mHandler.sendEmptyMessageDelayed(SHOW_PAIRINGACTIVITY, TIME);
            }
        }

        savePic(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher));
    }

    private final static int SHOW_MAINACTIVITY = 111;
    private final static int SHOW_PAIRINGACTIVITY = 222;
    private final static int SHOW_GUIDANCEACTIVITY = 333;
    private final static int GET_DATA_OF_DAY = 444;
    private final static int UPDATE = 555;

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private final WeakReference<LaunchActivity> mActivity;

        public MyHandler(LaunchActivity activity) {
            mActivity = new WeakReference<LaunchActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final LaunchActivity activity = mActivity.get();

            switch (msg.what) {
                case SHOW_MAINACTIVITY:
                    activity.intent = new Intent(activity, MainActivity.class);
                    activity.intent.putExtra("dataList", (Serializable) activity.dataList);
                    /*
                    for (DataOfDay day : activity.dataList) {
                        Log.i(TAG, day.toString());
                    }
                    */
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.alpha);
                    activity.exit();
                    break;
                case SHOW_PAIRINGACTIVITY:
                    activity.intent = new Intent(activity, PairingActivity.class);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.alpha);
                    activity.exit();
                    break;
                case SHOW_GUIDANCEACTIVITY:
                    activity.intent = new Intent(activity, GuidanceActivity.class);
                    activity.startActivity(activity.intent);
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.alpha);
                    activity.exit();
                    break;
                case GET_DATA_OF_DAY:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getDataOfDay();
                        }
                    }).start();
                    break;
                case UPDATE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (activity.dataList.size() > 0) {
                                activity.update(activity.dataList.get(activity.dataList.size() - 1));
                            }
                        }
                    }).start();
                default:
                    break;
            }
        }
    }

    /**
     * 将数据上传到服务器
     *
     * @param dataOfDay 需要上传到服务器的数据封装类
     */
    private void update(DataOfDay dataOfDay) {

        Log.w(TAG, "---------------- 开始讲数据上传到服务器 ----------------");
        HomeService service = new HomeService();
        Map<String, String> dateMap = new HashMap<>();

        dateMap.put("userId", SPPrivateUtils.getString(context, State.userId, "1"));
        dateMap.put("nickName", "Fiona");
        dateMap.put("currentEnergy", dataOfDay.getCurrentEnergy() + "");
        dateMap.put("targetEnergy", dataOfDay.getTargetEnergy() + "");
        dateMap.put("exerciseAngle", dataOfDay.getExerciseAngle() + "");
        dateMap.put("exerciseAmount", dataOfDay.getExerciseAmount() + "");
        dateMap.put("energyConsumption", dataOfDay.getEnergyConsumption() + "");
        dateMap.put("lowerTime", dataOfDay.getLowerTime() + "");
        dateMap.put("leftTime", dataOfDay.getLeftTime() + "");
        dateMap.put("rightTime", dataOfDay.getRightTime() + "");

        String msg = service.uploadDate(dateMap);
        if (msg == null) {
            Log.i(TAG, "服务器连接失败");
        } else if (msg.equals("no")) {
            Log.i(TAG, "上传失败，检查代码");
        } else if (msg.equals("success")) {
            Log.i(TAG, "上传成功");
        } else {
            Log.i(TAG, "未知错误");
        }
        Log.i(TAG, "---------------- 上传数据结束 ----------------");
    }

    private void getDataOfDay() {
        dataList = new HomeService().getUserDate(SPPrivateUtils.getString(context, State.userId, "1"));
        if (databaseOperate.queryHistory().size() == 0) {
            for (DataOfDay day : dataList) {
                databaseOperate.insertDataOfDay(day);
            }
        }
//        for (DataOfDay day : dataList) {
//            Log.i(TAG + "day", day.toString());
//            DatabaseOperate operate = new DatabaseOperate(context);
//            operate.insertDataOfDay(day);
//        }
    }

    public static String savePic(Bitmap b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.US);
        File outfile = new File(getSDCardPath() + "BoShi/icon");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = outfile + "/" + "icon.png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }

    public static String getSDCardPath() {
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);     //判断SDcard是否存在，不存在不进行操作
        String SDCardPath = null;
        if (flag) {
            SDCardPath =  Environment.getExternalStorageDirectory().getPath() + File.separator;
        }
        return SDCardPath;
    }

    public void insertVirtualData() {
        databaseOperate.deleteAllData();
        for (int i = 25; i < 30; i++) {
            DataOfDay dataOfDay = new DataOfDay();
            dataOfDay.setDateNum("02-" + i);
            dataOfDay.setCurrentEnergy(i * 1000 - 6000);
            dataOfDay.setTargetEnergy(30000);
            databaseOperate.insertDataOfDay(dataOfDay);
        }
        for (int i = 1; i < 5; i++) {
            DataOfDay dataOfDay = new DataOfDay();
            dataOfDay.setDateNum("03-0" + i);
            dataOfDay.setCurrentEnergy(23000 + i * 1000);
            dataOfDay.setTargetEnergy(30000);
            databaseOperate.insertDataOfDay(dataOfDay);
        }
        DataOfDay dataOfDay = new DataOfDay();
        dataOfDay.setDateNum("03-05");
        dataOfDay.setCurrentEnergy(32767);
        dataOfDay.setTargetEnergy(30000);
        dataOfDay.setLeftTime(33);
        dataOfDay.setLowerTime(32);
        dataOfDay.setRightTime(27);
        dataOfDay.setExerciseAngle(762);
        dataOfDay.setExerciseAmount(1372);
        dataOfDay.setEnergyConsumption(4763);
        databaseOperate.insertDataOfDay(dataOfDay);
        dataOfDay.setDateNum("03-06");
        dataOfDay.setCurrentEnergy(5236);
        dataOfDay.setTargetEnergy(30000);
        dataOfDay.setLeftTime(4);
        dataOfDay.setLowerTime(1);
        dataOfDay.setRightTime(5);
        dataOfDay.setExerciseAngle(793);
        dataOfDay.setExerciseAmount(3333);
        dataOfDay.setEnergyConsumption(9975);
        databaseOperate.insertDataOfDay(dataOfDay);
    }

    private void exit() {
        LaunchActivity.this.finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.alpha);
    }
}
