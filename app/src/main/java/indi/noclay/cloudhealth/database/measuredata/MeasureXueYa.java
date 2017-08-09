package indi.noclay.cloudhealth.database.measuredata;


import indi.noclay.cloudhealth.util.MyConstants;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureXueYa extends MeasureData {
    private Integer type = MyConstants.MEASURE_TYPE_XUEYA;


    public MeasureXueYa() {
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
