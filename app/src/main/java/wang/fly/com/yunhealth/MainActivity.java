package wang.fly.com.yunhealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.smssdk.SMSSDK;
import wang.fly.com.yunhealth.Fragments.DataFragment;
import wang.fly.com.yunhealth.Fragments.DoctorsFragment;
import wang.fly.com.yunhealth.Fragments.HomeFragment;
import wang.fly.com.yunhealth.Fragments.MeasureFragment;
import wang.fly.com.yunhealth.Fragments.MineFragment;
import wang.fly.com.yunhealth.LoginAndSign.LoginActivity;

import static android.Manifest.permission_group.SMS;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        color = getResources().getColor(R.color.lightseagreen);
        initDependencies();
        findView();
        setEvent();
        viewPager.setCurrentItem(2);
        setTab(2);
        checkIsLogined();
    }
    private void checkIsLogined() {
        SharedPreferences shared = getSharedPreferences("LoginState", MODE_PRIVATE);
        boolean isLogined = shared.getBoolean("loginRememberState", false);
        if (!isLogined) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
        measure_tv.setTextColor(getResources().getColor(R.color.lightgrey));
        data_tv.setTextColor(getResources().getColor(R.color.lightgrey));
        home_tv.setTextColor(getResources().getColor(R.color.lightgrey));
        doctor_tv.setTextColor(getResources().getColor(R.color.lightgrey));
        mine_tv.setTextColor(getResources().getColor(R.color.lightgrey));

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
