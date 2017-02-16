package com.neckguardian.activity.Recreation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.game.exercise.BalanceMaster;
import com.neckguardian.game.exercise.GameHistory;
import com.neckguardian.game.exercise.NeckGym;
import com.simo.utils.UIUtils;

/**
 * Created by 孤月悬空 on 2015/12/23.
 */
public class RecreationFragment extends Fragment {

    private View recreationView = null;
    private TextView titleText = null;
    private TextView history = null;
    private LinearLayout neckgym = null;
    private LinearLayout balanceMaster = null;

    private Context context = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleText = (TextView) recreationView.findViewById(R.id.title_text);
        history = (TextView) recreationView.findViewById(R.id.history);
        neckgym = (LinearLayout) recreationView.findViewById(R.id.neckgym);
        balanceMaster = (LinearLayout) recreationView.findViewById(R.id.balance_master);

        context = getActivity();

        UIUtils.hideTitleBar(this.getActivity(), R.id.fragment_recreation_layout);   //如果安卓系统在4.4以上，隐藏状态栏
//        UIUtils.steepToolBar(this.getActivity());//如果安卓系统在4.4以上，隐藏状态栏

        init();     //初始化
    }

    private void init() {
        titleText.setText(getResources().getText(R.string.title_recreation));

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameHistory.class);
                startActivity(intent);
            }
        });

        neckgym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NeckGym.class);
                startActivity(intent);
            }
        });
        balanceMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BalanceMaster.class);
                startActivity(intent);
            }
        });
    }

    private static final int GET_USERS = 111;

    public class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_USERS:

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recreationView = inflater.inflate(R.layout.fragment_recreation, container, false);   //设置需要显示的layout，把这个当做Activity来用
        return recreationView;
    }
}
