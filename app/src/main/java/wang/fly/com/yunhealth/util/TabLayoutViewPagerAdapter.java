package wang.fly.com.yunhealth.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by noclay on 2017/4/15.
 */

public class TabLayoutViewPagerAdapter extends FragmentPagerAdapter{
    private List<String> mTitles;
    private List<Fragment> mPages;

    public TabLayoutViewPagerAdapter(FragmentManager fm,
                                     List<String> titles,
                                     List<Fragment> mPages) {
        super(fm);
        this.mTitles = titles;
        this.mPages = mPages;
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
        if (mTitles != null){
            return mTitles.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null){
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }
}
