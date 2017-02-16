package com.neckguardian.activity.Mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.game.exercise.NeckGym;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.myView.GlideCircleTransform;
import com.simo.utils.myView.SlideSelectView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.DecimalFormat;
import java.util.List;

public class MyHealthActivity extends AppCompatActivity {

    private TextView titleText = null;
    private Context context;
    private AppManager mam = null; // Activity 管理器
    private final static String TAG = "MyHealthActivity";
    private RoundedImageView mineHeadImg = null;
    private TextView mineName = null;
    private TextView daysTime = null;
    private TextView hoursTime = null;
    private TextView minutesTime = null;
    private TextView exercise_Angle = null;
    private TextView exercise_Amount = null;
    private TextView energy_Consumption = null;
    private TextView lower_Time = null;
    private TextView aveAngle = null;
    private TextView aveAmount = null;
    private TextView aveConsumption = null;
    private TextView aveLowerTime = null;
    private TextView left_Time = null;
    private TextView right_Time = null;
    private TextView alwaysBody = null;
    private RelativeLayout lineLeft = null;
    private RelativeLayout lineAll = null;
    private RelativeLayout lineRight = null;
    private Button openJbc = null;

    private int exerciseAngle = 0;
    private int exerciseAmount = 0;
    private int energyConsumption = 0;
    private float lowerTime = 0;
    private float leftTime = 0;
    private float rightTime = 0;
    private float allTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_health);

        titleText = (TextView) super.findViewById(R.id.title_text);
        mineHeadImg = (RoundedImageView) super.findViewById(R.id.mine_head_img);
        mineName = (TextView) super.findViewById(R.id.username_mine);
        daysTime = (TextView) super.findViewById(R.id.days);
        hoursTime = (TextView) super.findViewById(R.id.hours);
        minutesTime = (TextView) super.findViewById(R.id.minutes);
        exercise_Angle = (TextView) super.findViewById(R.id.exerciseAngle);
        exercise_Amount = (TextView) super.findViewById(R.id.exerciseAmount);
        energy_Consumption = (TextView) super.findViewById(R.id.energyConsumption);
        lower_Time = (TextView) super.findViewById(R.id.lowerTime);
        aveAngle = (TextView) super.findViewById(R.id.aveAngle);
        aveAmount = (TextView) super.findViewById(R.id.aveAmount);
        aveConsumption = (TextView) super.findViewById(R.id.aveConsumption);
        aveLowerTime = (TextView) super.findViewById(R.id.aveLowerTime);
        left_Time = (TextView) super.findViewById(R.id.leftTime);
        right_Time = (TextView) super.findViewById(R.id.rightTime);
        alwaysBody = (TextView) super.findViewById(R.id.alwaysBody);
        lineLeft = (RelativeLayout) super.findViewById(R.id.lineLeft);
        lineAll = (RelativeLayout) super.findViewById(R.id.lineAll);
        lineRight = (RelativeLayout) super.findViewById(R.id.lineRight);
        openJbc = (Button) super.findViewById(R.id.openjbc);

        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = MyHealthActivity.this;
        mineName.setText(SPPrivateUtils.getString(context, State.nickName, "未登陆"));

        init();
    }

    private void init() {
        titleText.setText("健康管理");
        openJbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击了打开健脖操", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, NeckGym.class);
                startActivity(intent);
                mam.finishActivity(MyHealthActivity.this);
            }
        });
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            Glide.with(context)
                    .load(SPPrivateUtils.getString(context, State.headImg, State.headImg))
                    .transform(new GlideCircleTransform(context))
                    .into(mineHeadImg);
            Log.i(TAG, SPPrivateUtils.getString(context, State.headImg, State.headImg));
            mineName.setText(SPPrivateUtils.getString(context, State.nickName, "请登录"));
        } else {
            mineHeadImg.setImageResource(R.mipmap.nologin_n);
            mineName.setText("请先登录");
        }

        //设置总检测时间
//        long startTime = (long) SPPrivateUtils.get(context,State.startUseTime,(long)1);
        long startTime = System.currentTimeMillis() - (long) 1000 * 80 * 99 * 24;
        long nowTime = System.currentTimeMillis();
        long currentTime = nowTime - startTime;
        long days = currentTime / (1000 * 60 * 60 * 24);
        long hours = (currentTime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (currentTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        daysTime.setText(days + "");
        hoursTime.setText(hours + "");
        minutesTime.setText(minutes + "");

        DatabaseOperate databaseOperate = new DatabaseOperate(context);
        List<DataOfDay> dataOfDayList = databaseOperate.queryHistory();
        int countDay = dataOfDayList.size();
        for (DataOfDay day : dataOfDayList) {
            exerciseAngle += day.getExerciseAngle();
            exerciseAmount += day.getExerciseAmount();
            energyConsumption += day.getEnergyConsumption();
            lowerTime += day.getLowerTime();
            rightTime += day.getRightTime();
            leftTime += day.getLeftTime();
        }
        allTime = rightTime + leftTime;

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormat decimalFormat1 = new DecimalFormat();
        decimalFormat.applyPattern("0.0");
        decimalFormat1.applyPattern("0");
        exercise_Angle.setText(exerciseAngle + "");
        exercise_Amount.setText(exerciseAmount + "");
        energy_Consumption.setText(energyConsumption + "");
        lower_Time.setText(decimalFormat.format(lowerTime / 60) + "");
        if (allTime == 0) {
            left_Time.setText(0 + "%");
            right_Time.setText(0 + "%");
        } else {
            left_Time.setText(decimalFormat1.format(leftTime / allTime * 100) + "%");
            right_Time.setText(decimalFormat1.format(100 - (leftTime / allTime * 100)) + "%");
        }
        if (leftTime / allTime * 100.0 > 50) {
            alwaysBody.setText("习惯左倾");
            lineLeft.setVisibility(View.VISIBLE);
        } else if (leftTime / allTime * 100.0 < 50) {
            alwaysBody.setText("习惯右倾");
            lineRight.setVisibility(View.VISIBLE);
        } else {
            alwaysBody.setText("您的习惯很正常");
            lineAll.setVisibility(View.VISIBLE);
        }

        aveAngle.setText(exerciseAngle / countDay + "");
        aveAmount.setText(exerciseAmount / countDay + "");
        aveConsumption.setText(energyConsumption / countDay + "");
        aveLowerTime.setText(decimalFormat.format(lowerTime / 60 / countDay) + "");

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
