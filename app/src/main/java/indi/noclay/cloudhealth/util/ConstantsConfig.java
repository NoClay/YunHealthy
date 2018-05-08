package indi.noclay.cloudhealth.util;

import android.os.Environment;

import indi.noclay.cloudhealth.R;

/**
 * Created by noclay on 2017/4/10.
 */

public class ConstantsConfig {
    //缓存头像使用的目录
    public static final String APPLICATION_DIR = Environment.getExternalStorageDirectory() + "/CloudHealthy/";
    public static final String ROOT_PATH_IMAGE_DIR = APPLICATION_DIR + "userImage/";
    public static final String CACHE_DATA_DIR = APPLICATION_DIR + "userCache/";
    /**
     * Handler中的msg标识
     */
    public static final int MSG_LOAD_FAILED = -1;
    public static final int MSG_LOAD_EMPTY = 0;
    public static final int MSG_LOAD_SUCCESS = 1;
    public static final int MSG_LOAD_NO_MORE = 2;

    public static final String SRC_PATH_USER_IMAGE = ROOT_PATH_IMAGE_DIR + "src_user.jpg";
    public static final String SRC_PATH_MEDICINE = ROOT_PATH_IMAGE_DIR + "src_medicine.jpg";
    public static final String CROP_PATH_USER_IMAGE = ROOT_PATH_IMAGE_DIR + "crop_user.jpg";
    public static final String CROP_PATH_MEDICINE = ROOT_PATH_IMAGE_DIR + "crop_medicine.jpg";
    public static final String TEMP_USER_IMAGE = ROOT_PATH_IMAGE_DIR + "temp_user.jpg";
    public static final String TEMP_MEDICINE = ROOT_PATH_IMAGE_DIR + "temp_medicine.jpg";
    public static final String API_SHOW_APP_ID = "62682";
    public static final String API_SHOW_APP_SECRET = "a021a191dd1545c5a0276f470f57729c";
    public static final String API_BMOB_ID = "d2e2a48caabc1e5c399b20b2adea85eb";
    public static final String API_MOB_APP_KEY = "195be1e7755e2";
    public static final String API_MOD_APP_SECRET = "5bdd8a14d2e2f5734797443c982b0db4";
    public static final String API_MEDICINE_SEARCH = "http://route.showapi.com/93-97";
    public static final String API_MEDICINE_COMPANY_SEARCH = "http://route.showapi.com/93-95";
    public static final String API_NORMAL_ILLNESS_SEARCH = "http://route.showapi.com/546-2";
    public static final String API_MEDICINE_INFO = "http://route.showapi.com/93-33";
    public static final String API_ILLNESS_INFO = "http://route.showapi.com/546-3";

    public static final String PARAMS_KEY_WORD = "keyWord";
    public static final String PARAMS_SEARCH_TYPE = "searchType";
    public static final String PARAMS_BUNDLE = "bundle";
    public static final String PARAMS_MEDICINE_DATA = "medicineData";
    public static final String PARAMS_ID = "id";
    public static final String PARAMS_TITLE = "title";
    public static final String PARAMS_FRAGMENT_TYPE = "fragmentType";
    public static final String PARAMS_URL = "url";
    public static final String PARAMS_IS_TOP = "isTop";
    public static final String PARAMS_FILE_PATH = "filePath";
    public static final String PARAMS_FILE_NAME = "fileName";

    public static final int TYPE_MEDICINE_INFO = 0;
    public static final int TYPE_MEDICINE_COMPANY_INFO = 1;
    public static final int TYPE_NORMAL_ILLNESS = 2;
    public static final int TYPE_FRAGMENT_DYNAMIC = 3;
    public static final int TYPE_FRAGMENT_MEDICINE = 4;

    public static final String[] SEARCH_TYPE = {
            "药品信息", "药企信息", "常见疾病"
    };
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
            "测量", "数据", "主页", "百问", "我的"
    };

    public static final String[] TAB_DATA_MENU = {
            "动态", "数据", "用药", "报告单"
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
    public static final int CACHE_TIME_LENGTH = 2;

    public static final int LOAD_CACHE_MINUTE = 3;
    //上传的Receiver
    public static final int RECEIVER_TYPE_UPLOAD = 0;
    public static final int ANIMATION_DURATION = 200;
    public static final float ADD_LENGTH = 0.25f;

    public static final String DEFAULT_LOCAL_DATABASE = "LocalStore.db";

    public static String userId = null;


    public static final String[] MENU_CONTENTS = {
            "血常规",
            "血生化全套",
            "他克莫司浓度",
            "环孢素浓度",
            "雷帕霉素浓度",
            "尿常规",
            "群体反应抗体(PRA)",
            "超敏C反应蛋白",
            "凝血功能",
            "血气分析",
            "肿瘤标志物",
            "肝炎系列",
            "血型",
            "粪便常规",
            "甲状腺激素",
            "多瘤病毒",
            "乙肝病毒",
            "霉酚酸",
            "肾功能",
            "肝功能",
            "血脂",
            "糖化血红蛋白",
            "B2微球蛋白",
            "转铁蛋白+前白蛋白测定",
            "铁三项",
            "常规四项",
            "尿蛋白、尿素、肌酐测定",
            "腹水常规检查",
            "腹透液Cr、BuN、pro",
            "心电图",
            "心超",
            "颈部血管B超",
            "左上肢动脉超声",
            "左上肢静脉超声",
            "右上肢动脉超声",
            "右上肢静脉超声",
            "左下肢动脉超声",
            "左下肢静脉超声",
            "右下肢动脉超声",
            "右下肢静脉超声",
            "胸片",
            "肺部CT",
            "肝胆脾胰超声",
            "泌尿系B超",
            "移植肾超声",
            "胸水超声",
            "腹水超声",
            "头部CT",
            "头颅MRI",
            "颈部及腋下淋巴结超声",
            "腹股沟淋巴结超声",
            "肾动脉CT",
            "肾静脉CT",
            "磁共振泌尿系水成像检查",
            "CT尿路造影"
    };
    public static final int[] MENU_ICONS = {
            R.drawable.jcd_ic_rb,
            R.drawable.jcd_ic_bb,
            R.drawable.jcd_ic_fk,
            R.drawable.jcd_ic_csa,
            R.drawable.jcd_ic_rapa,
            R.drawable.jcd_ic_rt,
            R.drawable.jcd_ic_grad1,
            R.drawable.jcd_ic_acpd,
            R.drawable.jcd_ic_csa,
            R.drawable.jcd_ic_bga,
            R.drawable.jcd_ic_tm,
            R.drawable.jcd_ic_hbs,
            R.drawable.jcd_ic_bt,
            R.drawable.jcd_ic_fr,
            R.drawable.jcd_ic_pth,
            R.drawable.jcd_ic_bkv,
            R.drawable.jcd_ic_hbv_dna,
            R.drawable.jcd_ic_mpa,
            R.drawable.jcd_ic_shengongneng,
            R.drawable.jcd_ic_gangongneng,
            R.drawable.jcd_ic_xuezhi,
            R.drawable.ft_ic_tanghuaxuehong,
            R.drawable.ft_ic_weiqiudanbai,
            R.drawable.ft_ic_zhuantiedaibai,
            R.drawable.ft_ic_tiesanxiang,
            R.drawable.ft_ic_changgui,
            R.drawable.ft_ic_niaodanbai,
            R.drawable.ft_ic_fushui,
            R.drawable.ft_ic_futouye,
            R.drawable.jdc_icon_ecg,
            R.drawable.icon_xinchao,
            R.drawable.jcd_ic_jingbuxueguanbchao,
            R.drawable.icon_zuoshangzhidongmai,
            R.drawable.icon_zuoshangzhijingmai,
            R.drawable.icon_youshangzhidongmai,
            R.drawable.icon_youshangzhijingmai,
            R.drawable.icon_zuoxiazhidongmai,
            R.drawable.icon_zuoxiazhijingmai,
            R.drawable.icon_youxiazhidongmai,
            R.drawable.icon_youxiazhijingmai,
            R.drawable.icon_xiongpian,
            R.drawable.icon_feipian,
            R.drawable.icon_gandanpiyi,
            R.drawable.jcd_ic_miniaoxibchao,
            R.drawable.jcd_ic_ultrasound,
            R.drawable.icon_xiongshuichaosheng,
            R.drawable.icon_fubuchaosheng,
            R.drawable.icon_toulu,
            R.drawable.icon_toulumir,
            R.drawable.icon_jinbujiyexialinbajie,
            R.drawable.icon_fugugoulinbajie,
            R.drawable.icon_shendongmai,
            R.drawable.icon_shenjingmai,
            R.drawable.icon_cigongzhenminiao,
            R.drawable.icon_ctniaoluzaoying
    };
    public static final int[] MENU_TYPES = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2
    };
}
