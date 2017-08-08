package wang.fly.com.yunhealth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wang.fly.com.yunhealth.DataBasePackage.HeightAndWeight;
import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.Fragments.DataFragment;
import wang.fly.com.yunhealth.Fragments.DoctorsFragment;
import wang.fly.com.yunhealth.Fragments.HomeFragment;
import wang.fly.com.yunhealth.Fragments.MeasureFragment;
import wang.fly.com.yunhealth.Fragments.MineFragment;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.InputWeightDialog;
import wang.fly.com.yunhealth.ReceiverPackage.MedicineAlarmReceiver;
import wang.fly.com.yunhealth.Service.SynchronizeDataService;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.TabLayoutViewPagerAdapter;

import static wang.fly.com.yunhealth.util.MyConstants.userId;

/**
 * Created by noclay on 2017/4/15.
 */

public class MainActivityCopy extends AppCompatActivity {
    private ViewPager mMainVPager;
    private TabLayout mTabLayout;
    private LinearLayout mMainActivity;
    private List<String> mTitles;
    private List<Fragment> mPages;
    InputWeightDialog mInputWeightDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_copy);
        initView();
        initUpLoadThread();
        startAlarm();
    }



    private void initView() {
        mMainVPager = (ViewPager) findViewById(R.id.main_vPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mMainActivity = (LinearLayout) findViewById(R.id.main_activity);
        initTab();
        mMainVPager.setCurrentItem(2);
    }

    private void initTab() {
        mTitles = new ArrayList<>();
        mPages = new ArrayList<>();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String e :
                MyConstants.TAB_MENU) {
            mTitles.add(e);
            mTabLayout.addTab(mTabLayout.newTab().setText(e));
        }
        mPages.add(new MeasureFragment());
        mPages.add(new DataFragment());
        mPages.add(new HomeFragment());
        mPages.add(new DoctorsFragment());
        mPages.add(new MineFragment());
        TabLayoutViewPagerAdapter adapter = new TabLayoutViewPagerAdapter(getSupportFragmentManager(),
                mTitles, mPages);
        mMainVPager.setAdapter(adapter);
        mMainVPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        mTabLayout.setupWithViewPager(mMainVPager);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        userId = getSharedPreferences(
                "LoginState", MODE_PRIVATE).getString("userId", null);
        if (hasFocus){
            //窗口获取焦点并且已经登陆
            MyDataBase myDataBase = new MyDataBase(getApplicationContext(),
                    "LocalStore.db", null, MyConstants.DATABASE_VERSION);
            HeightAndWeight body = myDataBase.checkTodayWeight(new Date(), userId);
            if (body == null){
                //当天没有输入了体重
                body = myDataBase.checkLastWeight(userId);
                checkWeight(body);
            }
        }
    }

    /**
     * 初始化定时上传的线程
     */
    private void initUpLoadThread() {
        Intent in = new Intent(this, SynchronizeDataService.class);
        in.putExtra("type", MyConstants.RECEIVER_TYPE_UPLOAD);
        in.putExtra("isFirst", true);
        this.startService(in);

    }

    private void startAlarm() {
        Calendar c=Calendar.getInstance();//获取日期对象
        c.setTimeInMillis(System.currentTimeMillis());        //设置Calendar对象
        if (c.get(Calendar.MINUTE) < 30){
            c.set(Calendar.MINUTE, 30);            //设置闹钟的分钟数
        }else{
            c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
            c.set(Calendar.MINUTE, 00);
        }
        c.set(Calendar.SECOND, 0);                //设置闹钟的秒数
        c.set(Calendar.MILLISECOND, 0);            //设置闹钟的毫秒数
        Intent intent = new Intent(MainActivityCopy.this, MedicineAlarmReceiver.class);    //创建Intent对象
        intent.putExtra("time", c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        PendingIntent pi = PendingIntent.getBroadcast(MainActivityCopy.this, 0, intent, 0);    //创建PendingIntent
        //alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);        //设置闹钟
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d("clock", "startAlarm: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        alarm.set(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(), pi);        //设置闹钟，当前时间就唤醒
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
                                    "LocalStore.db", null, MyConstants.DATABASE_VERSION);
                            myDataBase.insertHeightAndWeight(
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
}
