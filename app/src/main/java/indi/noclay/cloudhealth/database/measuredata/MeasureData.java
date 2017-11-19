package indi.noclay.cloudhealth.database.measuredata;


import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import pers.noclay.foldlineview.FoldLineInterface;

/**
 * Created by 82661 on 2016/11/23.
 */

public class MeasureData extends BmobObject implements FoldLineInterface{
    private String name;
    private Float averageData;
    private Float maxData;
    private Float minData;
    private Integer count;
    private Boolean isAverageDanger;
    private Boolean isMaxDanger;
    private Boolean isMinDanger;
    public SignUserData owner;
    private BmobDate measureTime;

    public BmobDate getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(BmobDate measureTime) {
        this.measureTime = measureTime;
    }

    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }

    public MeasureData() {
        name = "";
        reset();
    }

    /**
     * 重置
     */
    public void reset(){
        averageData = 0.0f;
        maxData = Float.MIN_VALUE;
        minData = Float.MAX_VALUE;
        count = 0;
        isAverageDanger = false;
        isMaxDanger = false;
        isMinDanger = false;
    }

    public MeasureData copyTo(MeasureData measureData){
        measureData.setAverageData(this.getAverageData());
        measureData.setMaxData(this.getMaxData());
        measureData.setMinData(this.getMinData());
        measureData.setCount(this.getCount());
        measureData.setAverageDanger(this.getAverageDanger());
        measureData.setMaxDanger(this.getMaxDanger());
        measureData.setMinDanger(this.getMinDanger());
        measureData.setMeasureTime(this.getMeasureTime());
        return measureData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAverageData() {
        return averageData;
    }

    public boolean setAverageData(float averageData) {
        float last = this.averageData;
        float sum = getSum();
        this.count ++;
        this.averageData = (sum + averageData) / this.count;
        if (this.averageData != last){
            return true;
        }
        return false;
    }

    public float getMaxData() {
        return maxData;
    }

    public boolean setMaxData(float maxData) {
        if (this.maxData < maxData){
            this.maxData = maxData;
            return true;
        }
        return false;
    }

    public float getMinData() {
        return minData;
    }

    public boolean setMinData(float minData) {
        if (this.minData > minData){
            this.minData = minData;
            return true;
        }
        return false;
    }

    public Boolean getAverageDanger() {
        return isAverageDanger;
    }

    public Boolean getMaxDanger() {
        return isMaxDanger;
    }

    public Boolean getMinDanger() {
        return isMinDanger;
    }

    public void setMinDanger(boolean minDanger) {
        isMinDanger = minDanger;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public void setAverageDanger(boolean averageDanger) {
        isAverageDanger = averageDanger;
    }

    public void setMaxDanger(boolean maxDanger) {
        isMaxDanger = maxDanger;
    }

    public float getSum(){
        return count * averageData;
    }

    public String getDate(){
        return measureTime.getDate().substring(10, 16);
    }

    @Override
    public List<Float> getLinesAsList() {
        List<Float> result = new ArrayList<>();
        result.add(averageData);
        result.add(maxData);
        result.add(minData);
        return result;
    }

    @Override
    public String getLabel() {
        return getDate();
    }

    public static BmobObject getBmobObject(int type, MeasureData measureData, SignUserData owner){
        switch (type) {
            case ConstantsConfig.MEASURE_TYPE_XUEYANG: {
                MeasureXueYang measureXueYang = new MeasureXueYang();
                measureData.copyTo(measureXueYang);
                measureXueYang.setOwner(owner);
                return measureXueYang;
            }
            case ConstantsConfig.MEASURE_TYPE_MAIBO: {
                MeasureMaiBo measureMaiBo = new MeasureMaiBo();
                measureData.copyTo(measureMaiBo);
                measureMaiBo.setOwner(owner);
                return measureMaiBo;
            }
            case ConstantsConfig.MEASURE_TYPE_XINDIAN: {
                MeasureXinDian measureXinDian = new MeasureXinDian();
                measureData.copyTo(measureXinDian);
                measureXinDian.setOwner(owner);
                return measureXinDian;
            }
            case ConstantsConfig.MEASURE_TYPE_TIWEN: {
                MeasureTiWen measureTiWen = new MeasureTiWen();
                measureData.copyTo(measureTiWen);
                measureTiWen.setOwner(owner);
                return measureTiWen;
            }
            case ConstantsConfig.MEASURE_TYPE_FENCHEN: {
                MeasureFenChen measureFenChen = new MeasureFenChen();
                measureData.copyTo(measureFenChen);
                measureFenChen.setOwner(owner);
                return measureFenChen;
            }
            case ConstantsConfig.MEASURE_TYPE_NAODIAN: {
                MeasureNaoDian measureNaoDian = new MeasureNaoDian();
                measureData.copyTo(measureNaoDian);
                measureNaoDian.setOwner(owner);
                return measureNaoDian;
            }
            case ConstantsConfig.MEASURE_TYPE_XUEYA: {
                MeasureXueYa measureXueYa = new MeasureXueYa();
                measureData.copyTo(measureXueYa);
                measureXueYa.setOwner(owner);
                return measureXueYa;
            }
            case ConstantsConfig.MEASURE_TYPE_XUETANG: {
                MeasureXueTang measureXueTang = new MeasureXueTang();
                measureData.copyTo(measureXueTang);
                measureXueTang.setOwner(owner);
                return measureXueTang;
            }
        }
        return null;
    }
}
