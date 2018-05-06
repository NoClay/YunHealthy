package indi.noclay.cloudhealth.service;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.fragment.MeasureFragment;
import indi.noclay.cloudhealth.util.ABSMeasureDataResolver;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.CustomMeasureDataResolver;
import pers.noclay.bluetooth.BluetoothConnectionService;
import pers.noclay.bluetooth.BluetoothConstant;

import static indi.noclay.cloudhealth.database.MeasureTableHelper.addOneMeasureData;
import static indi.noclay.cloudhealth.database.MeasureTableHelper.checkOneMeasureDataCache;
import static indi.noclay.cloudhealth.database.measuredata.MeasureDataHelper.ERROR_RETURN_VALUE;
import static indi.noclay.cloudhealth.database.measuredata.MeasureDataHelper.isValidData;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_FENCHEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_MAIBO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_TIWEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XINDIAN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUETANG;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUEYANG;

/**
 * Created by NoClay on 2017/11/19.
 */

public class BluetoothConnectService extends BluetoothConnectionService implements
        ABSMeasureDataResolver.OnResolveListener {

    private static CustomMeasureDataResolver sDataResolver;
    private static Calendar sCalendar;
    private static List<MeasureData> sMeasureDataList;

    @Override
    public void onCreate() {
        super.onCreate();
        sDataResolver = new CustomMeasureDataResolver();
        sDataResolver.setOnResolveListener(this);
        sCalendar = Calendar.getInstance();
        sCalendar.setTimeInMillis(System.currentTimeMillis());
        initCacheList();
    }

    private void initCacheList() {
        sMeasureDataList = new ArrayList<>();
        for (int i = 0; i < ConstantsConfig.LABEL_STRING.length; i++) {
            MeasureData measure = new MeasureData();
            measure.setName(ConstantsConfig.LABEL_STRING[i]);
            sMeasureDataList.add(measure);
        }
    }


    /**
     * 检查本时段缓存处理
     *
     * @param type
     */
    public void checkMinuteAndCache(int type) {
        int minute = sCalendar.get(Calendar.MINUTE);
        if (minute % ConstantsConfig.CACHE_TIME_LENGTH == 0
                && !checkOneMeasureDataCache(type, sCalendar.getTime())) {
            Log.d("Cache", "checkMinuteAndCache: cache + " +
                    ConstantsConfig.LABEL_STRING[type] + "\tminute" + minute);
            addOneMeasureData(sMeasureDataList.get(type), type, sCalendar.getTime());
            sMeasureDataList.get(type).reset();
        }
    }

    /**
     * 比较一些数据，相同返回
     *
     * @param temp
     * @param result
     */
    private boolean compareData(MeasureData temp, float result) {
        boolean flag1, flag2, flag3;
        flag1 = temp.setAverageData(result);
        flag2 = temp.setMaxData(result);
        flag3 = temp.setMinData(result);
        return flag1 || flag2 || flag3;
    }

    @Override
    protected void beginBroadcastWrapper(int method, byte[] bytes, int type) {
        if (MeasureFragment.sIsBluetoothWorkable) {
            //该页面正在展示中
            super.beginBroadcastWrapper(method, bytes, type);
        }
        //本地进行缓存等
        if (method == BluetoothConstant.METHOD_ON_RECEIVE_MESSAGE) {
            sDataResolver.resolveData(bytes);
        }

    }

    @Override
    public void onResolve(String result, int type) {
        sCalendar.setTimeInMillis(System.currentTimeMillis());
        float tempValue;
        MeasureData temp;
        switch (type) {
            //每次都要求获取数据的字符串
            case 0: {//保留
                break;
            }
            case 1: {
                //血氧
                if (result.length() == 8) {
                    Log.d("workItem", "handleMessage: 血氧");
                    Log.d("workItem", "handleMessage: 脉搏");
                    checkMinuteAndCache(MEASURE_TYPE_XUEYANG);
                    checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_MAIBO);
                    //进行血氧的结果解析
                    tempValue = isValidData(result.substring(0, 2), MEASURE_TYPE_XUEYANG);
                    temp = sMeasureDataList.get(MEASURE_TYPE_XUEYANG);
                    if (tempValue != ERROR_RETURN_VALUE) {
                        compareData(temp, tempValue);
                    }
                    //进行脉搏的结果解析
                    tempValue = isValidData(result.substring(2, 4), MEASURE_TYPE_MAIBO);
                    temp = sMeasureDataList.get(ConstantsConfig.MEASURE_TYPE_MAIBO);
                    if (tempValue != ERROR_RETURN_VALUE) {
                        compareData(temp, tempValue);
                    }
                }
                break;
            }
            case 2: {
                //心电
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_XINDIAN);
                tempValue = isValidData(result, MEASURE_TYPE_XINDIAN);
                temp = sMeasureDataList.get(ConstantsConfig.MEASURE_TYPE_XINDIAN);
                if (tempValue != ERROR_RETURN_VALUE) {
                    compareData(temp, tempValue);
                }
                break;
            }
            case 3: {
                //血糖
                Log.d("workItem", "handleMessage: 血糖");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_XUETANG);
                tempValue = isValidData(result, MEASURE_TYPE_XUETANG);
                temp = sMeasureDataList.get(ConstantsConfig.MEASURE_TYPE_XUETANG);
                if (tempValue != ERROR_RETURN_VALUE) {
                    compareData(temp, tempValue);
                }
                break;
            }
            case 4: {
                //体温
                Log.d("workItem", "handleMessage: 体温");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_TIWEN);
                tempValue = isValidData(result, MEASURE_TYPE_TIWEN);
                temp = sMeasureDataList.get(ConstantsConfig.MEASURE_TYPE_TIWEN);
                if (tempValue != ERROR_RETURN_VALUE) {
                    compareData(temp, tempValue);
                }
                break;
            }
            case 5: {
                //粉尘
                Log.d("workItem", "handleMessage: 粉尘");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_FENCHEN);
                tempValue = isValidData(result, MEASURE_TYPE_FENCHEN);
                temp = sMeasureDataList.get(ConstantsConfig.MEASURE_TYPE_FENCHEN);
                if (tempValue != ERROR_RETURN_VALUE) {
                    compareData(temp, tempValue);
                }
                break;
            }
            default:
                break;
        }
    }
}
