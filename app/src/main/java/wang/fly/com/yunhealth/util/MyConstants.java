package wang.fly.com.yunhealth.util;

import android.os.Environment;

/**
 * Created by noclay on 2017/4/10.
 */

public class MyConstants {
    //缓存头像使用的目录
    public static final String PATH_ADD = Environment.getExternalStorageDirectory() +
            "/CloudHealthy/userImage/";
    public static final String CROP_PATH_USER_IMAGE = PATH_ADD + "crop.jpg";
    public static final String CROP_PATH_MEDICINE = PATH_ADD + "crop_medicine.jpg";

    public static final String[] TIME_ITEM = {
            "1天", "2天", "3天", "4天", "5天", "6天", "1周",
            "2周", "3周", "1月", "2月", "3月", "6月", "1年",
            "2年", "3年", "长期"
    };
    public static final int[] TIME_VALUE  = new int[]{
            1, 2, 3, 4, 5, 6, 7,
            14, 21, 30, 60, 90, 180, 365,
            730, 1095, ((int) Integer.MAX_VALUE)
    };
    public static final String[] TAB_MENU = {
            "测量", "数据", "主页", "医生", "我的"
    };

    //目前已经可以使用的功能
    public static final String []LABEL_STRING = {
            "血氧", "脉搏", "心电", "体温", "粉尘浓度",
            "血糖（待定）", "脑电（待定）", "血压（待定）"
    };
    public static final String []SECTIONS = {
            "我的群组", "内科", "外科", "手外科", "妇产科", "五官科",
            "皮肤性病", "中西医结合", "肝病", "精神心理科", "儿科", "男科",
            "生殖健康", "肿瘤科", "传染科", "老年科", "体检保健科", "成瘾医学科",
            "核医学科", "急诊科", "营养科"
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
    public static final int ANIMATION_DURATION = 200;

    public static String userId = null;
}
