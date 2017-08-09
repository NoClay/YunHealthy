package indi.noclay.cloudhealth.database.measuredata;


import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.util.MyConstants;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureNaoDian extends MeasureData {
    private Integer type = MyConstants.MEASURE_TYPE_NAODIAN;

    public MeasureNaoDian() {
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
