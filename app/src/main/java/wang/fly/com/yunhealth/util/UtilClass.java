package wang.fly.com.yunhealth.util;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by 82661 on 2016/11/6.
 */

public class UtilClass {
    /**
     * dp到px转换
     *
     * @param context
     * @param dp
     * @return
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px到dp的转换
     *
     * @param context
     * @param px
     * @return
     */
    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 保留两位小数，并返回字符串
     *
     * @param value
     * @return
     */
    public static String getTwoShortValue(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(value);//format 返回的是字符串
    }

    /**
     * 字节流转换为十六进制字符串
     *
     * @param src
     * @return
     */
    public static String valueOfBytes(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制字符串转换为bytes
     *
     * @param hexString
     * @return
     */
    public static byte[] valueOfString(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase().replace(" ", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean checkHexString(String src) {
        for (char c : src.toCharArray()) {
            if ("0123456789AaBbCcDdEeFf".indexOf(c) == -1){
                return false;
            }
        }
        return true;
    }


    /**
     * 将一个十六进制字符串转换为十进制整型
     */
    public static int valueOfHexString(String data){
        int result = Integer.valueOf(data, 16).intValue();
        String value = Integer.toBinaryString(result);
        if (value.length() == data.length() * 4 && value.startsWith("1")){
            result --;
            value = Integer.toBinaryString(result);
            StringBuilder string = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                if (i == 0){
                    string.append(1);
                }else if (value.charAt(i) == '1'){
                    string.append(0);
                }else{
                    string.append(1);
                }
            }
            result = -Integer.valueOf(string.toString(), 2);
        }
        return result;
    }
}
