package com.neckguardian.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.activity.pairing.PairingActivity;
import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

/**
 * 引导界面
 * Created by 孤月悬空 on 2016/2/25.
 */
public class GuidanceActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager = null;
    private MyPagerAdapter pagerAdapter = null;
    private CircleIndicator indicator = null;

    private int[] imgs = new int[]{
            R.mipmap.guidance_1, R.mipmap.guidance_2, R.mipmap.guidance_3, R.mipmap.guidance_4
    };
    private int[] textBig = new int[]{
            R.string.perfect_monitor, R.string.post_remind, R.string.interact_improve, R.string.health_alarm
    };
    private int[] textSmall = new int[]{
            R.string.whenever_move_quiet, R.string.prevent_tired, R.string.interact_exercise, R.string.the_most_timely
    };
    private List<Map<String, Integer>> changeList = null;

    private static final String TAG = "GuidanceActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_guidance);
//        super.setContentView(R.layout.item_guidance_last);

        viewPager = (ViewPager) super.findViewById(R.id.view_pager);
        indicator = (CircleIndicator) super.findViewById(R.id.indicator);
        context = GuidanceActivity.this;

//        Button startBtn = (Button) super.findViewById(R.id.start_btn);
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SPPrivateUtils.put(context, State.isFirstTime, true);
//                Intent intent = new Intent(GuidanceActivity.this, PairingActivity.class);
//                GuidanceActivity.this.startActivity(intent);
//                GuidanceActivity.this.finish();
//            }
//        });
        initChangeList();   //初始化viewpager需要用到的文字图片
        pagerAdapter = new MyPagerAdapter(GuidanceActivity.this, changeList);   //创建一个adapter
        viewPager.setAdapter(pagerAdapter);     //设置适配器
        indicator.setViewPager(viewPager);      //设置viewpager指示器，底部小圆点
    }

    private void initChangeList() {
        changeList = new ArrayList<>();

        Map<String, Integer> imgsMap = new HashMap<>();
        for (int i = 0; i < imgs.length; i++) {
            imgsMap.put("imgs_" + i, imgs[i]);
        }
        Log.i(TAG, imgsMap.toString());
        changeList.add(imgsMap);

        Map<String, Integer> bigsMap = new HashMap<>();
        for (int i = 0; i < textBig.length; i++) {
            bigsMap.put("bigs_" + i, textBig[i]);
        }
        changeList.add(bigsMap);

        Map<String, Integer> smallMap = new HashMap<>();
        for (int i = 0; i < textSmall.length; i++) {
            smallMap.put("smalls_" + i, textSmall[i]);
        }
        changeList.add(smallMap);
    }

    public class MyPagerAdapter extends PagerAdapter {
        private List<View> views = null;
        private List<Map<String, Integer>> list = null;
        private Context context = null;
        private LayoutInflater mLayoutInflater = null;

        public MyPagerAdapter(Context context, List<Map<String, Integer>> list) {
            this.context = context;
            this.list = list;
            this.mLayoutInflater = LayoutInflater.from(context);
            initViewPager();
        }

        private void initViewPager() {
            views = new ArrayList<>();
            for (int i = 0; i < list.get(0).size(); i++) {
                View view = mLayoutInflater.inflate(R.layout.item_pager_img, null);
                ImageView pagerImg = (ImageView) view.findViewById(R.id.pager_img);
                TextView guidance_big = (TextView) view.findViewById(R.id.guidance_big);
                TextView guidance_small = (TextView) view.findViewById(R.id.guidance_small);
                pagerImg.setImageResource(list.get(0).get("imgs_" + i));
                guidance_big.setText(getResources().getText(list.get(1).get("bigs_" + i)));
                guidance_small.setText(getResources().getText(list.get(2).get("smalls_" + i)));

                views.add(view);
            }
            View view = mLayoutInflater.inflate(R.layout.item_guidance_last, null);
            Button startBtn = (Button) view.findViewById(R.id.start_btn);
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SPPrivateUtils.put(context, State.isFirstTime, false);
                    Intent intent = new Intent(GuidanceActivity.this, MainActivity.class);
                    GuidanceActivity.this.startActivity(intent);
                    GuidanceActivity.this.finish();
                }
            });
            views.add(view);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));

            return views.get(position);
        }
    }

    //点击跳过，跳转到配对界面
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
                Log.i(TAG, "skip");
                SPPrivateUtils.put(context, State.isFirstTime, false);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
