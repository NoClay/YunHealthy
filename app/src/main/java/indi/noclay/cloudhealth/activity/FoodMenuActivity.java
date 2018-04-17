package indi.noclay.cloudhealth.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.fragment.FoodListFragment;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.TabLayoutViewPagerAdapter;

public class FoodMenuActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mInfoTitle;
    private ImageView mBack;
    private TabLayout mTabLayout;
    private ImageView mTabSetBt;
    private ViewPager mViewPager;
    private List<Fragment> mPages;
    TabLayoutViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        initView();
        initActionBar();
        getMenuData();
    }

    private void getMenuData() {
        List<String> subCategory = getMenuDataFromLocal();
    }

    private List<String> getMenuDataFromLocal() {
        return null;
    }

    private void initMenuByData(List<String> subCategory) {
        mPages = new ArrayList<>();
        adapter = new TabLayoutViewPagerAdapter(getSupportFragmentManager(), mPages, this);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < 11; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(ConstantsConfig.TAB_MENU[i % ConstantsConfig.TAB_MENU.length]));
            Bundle bundle = new Bundle();
            bundle.putString("title", ConstantsConfig.TAB_MENU[i % ConstantsConfig.TAB_MENU.length]);
            adapter.addTab(FoodListFragment.class, bundle, ConstantsConfig.TAB_MENU[i % ConstantsConfig.TAB_MENU.length], i, null);
        }
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initActionBar() {
        mInfoTitle.setText(R.string.title_healthy_food_menu);
        mBack.setOnClickListener(this);
    }

    private void initView() {
        mInfoTitle = (TextView) findViewById(R.id.info_title);
        mBack = (ImageView) findViewById(R.id.back);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabSetBt = (ImageView) findViewById(R.id.tabSetBt);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabSetBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
            case R.id.tabSetBt:{
                break;
            }
        }
    }
}
