package indi.noclay.cloudhealth.util;

/**
 * Created by clay on 2018/4/16.
 */

public class InternetUrlManager {
    //健康资讯的api（网址）
    public static final String HEALTHY_NEWS_URL = "http://news.99.com.cn/jiankang/";

    public static final String HEALTHY_FOOD_URL = "http://www.douguo.com/caipu/";

    public static final String HEALTHY_FOOD_MENU_URL = HEALTHY_FOOD_URL + "fenlei";

    public static String getHealthyFoodListURL(String subCategory){
        return HEALTHY_FOOD_URL + subCategory;
    }

    public static final String HEALTHY_LIFE = "https://www.sandingtv.com/";

}
