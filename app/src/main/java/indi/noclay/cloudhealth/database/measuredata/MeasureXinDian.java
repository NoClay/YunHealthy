package indi.noclay.cloudhealth.database.measuredata;


import indi.noclay.cloudhealth.util.MyConstants;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureXinDian extends MeasureData {
    private Integer type = MyConstants.MEASURE_TYPE_XINDIAN;

    public MeasureXinDian() {
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
