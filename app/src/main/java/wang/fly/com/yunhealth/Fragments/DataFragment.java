package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.R;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class DataFragment extends Fragment implements View.OnClickListener{
    private ViewPager viewPager;
    private List<Fragment> datas;
    private FragmentPagerAdapter mAdapter;
    private View tabLine;
    private TextView mDynamic_tv,mData_tv,mReport_tv,mMedical_tv;
    private int mCurrentPageIndex,myScreen;
    private int colorUnselected,colorSelected;
    private Context context;
    private View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(v == null) {
            v = inflater.inflate(R.layout.fragment_data, container, false);
            context = getContext();
            colorUnselected = getResources().getColor(R.color.black);
            colorSelected = getResources().getColor(R.color.lightSeaGreen);
            tabLine = v.findViewById(R.id.data_line);
            initTabView();
            findView(v);
            viewPager.setCurrentItem(0);
            setTab(0);
        }
        return v;
    }


    private void findView(View v) {
        viewPager = (ViewPager) v.findViewById(R.id.data_viewPager);
        mDynamic_tv = (TextView) v.findViewById(R.id.data_dynamic);
        mData_tv = (TextView) v.findViewById(R.id.data_data);
        mReport_tv = (TextView) v.findViewById(R.id.data_report);
        mMedical_tv = (TextView) v.findViewById(R.id.data_medicate);
        mDynamic_tv.setOnClickListener(this);
        mData_tv.setOnClickListener(this);
        mReport_tv.setOnClickListener(this);
        mMedical_tv.setOnClickListener(this);

        DataDynamicFragment dataDynamicFragment = new DataDynamicFragment();
        DataDataFragment dataDataFragment = new DataDataFragment();
        DataReportFragment dataReportFragment = new DataReportFragment();
        DataMedicalFragment dataMedicalFragment = new DataMedicalFragment();

        datas = new ArrayList<>();
        datas.add(dataDynamicFragment);
        datas.add(dataDataFragment);
        datas.add(dataReportFragment);
        datas.add(dataMedicalFragment);
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return datas.get(position);
            }

            @Override
            public int getCount() {
                return datas.size();
            }
        };

        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tabLine
                        .getLayoutParams();

                if (mCurrentPageIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (positionOffset * myScreen + mCurrentPageIndex
                            * myScreen);
                } else if (mCurrentPageIndex == 1 && position == 0)// 1->0
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * myScreen + (positionOffset - 1)
                            * myScreen);
                } else if (mCurrentPageIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * myScreen + positionOffset
                            * myScreen);
                } else if (mCurrentPageIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * myScreen + (positionOffset - 1)
                            * myScreen);
                }else if(mCurrentPageIndex == 2&&position == 2){    //2->3
                    lp.leftMargin = (int) (mCurrentPageIndex * myScreen + positionOffset
                            * myScreen);
                }else if(mCurrentPageIndex == 3&&position == 2) {     //3->2
                    lp.leftMargin = (int) (mCurrentPageIndex * myScreen + (positionOffset - 1)
                            * myScreen);
                }

                tabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetView();
                setTab(position);
                mCurrentPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void resetView(){
        mDynamic_tv.setTextColor(colorUnselected);
        mData_tv.setTextColor(colorUnselected);
        mReport_tv.setTextColor(colorUnselected);
        mMedical_tv.setTextColor(colorUnselected);
    }
    private void initTabView() {
        //获取屏幕的参数，并将这些参数存储到outMetrics中
        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        myScreen = outMetrics.widthPixels / 4;
        ViewGroup.LayoutParams lp = tabLine.getLayoutParams();
        lp.width = myScreen;
        tabLine.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        resetView();
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tabLine
                .getLayoutParams();
        switch (v.getId()){
            case R.id.data_dynamic:
                lp.leftMargin = 0;
                setTab(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.data_data:
                lp.leftMargin = myScreen;
                setTab(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.data_report:
                lp.leftMargin = 2*myScreen;
                setTab(2);
                viewPager.setCurrentItem(2);
                break;
            case R.id.data_medicate:
                lp.leftMargin = 3*myScreen;
                setTab(3);
                viewPager.setCurrentItem(3);
                break;
            default:break;
        }
    }

    private void setTab(int i){
        switch (i){
            case 0:
                mDynamic_tv.setTextColor(colorSelected);
                break;
            case 1:
                mData_tv.setTextColor(colorSelected);
                break;
            case 2:
                mReport_tv.setTextColor(colorSelected);
                break;
            case 3:
                mMedical_tv.setTextColor(colorSelected);
                break;
            default:break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(v != null){
            ((ViewGroup)v.getParent()).removeView(v);
        }
    }
}
