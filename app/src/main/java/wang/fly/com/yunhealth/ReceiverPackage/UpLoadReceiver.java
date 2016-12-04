package wang.fly.com.yunhealth.ReceiverPackage;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.Service.UpLoadService;

/**
 * Created by 82661 on 2016/12/3.
 */

public class UpLoadReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type", MainActivity.RECEIVER_TYPE_UPLOAD);
        if (type == MainActivity.RECEIVER_TYPE_UPLOAD){
            Intent i = new Intent(context, UpLoadService.class);
            i.putExtra("type", MainActivity.RECEIVER_TYPE_UPLOAD);
            context.startService(i);
        }
    }
}
