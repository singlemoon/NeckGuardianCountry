package com.neckguardian.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.service.Utils.HttpConnUtil;

import java.util.Map;

/**
 * Created by Jiar on 2016/1/25.
 */
public class HeadImgService extends BaseServiceImpl {
    public String uploadImg(Map<String, String> map) {
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
