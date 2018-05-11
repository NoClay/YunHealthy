package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.interfaces.CollectionFormat;

/**
 * Created by clay on 2018/4/19.
 */

public class FoodShowItem extends BmobObject implements Serializable, CollectionFormat{
    private String foodImageUrl;
    private String foodName;
    private String foodTag;
    private String foodCategory;
    private String foodDetailUrl;
    private SignUserData owner;

    public String getName(){
        return foodName;
    }

    public String getFoodDetailUrl() {
        return foodDetailUrl;
    }

    public void setFoodDetailUrl(String foodDetailUrl) {
        this.foodDetailUrl = foodDetailUrl;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodTag() {
        return foodTag;
    }

    public void setFoodTag(String foodTag) {
        this.foodTag = foodTag;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }
}
