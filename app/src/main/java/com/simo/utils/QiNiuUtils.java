package com.simo.utils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

/**
 * 七牛 工具类
 * Created by Yancy on 2015/12/7.
 */
public class QiNiuUtils {

    private final static String TAG = "QiNiuUtils";
    private OnCallBack onCallBack;

    private UploadManager uploadManager = new UploadManager();

    private String token;
    private String fileName;
    private String filePath;

    private QiNiuUtils(String token, String fileName, String filePath, OnCallBack onCallBack) {
        this.token = token;
        this.fileName = fileName;
        this.filePath = filePath;
        this.onCallBack = onCallBack;
    }

    public static void imageUploading(String token, String fileName, String filePath, OnCallBack onCallBack) {
        new QiNiuUtils(token, fileName, filePath, onCallBack).upload();
    }

    /**
     * 上传图片
     */
    public void upload() {
        try {
            byte[] bytes = ImageUtils.getimage(filePath);
            uploadManager.put(bytes, fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    onCallBack.UpCompletionHandler(key, info, response);
                }
            }, new UploadOptions(null, null, false, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {
                    onCallBack.UploadOptions(key, percent);
                }
            }, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnCallBack {
        void UpCompletionHandler(String key, ResponseInfo info, JSONObject response);

        void UploadOptions(String key, double percent);
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