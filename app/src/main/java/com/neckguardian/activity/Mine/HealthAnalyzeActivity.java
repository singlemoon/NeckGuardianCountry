package com.neckguardian.activity.Mine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.makeramen.roundedimageview.RoundedImageView;
import com.model.DataOfDay;
import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.myView.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;


public class HealthAnalyzeActivity extends BaseActivity {

    private TextView titleText = null;
    private LineChart chart = null;
    private ImageView headImgHealth = null;
    private TextView nickNameHealth = null;
    private TextView healthProgress = null;
    private ProgressBar healthIndex = null;
    private TextView healthTip1 = null;
    private TextView healthTip2 = null;

    private LineDataSet dataSet = null;
    private LineData data = null;
    private ArrayList<Entry> yValue = null;
    private ArrayList<String> xValue = null;
    private List<DataOfDay> days = null;
    private DatabaseOperate db = null;

    private float[] yValues = new float[]{
            3000f, 6000f, 8000f, 7561f, 10222f, 11666f, 13999f, 17123f
    };
    private String[] week = new String[]{
            "3月", "周二", "周三", "周四", "周五", "周六", "周日", "周一"
    };

    private Context context = null;
    private static final String TAG = "HealthAnalyzeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_health_analyze);

        titleText = (TextView) super.findViewById(R.id.title_text);
        chart = (LineChart) super.findViewById(R.id.heath_line_chart);
        headImgHealth = (ImageView) super.findViewById(R.id.head_img_health);
        nickNameHealth = (TextView) super.findViewById(R.id.nick_name_health);
        healthProgress = (TextView) super.findViewById(R.id.health_progress);
        healthIndex = (ProgressBar) super.findViewById(R.id.health_index);
        healthTip1 = (TextView) super.findViewById(R.id.health_tip_1);
        healthTip2 = (TextView) super.findViewById(R.id.health_tip_2);

        context = HealthAnalyzeActivity.this;
        db = new DatabaseOperate(context);
        init();
    }

    private void init() {
        titleText.setText(getResources().getString(R.string.health_analyze));

        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            Glide.with(context)
                    .load(SPPrivateUtils.getString(context, State.headImg, State.headImg))
//                    .load("http://q.qlogo.cn/qqapp/1105105842/B0FADE0BC740E60191C58387D2FCE73F/40")
                    .transform(new GlideCircleTransform(context))
                    .into(headImgHealth);
            Log.i(TAG, SPPrivateUtils.getString(context, State.headImg, State.headImg));
            nickNameHealth.setText(SPPrivateUtils.getString(context, State.nickName, "请登录"));
        } else {
            headImgHealth.setImageResource(R.mipmap.nologin_n);
            nickNameHealth.setText("请先登录");
        }

        xValue = new ArrayList<>();
        yValue = new ArrayList<>();
        days = db.queryHistory();
        int sum = 0;
        float average = 0;
        int progress = 0;
        for (int i = 0; i < days.size(); i++) {
            String split[] = days.get(i).getDateNum().split("-");
            if (i == 0) {
                xValue.add("");
            } else {
                xValue.add(split[1]);
            }
            yValue.add(new Entry((days.get(i).getCurrentEnergy() * 1f), i));
            sum += days.get(i).getCurrentEnergy();
        }
        average = sum * 1f / days.size();
        progress = (int) (average / 300);
        Log.i(TAG, "sum = " + sum);
        Log.i(TAG, "average = " + average);
        healthProgress.setText(progress + "%");
        healthIndex.setProgress(progress);
        if (progress >= 85) {
            healthTip1.setText(getResources().getString(R.string.health_tip1_1));
            healthTip2.setText(getResources().getString(R.string.health_tip1_2));
        } else if (progress >= 60) {
            healthTip1.setText(getResources().getString(R.string.health_tip2_1));
            healthTip2.setText(getResources().getString(R.string.health_tip2_2));
        } else {
            healthTip1.setText(getResources().getText(R.string.health_tip3_1));
            healthTip2.setText(getResources().getText(R.string.health_tip3_2));
        }
//        for (int i = 0; i < 8; i++) {
//            if (i == 0) {
//                xValue.add("");
//            } else {
//                xValue.add(i + "");
//            }
//            yValue.add(new Entry(yValues[i], i));
//        }
        dataSet = new LineDataSet(yValue, "");
        dataSet.setColor(getResources().getColor(R.color.text_color_blue_2f));
        dataSet.setDrawCircles(false);
        dataSet.setDrawCubic(false);
        dataSet.setDrawValues(false);
//        dataSet.disableDashedLine();
//        dataSet.enableDashedLine(0, 0, 0);
//        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setLineWidth(2f);

        data = new LineData(xValue, dataSet);

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(getResources().getColor(R.color.white));

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setEnabled(false);
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);

        YAxis yr = chart.getAxisRight();
        yr.setEnabled(false);
        yr.setDrawLabels(false);
        YAxis yl = chart.getAxisLeft();
        yl.setDrawGridLines(false);
        // animate calls invalidate()...
        chart.animateX(2500);

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
        this.finish();
    }

}
