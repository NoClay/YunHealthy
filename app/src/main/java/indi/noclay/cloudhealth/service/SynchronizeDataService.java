package indi.noclay.cloudhealth.service;

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
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.receiver.UpLoadReceiver;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/12/3.
 */

public class SynchronizeDataService extends Service{
    private static final String TAG = "tongzhi";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
        int type = intent == null ? ConstantsConfig.RECEIVER_TYPE_UPLOAD : intent.getIntExtra("type", ConstantsConfig.RECEIVER_TYPE_UPLOAD);
        boolean isFirstStart = intent != null && intent.getBooleanExtra("isFirst", false);
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
                final LocalDataBase myDataBase = new LocalDataBase(getApplicationContext(),
                        "LocalStore.db", null, ConstantsConfig.DATABASE_VERSION);
                int count = myDataBase.upLoadMeasureData(id);
                if (count == LocalDataBase.ERROR_LOAD){
                    makeANotification("数据同步失败");
                }else{
                    makeANotification("数据同步完成");
                }
                final SignUserData userData = SharedPreferenceHelper.getLoginUser();
                if (userData != null){
                    BmobQuery<MedicineDetail> query = new BmobQuery<MedicineDetail>();
                    query.addWhereEqualTo("owner", userData);
                    query.findObjects(new FindListener<MedicineDetail>() {
                        @Override
                        public void done(List<MedicineDetail> list, BmobException e) {
                            Log.d(TAG, "done: list = " + list);
                            Log.e(TAG, "done: ", e);
                            if (e == null && list != null){
                                Log.d(TAG, "done: size = " + list.size());
                                for (int i = 0; i < list.size(); i++) {
                                    Log.d(TAG, "done: id = " + list.get(i).getObjectId());
                                    LocalDataBase.insertMedicineDetail(list.get(i));
                                }
                            }
                        }
                    });
                }


            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d(TAG, "onStartCommand: nowTime " + UtilClass.valueOfCalendar(calendar));
        if (calendar.get(Calendar.MINUTE) < ConstantsConfig.LOAD_CACHE_MINUTE){
            calendar.set(Calendar.MINUTE, ConstantsConfig.LOAD_CACHE_MINUTE);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }else{
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            calendar.set(Calendar.MINUTE, ConstantsConfig.LOAD_CACHE_MINUTE);
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
