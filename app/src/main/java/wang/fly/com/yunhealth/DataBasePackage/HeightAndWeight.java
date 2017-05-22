package wang.fly.com.yunhealth.DataBasePackage;

import java.util.Date;

import wang.fly.com.yunhealth.MyViewPackage.CircleShowData;

/**
 * Created by no_clay on 2017/1/26.
 */

public class HeightAndWeight {
    private float height;
    private float weight;
    private SignUserData owner;
    private Date mDate;
    private float quality;
    private int state;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public void setState(int state) {
        this.state = state;
    }

    public HeightAndWeight(float height, float weight, SignUserData owner) {
        this.height = height;
        this.weight = weight;
        this.owner = owner;
        setWeight(weight, height);
    }

    public HeightAndWeight() {
    }

    public HeightAndWeight(float height, float weight) {
        this.height = height;
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight, float height) {
        this.weight = weight;
        this.height = height;
        if(height > 0){
            this.quality = weight / height / height * 10000;
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

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }
}
