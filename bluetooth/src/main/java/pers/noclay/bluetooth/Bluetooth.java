package pers.noclay.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_OK;
import static pers.noclay.bluetooth.BluetoothConstant.ARG_FROM_CLIENT;
import static pers.noclay.bluetooth.BluetoothConstant.ARG_FROM_SERVER;

/**
 * Created by i-gaolonghai on 2017/8/18.
 */

public class Bluetooth {

    private static BluetoothAdapter sBluetoothAdapter;
    private static List<BluetoothDevice> sBondedBTDevices;
    private static BluetoothWrapper sBluetoothWrapper;
    private static OnPrepareBluetoothListener sPrepareBluetoothListener;
    private static ABSCreateBondStrategy sCustomCreateBondStrategy;
    private static OnCreateBondResultListener sOnCreateBondResultListener;
    private static Timer sTimer;
    private static boolean sHasConnected = false;
    private static AtomicBoolean sIsSupportBluetooth;
    public static final String TAG = "BluetoothLogger";
    private static Context sContext;
    private static ClientThread sClientThread;
    private static ServerThread sServerThread;
    private static ConnectThread sConnectThread;
    private static Handler sHandler;

    public static void setHandler(Handler handler) {
        sHandler = handler;
    }

    public static void setApplicationContext(Context applicationContext){
        sContext = applicationContext;
    }
    public static class Handler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BluetoothConstant.MESSAGE_CONNECT_SUCCESS){
                onConnectSuccess(msg);
                Log.d(TAG, "handleMessage: success");
            }else if (msg.what == BluetoothConstant.MESSAGE_CONNECT_FAILED){
                sHasConnected = false;
            }
        }
    }

    public static void onConnectSuccess(Message msg){
        BluetoothSocket socket = null;
        if (msg.arg1 == ARG_FROM_CLIENT) {
            socket = sClientThread.getBluetoothSocket();
        } else if (msg.arg1 == ARG_FROM_SERVER) {
            socket = sServerThread.getBluetoothSocket();
        }
        sConnectThread = new ConnectThread(socket, sHandler, msg.arg1);
        sConnectThread.start();
        sHasConnected = true;
    }

    /**
     * BluetoothConfig，初始化蓝牙SDK
     * @param config
     */
    public static void initialize(BluetoothConfig config) {
        if (isSupportedBluetooth()) {
            BluetoothWrapper.config(config);
            sBluetoothWrapper = BluetoothWrapper.getInstance();
            registerBluetoothReceiver();
            if (sBluetoothWrapper.isServerEnable()){
                sServerThread = new ServerThread(sHandler, sBluetoothWrapper.getUUID());
                sServerThread.start();
            }
            sHasConnected = false;
            //设置定时器
            sTimer = new Timer(BluetoothConstant.getConnectName());
        }
    }

    /**
     * 外部调用，发送message给已连接设备
     * @param message
     */
    public static void sendMessage(String message) throws BluetoothException {
        if (sHasConnected && sConnectThread != null && sConnectThread.isAlive()){
            sConnectThread.write(message);
        }
        throw new BluetoothException(BluetoothConstant.ERROR_NOT_CONNECTED);
    }

    /**
     * 获取已经配对的设备
     * @return
     */
    public static List<BluetoothDevice> getBondedBTDevices() {
        if (!isSupportedBluetooth()) {
            return null;
        } else {
            Set<BluetoothDevice> temp = sBluetoothAdapter.getBondedDevices();
            if (sBondedBTDevices == null) {
                sBondedBTDevices = new ArrayList<>();
            } else {
                sBondedBTDevices.clear();
            }
            sBondedBTDevices.addAll(temp);
            return sBondedBTDevices;
        }
    }

    public static void startConnect(ABSCreateBondStrategy createBondStrategy,
                                    OnCreateBondResultListener onCreateBondResultListener) {
        if (isSupportedBluetooth()){
            openBluetooth();
            requestPermission();
            openBluetoothDiscoverable();
        }
        if (!isBonded(getTargetAddress())){
            //开始配对
            if (createBondStrategy != null){
                if (onCreateBondResultListener != null){
                    createBondStrategy.setOnCreateBondResultListener(onCreateBondResultListener);
                }
                createBond(createBondStrategy);
            }else{
                createBond(onCreateBondResultListener);
            }
        }
        //开始配对
        if (isBonded(getTargetAddress())){
            Log.d(TAG, "startConnect: 开始连接");
            sClientThread = new ClientThread(
                    sBluetoothWrapper.getTargetAddress(),
                    sBluetoothWrapper.getUUID(),
                    sHandler);
            sClientThread.start();
        }
    }

    public static void startConnect(ABSCreateBondStrategy createBondStrategy){
        startConnect(createBondStrategy, null);
    }

    public static void startConnect(
                                    OnCreateBondResultListener onCreateBondResultListener){
        startConnect(null, onCreateBondResultListener);
    }

    public static void startConnect(){
        startConnect(null, null);
    }

    public static void createBond(ABSCreateBondStrategy createBondStrategy){
        if (isSupportedBluetooth()){
            openBluetooth();
            requestPermission();
            openBluetoothDiscoverable();
        }
        sBluetoothWrapper.getReceiver().setCreateBondStrategy(createBondStrategy);
        if (getTargetAddress() != null){
            try {
                Log.d(TAG, "createBond: target = " + getTargetAddress());
                BluetoothUtils.createBond(getTargetDevice().getClass(), getTargetDevice());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (sOnCreateBondResultListener != null){
            sOnCreateBondResultListener.onCreateBondFail(BluetoothConstant.ERROR_NO_SUCH_MAC_ADDRESS);
        }
    }

    public static void createBond(OnCreateBondResultListener onCreateBondResultListener){
        if (sCustomCreateBondStrategy == null){
            if (sBluetoothWrapper.isAutoPairAble()){
                //自动配对
                createBond(new BluetoothAutoCreateBondStrategy(getTargetDevice(),
                        sBluetoothWrapper.getContext(),
                        sBluetoothWrapper.getReceiver(),
                        onCreateBondResultListener));
            }else{
                createBond(new BluetoothCreateBondStrategy(getTargetDevice(), onCreateBondResultListener));
            }
        }else{
            sCustomCreateBondStrategy.setOnCreateBondResultListener(onCreateBondResultListener);
            createBond(sCustomCreateBondStrategy);
        }
    }

    public static boolean isBonded(String address) {
        getBondedBTDevices();
        for (BluetoothDevice device : sBondedBTDevices) {
            if (device.getAddress().equals(address)){
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportedBluetooth(){
        if (sBluetoothAdapter == null && sIsSupportBluetooth == null) {
            sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        sIsSupportBluetooth = sBluetoothAdapter == null ? new AtomicBoolean(false) : new AtomicBoolean(true);
        if (!sIsSupportBluetooth.get()) {
            if (sPrepareBluetoothListener != null) {
                sPrepareBluetoothListener.onNonSupportable(BluetoothConstant.NON_SUPPORT_BLUETOOTH);
            }
            try {
                throw new BluetoothException(BluetoothConstant.ERROR_BLUETOOTH_NOT_SUPPORTABLE);
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
        }
        return sIsSupportBluetooth.get();
    }


    public static void onResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothConstant.REQUEST_OPEN_BLUETOOTH: {
                if (resultCode == RESULT_OK) {
                    sPrepareBluetoothListener.onOpenBluetooth(true);
                } else {
                    sPrepareBluetoothListener.onOpenBluetooth(false);
                }
                break;
            }
        }
    }

    public static void onDestroy() {
        unregisterBluetoothReceiver();
    }


    public static void startSearch() {
        startSearch(null);
    }

    public static void startSearch(OnFinishDiscoveryDevice onlineDevices) {
        openBluetooth();
        requestPermission();
        openBluetoothDiscoverable();
        if (!isSupportedBluetooth()) {
            return;
        }
        if (sBluetoothAdapter.isDiscovering()) {
            return;
        }
        if (onlineDevices != null){
            sBluetoothWrapper.getReceiver().setOnFinishDiscoveryDevice(onlineDevices);
        }
        sBluetoothAdapter.startDiscovery();
    }


    public static void openBluetooth() {
        if (isSupportedBluetooth() && !isBluetoothEnable()) {
            Context context = sBluetoothWrapper.getContext();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(intent, BluetoothConstant.REQUEST_OPEN_BLUETOOTH);
        }
    }


    public static boolean isBluetoothEnable() {
        if (isSupportedBluetooth()) {
            return sBluetoothAdapter.isEnabled();
        }
        return false;
    }


    public static void requestPermission() {
        Activity activity = (Activity) sBluetoothWrapper.getContext();
        List<String> results = null;
        final String[] permissions = {
                BluetoothConstant.PERMISSION_ACCESS_COARSE_LOCATION,
                BluetoothConstant.PERMISSION_BLUETOOTH,
                BluetoothConstant.PERMISSION_BLUETOOTH_ADMIN,
                BluetoothConstant.PERMISSION_BLUETOOTH_PRIVILEGED
        };
        for (String permission : permissions) {
            BluetoothUtils.requestPermission(activity, permission);
        }
        for (String permission : permissions) {
            if (!BluetoothUtils.hasPermission(activity, permission)) {
                if (results == null) {
                    results = new ArrayList<>();
                }
                results.add(permission);
            }
        }
        if (results != null && sPrepareBluetoothListener != null) {
            String[] temp = new String[results.size()];
            for (int i = 0; i < results.size(); i++) {
                temp[i] = results.get(i);
            }
            sPrepareBluetoothListener.onRequestPermission(false, temp);
        } else if (results == null && sPrepareBluetoothListener != null) {
            sPrepareBluetoothListener.onRequestPermission(true, null);
        }
    }



    private static void registerBluetoothReceiver() {
        BluetoothReceiver receiver = sBluetoothWrapper.getReceiver();
        Context context = sBluetoothWrapper.getApplicationContext();
        IntentFilter filter;
        for (String action : BluetoothConstant.DEFAULT_BROADCAST_ACTIONS) {
            filter = new IntentFilter(action);
            context.registerReceiver(receiver, filter);
        }
    }


    private static void unregisterBluetoothReceiver() {
        sBluetoothWrapper.getApplicationContext()
                .unregisterReceiver(sBluetoothWrapper.getReceiver());
    }


    /**
     * 打开蓝牙的可见性，在我测试的小米6.0上，无法计时关闭可见性
     */
    private static void openBluetoothDiscoverable() {
        //开启蓝牙可见性
        if (sBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, sBluetoothWrapper.getDiscoverableTimeThreshold());
            sBluetoothWrapper.getActivity().startActivity(intent);
            sTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sBluetoothWrapper.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sPrepareBluetoothListener != null){
                                closeBluetoothDiscoverable();
                                sPrepareBluetoothListener.onCloseBluetoothDiscoverable();
                            }
                        }
                    });
                }
            }, sBluetoothWrapper.getDiscoverableTimeThreshold() * 1000);
        }
        if (sPrepareBluetoothListener != null) {
            sPrepareBluetoothListener.onOpenBluetoothDiscoverable();
        }
    }

    /**
     * 强制关闭蓝牙的可见性，亲测可用
     */
    private static void closeBluetoothDiscoverable() {
        //尝试关闭蓝牙可见性
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);
            setDiscoverableTimeout.invoke(sBluetoothAdapter, 1);
            setScanMode.invoke(sBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isHasConnected() {
        return sHasConnected;
    }

    public static BluetoothDevice getTargetDevice(){
        if (isSupportedBluetooth() && getTargetAddress() != null){
            return sBluetoothAdapter.getRemoteDevice(getTargetAddress());
        }
        return null;
    }


    public static OnPrepareBluetoothListener getPrepareBluetoothListener() {
        return sPrepareBluetoothListener;
    }

    public static void setPrepareBluetoothListener(OnPrepareBluetoothListener prepareBluetoothListener) {
        sPrepareBluetoothListener = prepareBluetoothListener;
    }

    public static OnBTDeviceDiscoveryListener getOnDiscoveryBluetoothDevice() {
        if (isSupportedBluetooth()) {
            return sBluetoothWrapper.getReceiver().getOnBTDeviceDiscoveryListener();
        }
        return null;
    }

    public static void setOnDiscoveryBluetoothDevice(OnBTDeviceDiscoveryListener onBTDeviceDiscoveryListener) {
        if (isSupportedBluetooth()) {
            sBluetoothWrapper.getReceiver().setOnBTDeviceDiscoveryListener(onBTDeviceDiscoveryListener);
        }
    }

    public static OnBTBroadCastListener getBroadCastListener() {
        if (isSupportedBluetooth()) {
            return sBluetoothWrapper.getReceiver().getBroadCastListener();
        }
        return null;
    }

    public static void setBroadCastListener(OnBTBroadCastListener broadCastListener) {
        if (isSupportedBluetooth()) {
            sBluetoothWrapper.getReceiver().setBroadCastListener(broadCastListener);
        }
    }


    public static String getTargetAddress() {
        if (isSupportedBluetooth()) {
            return sBluetoothWrapper.getTargetAddress();
        }
        return null;
    }

    public static void setBluetoothWrapper(BluetoothWrapper wrapper) {
        sBluetoothWrapper = wrapper;
    }
    public static UUID getUUID(){
        if (isSupportedBluetooth()){
            return sBluetoothWrapper.getUUID();
        }
        return null;
    }

    public static void setTargetAddress(String targetAddress){
        if (isSupportedBluetooth()) {
            String temp = BluetoothUtils.getMacAddress(targetAddress);
            if (temp != null){
                sBluetoothWrapper.setTargetAddress(BluetoothUtils.getMacAddress(targetAddress));
            }
        }
        try {
            throw new BluetoothException(targetAddress, BluetoothConstant.ERROR_NO_SUCH_MAC_ADDRESS);
        } catch (BluetoothException e) {
            e.printStackTrace();
        }
    }


    public static ABSCreateBondStrategy getCustomCreateBondStrategy() {
        return sCustomCreateBondStrategy;
    }

    public static OnCreateBondResultListener getOnCreateBondResultListener() {
        return sOnCreateBondResultListener;
    }

    public static void setCustomCreateBondStrategy(ABSCreateBondStrategy customCreateBondStrategy) {
        if (isSupportedBluetooth()){
            sCustomCreateBondStrategy = customCreateBondStrategy;
            if (sBluetoothWrapper != null){
                sBluetoothWrapper.getReceiver().setCreateBondStrategy(sCustomCreateBondStrategy);
            }
        }
    }

    public static void setOnCreateBondResultListener(OnCreateBondResultListener onCreateBondResultListener) {
        if (isSupportedBluetooth()){
            sOnCreateBondResultListener = onCreateBondResultListener;
            if (sBluetoothWrapper != null){
                sBluetoothWrapper.getReceiver()
                        .getCreateBondStrategy()
                        .setOnCreateBondResultListener(onCreateBondResultListener);
            }
        }
    }



    public static long getDiscoverableTimeThreshold() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.getDiscoverableTimeThreshold();
        }
        return -1;
    }

    public static Context getApplicationContext() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.getApplicationContext();
        }
        return null;
    }

    public static boolean isHoldLongConnectAble() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.isHoldLongConnectAble();
        }
        return false;
    }

    public static boolean isAutoPairAble() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.isAutoPairAble();
        }
        return false;
    }

    public static String getPairPassword() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.getPairPassword();
        }
        return null;
    }


    public static void setAutoPairAble(boolean autoPairAble) {
        if (sBluetoothWrapper != null){
            sBluetoothWrapper.setAutoPairAble(autoPairAble);
        }
    }

    public static void setContext(Context context) {
        if (sBluetoothWrapper != null){
            sBluetoothWrapper.setContext(context);
        }
    }


    public static void setPairPassword(String pairPassword) {
        if (sBluetoothWrapper != null){
            sBluetoothWrapper.setPairPassword(pairPassword);
        }
    }

    public static long getConnectTimeThreshold() {
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.getConnectTimeThreshold();
        }
        return -1;
    }

    public static void setConnectTimeThreshold(long connectTimeThreshold) {
        if (sBluetoothWrapper != null){
            sBluetoothWrapper.setConnectTimeThreshold(connectTimeThreshold);
        }
    }

    public static void setDiscoverableTimeThreshold(long discoverableTimeThreshold) {
        if (sBluetoothWrapper != null) {
            sBluetoothWrapper.setDiscoverableTimeThreshold(discoverableTimeThreshold);
        }
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        return sBluetoothAdapter;
    }

    public static boolean isServerEnable(){
        if (sBluetoothWrapper != null){
            return sBluetoothWrapper.isServerEnable();
        }
        return false;
    }
}
