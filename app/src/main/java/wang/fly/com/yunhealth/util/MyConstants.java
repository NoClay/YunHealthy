package wang.fly.com.yunhealth.util;

import android.os.Environment;

/**
 * Created by noclay on 2017/4/10.
 */

public class MyConstants {
    public static final String[] TAB_MENU = {
            "测量",
            "数据",
            "主页",
            "医生",
            "我的"
    };

    //缓存头像使用的目录
    public static final String PATH_ADD = Environment.getExternalStorageDirectory() +
            "/CloudHealthy/userImage/";
    //目前已经可以使用的功能
    public static final String []LABEL_STRING = {
            "血氧",
            "脉搏",
            "心电",
            "体温",
            "粉尘浓度",
            "血糖（待定）",
            "脑电（待定）",
            "血压（待定）"
    };

    //功能对应的标志位
    public static final int MEASURE_TYPE_XUEYANG = 0;
    public static final int MEASURE_TYPE_MAIBO = 1;
    public static final int MEASURE_TYPE_XINDIAN = 2;
    public static final int MEASURE_TYPE_TIWEN = 3;
    public static final int MEASURE_TYPE_FENCHEN = 4;
    public static final int MEASURE_TYPE_XUETANG = 5;
    public static final int MEASURE_TYPE_NAODIAN = 6;
    public static final int MEASURE_TYPE_XUEYA = 7;

    //数据库版本
    public static final int DATABASE_VERSION = 1;
    //进行本地数据库缓存的间隔, 每小时的0, 15, 30 , 45
    public static final int CACHE_TIME_LENGTH = 15;
    public static final int LOAD_CACHE_MINUTE = 5;

    //上传的Receiver
    public static final int RECEIVER_TYPE_UPLOAD = 0;
    public static String userId = null;
}
