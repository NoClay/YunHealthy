package wang.fly.com.yunhealth.util;

import java.util.Date;

import wang.fly.com.yunhealth.MyViewPackage.CircleShowData;

/**
 * Created by 82661 on 2016/11/10.
 */

public class DynamicData {
    private Date date;
    private float weight;
    private float quality;
    private float height;
    private int state;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight, float height) {
        this.weight = weight;
        this.height = height;
        if(height > 0){
            this.quality = weight / height / height;
            if(quality < 18.5){
                state = CircleShowData.THINER;
            }else if (quality < 25){
                state = CircleShowData.NORMAL;
            }else if (quality < 28){
                state = CircleShowData.FAT;
            }else if (quality < 32){
                state = CircleShowData.FATER;
            }else{
                state = CircleShowData.FATEST;
            }
        }
    }

    public float getQuality() {
        return quality;
    }

    public float getHeight() {
        return height;
    }

    public int getState() {
        return state;
    }
}
