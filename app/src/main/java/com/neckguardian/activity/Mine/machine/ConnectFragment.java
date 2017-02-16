package com.neckguardian.activity.Mine.machine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.neckguardian.R;
import com.neckguardian.activity.pairing.PairingActivity;

/**
 * 点击按钮连接脖士的fragment
 * Created by 孤月悬空 on 2016/2/22.
 */
public class ConnectFragment extends Fragment {
    private View connectView = null;
    private Button connectBtn = null;

    private Context context = null;
    private final String TAG = "ConnectFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        connectBtn = (Button) connectView.findViewById(R.id.connect_btn);

        context = this.getActivity();

        init();
    }

    private void init() {
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PairingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        connectView = inflater.inflate(R.layout.fragment_connect, container, false);       //设置需要显示的layout，把这个当做Activity来用
        return connectView;
    }
}
