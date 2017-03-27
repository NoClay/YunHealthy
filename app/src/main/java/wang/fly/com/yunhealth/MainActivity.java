package wang.fly.com.yunhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.smssdk.SMSSDK;
import wang.fly.com.yunhealth.Activity.ActivityCollector;
import wang.fly.com.yunhealth.DataBasePackage.HeightAndWeight;
import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.Fragments.DataFragment;
import wang.fly.com.yunhealth.Fragments.DoctorsFragment;
import wang.fly.com.yunhealth.Fragments.HomeFragment;
import wang.fly.com.yunhealth.Fragments.MeasureFragment;
import wang.fly.com.yunhealth.Fragments.MineFragment;
import wang.fly.com.yunhealth.LoginAndSign.LoginActivity;
import wang.fly.com.yunhealth.MyViewPackage.InputWeightDialog;
import wang.fly.com.yunhealth.Service.UpLoadService;
import wang.fly.com.yunhealth.util.UtilClass;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    InputWeightDialog mInputWeightDialog;
    private ViewPager viewPager;
    private DataFragment dataFragment;
    private DoctorsFragment doctorsFragment;
    private HomeFragment homeFragment;
    private MeasureFragment measureFragment;
    private MineFragment mineFragment;
    private List<Fragment> mDatas;
    private FragmentPagerAdapter mAdapter;
    private TextView measure_tv,data_tv,doctor_tv,home_tv,mine_tv;
    int color;
    //需要连接的蓝牙地址
    //缓存头像使用的目录
    public static final String PATH_ADD = Environment.getExternalStorageDirectory() +
            "/CloudHealthy/userImage/";
    //目前已经可以使用的功能
    public static final String []LABEL_STRING = {
            "血氧",
            "脉搏",
            "心电",
            "体温",
            "粉尘浓度",
            "血糖（待定）",
            "脑电（待定）",
            "血压（待定）"
    };
    //功能对应的标志位
    public static final int MEASURE_TYPE_XUEYANG = 0;
    public static final int MEASURE_TYPE_MAIBO = 1;
    public static final int MEASURE_TYPE_XINDIAN = 2;
    public static final int MEASURE_TYPE_TIWEN = 3;
    public static final int MEASURE_TYPE_FENCHEN = 4;
    public static final int MEASURE_TYPE_XUETANG = 5;
    public static final int MEASURE_TYPE_NAODIAN = 6;
    public static final int MEASURE_TYPE_XUEYA = 7;
    //数据库版本
    public static final int DATABASE_VERSION = 1;
    //进行本地数据库缓存的间隔, 每小时的0, 15, 30 , 45
    public static final int CACHE_TIME_LENGTH = 15;
    public static final int LOAD_CACHE_MINUTE = 5;
    public static final int RECEIVER_TYPE_UPLOAD = 0;
    public static final int REQUEST_LOGIN = 0;
    public static boolean sLoginState = false;
    public static String userId;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        color = getResources().getColor(R.color.lightSeaGreen);
        initDependencies();
        findView();
        setEvent();
        initDir();
        viewPager.setCurrentItem(2);
        setTab(2);
        checkIsLogined();
        initRootView();
    }

    private void initRootView() {
        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //下面的代码主要是为了解决软键盘弹出后遮挡住文字录入PopWindow的问题
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);//获取rootView的可视区域
                int screenHeight = rootView.getHeight();//获取rootView的高度
                int keyboardHeight = screenHeight - r.bottom;
                //用rootView的高度减去rootView的可视区域高度得到软键盘高度
                if (mInputWeightDialog != null && mInputWeightDialog.isShowing()){
                    //调整PopWindow的位置
                    mInputWeightDialog.updateLocation(0, keyboardHeight);
                }
            }
        });
    }

    /**
     * 每次启动后检查体重时候输入
     * @param body
     */
    private void checkWeight(final HeightAndWeight body) {
        mInputWeightDialog = new InputWeightDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.commit_action:{
                        if (mInputWeightDialog.checkData()){
                            SignUserData login = new SignUserData();
                            login.setObjectId(userId);
                            HeightAndWeight data = new HeightAndWeight(
                                    mInputWeightDialog.getInputHeight(),
                                    mInputWeightDialog.getInputWeight(),
                                    login);
                            data.setDate(new Date());
                            MyDataBase myDataBase = new MyDataBase(getApplicationContext(),
                                    "LocalStore.db", null, MainActivity.DATABASE_VERSION);
                            SQLiteDatabase database = myDataBase.getWritableDatabase();
                            myDataBase.insertHeightAndWeight(database,
                                    data,
                                    userId,
                                    new Date());
                            mInputWeightDialog.dismiss();
                        }
                        break;
                    }
                    case R.id.cancel_action:{
                        break;
                    }
                }
                mInputWeightDialog.dismiss();
            }
        });
        if (mInputWeightDialog != null && body != null){
            mInputWeightDialog.setInput(body.getHeight(), body.getWeight());
        }
        mInputWeightDialog.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mInputWeightDialog.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mInputWeightDialog.showAtLocation(findViewById(R.id.main_activity),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }


    /**
     * 初始化定时上传的线程
     */
    private void initUpLoadThread() {
        Intent in = new Intent(this, UpLoadService.class);
        in.putExtra("type", MainActivity.RECEIVER_TYPE_UPLOAD);
        in.putExtra("isFirst", true);
        this.startService(in);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initDir() {
        UtilClass.requestPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        File file = new File(Environment.getExternalStorageDirectory() + "/CloudHealthy/userImage");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void checkIsLogined() {
        SharedPreferences shared = getSharedPreferences("LoginState", MODE_PRIVATE);
        boolean isLogined = shared.getBoolean("loginRememberState", false);
        if (!isLogined) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }else{
            initUpLoadThread();
            sLoginState = true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        userId = getSharedPreferences(
                "LoginState", MODE_PRIVATE).getString("userId", null);
        if (hasFocus && sLoginState){
            //窗口获取焦点并且已经登陆
            MyDataBase myDataBase = new MyDataBase(getApplicationContext(),
                    "LocalStore.db", null, MainActivity.DATABASE_VERSION);
            SQLiteDatabase database = myDataBase.getWritableDatabase();
            HeightAndWeight body = myDataBase.checkTodayWeight(database, new Date(), userId);
            if (body == null){
                //当天没有输入了体重
                body = myDataBase.checkLastWeight(database, userId);
                checkWeight(body);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_LOGIN:{
                initUpLoadThread();
            }
        }
    }

    /**
     * 初始化依赖第三方：
     * Bmob
     * Mob
     */
    private void initDependencies() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("d2e2a48caabc1e5c399b20b2adea85eb")
                .setConnectTimeout(15)
                .setUploadBlockSize(1024 * 1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);

        //初始化Mob
        SMSSDK.initSDK(this, "195be1e7755e2", "5bdd8a14d2e2f5734797443c982b0db4");
    }

    private void setEvent() {
        measure_tv.setOnClickListener(this);
        data_tv.setOnClickListener(this);
        home_tv.setOnClickListener(this);
        doctor_tv.setOnClickListener(this);
        mine_tv.setOnClickListener(this);
    }

    private void findView() {
        measure_tv = (TextView) findViewById(R.id.main_tv01);
        data_tv = (TextView) findViewById(R.id.main_tv02);
        home_tv = (TextView) findViewById(R.id.main_tv03);
        doctor_tv = (TextView) findViewById(R.id.main_tv04);
        mine_tv = (TextView) findViewById(R.id.main_tv05);

        viewPager = (ViewPager) findViewById(R.id.main_vPager);
        measureFragment = new MeasureFragment();
        dataFragment = new DataFragment();
        homeFragment = new HomeFragment();
        doctorsFragment = new DoctorsFragment();
        mineFragment = new MineFragment();

        mDatas = new ArrayList<>();

        mDatas.add(measureFragment);
        mDatas.add(dataFragment);
        mDatas.add(homeFragment);
        mDatas.add(doctorsFragment);
        mDatas.add(mineFragment);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mDatas.get(position);
            }

            @Override
            public int getCount() {
                return mDatas.size();
            }
        };
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetView();
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
    将Tab的文字全部初始化为亮灰色
     */
    public void resetView(){
        measure_tv.setTextColor(getResources().getColor(R.color.lightGray));
        data_tv.setTextColor(getResources().getColor(R.color.lightGray));
        home_tv.setTextColor(getResources().getColor(R.color.lightGray));
        doctor_tv.setTextColor(getResources().getColor(R.color.lightGray));
        mine_tv.setTextColor(getResources().getColor(R.color.lightGray));
    }
    @Override
    public void onClick(View view) {
        resetView();
        switch (view.getId()){
            case R.id.main_tv01:
                setTab(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_tv02:
                setTab(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.main_tv03:
                setTab(2);
                viewPager.setCurrentItem(2);
                break;
            case R.id.main_tv04:
                setTab(3);
                viewPager.setCurrentItem(3);
                break;
            case R.id.main_tv05:
                setTab(4);
                viewPager.setCurrentItem(4);
                break;
            default:break;
        }
    }

    public void setTab(int i){
        switch (i){
            case 0:
                measure_tv.setTextColor(color);
                break;
            case 1:
                data_tv.setTextColor(color);
                break;
            case 2:
                home_tv.setTextColor(color);
                break;
            case 3:
                doctor_tv.setTextColor(color);
                break;
            case 4:
                mine_tv.setTextColor(color);
                break;
            default:break;
        }
    }
}
