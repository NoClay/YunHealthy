package wang.fly.com.yunhealth.DataBasePackage;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by noclay on 2017/4/23.
 */

public class MedicineDetial extends BmobObject{
    private SignUserData owner;
    //服药原因
    private String tag;
    private String medicineName;
    private String medicinePicture;
    private String useType;
    //医嘱
    private String doctor;
    //每隔几天吃药
    private Integer day;
    //吃多少天
    private Integer dayLength;
    //已经吃了多少天
    private Integer dayCount;

    //时间点
    private List<String> times;
    //剂量
    private List<String> doses;

    private BmobDate startTime;

    private String useSuggestion;

}
