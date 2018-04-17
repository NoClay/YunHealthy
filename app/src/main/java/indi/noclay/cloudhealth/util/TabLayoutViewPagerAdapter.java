package indi.noclay.cloudhealth.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noclay on 2017/4/15.
 */

public class TabLayoutViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mPages;
    private ArrayList<TabSpec> mTabs = new ArrayList<>();
    private Context mContext;

    public TabLayoutViewPagerAdapter(FragmentManager fm,
                                     List<Fragment> mPages, Context context) {
        super(fm);
        this.mPages = mPages;
        this.mContext = context;
    }

    public void addTab(final Class<? extends Fragment> cls, final Bundle args, final String name, final int position, final Fragment parent) {
        addTab(new TabSpec(name, cls, args, position, parent));
    }

    public void addTab(final TabSpec spec) {
        final Fragment fragment = Fragment.instantiate(mContext, spec.cls.getName());
        fragment.setArguments(spec.args);
        fragment.setTargetFragment(spec.mParent, 0);
        mTabs.add(spec);
        mPages.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        if (mPages != null){
            return mPages.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mTabs != null && mTabs.size() >= 0){
            return mTabs.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTabs != null && mTabs.size() >= 0){
            return mTabs.get(position).name;
        }
        return super.getPageTitle(position);
    }
}
