package indi.noclay.cloudhealth.database;

/**
 * Created by clay on 2018/4/18.
 */

public class FoodKind {
    String foodKindName;
    boolean isShow;
    String userId;

    public FoodKind() {
    }

    public FoodKind(String foodKindName, boolean isShow, String userId) {
        this.foodKindName = foodKindName;
        this.isShow = isShow;
        this.userId = userId;
    }

    public String getFoodKindName() {
        return foodKindName;
    }

    public void setFoodKindName(String foodKindName) {
        this.foodKindName = foodKindName;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
