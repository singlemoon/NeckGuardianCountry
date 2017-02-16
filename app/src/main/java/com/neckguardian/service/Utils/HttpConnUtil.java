package com.neckguardian.service.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * HttpConnUtil
 * Created by 孤月悬空 on 2015/12/30.
 */
public class HttpConnUtil {
    private static final String TAG = "HttpConnUtil";
    private static final int TIMEOUT_IN_MILLIONS = 5000;

    /**
     * Get请求，获得返回数据
     *
     * @return 返回信息
     */
    public static String doGet(HttpURLConnection conn) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try{
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e) {
            Log.i(TAG, "服务器连接失败");
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
            }
            conn.disconnect();
        }

        return null;

    }

    public static String doPost(HttpURLConnection conn, String data) {

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
//            通用的请求属性
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "*/*");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);

            //如果信息内容部位空，发送信息
            if (data != null && !data.trim().equals("")) {
                OutputStream os = conn.getOutputStream();
                out = new PrintWriter(os);
                out.write(data);
                out.flush();
            }

            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static HttpURLConnection initHttp(String url) {

        Log.i(TAG, url);
        HttpURLConnection httpURLConnection = null;
        try {
            //打开与服务器的链接
            URL localURL = new URL(url);
            URLConnection connection = localURL.openConnection();
            httpURLConnection = (HttpURLConnection) connection;
//            发送请求必须设置输入输出流可用
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
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