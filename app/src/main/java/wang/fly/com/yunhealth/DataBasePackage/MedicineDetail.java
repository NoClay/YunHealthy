package wang.fly.com.yunhealth.DataBasePackage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by noclay on 2017/4/23.
 */

public class MedicineDetail extends BmobObject{
    private SignUserData owner;
    private String medicineName;
    private String medicinePicture;
    private String useType;
    //服药原因
    private String tag;
    //医嘱
    private String doctor;
    //吃多少天
    private Integer dayLength;
    //已经吃了多少天
    private Integer dayCount;
    //时间点
    private List<String> times;
    //剂量
    private List<Float> doses;

    private BmobDate startTime;
    //服药的单位
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicinePicture() {
        return medicinePicture;
    }

    public void setMedicinePicture(String medicinePicture) {
        this.medicinePicture = medicinePicture;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public Integer getDayLength() {
        return dayLength;
    }

    public void setDayLength(Integer dayLength) {
        this.dayLength = dayLength;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }

    public List<String> getTimes() {
        if (times == null){
            return new ArrayList<>();
        }
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<Float> getDoses() {
        if (doses == null){
            return new ArrayList<>();
        }
        return doses;
    }

    public void setDoses(List<Float> doses) {
        this.doses = doses;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public void setStartTime(BmobDate startTime) {
        this.startTime = startTime;
    }
}
