package indi.noclay.cloudhealth.util;

import android.os.Environment;

/**
 * Created by noclay on 2017/4/10.
 */

public class MyConstants {
    //缓存头像使用的目录
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() +
            "/CloudHealthy/userImage/";
    public static final String SRC_PATH_USER_IMAGE = ROOT_PATH + "src_user.jpg";
    public static final String SRC_PATH_MEDICINE = ROOT_PATH + "src_medicine.jpg";
    public static final String CROP_PATH_USER_IMAGE = ROOT_PATH + "crop_user.jpg";
    public static final String CROP_PATH_MEDICINE = ROOT_PATH + "crop_medicine.jpg";
    public static final String TEMP_USER_IMAGE = ROOT_PATH + "temp_user.jpg";
    public static final String TEMP_MEDICINE = ROOT_PATH + "temp_medicine.jpg";

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
    public static final String []TIMES = {
            "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30",
            "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30",
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30",
            "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"
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
    public static final float ADD_LENGTH = 0.25f;

    public static String userId = null;
}
