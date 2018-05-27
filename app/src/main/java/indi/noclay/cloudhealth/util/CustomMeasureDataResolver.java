package indi.noclay.cloudhealth.util;

import java.util.Arrays;

import pers.noclay.utiltool.NormalUtils;

/**
 * Created by NoClay on 2017/11/19.
 */

public class CustomMeasureDataResolver extends ABSMeasureDataResolver {
    String last = null;


    public CustomMeasureDataResolver() {
        this.last = "";
    }

    @Override
    public void resolveData(byte[] datas) {
        int start = -1;
        int end = 0;
        for (int i = 0; i < datas.length; i++) {
            if (i < datas.length - 2
                    && (datas[i] & 0xff)  == 0xdc
                    && (datas[i + 1] & 0xff) == 0xba){
                //找到一个锚点
                end = i;
                if (start == -1){
                    start = end;
                } else if (start < end && end < datas.length){
                    byte[] data = new byte[end - start];
                    for (int j = 0; j < end - start; j++) {
                        data[j] = datas[start + j];
                    }
                    data = Arrays.copyOfRange(datas, start, end);
                    analysisData(data);
                    start = end;
                }
            }
        }
    }
    private void analysisData(byte[] datas){
        if (datas == null || datas.length < 6){
            return;
        }
        int len = datas.length;
        int type = datas[2] & 0x0f;
        int dataLength = (datas[3] & 0xff) * 256 + (datas[4] & 0xff);
        if (len != 6 + dataLength){
            return; //校验数据包长度
        }
        int sum = 0;
        for (int i = 0; i < len - 1; i++) {
            sum += datas[i]; //累加得出校验和
        }
        if ((sum & 0xff) != (datas[len - 1] & 0xff)){
            return; //校验和
        }
        String data = NormalUtils.hexValueOfBytes(datas);
        if (data != null && data.length() == len * 2){
            mOnResolveListener.onResolve(data.substring(10, 10 + dataLength * 2), type);
        }
    }
    /**
     * 处理的字符串应该符合如下规则
     * dcba + e[0~9] + [0~9][0~9] + [0~9][0~9] + ~ + sum
     *
     * @param substring
     */
    private void analysisData(String substring) {
        if (substring == null) {
            return;
        }
        int len = substring.length();
        if (len < 12) {
            return;
        }
        int type = Integer.valueOf(substring.charAt(5) + "", 16);
        int highLength = Integer.valueOf(substring.charAt(6) + substring.charAt(7) + "", 16);
        int lowLength = Integer.valueOf(substring.charAt(8) + substring.charAt(9) + "", 16);
        if (len != (12 + (highLength + lowLength) * 2)) {
            //长度不符合
            return;
        }
        int sum = 0;
        for (int i = 2; i < len; ) {
            sum += Integer.valueOf(substring.charAt(i - 2) + "" + substring.charAt(i - 1), 16);
            i += 2;
        }
        String sumString = Integer.toHexString(sum);
        if (!sumString.substring(sumString.length() - 2, sumString.length())
                .equals(substring.substring(substring.length() - 2, substring.length()))) {
            return;
        }
        String dataString = substring.substring(10, 10 + (highLength + lowLength) * 2);
        if (dataString.length() > 0) {
            mOnResolveListener.onResolve(dataString, type);
        }
    }
    
}
