package com.neckguardian.activity.pairing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.neckguardian.R;
import com.neckguardian.activity.MainActivity;
import com.simo.BaseActivity;

/**
 * Created by 孤月悬空 on 2016/1/18.
 */
public class AdjustingFirActivity extends BaseActivity {

    private Intent intent = null;
    private Button nextStep = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_adjusting_fir);

        nextStep = (Button) super.findViewById(R.id.next_step);

        init();
    }

    private void init() {
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(AdjustingFirActivity.this, AdjustingSecActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                AdjustingFirActivity.this.finish();
            }
        });
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.skip:
                intent = new Intent(this, AdjustingSecActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                this.finish();
                break;
            default:
                break;
        }
    }
}
