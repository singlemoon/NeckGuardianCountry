package com.neckguardian.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.service.Utils.HttpConnUtil;

import java.util.Map;

/**
 * 意见反馈的逻辑类
 * Created by 孤月悬空 on 2016/1/21.
 */
public class AdviceService extends BaseServiceImpl {

    public String sendFeedback(Map<String, String> map) {
        url = Global.DOMAIN + Global.ENTRANCE_6;
        String json = parseInJson(map);
        String result = HttpConnUtil.doPost(HttpConnUtil.initHttp(url), json);

//        return parseInMap(result);
        return "success";
    }

    private String parseInMap(String result) {
        gson = new Gson();
        Map<String, String> map = gson.fromJson(result, new TypeToken<Map<String, String>>() {
        }.getType());
        if (map != null) {
            return map.get("msg");
        }
        return null;
    }

    private String parseInJson(Map<String, String> map) {
        gson = new Gson();

        return gson.toJson(map);
    }
}
