package wang.fly.com.yunhealth.util;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by 82661 on 2016/11/6.
 */

public class UtilClass {
    /**
     * dp到px转换
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
     * @param value
     * @return
     */
    public static String getTwoShortValue(float value){
        DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(value);//format 返回的是字符串
    }
}
