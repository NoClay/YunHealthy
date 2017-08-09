package indi.noclay.cloudhealth.util;

/**
 * function:用于描述折现统计图中固定的点
 * Created by 82661 on 2016/11/14.
 */

public class DataInfo {
    private float data;
    private String date;

    public DataInfo(float data, String date) {
        this.data = data;
        this.date = date;
    }

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
