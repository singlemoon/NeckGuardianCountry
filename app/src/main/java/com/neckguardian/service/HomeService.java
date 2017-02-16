package com.neckguardian.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.DataOfDay;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.service.Utils.HttpConnUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页的逻辑类
 * Created by 孤月悬空 on 2016/1/16.
 */
public class HomeService extends BaseServiceImpl {

    /*
    上传的方法
     */
    public String uploadDate(Map<String, String> dataMap) {
        url = Global.DOMAIN + Global.ENTRANCE_8;
        String mapJson = parseInJson_2(dataMap);
        String msgJson = HttpConnUtil.doPost(HttpConnUtil.initHttp(url), mapJson);

        return parseInString(msgJson);
    }

    private String parseInString(String msgJson) {
        gson = new Gson();
        Map<String, String> map = gson.fromJson(msgJson, new TypeToken<Map<String, String>>() {
        }.getType());

        if (map != null) {
            return map.get("msg");
        } else {
            return null;
        }
//        return "success";
    }

    private String parseInJson_2(Map<String, String> data) {
        gson = new Gson();

        return gson.toJson(data);
    }

    /*
    获得数据的方法
     */
    public List<DataOfDay> getUserDate(String userId) {
        url = Global.DOMAIN + Global.ENTRANCE_5;
        userId = "1";
        Map<String, String> map = new HashMap<>();
        map.put("usetId", userId);
        String jsonUserId = parseInJson(userId);
        String result = HttpConnUtil.doPost(HttpConnUtil.initHttp(url), jsonUserId);
//        String result = "iii";
        return parseInMap(result);
    }

    private List<DataOfDay> parseInMap(String result) {
        gson = new Gson();
        List<Map<String, String>> listDate = gson.fromJson(result, new TypeToken<List<Map<String, String>>>() {
        }.getType());
        List<DataOfDay> dataList = new ArrayList<>();
        /*List<Map<String, String>> listDate = new ArrayList<>();
        for (int i = 1; i < 14; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("currentEnergy", i * 500 + "");
            map.put("targetEnergy", 30000 + "");
            map.put("dateTime", 3 + "-" + i);
            map.put("exerciseAngle", i * 3 + "");
            map.put("exerciseAmount", i * 123 + "");
            map.put("energyConsumption", i * 4563 + "");
            map.put("lowerTime", i * 3 + "");
            map.put("leftTime", i * 3 + "");
            map.put("rightTime", i * 3 + "");
            listDate.add(map);
        }*/
        if (listDate == null) {
            Log.i("服务器连接", "失败");
            return null;
        } else {
            for (int i = 0; i < listDate.size(); i++) {
                DataOfDay dataOfDay = new DataOfDay();
                dataOfDay.setCurrentEnergy(Integer.parseInt(listDate.get(i).get("currentEnergy")));
                dataOfDay.setTargetEnergy(Integer.parseInt(listDate.get(i).get("targetEnergy")));
                String[] date = listDate.get(i).get("dateTime").split("-");
                dataOfDay.setDateNum(date[1] + "-" + date[2]);
                dataOfDay.setExerciseAngle(Integer.parseInt(listDate.get(i).get("exerciseAngle")));
                dataOfDay.setExerciseAmount(Integer.parseInt(listDate.get(i).get("exerciseAmount")));
                dataOfDay.setEnergyConsumption(Integer.parseInt(listDate.get(i).get("energyConsumption")));
                dataOfDay.setLowerTime(Integer.parseInt(listDate.get(i).get("lowerTime")));
                dataOfDay.setLeftTime(Integer.parseInt(listDate.get(i).get("leftTime")));
                dataOfDay.setRightTime(Integer.parseInt(listDate.get(i).get("rightTime")));
                dataList.add(dataOfDay);
            }
            return dataList;
        }
    }

    private String parseInJson(String userId) {
        gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        return gson.toJson(map);
    }
}
