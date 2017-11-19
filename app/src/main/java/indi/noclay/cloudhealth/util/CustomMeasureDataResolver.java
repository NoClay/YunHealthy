package indi.noclay.cloudhealth.util;

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
        String data = last + NormalUtils.hexValueOfBytes(datas);
        int start = 0;
        int end = 0;
        while (start != -1 && end != -1){
            start = data.indexOf("dcba");
            if (start != -1 && data.length() > 4) {
                end = data.indexOf("dcba", start + 1);
            }
            if (start != -1 && end != -1 && start != end) {
                analysisData(data.substring(start, end));
                data = data.substring(end);
            }
        }
        last = data;
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
        if (!UtilClass.checkHexString(substring)) {
            return;
        }
        if (!substring.startsWith("dcbae")) {
            return;
        }
        int type = Integer.valueOf(substring.substring(5, 6), 16);
        int highLength = Integer.valueOf(substring.substring(6, 8), 16);
        int lowLength = Integer.valueOf(substring.substring(8, 10), 16);
        if (len != (12 + (highLength + lowLength) * 2)) {
            //长度不符合
            return;
        }
        int sum = 0;
        for (int i = 2; i < len; ) {
            sum += Integer.valueOf(substring.substring(i - 2, i), 16);
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
