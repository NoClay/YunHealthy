package wang.fly.com.yunhealth.DataBasePackage.MeasureData;


import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MainActivity;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureTiWen extends MeasureData {
    private Integer type = MainActivity.MEASURE_TYPE_TIWEN;
    private SignUserData owner;

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }
    public MeasureTiWen() {
        super();
        setName(MainActivity.LABEL_STRING[type]);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
