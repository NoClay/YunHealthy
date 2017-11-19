package indi.noclay.cloudhealth.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;


/**
 * Created by noclay on 2017/5/23.
 */

public class MedicineAlarmReceiver extends BroadcastReceiver{
    private static final String TAG = "MedicineAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra("time");
        if (LocalDataBase.isNeedEatMedicine(time)){//需要吃药
            Log.d(TAG, "onReceive: need");
            Intent intent1 = new Intent();
            intent1.setAction("cloudHealth.intent.MedicineClock");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("type", DataMedicalFragment.CLOCK_MEDICINE);
            intent1.putExtra("time", time);
            context.startActivity(intent1);
        }
        startAlarm(context);
    }

    private void startAlarm(Context context) {
        Calendar c=Calendar.getInstance();//获取日期对象
        c.setTimeInMillis(System.currentTimeMillis());        //设置Calendar对象
        if (c.get(Calendar.MINUTE) < 30){
            c.set(Calendar.MINUTE, 30);            //设置闹钟的分钟数
        }else{
            c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
            c.set(Calendar.MINUTE, 00);
        }
        c.set(Calendar.SECOND, 0);                //设置闹钟的秒数
        c.set(Calendar.MILLISECOND, 0);            //设置闹钟的毫秒数
        Intent intent = new Intent(context, MedicineAlarmReceiver.class);    //创建Intent对象
        intent.putExtra("time", c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);    //创建PendingIntent
        //alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);        //设置闹钟
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d("clock", "startAlarm: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        alarm.set(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(), pi);        //设置闹钟，当前时间就唤醒
    }
}
