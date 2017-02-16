package com.neckguardian.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.service.Utils.HttpConnUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录的逻辑类
 * Created by 孤月悬空 on 2016/1/10.
 */
public class LoginService extends BaseServiceImpl {
    private final static String TAG = "LoginService";

    public Map<String, String> login(Map<String, String> username) {

        if (TextUtils.isEmpty(username.get("tele"))) {      //判断是否为第三方登陆
            url = Global.DOMAIN + Global.ENTRANCE_1;
        } else {
            url = Global.DOMAIN + Global.ENTRANCE_2;
        }

        String data = parseInJson(username);
        String jsonResult = HttpConnUtil.doPost(HttpConnUtil.initHttp(url), data);
//        Map<String , String> map = parseInMap(jsonResult);
        Map<String, String> map = new HashMap<>();
        map.put("msg", "success");

        return map;
    }

    private Map<String, String> parseInMap(String jsonString) {
        gson = new Gson();

        return gson.fromJson(jsonString,
                new TypeToken<Map<String, String>>(){}.getType());
    }

    private String parseInJson(Map<String, String> username) {
        gson = new Gson();

        return gson.toJson(username);
    }
}
