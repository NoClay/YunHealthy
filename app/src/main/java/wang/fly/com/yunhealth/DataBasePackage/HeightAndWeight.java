package wang.fly.com.yunhealth.DataBasePackage;

import java.util.Date;

/**
 * Created by no_clay on 2017/1/26.
 */

public class HeightAndWeight {
    private float height;
    private float weight;
    private SignUserData owner;
    private Date mDate;

    public HeightAndWeight(float height, float weight, SignUserData owner) {
        this.height = height;
        this.weight = weight;
        this.owner = owner;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }
}
