package wang.fly.com.yunhealth.DataBasePackage.MeasureData;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;

/**
 * Created by 82661 on 2016/11/23.
 */

public class MeasureData extends BmobObject{
    private String name;
    private Float averageData;
    private Float maxData;
    private Float minData;
    private Integer count;
    private Boolean isAverageDanger;
    private Boolean isMaxDanger;
    private Boolean isMinDanger;
    public SignUserData owner;
    private BmobDate measureTime;

    public BmobDate getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(BmobDate measureTime) {
        this.measureTime = measureTime;
    }

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }

    public MeasureData() {
        name = "";
        reset();
    }

    /**
     * 重置
     */
    public void reset(){
        averageData = 0.0f;
        maxData = Float.MIN_VALUE;
        minData = Float.MAX_VALUE;
        count = 0;
        isAverageDanger = false;
        isMaxDanger = false;
        isMinDanger = false;
    }

    public MeasureData copyTo(MeasureData measureData){
        measureData.setAverageData(this.getAverageData());
        measureData.setMaxData(this.getMaxData());
        measureData.setMinData(this.getMinData());
        measureData.setCount(this.getCount());
        measureData.setAverageDanger(this.getAverageDanger());
        measureData.setMaxDanger(this.getMaxDanger());
        measureData.setMinDanger(this.getMinDanger());
        measureData.setMeasureTime(this.getMeasureTime());
        return measureData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAverageData() {
        return averageData;
    }

    public boolean setAverageData(float averageData) {
        float last = this.averageData;
        float sum = getSum();
        this.count ++;
        this.averageData = (sum + averageData) / this.count;
        if (this.averageData != last){
            return true;
        }
        return false;
    }

    public float getMaxData() {
        return maxData;
    }

    public boolean setMaxData(float maxData) {
        if (this.maxData < maxData){
            this.maxData = maxData;
            return true;
        }
        return false;
    }

    public float getMinData() {
        return minData;
    }

    public boolean setMinData(float minData) {
        if (this.minData > minData){
            this.minData = minData;
            return true;
        }
        return false;
    }

    public Boolean getAverageDanger() {
        return isAverageDanger;
    }

    public Boolean getMaxDanger() {
        return isMaxDanger;
    }

    public Boolean getMinDanger() {
        return isMinDanger;
    }

    public void setMinDanger(boolean minDanger) {
        isMinDanger = minDanger;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public void setAverageDanger(boolean averageDanger) {
        isAverageDanger = averageDanger;
    }

    public void setMaxDanger(boolean maxDanger) {
        isMaxDanger = maxDanger;
    }

    public float getSum(){
        return count * averageData;
    }

    public String getDate(){
        return measureTime.getDate().substring(10, 16);
    }
}
