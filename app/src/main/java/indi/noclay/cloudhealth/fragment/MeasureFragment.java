package indi.noclay.cloudhealth.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import indi.noclay.cloudhealth.MainActivityCopy;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecycleAdapterForMeasureOnly;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.service.BluetoothConnectService;
import indi.noclay.cloudhealth.util.ABSMeasureDataResolver;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.CustomMeasureDataResolver;
import indi.noclay.cloudhealth.util.FileCacheUtil;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;
import pers.noclay.bluetooth.Bluetooth;
import pers.noclay.bluetooth.BluetoothConfig;

import static indi.noclay.cloudhealth.database.MeasureTableHelper.addOneMeasureData;
import static indi.noclay.cloudhealth.database.MeasureTableHelper.checkOneMeasureDataCache;
import static indi.noclay.cloudhealth.database.measuredata.MeasureDataHelper.ERROR_RETURN_VALUE;
import static indi.noclay.cloudhealth.database.measuredata.MeasureDataHelper.isValidData;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_FENCHEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_MAIBO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_TIWEN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XINDIAN;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUETANG;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MEASURE_TYPE_XUEYANG;
import static pers.noclay.bluetooth.BluetoothConstant.MESSAGE_CONNECT_FAILED;
import static pers.noclay.bluetooth.BluetoothConstant.MESSAGE_CONNECT_SUCCESS;
import static pers.noclay.bluetooth.BluetoothConstant.MESSAGE_READ_STRING;

/*
 * Created by NoClay on 2016/11/2.
 */
public class MeasureFragment extends Fragment implements
        View.OnClickListener,
        ABSMeasureDataResolver.OnResolveListener {
    private TextView connectDevice;
    private RecyclerView recyclerView;
    private Context context;
    private RecycleAdapterForMeasureOnly myAdapter;
    private ProgressBar load;
    private LocalDataBase myDataBase;
    private List<MeasureData> measureDataList;
    private long last;
    private Calendar calendar;
    private String data;
    long time;
    long count = 0;
    public static boolean sIsBluetoothWorkable = true;
    //请求
    static final int REQUEST_OPEN_BLUETOOTH = 0;
    static final int MSG_WAIT_CONNECT = 0;
    static final int MSG_CONNECT_SUCCESS = 1;
    static final int MSG_START_CONNECT = 2;
    static final int MSG_READ_DATA = 3;
    static final int MSG_CONNECT_FAILED = 4;
    static CustomMeasureDataResolver sDataResolver;
    private static final String TAG = "MeasureFragment";
    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measure, container, false);
        context = getContext();
        mView = v;
        findView(v);
        initBluetoothSDK();
        UtilClass.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        UtilClass.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        sDataResolver = new CustomMeasureDataResolver();
        sDataResolver.setOnResolveListener(this);
        return v;
    }

    private void initBluetoothSDK() {
        BluetoothConfig config = new BluetoothConfig.Builder(getContext())
                .setAutoPairAble(true)
                .setUUID("00001101-0000-1000-8000-00805F9B34FB")
                .setServerEnable(false)
                .setConnectionServiceClass(BluetoothConnectService.class)
                .build();
        Bluetooth.setHandler(((Bluetooth.Handler) mHandler));
        Bluetooth.initialize(config);
        Bluetooth.setApplicationContext(this.getActivity().getApplicationContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        if (myAdapter != null) {
            myAdapter.startRefreshing();
        }
        sIsBluetoothWorkable = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        sIsBluetoothWorkable = false;
        if (myAdapter != null) {
            myAdapter.stopRefreshing();
        }
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.measure_recycleView);
        connectDevice = (TextView) v.findViewById(R.id.connect_device);
        load = (ProgressBar) v.findViewById(R.id.load);
        measureDataList = new ArrayList<>();
        for (int i = 0; i < ConstantsConfig.LABEL_STRING.length; i++) {
            MeasureData measure = new MeasureData();
            measure.setName(ConstantsConfig.LABEL_STRING[i]);
            measureDataList.add(measure);
        }
        load.setVisibility(View.INVISIBLE);
        connectDevice.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        DefaultItemAnimator d = new DefaultItemAnimator();
        d.setMoveDuration(0);
        d.setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(d);
        myAdapter = new RecycleAdapterForMeasureOnly(R.layout.item_measure_data_show,
                context, measureDataList);
        recyclerView.setAdapter(myAdapter);
        //本地缓存所需要的初始化
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(SystemClock.currentThreadTimeMillis());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_device: {
                String address = SharedPreferenceHelper.getDevice();
                if (UtilClass.isMacAddress(address)) {
                    toToast("正在连接设备");
                    Bluetooth.setTargetAddress(address);
                    Bluetooth.startConnect();
                    load.setVisibility(View.VISIBLE);
                    connectDevice.setVisibility(View.GONE);
                } else {
                    Snackbar.make(getView(), "暂无设备，请点击我的设备添加设备", Snackbar.LENGTH_SHORT)
                            .setAction("好的", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getActivity() instanceof MainActivityCopy) {
                                        MainActivityCopy instance = (MainActivityCopy) getActivity();
                                        instance.setCurrentPage(MainActivityCopy.PAGE_MINE);
                                    }
                                }
                            }).show();
                }
                break;
            }
        }
    }

    private void toToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bluetooth.onResult(requestCode, resultCode, data);
    }

    /**
     * 检查本时段缓存处理
     *
     * @param type
     * @param minute
     */
    public void checkMinuteAndCache(int type, int minute) {
        if (minute % ConstantsConfig.CACHE_TIME_LENGTH == 0
                && !checkOneMeasureDataCache(type, calendar.getTime())) {
            Log.d("Cache", "checkMinuteAndCache: cache + " +
                    ConstantsConfig.LABEL_STRING[type] + "\tminute" + minute);
            addOneMeasureData(measureDataList.get(type), type, calendar.getTime());
            measureDataList.get(type).reset();
        }
    }

    /**
     * 比较一些数据，相同返回
     *
     * @param temp
     * @param result
     */
    private boolean compareData(MeasureData temp, float result) {
        boolean flag1, flag2, flag3;
        flag1 = temp.setAverageData(result);
        flag2 = temp.setMaxData(result);
        flag3 = temp.setMinData(result);
        return flag1 || flag2 || flag3;
    }

    @Override
    public void onDestroyView() {
        Bluetooth.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onResolve(String result, int type) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int minute = calendar.get(Calendar.MINUTE);
        MeasureData temp;
        float resultValue;
        switch (type) {
            //每次都要求获取数据的字符串
            case 0: {//保留
                break;
            }
            case 1: {
                //血氧
                if (result.length() == 8) {
                    Log.d("workItem", "handleMessage: 血氧");
                    Log.d("workItem", "handleMessage: 脉搏");
                    checkMinuteAndCache(MEASURE_TYPE_XUEYANG, minute);
                    checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_MAIBO, minute);
                    //进行血氧的结果解析
                    resultValue = isValidData(result.substring(0, 2), MEASURE_TYPE_XUEYANG);
                    temp = measureDataList.get(MEASURE_TYPE_XUEYANG);
                    if (resultValue != ERROR_RETURN_VALUE && compareData(temp, resultValue)) {
                        myAdapter.notifyItemChanged(MEASURE_TYPE_XUEYANG);
                    }
                    //进行脉搏的结果解析
                    resultValue = isValidData(result.substring(2, 4), MEASURE_TYPE_MAIBO);
                    temp = measureDataList.get(ConstantsConfig.MEASURE_TYPE_MAIBO);
                    if (resultValue != ERROR_RETURN_VALUE && compareData(temp, resultValue)) {
                        myAdapter.notifyItemChanged(ConstantsConfig.MEASURE_TYPE_MAIBO);
                    }
                }
                break;
            }
            case 2: {
                //心电
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_XINDIAN, minute);
                resultValue = isValidData(result, MEASURE_TYPE_XINDIAN);
                temp = measureDataList.get(ConstantsConfig.MEASURE_TYPE_XINDIAN);
                if (resultValue != ERROR_RETURN_VALUE){
                    myAdapter.startRefreshing();
                    myAdapter.drawHeartWavesPoint(resultValue);
                    FileCacheUtil.appendToFile(((int) resultValue));
                    if (compareData(temp, resultValue)) {
                        myAdapter.notifyItemChanged(ConstantsConfig.MEASURE_TYPE_XINDIAN);
                    }
                }
                break;
            }
            case 3: {
                //血糖
                Log.d("workItem", "handleMessage: 血糖");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_XUETANG, minute);
                resultValue = isValidData(result, MEASURE_TYPE_XUETANG);
                temp = measureDataList.get(ConstantsConfig.MEASURE_TYPE_XUETANG);
                if (resultValue != ERROR_RETURN_VALUE && compareData(temp, resultValue)) {
                    myAdapter.notifyItemChanged(ConstantsConfig.MEASURE_TYPE_XUETANG);
                }
                break;
            }
            case 4: {
                //体温
                Log.d("workItem", "handleMessage: 体温");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_TIWEN, minute);
                resultValue = isValidData(result, MEASURE_TYPE_TIWEN);
                temp = measureDataList.get(ConstantsConfig.MEASURE_TYPE_TIWEN);
                if (resultValue != ERROR_RETURN_VALUE && compareData(temp, resultValue)) {
                    myAdapter.notifyItemChanged(ConstantsConfig.MEASURE_TYPE_TIWEN);
                }
                break;
            }
            case 5: {
                //粉尘
                Log.d("workItem", "handleMessage: 粉尘");
                checkMinuteAndCache(ConstantsConfig.MEASURE_TYPE_FENCHEN, minute);
                resultValue = isValidData(result, MEASURE_TYPE_FENCHEN);
                temp = measureDataList.get(ConstantsConfig.MEASURE_TYPE_FENCHEN);
                if (resultValue != ERROR_RETURN_VALUE && compareData(temp, resultValue)) {
                    myAdapter.notifyItemChanged(ConstantsConfig.MEASURE_TYPE_FENCHEN);
                }
                break;
            }
            default:
                break;
        }
    }

    Handler mHandler = new MeasureHandler();

    private class MeasureHandler extends Bluetooth.Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CONNECT_FAILED: {
                    Log.d(TAG, "handleMessage: 连接失败");
                    connectDevice.setClickable(true);
                    connectDevice.setText("连接失败");
                    connectDevice.setVisibility(View.VISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    break;
                }
                case MESSAGE_CONNECT_SUCCESS: {
                    Log.d(TAG, "handleMessage: 连接成功");
                    connectDevice.setText("已连接");
                    connectDevice.setVisibility(View.VISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    break;
                }
                case MESSAGE_READ_STRING: {
                    //进行数据的分类设定
                    Log.d("connectThread", "handleMessage: handle ");
                    sDataResolver.resolveData((byte[]) msg.obj);
                    break;
                }
                case MSG_READ_DATA:{
                    Log.d(TAG, "handleMessage: type = " + msg.arg1);

                    break;
                }
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}