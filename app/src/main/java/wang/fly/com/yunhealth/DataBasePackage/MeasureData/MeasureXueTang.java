package wang.fly.com.yunhealth.DataBasePackage.MeasureData;

import wang.fly.com.yunhealth.util.MyConstants;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureXueTang extends MeasureData{
    private Integer type = MyConstants.MEASURE_TYPE_XUETANG;

    public MeasureXueTang() {
        super();
        setName(MyConstants.LABEL_STRING[type]);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
