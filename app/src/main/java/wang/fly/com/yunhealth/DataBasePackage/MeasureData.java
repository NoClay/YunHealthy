package wang.fly.com.yunhealth.DataBasePackage;

/**
 * Created by 82661 on 2016/11/23.
 */

public class MeasureData {
    private String name;
    private float averageData;
    private float maxData;
    private float minData;
    private int count;
    private boolean isAgerageDanger;
    private boolean isMaxDanger;
    private boolean isMinDanger;

    public MeasureData() {
        name = "";
        averageData = 0;
        maxData = Integer.MIN_VALUE;
        minData = Integer.MAX_VALUE;
        count = 0;
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

    public boolean isMinDanger() {
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

    public boolean isAgerageDanger() {
        return isAgerageDanger;
    }

    public void setAgerageDanger(boolean agerageDanger) {
        isAgerageDanger = agerageDanger;
    }

    public boolean isMaxDanger() {
        return isMaxDanger;
    }

    public void setMaxDanger(boolean maxDanger) {
        isMaxDanger = maxDanger;
    }

    public float getSum(){
        return count * averageData;
    }
}
