package com.neckguardian.activity.Mine;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.manager.AppManager;
import com.simo.utils.SPPrivateUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class HealthSetActivity extends AppCompatActivity {

    private TextView titleText = null;
    private AppManager mam = null; // Activity 管理器
    private Context context;
    private final static String TAG = "HealthSetActivity";

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private TextView jdl1 = null;
    private TextView jdl2 = null;
    private TextView jdl3 = null;
    private TextView jdl4 = null;
    private TextView jdl5 = null;
    private TextView jz1 = null;
    private TextView jz2 = null;
    private TextView jz3 = null;
    private TextView jz4 = null;
    private TextView jz5 = null;
    private TextView lmd1 = null;
    private TextView lmd2 = null;
    private TextView lmd3 = null;
    private TextView lmd4 = null;
    private TextView lmd5 = null;

    private DiscreteSeekBar jdlSeerbar = null;
    private DiscreteSeekBar jzSeerbar = null;
    private DiscreteSeekBar lmdSeerbar = null;
    private TextView targetValue = null;
    private TextView jzTime = null;
    private TextView wrongLmd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_set);

        titleText = (TextView) super.findViewById(R.id.title_text);
        mam = AppManager.getInstance();
        mam.addActivity(this);
        context = HealthSetActivity.this;

        jdl1 = (TextView) super.findViewById(R.id.jdl1);
        jdl2 = (TextView) super.findViewById(R.id.jdl2);
        jdl3 = (TextView) super.findViewById(R.id.jdl3);
        jdl4 = (TextView) super.findViewById(R.id.jdl4);
        jdl5 = (TextView) super.findViewById(R.id.jdl5);
        jz1 = (TextView) super.findViewById(R.id.jz1);
        jz2 = (TextView) super.findViewById(R.id.jz2);
        jz3 = (TextView) super.findViewById(R.id.jz3);
        jz4 = (TextView) super.findViewById(R.id.jz4);
        jz5 = (TextView) super.findViewById(R.id.jz5);
        lmd1 = (TextView) super.findViewById(R.id.lmd1);
        lmd2 = (TextView) super.findViewById(R.id.lmd2);
        lmd3 = (TextView) super.findViewById(R.id.lmd3);
        lmd4 = (TextView) super.findViewById(R.id.lmd4);
        lmd5 = (TextView) super.findViewById(R.id.lmd5);

        jdlSeerbar = (DiscreteSeekBar) super.findViewById(R.id.jdl_seekbar);
        jzSeerbar = (DiscreteSeekBar) super.findViewById(R.id.jz_seekbar);
        lmdSeerbar = (DiscreteSeekBar) super.findViewById(R.id.lmd_seekbar);
        targetValue = (TextView) super.findViewById(R.id.targetValue);
        jzTime = (TextView) super.findViewById(R.id.jz_time);
        wrongLmd = (TextView) super.findViewById(R.id.wrongLmd);

        params.gravity = Gravity.TOP;
        params1.gravity = Gravity.TOP;
        params1.weight = 1;
        params2.weight = 1;

        init();

    }

    private void jdlSet() {
        switch (SPPrivateUtils.getInt(context, State.progressValue, 3)) {
            case 1:
                targetValue.setText(10000 + "(最低)");
                clearJdlParams();
                jdl1.setLayoutParams(params1);
                break;
            case 2:
                targetValue.setText(20000 + "(适中)");
                clearJdlParams();
                jdl2.setLayoutParams(params1);
                break;
            case 3:
                targetValue.setText(30000 + "(推荐)");
                clearJdlParams();
                jdl3.setLayoutParams(params1);
                break;
            case 4:
                targetValue.setText(40000 + "(活跃)");
                clearJdlParams();
                jdl4.setLayoutParams(params1);
                break;
            case 5:
                targetValue.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 50000) + "(自定义)");
                clearJdlParams();
                jdl5.setLayoutParams(params);
                break;
        }

        jdlSeerbar.setProgress((int) SPPrivateUtils.get(context, State.progressValue, 3));

        jdlSeerbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                switch (value) {
                    case 1:
                        targetValue.setText(10000 + "(最低)");
                        clearJdlParams();
                        jdl1.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.targetEnergy, 10000);
                        SPPrivateUtils.put(context, State.progressValue, 1);
                        break;
                    case 2:
                        targetValue.setText(20000 + "(适中)");
                        clearJdlParams();
                        jdl2.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.targetEnergy, 20000);
                        SPPrivateUtils.put(context, State.progressValue, 2);
                        break;
                    case 3:
                        targetValue.setText(30000 + "(推荐)");
                        clearJdlParams();
                        jdl3.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.targetEnergy, 30000);
                        SPPrivateUtils.put(context, State.progressValue, 3);
                        break;
                    case 4:
                        targetValue.setText(40000 + "(活跃)");
                        clearJdlParams();
                        jdl4.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.targetEnergy, 40000);
                        SPPrivateUtils.put(context, State.progressValue, 4);
                        break;
                    case 5:
                        clearJdlParams();
                        jdl5.setLayoutParams(params);
                        targetValue.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 30000) + "(自定义)");
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                if (seekBar.getProgress() == 5) {
                    final EditText zidingyi = new EditText(context);
                    zidingyi.setInputType(InputType.TYPE_CLASS_NUMBER);
                    zidingyi.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    zidingyi.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 30000) + "");
                    SPPrivateUtils.put(context, State.progressValue, 5);

                   /* AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("自定义目标").setView(zidingyi)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            if (zidingyi.getText().toString().equals("") || Integer.parseInt(zidingyi.getText().toString()) == 0) {
                                Toast.makeText(context, "请输入1-99999的数字", Toast.LENGTH_SHORT).show();
                                targetValue.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 40000) + "(自定义)");
                                return;
                            }
                            targetValue.setText(zidingyi.getText().toString() + "(自定义)");
                            SPPrivateUtils.put(context, State.targetEnergy, Integer.parseInt(zidingyi.getText().toString()));
                        }
                    });
                    builder.show();*/

                    final AlertDialog alert = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.util_edit_dialog, null);
                    final EditText editText = (EditText) view.findViewById(R.id.edit_text);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                    editText.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 30000) + "");
                    LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                    LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().equals("")
                                    || Integer.parseInt(editText.getText().toString()) == 0
                                    || Integer.parseInt(editText.getText().toString()) > 99999) {
                                Toast.makeText(context, "请输入1-99999的数字", Toast.LENGTH_SHORT).show();
                                targetValue.setText(SPPrivateUtils.getInt(context, State.targetEnergy, 40000) + "(自定义)");
                            } else {
                                targetValue.setText(editText.getText().toString() + "(自定义)");
                                SPPrivateUtils.put(context, State.targetEnergy, Integer.parseInt(zidingyi.getText().toString()));
                                alert.dismiss();
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.setView(view);
                    alert.show();
                }
            }
        });

    }

    //僵坐提醒间隔设置
    private void jzSet() {
        switch (SPPrivateUtils.getInt(context, State.progressJz, 3)) {
            case 1:
                jzTime.setText(90 + "分钟(最长)");
                clearJzParams();
                jz1.setLayoutParams(params1);
                break;
            case 2:
                jzTime.setText(60 + "分钟(适中)");
                clearJzParams();
                jz2.setLayoutParams(params1);
                break;
            case 3:
                jzTime.setText(45 + "分钟(推荐)");
                clearJzParams();
                jz3.setLayoutParams(params1);
                break;
            case 4:
                jzTime.setText(15 + "分钟(频繁)");
                clearJzParams();
                jz4.setLayoutParams(params1);
                break;
            case 5:
                jzTime.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "分钟(自定义)");
                clearJzParams();
                jz5.setLayoutParams(params);
                break;
        }

        jzSeerbar.setProgress((int) SPPrivateUtils.get(context, State.progressJz, 3));

        jzSeerbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                switch (value) {
                    case 1:
                        jzTime.setText(90 + "分钟(最长)");
                        clearJzParams();
                        jz1.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.longTimeSit, 90);
                        SPPrivateUtils.put(context, State.progressJz, 1);
                        break;
                    case 2:
                        jzTime.setText(60 + "分钟(适中)");
                        clearJzParams();
                        jz2.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.longTimeSit, 60);
                        SPPrivateUtils.put(context, State.progressJz, 2);
                        break;
                    case 3:
                        jzTime.setText(45 + "分钟(推荐)");
                        clearJzParams();
                        jz3.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.longTimeSit, 45);
                        SPPrivateUtils.put(context, State.progressJz, 3);
                        break;
                    case 4:
                        jzTime.setText(15 + "分钟(频繁)");
                        clearJzParams();
                        jz4.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.longTimeSit, 15);
                        SPPrivateUtils.put(context, State.progressJz, 4);
                        break;
                    case 5:
                        clearJzParams();
                        jz5.setLayoutParams(params);
                        jzTime.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "分钟(自定义)");
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                if (seekBar.getProgress() == 5) {
//                    final EditText zidingyi2 = new EditText(context);
//                    zidingyi2.setInputType(InputType.TYPE_CLASS_NUMBER);
//                    zidingyi2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
//                    zidingyi2.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "");
                    SPPrivateUtils.put(context, State.progressJz, 5);
/*
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("自定义目标").setView(zidingyi2)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            if (zidingyi2.getText().toString().equals("") || Integer.parseInt(zidingyi2.getText().toString()) == 0
                                    || Integer.parseInt(zidingyi2.getText().toString()) > 90) {
                                Toast.makeText(context, "请输入1-90的数字", Toast.LENGTH_SHORT).show();
                                jzTime.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "分钟(自定义)");
                                return;
                            }
                            jzTime.setText(zidingyi2.getText().toString() + "分钟(自定义)");
                            SPPrivateUtils.put(context, State.longTimeSit, Integer.parseInt(zidingyi2.getText().toString()));
                        }
                    });
                    builder.show();
*/

                    final AlertDialog alert = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.util_edit_dialog, null);
                    final EditText editText = (EditText) view.findViewById(R.id.edit_text);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    editText.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "");
                    LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                    LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().equals("")
                                    || Integer.parseInt(editText.getText().toString()) == 0
                                    || Integer.parseInt(editText.getText().toString()) > 90) {
                                Toast.makeText(context, "请输入1-90的数字", Toast.LENGTH_SHORT).show();
                                jzTime.setText(SPPrivateUtils.getInt(context, State.longTimeSit, 45) + "分钟(自定义)");
                            } else {
                                jzTime.setText(editText.getText().toString() + "(自定义)");
                                SPPrivateUtils.put(context, State.longTimeSit, Integer.parseInt(editText.getText().toString()));
                                alert.dismiss();
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.setView(view);
                    alert.show();
                }

            }
        });
    }

    //异常姿态灵敏度设置
    private void lmdSet() {
        switch (SPPrivateUtils.getInt(context, State.progressLmd, 3)) {
            case 1:
                wrongLmd.setText(60 + "秒(迟钝)");
                clearLmdParams();
                lmd1.setLayoutParams(params1);
                break;
            case 2:
                wrongLmd.setText(40 + "秒(适中)");
                clearLmdParams();
                lmd2.setLayoutParams(params1);
                break;
            case 3:
                wrongLmd.setText(30 + "秒(推荐)");
                clearLmdParams();
                lmd3.setLayoutParams(params1);
                break;
            case 4:
                wrongLmd.setText(10 + "秒(敏感)");
                clearLmdParams();
                lmd4.setLayoutParams(params1);
                break;
            case 5:
                wrongLmd.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 30) + "秒(自定义)");
                clearLmdParams();
                lmd5.setLayoutParams(params);
                break;
        }

        lmdSeerbar.setProgress((int) SPPrivateUtils.get(context, State.progressLmd, 3));

        lmdSeerbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                switch (value) {
                    case 1:
                        wrongLmd.setText(60 + "秒(迟钝)");
                        clearLmdParams();
                        lmd1.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.wrongSetting, 60);
                        SPPrivateUtils.put(context, State.progressLmd, 1);
                        break;
                    case 2:
                        wrongLmd.setText(40 + "秒(适中)");
                        clearLmdParams();
                        lmd2.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.wrongSetting, 40);
                        SPPrivateUtils.put(context, State.progressLmd, 2);
                        break;
                    case 3:
                        wrongLmd.setText(30 + "秒(推荐)");
                        clearLmdParams();
                        lmd3.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.wrongSetting, 30);
                        SPPrivateUtils.put(context, State.progressLmd, 3);
                        break;
                    case 4:
                        wrongLmd.setText(10 + "秒(敏感)");
                        clearLmdParams();
                        lmd4.setLayoutParams(params1);
                        SPPrivateUtils.put(context, State.wrongSetting, 10);
                        SPPrivateUtils.put(context, State.progressLmd, 4);
                        break;
                    case 5:
                        clearLmdParams();
                        lmd5.setLayoutParams(params);
                        wrongLmd.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 30) + "秒(自定义)");
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                if (seekBar.getProgress() == 5) {
                    final EditText zidingyi3 = new EditText(context);
                    zidingyi3.setInputType(InputType.TYPE_CLASS_NUMBER);
                    zidingyi3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    zidingyi3.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 30) + "");
                    SPPrivateUtils.put(context, State.progressLmd, 5);

                    /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("自定义目标").setView(zidingyi3)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            if (zidingyi3.getText().toString().equals("") || Integer.parseInt(zidingyi3.getText().toString()) == 0) {
                                Toast.makeText(context, "请输入1-99的数字", Toast.LENGTH_SHORT).show();
                                wrongLmd.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 30) + "秒(自定义)");
                                return;
                            }
                            wrongLmd.setText(zidingyi3.getText().toString() + "秒(自定义)");
                            SPPrivateUtils.put(context, State.wrongSetting, Integer.parseInt(zidingyi3.getText().toString()));
                        }
                    });
                    builder.show();*/
                    final AlertDialog alert = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.util_edit_dialog, null);
                    final EditText editText = (EditText) view.findViewById(R.id.edit_text);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    editText.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 45) + "");
                    LinearLayout confirm = (LinearLayout) view.findViewById(R.id.confirm);
                    LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().equals("")
                                    || Integer.parseInt(editText.getText().toString()) == 0
                                    || Integer.parseInt(editText.getText().toString()) > 99) {
                                Toast.makeText(context, "请输入1-99的数字", Toast.LENGTH_SHORT).show();
                                wrongLmd.setText(SPPrivateUtils.getInt(context, State.wrongSetting, 30) + "秒(自定义)");
                            } else {
                                wrongLmd.setText(editText.getText().toString() + "秒(自定义)");
                                SPPrivateUtils.put(context, State.wrongSetting, Integer.parseInt(zidingyi3.getText().toString()));
                                alert.dismiss();
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    alert.setView(view);
                    alert.show();
                }

            }
        });
    }

    private void clearJdlParams() {
        jdl1.setLayoutParams(params2);
        jdl2.setLayoutParams(params2);
        jdl3.setLayoutParams(params2);
        jdl4.setLayoutParams(params2);
        jdl5.setLayoutParams(params3);
    }

    private void clearJzParams() {
        jz1.setLayoutParams(params2);
        jz2.setLayoutParams(params2);
        jz3.setLayoutParams(params2);
        jz4.setLayoutParams(params2);
        jz5.setLayoutParams(params3);
    }

    private void clearLmdParams() {
        lmd1.setLayoutParams(params2);
        lmd2.setLayoutParams(params2);
        lmd3.setLayoutParams(params2);
        lmd4.setLayoutParams(params2);
        lmd5.setLayoutParams(params3);
    }


    private void init() {
        titleText.setText("健康管理");
        jdlSet();
        jzSet();
        lmdSet();
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
