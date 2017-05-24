package wang.fly.com.yunhealth.ReceiverPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.Fragments.DataMedicalFragment;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.SharedPreferenceHelper;

/**
 * Created by noclay on 2017/5/23.
 */

public class MedicineAlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra("time");
        MyDataBase dbHelper = new MyDataBase(context, "LocalStore.db", null, MyConstants.DATABASE_VERSION);
        SignUserData user = SharedPreferenceHelper.getLoginUser();
        if (user != null && dbHelper.isNeedEatMedicine(time, user.getObjectId())){
//需要吃药
            Intent intent1 = new Intent();
            intent1.setAction("cloudHealth.intent.MedicineClock");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("type", DataMedicalFragment.CLOCK_MEDICINE);
            intent1.putExtra("time", time);
            context.startActivity(intent1);
        }
    }
}
