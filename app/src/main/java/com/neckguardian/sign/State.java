package com.neckguardian.sign;

/**
 * 一些通用的标记放在这里
 * Created by 孤月悬空 on 2015/12/27.
 */
public class State {
    //用户配置
    public static final String isFirstTime = "isFirstTime";         //是否为第一次打开本App
    public static final String isPairing = "isParing";              //是否配对
    public static final String isLogin = "isLogin";                 //是否登陆
    public static final String userId = "userId";                   //用户ID
    public static final String nickName = "nickName";               //用户昵称
    public static final String headImg = "headImg";                 //用户头像
    public static final String isHeartChecked = "isHeartChecked";   //是否已经点赞
    public static final String isQQBinding = "isQQBinding";         //QQ是否已经绑定
    public static final String isSinaWeiBoBinding = "isSinaWeiBoBinding";       //新浪微博是否已经绑定
    public static final String isMobileBinding = "isMobileBinding";             //手机号是否已经绑定
    public static final String isHavingData = "isHavingDate";           //首页获取数据时判断本地是否已经存在数据
    public static final String useShock = "useShock";               //通知是否使用震动
    public static final String useBell = "useBell";                 //通知是否使用响铃
    public static final String useVoice = "useVoice";               //通知是否使用语音
    public static final String infoTint = "infoTint";               //通知设置栏的提示文字
    public static final String bellName = "bellName";               //铃声的名字
    public static final String isToCloud = "isToCloud";             //是否上传到云端
    public static final String isToAnother = "isToAnother";         //他人是否可见我的信息
    public static final String isAnonData = "isAnonData";           //是否匿名发送反馈消息
    public static final String useWifi = "useWifi";                 //网络选项使用wifi
    public static final String useAllNet = "useAllNet";             //使用所有网络
    public static final String netTint = "netTint";                 //网络设置栏的提示文字
    public static final String isNetworkAvailable = "isNetworkAvailable";   //网络是否可用
    public static final String cacheHint = "cacheHint";             //缓存清理栏的显示
    public static final String dayOfYear = "dayOfYear";

    public static final String remind_time = "remind_time";     //多少时间提醒一次
    public static final String bellId = "bellId";           //铃声的Id

    //    短信验证码的key
    public static final String SMSSDK_APPKEY = "e57cc95bd5c0";
    public static final java.lang.String SMSSDK_APPSECRET = "ed6fce381332e39d6dbdf1f5a63a4d04";


    public static int LOGIN = 200;         //登陆
    public static int LOGIN_OK = 300;      //登陆成功

    // 保存数据
    public static final String currentEnergy = "currentEnergy";
    public static final String targetEnergy = "targetEnergy";        //目标的颈动量
    public static final String longTimeSit = "longTimeSit";         //僵坐时间
    public static final String wrongSetting = "wrongSetting";       //异常姿态
    public static final String startUseTime = "startUseTime";       //使用从绑定开始使用的时间
    public static final String ELECTRIC_QUANTITY = "ELECTRIC_QUANTITY";     //电量
    public static final String progressValue = "progressValue";
    public static final String progressJz = "progressJz";
    public static final String progressLmd = "progressLmd";
    public static final String leftTime = "leftTime";   //在蓝牙连接时的左倾时间
    public static final String rightTiem = "rightTime"; //              右倾时间
    public static final String lowerTime = "lowerTime"; //              低头时间


    public static final int BTCONNECT = 9737;      //

    // 蓝牙设备
    public static final String BLEDEVICE = "bleDevice";

    //SQLite字段
    public static final String DATABASE_NAME = "neck_guardian.db";          //数据库名称
    public static final String TABLE_NAME = "home_data";                    //主页表名称
    public static final String CURRENT_ENERGY = "CURRENT_ENERGY";           //当前颈动量
    public static final String TARGET_ENERGY = "TARGET_ENERGY";             //目标颈动量
    public static final String DATE_NUM = "DATE_NUM";                       //日期
    public static final String EXERCISE_ANGLE = "EXERCISE_ANGLE";           //颈动角度
    public static final String EXERCISE_AMOUNT = "EXERCISE_AMOUNT";         //运动量
    public static final String ENERGY_CONSUMPTION = "ENERGY_CONSUMPTION";   //消耗鞥能量
    public static final String LOWER_TIME = "LOWER_TIME";                   //低头时间
    public static final String LEFT_TIME = "LEFT_TIME";                     //左倾时间
    public static final String RIGHT_TIME = "RIGHT_TIME";                   //右倾时间

    public static final String TABLE_GAME = "game_history";                 //游戏历史
    public static final String SHOW_TIME = "SHOW_TIME";                     //游戏日期
    public static final String GAME_TIME = "GAME_TIME";                     //游戏时间
    public static final String GAME_NAME = "GAME_NAME";                     //游戏名称
    public static final String GAME_USE_TIME = "GAME_USE_TIME";             //游戏运行时间
    public static final String COMPLETENESS = "COMPLETENESS";               //完成度
}
