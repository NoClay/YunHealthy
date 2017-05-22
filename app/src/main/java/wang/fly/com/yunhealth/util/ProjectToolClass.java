package wang.fly.com.yunhealth.util;

/**
 * Created by noclay on 2017/5/19.
 */

public class ProjectToolClass {
    /**
     * 判断一个账户名（手机号）是否是账户
     * @param name
     * @return
     */
    public static boolean isAccentName(String name){
        if (name.length() != 11){
            return false;
        }
        return true;
    }
}
