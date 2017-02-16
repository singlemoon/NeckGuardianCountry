package com.neckguardian.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.User;
import com.neckguardian.service.Utils.Global;
import com.neckguardian.service.Utils.HttpConnUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行榜的逻辑类
 * Created by 孤月悬空 on 2015/12/30.
 */
public class RankUserService extends BaseServiceImpl{

    private String TAG = "RankUserService";
    private List<User> users = null;
    /**
     * 从服务器端获取排名信息
     * @return              返回的是所有的排行信息
     */
    public List<User> getUsers() {
        url = Global.DOMAIN + Global.ENTRANCE_3;        //链接

        String usersParams = HttpConnUtil.doGet(HttpConnUtil.initHttp(url));

        if (usersParams == null || usersParams.trim().equals("")) {
            Log.i(TAG, "服务器返回信息为空，可能是服务器错误！");
        }  else {
            Log.i(TAG, "成功获得了用户信息");
            users = parseInList(usersParams);

            ranking();

            return users;
        }
        return null;
    }

    /**
     * 排序
     */
    private void ranking() {
        Log.i(TAG + "RankNumber", users.size()+"");
        for (int i = 0; i < users.size(); i++) {
            int count = 0;
            for (int j = 0; j < users.size(); j++) {
                if (Double.parseDouble(users.get(i).getNeckMax()) < Double.parseDouble(users.get(j).getNeckMax())) {
                    count++;
                }
            }
            users.get(i).setRankNumber(count + 1);
        }

        User user;
        for (int i = users.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                if (users.get(j + 1).getRankNumber() < users.get(j).getRankNumber()) {
                    user = users.get(j);
                    users.set(j, users.get(j + 1));
                    users.set(j + 1, user);
                }
            }
        }
    }

    /**
     * 将json转换为list
     * @param usersParams   json字符串
     * @return  用户列表
     */
    private List<User> parseInList(String usersParams) {
        gson = new Gson();
        List<Map<String, String>> list = gson.fromJson(usersParams,
                new TypeToken<List<Map<String, Object>>>(){}.getType());
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, String> userMap = list.get(i);
            User user = null;
            for (int j = 0; j < userMap.size(); j++) {
                user = new User();
                user.setUserId(userMap.get("userId"));
                user.setPic(Global.QINIU_IMG_PREFIX + userMap.get("pic"));
                user.setNickName(userMap.get("nickName"));
                user.setCarMax(userMap.get("carMax"));
                user.setNeckMax(userMap.get("neckMax"));
                user.setAnswerMax(userMap.get("answerMax"));
                user.setCounts(userMap.get("counts"));
//                Log.i(TAG, Global.QINIU_IMG_PREFIX + userMap.get("pic"));
            }
            userList.add(user);
        }
        return userList;
    }

    /**
     * 将信息变为Json格式
     * @param userId    需要转换的信息
     * @return          已经转换好的信息
     */
    private String parseInGson(String userId) {
        gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);

        return gson.toJson(map);
    }

    public String isChanged(String userId) {
        url = Global.DOMAIN + Global.ENTRANCE_4;
        return HttpConnUtil.doPost(HttpConnUtil.initHttp(url), parseInGson(userId));
    }
}
