package wang.fly.com.yunhealth.Fragments;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import wang.fly.com.yunhealth.Adapter.RecycleAdapterForMeasureOnly;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureData;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureXueYang;
import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.ClsUtils;
import wang.fly.com.yunhealth.util.UtilClass;

import static android.R.attr.type;
import static android.app.Activity.RESULT_OK;
import static android.media.CamcorderProfile.get;
import static wang.fly.com.yunhealth.R.drawable.min;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class MeasureFragment extends Fragment implements View.OnClickListener {
    private TextView connectDevice;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private RecycleAdapterForMeasureOnly myAdapter;
    private ProgressBar load;
    private boolean isTryingConnecting = false;
    private MyDataBase myDataBase;
    private SQLiteDatabase database;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice theDestDevice;
    BroadcastReceiver receiver;
    private ClientThread clientThread;
    private ConnectedThread connectThread;
    private List<MeasureData> measureDataList;
    private Date date;
    private long last;
    //请求
    static final int REQUEST_OPEN_BLUETOOTH = 0;
    static final int MSG_WAIT_CONNECT = 0;
    static final int MSG_CONNECT_SUCCESS = 1;
    static final int MSG_START_CONNECT = 2;
    static final int MSG_READ_STRING = 3;
    static final int MSG_CONNECT_FAILED = 4;
    private final static String deviceAddress = MainActivity.DEVICE_ADDRESS;
    private static final String TAG = "MeasureFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.measurefragment_layout, container, false);
        context = getContext();
        findView(v);
        registerBroadReceiver();
        return v;
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.measure_recycleView);
        connectDevice = (TextView) v.findViewById(R.id.connect_device);
        load = (ProgressBar) v.findViewById(R.id.load);
        measureDataList = new ArrayList<>();
        for (int i = 0; i < MainActivity.LABEL_STRING.length; i++) {
            MeasureData measure = new MeasureData();
            measure.setName(MainActivity.LABEL_STRING[i]);
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
        myAdapter = new RecycleAdapterForMeasureOnly(R.layout.measure_data_show_item,
                context, measureDataList);
        recyclerView.setAdapter(myAdapter);
        //本地缓存所需要的初始化
        date = new Date();
        myDataBase = new MyDataBase(getActivity().getApplicationContext(),
                "LocalStore.db", null, MainActivity.DATABASE_VERSION);
        database = myDataBase.getWritableDatabase();

        /**
         * 数据库测试
         */
        Cursor cursor = database.query("MeasureDataCache", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("dataCache", "findView: name " + cursor.getString(cursor.getColumnIndex("name")));
                Log.d("dataCache", "findView: isAverageDanger " + cursor.getString(cursor.getColumnIndex("isAverageDanger")));
                Log.d("dataCache", "findView: average " + cursor.getFloat(cursor.getColumnIndex("average")));
                Log.d("dataCache", "findView: createTime " + cursor.getString(cursor.getColumnIndex("createTime")));
            } while (cursor.moveToNext());
        }


    }

    private void registerBroadReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND: {
                                          /* 从intent中取得搜索结果数据 */
                        BluetoothDevice device = intent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (TextUtils.equals(device.getAddress(), deviceAddress)) {
                            theDestDevice = device;
                            if (theDestDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                                //没有进行过配对
                                try {
                                    ClsUtils.createBond(theDestDevice.getClass(), theDestDevice);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (theDestDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                //已经配对过，进行连接

                            }
                        }
                        Log.d(TAG, "设备：" + device.getName() + " address: " + device.getAddress());
                        break;
                    }
                    case BluetoothDevice.ACTION_PAIRING_REQUEST: {
                        Log.d(TAG, "onReceive: 进行配对");
                        try {
                            boolean ret = ClsUtils.setPin(theDestDevice.getClass(), theDestDevice, "1234");
                            //1.确认配对
                            ClsUtils.setPairingConfirmation(theDestDevice.getClass(), theDestDevice, true);
                            //2.终止有序广播
                            abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                            //3.调用setPin方法进行配对...

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //终止广播
                        abortBroadcast();
                        break;
                    }
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                        theDestDevice = bluetoothAdapter.getRemoteDevice(theDestDevice.getAddress());
                        if (theDestDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                            Log.d(TAG, "onReceive: 配对成功");
                            clientThread = new ClientThread();
                            isTryingConnecting = true;
                            clientThread.start();
                        } else if (theDestDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                            Log.d(TAG, "onReceive: 没有配对");
                        } else if (theDestDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                            Log.d(TAG, "onReceive: 正在配对");
                        }
                        break;
                    }
                    case BluetoothDevice.ACTION_ACL_CONNECTED: {

                        break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        getContext().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getContext().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        getContext().registerReceiver(receiver, filter);
    }

    /**
     * 请求获取用户粗略定位的权限
     * android 6.0及其以上使用
     */
    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int check = getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_device: {
//                //连接设备
//                connectDevice.setVisibility(View.INVISIBLE);
//                connectDevice.setClickable(false);
//                load.setVisibility(View.VISIBLE);
//                mayRequestLocation();
//                openBluetooth();
                /**
                 * 尝试上传数据
                 */
                String id = context.getSharedPreferences("LoginState",
                        Context.MODE_PRIVATE).getString("userId", null);
                if (id == null){
                    UtilClass.toToast(context, "上传失败,用户未登录");
                }
                if (myDataBase.upLoadMeasureData(database, id)){
                    UtilClass.toToast(context, "上传成功");
                }else{
                    UtilClass.toToast(context, "上传失败");
                }
                break;
            }
        }
    }

    private void toToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth() {
        //判断蓝牙的打开状态
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            toToast("本机不支持蓝牙设备");
            System.exit(0);
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_OPEN_BLUETOOTH);
        } else {
            searchBluetoothDevice();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_OPEN_BLUETOOTH: {
                if (resultCode == RESULT_OK) {
                    //搜索蓝牙设备
                    searchBluetoothDevice();
                } else {
                    toToast("未打开蓝牙，故无法测量实时数据");
                }
                break;
            }
        }
    }

    /**
     * 搜索蓝牙设备，添加到显示页面
     */
    private void searchBluetoothDevice() {
        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> it = bondedDevice.iterator();
        boolean flag = false;
        while (it.hasNext()) {
            BluetoothDevice blue = it.next();
            Log.d(TAG, "onActivityResult: name: " + blue.getName());
            Log.d(TAG, "onActivityResult: address: " + blue.getAddress());
            if (blue.getAddress().equals(deviceAddress)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //已经配对设备，直接进行链接
            Log.d(TAG, "onActivityResult: 已经配对，直接进行连接");
            clientThread = new ClientThread();
            isTryingConnecting = true;
            clientThread.start();
        } else {
            //搜索蓝牙进行配对
            if (bluetoothAdapter.isDiscovering()) {//正在查找
                Log.d(TAG, "onClick: 正在查找设备");
            } else {
                bluetoothAdapter.startDiscovery();
            }
        }

    }

    private class ClientThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        public ClientThread() {
            theDestDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            try {
                bluetoothSocket = theDestDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                Log.d(TAG, "ClientThread: 构造socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();

            Message message = Message.obtain();
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.connect();
                    Log.d(TAG, "run: connected" + bluetoothSocket.isConnected());
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e);
                    e.printStackTrace();
                    initConnect();
                }
                message.what = bluetoothSocket.isConnected() ? MSG_CONNECT_SUCCESS : MSG_CONNECT_FAILED;
                handler.sendMessage(message);
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public BluetoothSocket getBluetoothSocket() {
            return bluetoothSocket;
        }

        public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
        }
    }

    public void initConnect() {
        if (clientThread != null) {
            clientThread.cancel();
            clientThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private OutputStream outputStream;
        private InputStream inputStream;
        byte[] bytes = new byte[512];
        private boolean flag = false;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            try {
                outputStream = this.bluetoothSocket.getOutputStream();
                inputStream = this.bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            String data = "";
            while (flag) {
                if (inputStream == null)
                    return;
                try {
                    inputStream.read(bytes);
                    data = resolveData(UtilClass.valueOfBytes(bytes) + data);
                    Log.d(TAG, "run: receive" + data);
                    Log.d(TAG, "run: connet" + bluetoothSocket.isConnected());
                } catch (IOException e) {
                    cancel();
                    Message message = Message.obtain();
                    message.what = MSG_CONNECT_FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }

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

        public void write(String string) {
            byte[] bytes;
            bytes = string.getBytes();
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            this.flag = false;
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    connectDevice.setClickable(false);
                    connectThread = new ConnectedThread(clientThread.getBluetoothSocket());
                    connectThread.setFlag(true);
                    connectThread.setDaemon(true);
                    connectThread.start();
                    break;
                }
                case MSG_READ_STRING: {
                    //进行数据的分类设定
                    date.setTime(System.currentTimeMillis());
                    int minute = date.getMinutes();
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
                                checkMinuteAndCache(MainActivity.MEASURE_TYPE_XUEYANG, minute);
                                checkMinuteAndCache(MainActivity.MEASURE_TYPE_MAIBO, minute);
                                //进行血氧的结果解析
                                float result = UtilClass.valueOfHexString(data.substring(0, 2));
                                temp = measureDataList.get(MainActivity.MEASURE_TYPE_XUEYANG);
                                if (result > 0 && result < 100 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_XUEYANG);
                                }
                                //进行脉搏的结果解析
                                result = UtilClass.valueOfHexString(data.substring(2, 4));
                                temp = measureDataList.get(MainActivity.MEASURE_TYPE_MAIBO);
                                if (result > 0 && result < 255 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_MAIBO);
                                }
                            }
                            break;
                        }
                        case 2: {
                            //心电
                            if (data.length() == 4) {
                                Log.d("workItem", "handleMessage: 心电");
                                long now = Calendar.getInstance().getTimeInMillis();
                                if (now - last >= 200) {
                                    checkMinuteAndCache(MainActivity.MEASURE_TYPE_XINDIAN, minute);
                                    int result = UtilClass.valueOfHexString(data);
                                    temp = measureDataList.get(MainActivity.MEASURE_TYPE_XINDIAN);
                                    myAdapter.heartWavesView.drawNextPoint(result);
                                    if (compareData(temp, (float) (result))) {
                                        myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_XINDIAN);
                                    }
                                    last = now;
                                }
                            }
                            break;
                        }
                        case 3: {
                            //血糖
                            if (data.length() == 12) {
                                Log.d("workItem", "handleMessage: 血糖");
                                checkMinuteAndCache(MainActivity.MEASURE_TYPE_XUETANG, minute);
                                float result = UtilClass.valueOfHexString(
                                        data.substring(10, 12)) / 10.0f;
                                temp = measureDataList.get(MainActivity.MEASURE_TYPE_XUETANG);
                                if (result > 0 && result < 300 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_XUETANG);
                                }
                            }
                            break;
                        }
                        case 4: {
                            //体温
                            if (data.length() == 4) {
                                Log.d("workItem", "handleMessage: 体温");
                                checkMinuteAndCache(MainActivity.MEASURE_TYPE_TIWEN, minute);
                                float result = UtilClass.valueOfHexString(data) / 100.0f;
                                temp = measureDataList.get(MainActivity.MEASURE_TYPE_TIWEN);
                                if (compareData(temp, result)) {
                                    //修改各项
                                    myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_TIWEN);
                                }
                            }
                            break;
                        }
                        case 5: {
                            //粉尘
                            if (data.length() == 8) {
                                Log.d("workItem", "handleMessage: 粉尘");
                                checkMinuteAndCache(MainActivity.MEASURE_TYPE_FENCHEN, minute);
                                float result = UtilClass.valueOfHexString(data) / 100.0f;
                                temp = measureDataList.get(MainActivity.MEASURE_TYPE_FENCHEN);
                                if (result > 0 && result < 800 && compareData(temp, result)) {
                                    myAdapter.notifyItemChanged(MainActivity.MEASURE_TYPE_FENCHEN);
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
        if (database.isOpen()
                && minute % MainActivity.CACHE_TIME_LENGTH == 0
                && !myDataBase.checkOneMeasureDataCache(database,
                type, date)) {
            myDataBase.addOneMeasureData(database,
                    measureDataList.get(type), type, date);
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
        getContext().unregisterReceiver(receiver);
        isTryingConnecting = false;
        initConnect();
        super.onDestroyView();
    }
}