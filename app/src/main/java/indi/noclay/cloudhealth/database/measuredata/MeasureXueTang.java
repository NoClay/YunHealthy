package indi.noclay.cloudhealth.database.measuredata;


import indi.noclay.cloudhealth.util.ConstantsConfig;

/**
 * Created by 82661 on 2016/12/2.
 */

public class MeasureXueTang extends MeasureData {
    private Integer type = ConstantsConfig.MEASURE_TYPE_XUETANG;

    public MeasureXueTang() {
        super();
        setName(ConstantsConfig.LABEL_STRING[type]);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
