package indi.noclay.cloudhealth.database.measuredata;


import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_FENCHEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_MAIBO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_TIWEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XINDIAN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUETANG;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUEYANG;

/**
 * Created by NoClay on 2018/5/6.
 * 用于检测实时检测数据是否合法，有效
 */

public abstract class MeasureDataHelper {
    public static final float ERROR_RETURN_VALUE = -1024.0f;

    /**
     * 有效则返回具体数值，否则返回-1024
     *
     * @param data
     * @param type
     * @return
     */
    public static float isValidData(String data, int type) {
        float result = 0;
        switch (type) {
            //每次都要求获取数据的字符串
            case MEASURE_TYPE_XUEYANG: {
                //血氧
                if (data.length() == 4) {
                    result = UtilClass.valueOfHexString(data);
                    if (data.length() == 8) {
                        //进行血氧的结果解析
                        if (result > 0 && result < 100) {
                            return result;
                        }
                    }
                }
                break;
            }
            case MEASURE_TYPE_MAIBO: {
                //进行脉搏的结果解析
                if (data.length() == 4) {
                    result = UtilClass.valueOfHexString(data);
                    if (result > 0 && result < 255) {
                        return result;
                    }
                }
                break;
            }
            case MEASURE_TYPE_XINDIAN: {
                //心电
                if (data.length() == 4) {
                    result = UtilClass.valueOfHexString(data);
                    if (result > 0 && result < 4096) {
                        return result;
                    }
                }
                break;
            }
            case MEASURE_TYPE_XUETANG: {
                //血糖
                if (data.length() == 12) {
                    result = UtilClass.valueOfHexString(data.substring(10, 12)) / 10.0f;
                    if (result > 0 && result < 300) {
                        return result;
                    }
                }
                break;
            }
            case MEASURE_TYPE_TIWEN: {
                //体温
                if (data.length() == 4) {
                    result = UtilClass.valueOfHexString(data) / 100.0f;
                    if (result > -40 && result < 125) {
                        return result;
                    }
                }
                break;
            }
            case MEASURE_TYPE_FENCHEN: {
                //粉尘
                if (data.length() == 8) {
                    result = UtilClass.valueOfHexString(data) / 100.0f;
                    if (result > 0 && result < 800) {
                        return result;
                    }
                }
                break;
            }
            default:
                break;
        }
        return ERROR_RETURN_VALUE;
    }
}
