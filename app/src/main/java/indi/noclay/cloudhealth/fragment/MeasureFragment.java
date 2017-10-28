package indi.noclay.cloudhealth.fragment;

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
import indi.noclay.cloudhealth.database.MyDataBase;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.util.MyConstants;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;
import pers.noclay.bluetooth.Bluetooth;
import pers.noclay.bluetooth.BluetoothConfig;
import pers.noclay.bluetooth.OnConnectListener;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener, OnConnectListener {
    private TextView connectDevice;
    private RecyclerView recyclerView;
    private Context context;
    private RecycleAdapterForMeasureOnly myAdapter;
    private ProgressBar load;
    private MyDataBase myDataBase;
    private List<MeasureData> measureDataList;
    private long last;
    private Calendar calendar;
    private String data;
    long time;
    long count = 0;
    //请求
    static final int REQUEST_OPEN_BLUETOOTH = 0;
    static final int MSG_WAIT_CONNECT = 0;
    static final int MSG_CONNECT_SUCCESS = 1;
    static final int MSG_START_CONNECT = 2;
    static final int MSG_READ_STRING = 3;
    static final int MSG_CONNECT_FAILED = 4;
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
        return v;
    }

    private void initBluetoothSDK() {
        BluetoothConfig config = new BluetoothConfig.Builder(getContext())
                .setAutoPairAble(true)
                .setUUID("00001101-0000-1000-8000-00805F9B34FB")
                .build();
        Bluetooth.initialize(config);
        Bluetooth.setOnConnectListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myAdapter != null) {
            myAdapter.startRefreshing();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAdapter != null){
            myAdapter.stopRefreshing();
        }
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.measure_recycleView);
        connectDevice = (TextView) v.findViewById(R.id.connect_device);
        load = (ProgressBar) v.findViewById(R.id.load);
        measureDataList = new ArrayList<>();
        for (int i = 0; i < MyConstants.LABEL_STRING.length; i++) {
            MeasureData measure = new MeasureData();
            measure.setName(MyConstants.LABEL_STRING[i]);
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
        myDataBase = new MyDataBase(getActivity().getApplicationContext(),
                "LocalStore.db", null, MyConstants.DATABASE_VERSION);

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

    @Override
    public void onConnectFail(int i) {
        handler.sendEmptyMessage(MSG_CONNECT_FAILED);
    }

    @Override
    public void onConnectSuccess() {
        handler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
    }

    @Override
    public void onConnectStart() {
        handler.sendEmptyMessage(MSG_START_CONNECT);
    }

    @Override
    public void onReceiveMessage(byte[] bytes) {
        data = resolveData(UtilClass.valueOfBytes(bytes) + data);

    }


    private String resolveData(String data) {
        int start = 0;
        int end = 0;
        while (start != -1 && end != -1) {
            start = data.indexOf("dcba");
            if (start != -1 && data.length() > 4) {
                end = data.indexOf("dcba", start + 1);
            }
            if (start != -1 && end != -1 && start != end) {
                analysisData(data.substring(start, end));
                data = data.substring(end);
            }
        }
        return data;
    }

    /**
     * 处理的字符串应该符合如下规则
     * dcba + e[0~9] + [0~9][0~9] + [0~9][0~9] + ~ + sum
     *
     * @param substring
     */
    private boolean analysisData(String substring) {
        //字符串的长度
        if (substring == null) {
            return false;
        }
        int len = substring.length();
        if (len < 12) {
            return false;
        }
        if (!UtilClass.checkHexString(substring)) {
            return false;
        }
        if (!substring.startsWith("dcbae")) {
            return false;
        }
        int type = Integer.valueOf(substring.substring(5, 6), 16);
        int highLength = Integer.valueOf(substring.substring(6, 8), 16);
        int lowLength = Integer.valueOf(substring.substring(8, 10), 16);
        if (len != (12 + (highLength + lowLength) * 2)) {
            //长度不符合
            return false;
        }
        int sum = 0;
        for (int i = 2; i < len; ) {
            sum += Integer.valueOf(substring.substring(i - 2, i), 16);
            i += 2;
        }
        String sumString = Integer.toHexString(sum);
        if (!sumString.substring(sumString.length() - 2, sumString.length())
                .equals(substring.substring(substring.length() - 2, substring.length()))) {
            return false;
        }
        String dataString = substring.substring(10, 10 + (highLength + lowLength) * 2);
        if (dataString.length() <= 0) {
            return false;
        }
        Message message = Message.obtain();
        message.what = MSG_READ_STRING;
        message.arg1 = type;
        message.obj = dataString;
        handler.sendMessage(message);
        return true;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_CONNECT_FAILED: {
                    Log.d(TAG, "handleMessage: 连接失败");
                    connectDevice.setClickable(true);
                    connectDevice.setText("连接失败");
                    connectDevice.setVisibility(View.VISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    break;
                }
                case MSG_CONNECT_SUCCESS: {
                    Log.d(TAG, "handleMessage: 连接成功");
                    connectDevice.setText("已连接");
                    connectDevice.setVisibility(View.VISIBLE);
                    load.setVisibility(View.INVISIBLE);
                    break;
                }
                case MSG_READ_STRING: {
                    //进行数据的分类设定
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    int minute = calendar.get(Calendar.MINUTE);
                    String data = (String) msg.obj;
                    MeasureData temp;
                    switch (msg.arg1) {
                        //每次都要求获取数据的字符串
                        case 0: {//保留
                            break;
                        }
                        case 1: {
                            //血氧
                            if (data.length() == 8) {
                                Log.d("workItem", "handleMessage: 血氧");
                                Log.d("workItem", "handleMessage: 脉搏");
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_XUEYANG, minute);
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_MAIBO, minute);
                                //进行血氧的结果解析
                                float result = UtilClass.valueOfHexString(data.substring(0, 2));
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_XUEYANG);
                                if (result > 0 && result < 100 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_XUEYANG);
                                }
                                //进行脉搏的结果解析
                                result = UtilClass.valueOfHexString(data.substring(2, 4));
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_MAIBO);
                                if (result > 0 && result < 255 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_MAIBO);
                                }
                            }
                            break;
                        }
                        case 2: {
                            //心电
                            if (data.length() == 4) {
                                time = System.currentTimeMillis();
                                count++;
                                Log.d("displayHeart", "handleMessage: time = " + time / 1000 + " count = " + count);
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_XINDIAN, minute);
                                int result = UtilClass.valueOfHexString(data);
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_XINDIAN);
//                                myAdapter.drawHeartWavesPoint(result);
                                if (compareData(temp, (float) (result))) {
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_XINDIAN);
                                }
                            }
                            break;
                        }
                        case 3: {
                            //血糖
                            if (data.length() == 12) {
                                Log.d("workItem", "handleMessage: 血糖");
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_XUETANG, minute);
                                float result = UtilClass.valueOfHexString(
                                        data.substring(10, 12)) / 10.0f;
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_XUETANG);
                                if (result > 0 && result < 300 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_XUETANG);
                                }
                            }
                            break;
                        }
                        case 4: {
                            //体温
                            if (data.length() == 4) {
                                Log.d("workItem", "handleMessage: 体温");
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_TIWEN, minute);
                                float result = UtilClass.valueOfHexString(data) / 100.0f;
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_TIWEN);
                                if (compareData(temp, result)) {
                                    //修改各项
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_TIWEN);
                                }
                            }
                            break;
                        }
                        case 5: {
                            //粉尘
                            if (data.length() == 8) {
                                Log.d("workItem", "handleMessage: 粉尘");
                                checkMinuteAndCache(MyConstants.MEASURE_TYPE_FENCHEN, minute);
                                float result = UtilClass.valueOfHexString(data) / 100.0f;
                                temp = measureDataList.get(MyConstants.MEASURE_TYPE_FENCHEN);
                                if (result > 0 && result < 800 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MyConstants.MEASURE_TYPE_FENCHEN);
                                }
                            }
                            break;
                        }
                        case 6: {
                            //脑电（待定）
                            break;
                        }
                        case 7: {
                            //血压（待定）
                            break;
                        }
                        case 8: {
                            break;
                        }
                        case 9: {
                            break;
                        }
                        case 10: {
                            break;
                        }
                        case 11: {
                            break;
                        }
                        case 12: {
                            break;
                        }
                        case 13: {
                            break;
                        }
                        case 14: {
                            break;
                        }
                        case 15: {
                            break;
                        }
                    }
                }
            }
        }
    };

    /**
     * 检查本时段缓存处理
     *
     * @param type
     * @param minute
     */
    public void checkMinuteAndCache(int type, int minute) {
        String userId = context.getSharedPreferences("LoginState",
                Context.MODE_PRIVATE).getString("userId", null);
        if (minute % MyConstants.CACHE_TIME_LENGTH == 0
                && !myDataBase.checkOneMeasureDataCache(
                type, calendar.getTime(), userId)) {
            Log.d("Cache", "checkMinuteAndCache: cache + " +
                    MyConstants.LABEL_STRING[type] + "\tminute" + minute);
            myDataBase.addOneMeasureData(
                    measureDataList.get(type), type, calendar.getTime(), userId);
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
}