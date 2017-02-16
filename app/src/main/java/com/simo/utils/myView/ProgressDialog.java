package com.simo.utils.myView;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.neckguardian.R;


/**
 * 加载弹出框
 * Created by Yancy on 2015/12/9.
 */
public class ProgressDialog {

    private final static String TAG = "ProgressDialog";

    private LayoutInflater layoutInflater;
    private AlertDialog dialog;

    private Context context;
    private String content;

    private TextView progressText;

    private ProgressDialog(Builder builder) {
        this.context = builder.context;
        this.content = builder.content;
        dialog = new AlertDialog.Builder(context, R.style.MyDialogStyle).create();
        layoutInflater = LayoutInflater.from(context);
    }

    public void show() {
        View view = layoutInflater.inflate(R.layout.util_progress, null);
        progressText = (TextView) view.findViewById(R.id.progress_text);
        if (!TextUtils.isEmpty(content)) {
            progressText.setText(content);
        }
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


    public static class Builder {
        private Context context;
        private String content;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ProgressDialog show() {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            return progressDialog;
        }

        public ProgressDialog build() {
            return new ProgressDialog(this);
        }
    }

}
/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */