package indi.noclay.cloudhealth;

import android.annotation.SuppressLint;
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

import indi.noclay.cloudhealth.database.HeightAndWeight;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.fragment.DataFragment;
import indi.noclay.cloudhealth.fragment.DoctorsFragment;
import indi.noclay.cloudhealth.fragment.HomeFragment;
import indi.noclay.cloudhealth.fragment.MeasureFragment;
import indi.noclay.cloudhealth.fragment.MineFragment;
import indi.noclay.cloudhealth.myview.dialog.InputWeightDialog;
import indi.noclay.cloudhealth.receiver.MedicineAlarmReceiver;
import indi.noclay.cloudhealth.service.SynchronizeDataService;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.TabLayoutViewPagerAdapter;


/**
 * Created by noclay on 2017/4/15.
 */

public class MainActivityCopy extends AppCompatActivity {
    private ViewPager mMainVPager;
    private TabLayout mTabLayout;
    private List<Fragment> mPages;
    InputWeightDialog mInputWeightDialog;
    public static final int PAGE_MEASURE = 0;
    public static final int PAGE_DATA = 1;
    public static final int PAGE_HOME = 2;
    public static final int PAGE_DOCTOR = 3;
    public static final int PAGE_MINE = 4;

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
        initTab();
        mMainVPager.setCurrentItem(2);
    }

    public void setCurrentPage(int page) {
        if (page >= mPages.size() || page < 0) {
            return;
        } else {
            mMainVPager.setCurrentItem(page);
        }
    }

    private void initTab() {
        mPages = new ArrayList<>();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        TabLayoutViewPagerAdapter adapter = new TabLayoutViewPagerAdapter(getSupportFragmentManager(),
                mPages, this);
        for (int i = 0; i < ConstantsConfig.TAB_MENU.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(ConstantsConfig.TAB_MENU[i]));
            switch (i) {
                case 0:adapter.addTab(MeasureFragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
                case 1:adapter.addTab(DataFragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
                case 2:adapter.addTab(HomeFragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
                case 3:adapter.addTab(DoctorsFragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
                case 4:adapter.addTab(MineFragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
                default:adapter.addTab(Fragment.class, null, ConstantsConfig.TAB_MENU[i], i, null);
                    break;
            }
        }
        mMainVPager.setAdapter(adapter);
        mMainVPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        mTabLayout.setupWithViewPager(mMainVPager);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ConstantsConfig.userId = getSharedPreferences(
                "LoginState", MODE_PRIVATE).getString("userId", null);
        if (hasFocus) {
            //窗口获取焦点并且已经登陆

            HeightAndWeight body = LocalDataBase.checkTodayWeight(new Date());
            if (body == null) {
                //当天没有输入了体重
                body = LocalDataBase.checkLastWeight();
                checkWeight(body);
            }
        }
    }

    /**
     * 初始化定时上传的线程
     */
    private void initUpLoadThread() {
        Intent in = new Intent(this, SynchronizeDataService.class);
        in.putExtra("type", ConstantsConfig.RECEIVER_TYPE_UPLOAD);
        in.putExtra("isFirst", true);
        this.startService(in);

    }

    private void startAlarm() {
        Calendar c = Calendar.getInstance();//获取日期对象
        c.setTimeInMillis(System.currentTimeMillis());        //设置Calendar对象
        if (c.get(Calendar.MINUTE) < 30) {
            c.set(Calendar.MINUTE, 30);            //设置闹钟的分钟数
        } else {
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
     *
     * @param body
     */
    @SuppressLint("WrongConstant")
    private void checkWeight(final HeightAndWeight body) {
        mInputWeightDialog = new InputWeightDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.commit_action: {
                        if (mInputWeightDialog.checkData()) {
                            SignUserData login = new SignUserData();
                            login.setObjectId(ConstantsConfig.userId);
                            HeightAndWeight data = new HeightAndWeight(
                                    mInputWeightDialog.getInputHeight(),
                                    mInputWeightDialog.getInputWeight(),
                                    login);
                            data.setDate(new Date());
                            LocalDataBase.insertHeightAndWeight(data, new Date());
                            mInputWeightDialog.dismiss();
                        }
                        break;
                    }
                    case R.id.cancel_action: {
                        break;
                    }
                }
                mInputWeightDialog.dismiss();
            }
        });
        if (mInputWeightDialog != null && body != null) {
            mInputWeightDialog.setInput(body.getHeight(), body.getWeight());
        }
        mInputWeightDialog.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mInputWeightDialog.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mInputWeightDialog.showAtLocation(findViewById(R.id.main_activity),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
}
