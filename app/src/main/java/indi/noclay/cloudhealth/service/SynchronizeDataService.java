package indi.noclay.cloudhealth.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.MeasureTableHelper;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.receiver.UpLoadReceiver;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.MeasureTableHelper.ERROR_LOAD;
import static indi.noclay.cloudhealth.database.MeasureTableHelper.upLoadMeasureData;
import static indi.noclay.cloudhealth.database.MedicineTableHelper.insertMedicineDetail;
import static indi.noclay.cloudhealth.util.ConstantsConfig.LOAD_CACHE_MINUTE;

/**
 * Created by 82661 on 2016/12/3.
 */

public class SynchronizeDataService extends Service{
    private static final String TAG = "tongzhi";
    public static final int UP_LOAD_START = 0;
    public static final int UP_LOAD_ING = 1;
    public static final int UP_LOAD_END = 2;
    public static final int UP_LOAD_FAIL = 3;

    private Handler mHandler = new SynchronizedHandler();
    private class SynchronizedHandler extends Handler{
        int count = 0;
        int threshold = 0;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UP_LOAD_START:{
                    threshold = msg.arg1;
                    Log.d(TAG, "开始上传: size = " + msg.arg1);
                    break;
                }
                case UP_LOAD_ING:{
                    count += msg.arg1;
                    if (msg.arg2 == threshold / 50){
                        if (count < threshold * 0.8){
                            //上传失败
                            makeANotification("数据上传失败，之后重试");
                        }else{
                            MeasureTableHelper.deleteAll();
                            makeANotification("数据上传成功" + count + "条");
                        }
                    }
                    Log.d(TAG, "上传中: successCount = " + msg.arg1 + "... groupIndex = " + msg.arg2);
                    break;
                }
                case UP_LOAD_END:{
                    Log.d(TAG, "上传结束: ");
                    break;
                }
                case UP_LOAD_FAIL:{
                    makeANotification("数据上传失败，之后重试");
                    Log.d(TAG, "上传失败: ");
                    break;
                }
            }
        }
    }

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
                if (!UtilClass.checkNetwork(getApplicationContext())){
                    makeANotification("网络异常，无法同步");
                    return;
                }
                String id = SharedPreferenceHelper.getLoginUserId();
                Log.d(TAG, "run: id" + id);
                if (TextUtils.isEmpty(id) || id.length() != 10){
                    makeANotification("您还没有登录");
                }
                int count = upLoadMeasureData(id, mHandler);
                if (count == ERROR_LOAD){
                    makeANotification("数据同步失败");
                }else{
                    makeANotification("数据同步完成");
                }

                //同步用药闹钟数据
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
                                    insertMedicineDetail(list.get(i));
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
        calendar.setTimeInMillis(System.currentTimeMillis() + LOAD_CACHE_MINUTE * 60 * 1000);
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
