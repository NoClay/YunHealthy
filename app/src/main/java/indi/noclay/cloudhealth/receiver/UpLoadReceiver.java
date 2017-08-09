package indi.noclay.cloudhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import indi.noclay.cloudhealth.service.SynchronizeDataService;
import indi.noclay.cloudhealth.util.MyConstants;


/**
 * Created by 82661 on 2016/12/3.
 */

public class UpLoadReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type", MyConstants.RECEIVER_TYPE_UPLOAD);
        if (type == MyConstants.RECEIVER_TYPE_UPLOAD){
            Intent i = new Intent(context, SynchronizeDataService.class);
            i.putExtra("type", MyConstants.RECEIVER_TYPE_UPLOAD);
            context.startService(i);
        }
    }
}
