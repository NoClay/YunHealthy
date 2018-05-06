package indi.noclay.cloudhealth.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.TabLayoutViewPagerAdapter;


/*
 * Created by NoClay on 2016/11/2.
 */
public class DataFragment extends Fragment {
    private List<Fragment> datas;
    private TabLayoutViewPagerAdapter adapter;
    private int mCurrentPageIndex, myScreen;
    private int colorUnselected, colorSelected;
    private Context context;
    private View v;
    private TabLayout tabLayout;
    private ViewPager dataViewPager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_data, container, false);
        context = getContext();
        initView();
        initMenuByData();
        return v;
    }

    private void initView() {
        tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        dataViewPager = (ViewPager) v.findViewById(R.id.data_viewPager);
    }

    private void initMenuByData() {
        datas = new ArrayList<>();
        adapter = new TabLayoutViewPagerAdapter(getChildFragmentManager(), datas, this.getContext());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //不显示报告单
        for (int i = 0; i < ConstantsConfig.TAB_DATA_MENU.length - 1; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(ConstantsConfig.TAB_DATA_MENU[i]));
            Bundle bundle = new Bundle();
            bundle.putString("title", ConstantsConfig.TAB_DATA_MENU[i]);
            Class cls = Fragment.class;
            switch (i) {
                case 0:
                    cls = DataDynamicFragment.class;
                    break;
                case 1:
                    cls = DataDataFragment.class;
                    break;
                case 2:
                    cls = DataMedicalFragment.class;
                    break;
                case 3:
                    cls = DataReportFragment.class;
                    break;
            }
            adapter.addTab(cls, bundle, ConstantsConfig.TAB_DATA_MENU[i], i, null);
        }
        dataViewPager.setAdapter(adapter);
        dataViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        tabLayout.setupWithViewPager(dataViewPager);
    }

    public void setCurrentPage(int page) {
        if (page < 0 || page > datas.size()) {
            return;
        }
        dataViewPager.setCurrentItem(page);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (v != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
    }

}
