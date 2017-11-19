package indi.noclay.cloudhealth.service;

import android.util.Log;

import indi.noclay.cloudhealth.fragment.MeasureFragment;
import indi.noclay.cloudhealth.util.ABSMeasureDataResolver;
import indi.noclay.cloudhealth.util.CustomMeasureDataResolver;
import pers.noclay.bluetooth.BluetoothConnectionService;
import pers.noclay.bluetooth.BluetoothConstant;

/**
 * Created by NoClay on 2017/11/19.
 */

public class BluetoothConnectService extends BluetoothConnectionService implements
        ABSMeasureDataResolver.OnResolveListener{

    private static CustomMeasureDataResolver sDataResolver;

    @Override
    public void onCreate() {
        super.onCreate();
        sDataResolver = new CustomMeasureDataResolver();
        sDataResolver.setOnResolveListener(this);
    }

    @Override
    protected void beginBroadcastWrapper(int method, byte[] bytes, int type) {
        if (MeasureFragment.sIsBluetoothWorkable){
            //该页面正在展示中
            beginBroadcast(method, bytes, type);
        }
        //本地进行缓存等
        if (method == BluetoothConstant.METHOD_ON_RECEIVE_MESSAGE){
            sDataResolver.resolveData(bytes);
        }

    }

    @Override
    public void onResolve(String result, int type) {
        switch(type){
                //每次都要求获取数据的字符串
                case 0:{//保留
                    break;
                }
                case 1:{
                    //血氧
                    if(result.length()==8){
                        Log.d("workItem","handleMessage: 血氧");
                        Log.d("workItem","handleMessage: 脉搏");
                    }
                    break;
                }
                case 2:{
                    //心电
                    if(result.length()==4){

                    }
                    break;
                }
                case 3:{
                    //血糖
                    if(result.length()==12){
                        Log.d("workItem","handleMessage: 血糖");

                    }
                    break;
                }
                case 4:{
                    //体温
                    if(result.length()==4){
                        Log.d("workItem","handleMessage: 体温");

                    }
                    break;
                }
                case 5:{
                    //粉尘
                    if(result.length()==8){
                        Log.d("workItem","handleMessage: 粉尘");

                    }
                    break;
                }
                default:break;
        }
    }
}
