package wang.fly.com.yunhealth.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.ReceiverPackage.UpLoadReceiver;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/12/3.
 */

public class UpLoadService extends Service{
    private static final String TAG = "tongzhi";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra("type", MyConstants.RECEIVER_TYPE_UPLOAD);
        boolean isFirstStart = intent.getBooleanExtra("isFirst", false);
        Log.d(TAG, "onStartCommand: isFirstStart" + isFirstStart);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //在这里进行上传操作
                if (UtilClass.checkNetwork(getApplicationContext())){
                    makeANotification("网络异常，无法同步");
                }
                String id = getApplicationContext()
                        .getSharedPreferences("LoginState", MODE_PRIVATE)
                        .getString("userId", "");
                Log.d(TAG, "run: id" + id);
                if (id.isEmpty() || id.length() != 10){
                    makeANotification("您还没有登录");
                }
                MyDataBase myDataBase = new MyDataBase(getApplicationContext(),
                        "LocalStore.db", null, MyConstants.DATABASE_VERSION);
                int count = myDataBase.upLoadMeasureData(id);
                if (count == MyDataBase.ERROR_LOAD){
                    makeANotification("测量信息上传失败");
                }else if (count == 0){
                    makeANotification("本地无缓存");
                }else{
                    makeANotification("测量信息上传" + count + "条成功！");
                }

            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d(TAG, "onStartCommand: nowTime " + UtilClass.valueOfCalendar(calendar));
        if (calendar.get(Calendar.MINUTE) < MyConstants.LOAD_CACHE_MINUTE){
            calendar.set(Calendar.MINUTE, MyConstants.LOAD_CACHE_MINUTE);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }else{
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            calendar.set(Calendar.MINUTE, MyConstants.LOAD_CACHE_MINUTE);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        Log.d(TAG, "onStartCommand: clickTime  " + UtilClass.valueOfCalendar(calendar));
        long triggerAtTime = calendar.getTimeInMillis();
        Intent i = new Intent(this, UpLoadReceiver.class);
        i.putExtra("type", type);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void makeANotification(String content){
        Log.d(TAG, "makeANotification: 创建一个通知");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("云健康")
                .setContentText(content)
                .setTicker("您有一条信息")
                .setContentIntent(getDefaultIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification no = mBuilder.build();
        manager.notify(1, no);
    }

    public PendingIntent getDefaultIntent(int flags){
        PendingIntent p = PendingIntent.getActivity(getApplicationContext(),
                1, new Intent(), flags);
        return p;
    }
}
