package wang.fly.com.yunhealth.Activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 寒 on 2016/6/5.
 */
public class ActivityCollector {
    public static List<Activity> activityList = new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity :activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
