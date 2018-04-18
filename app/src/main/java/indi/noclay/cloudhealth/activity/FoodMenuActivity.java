package indi.noclay.cloudhealth.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.FoodKind;
import indi.noclay.cloudhealth.database.FoodKindTableHelper;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.fragment.FoodListFragment;
import indi.noclay.cloudhealth.myview.dialog.FoodMenuTabSetDialog;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.InternetUrlManager;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.TabLayoutViewPagerAdapter;

import static indi.noclay.cloudhealth.database.FoodKindTableHelper.getFoodKindFromLocal;
import static indi.noclay.cloudhealth.database.FoodKindTableHelper.insertFoodKind;

public class FoodMenuActivity extends AppCompatActivity implements View.OnClickListener, Dialog.OnDismissListener{

    private TextView mInfoTitle;
    private ImageView mBack;
    private TabLayout mTabLayout;
    private ImageView mTabSetBt;
    private ViewPager mViewPager;
    private View mContentLayer;
    private List<Fragment> mPages;
    private List<FoodKind> subCategory;
    TabLayoutViewPagerAdapter adapter;
    public static final int LOAD_SUCCESS = 1;
    public static final int LOAD_EMPITY = 0;
    public static final int LOAD_FAILED = -1;
    private FoodMenuTabSetDialog menuTabSetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        initView();
        initActionBar();
        getMenuData();
    }

    private void getMenuData() {
        subCategory = getFoodKindFromLocal(true);
        if (subCategory == null || subCategory.size() <= 0){
            getFoodKindInternet();
        }else{
            initMenuByData(subCategory);
        }
    }

    /**
     * 从网络获取
     */
    private void getFoodKindInternet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = SharedPreferenceHelper.getLoginUserId();
                    Document document = Jsoup.connect(InternetUrlManager.HEALTHY_FOOD_MENU_URL).get();
                    Elements elements = document.select("#ddd16 > ul > li");
                    for (Element e : elements) {
                        FoodKind foodKind = new FoodKind();
                        foodKind.setUserId(userId);
                        foodKind.setShow(true);
                        foodKind.setFoodKindName(e.text());
                        subCategory.add(foodKind);
                    }
                    elements = document.select("#ddd4 > ul > li");
                    for (Element e : elements) {
                        FoodKind foodKind = new FoodKind();
                        foodKind.setUserId(userId);
                        foodKind.setShow(true);
                        foodKind.setFoodKindName(e.text());
                        subCategory.add(foodKind);
                    }
                    if (subCategory.size() > 0){
                        insertFoodKind(subCategory);
                        menuHandler.sendEmptyMessage(LOAD_SUCCESS);
                    }else if (subCategory.size() == 0){
                        menuHandler.sendEmptyMessage(LOAD_EMPITY);
                    }
                } catch (IOException e) {
                    menuHandler.sendEmptyMessage(LOAD_FAILED);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    FoodMenuHandler menuHandler = new FoodMenuHandler();

    @Override
    public void onDismiss(DialogInterface dialog) {
        getMenuData();
    }

    public class FoodMenuHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_FAILED:{
                    break;
                }
                case LOAD_SUCCESS:{
                    initMenuByData(subCategory);
                    break;
                }
            }
        }
    }


    private void initMenuByData(List<FoodKind> subCategory) {
        mPages = new ArrayList<>();
        adapter = new TabLayoutViewPagerAdapter(getSupportFragmentManager(), mPages, this);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        int i = 0;
        for (FoodKind temp :
                subCategory) {
            mTabLayout.addTab(mTabLayout.newTab().setText(temp.getFoodKindName()));
            Bundle bundle = new Bundle();
            bundle.putString("title", temp.getFoodKindName());
            adapter.addTab(FoodListFragment.class, bundle, temp.getFoodKindName(), i ++, null);
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
        mContentLayer = findViewById(R.id.contentLayer);
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
                menuTabSetDialog = new FoodMenuTabSetDialog(FoodMenuActivity.this);
                menuTabSetDialog.setOwnerActivity(this);
                menuTabSetDialog.showFullParent(mContentLayer);
                if (menuTabSetDialog != null){
                    menuTabSetDialog.setOnDismissListener(this);
                }
                break;
            }
        }
    }
}
