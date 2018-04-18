package indi.noclay.cloudhealth.viewholder;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by clay on 2018/4/18.
 */

public abstract class BaseCard extends RecyclerView.ViewHolder{
    protected final String TAG = this.getClass().getSimpleName();

    protected int mCardIndex;

    protected  int mRealIndex;

    protected Handler mHandler;

    protected Activity mActivity;

    protected Fragment mFragment;

    public BaseCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView);
        this.mHandler = mHandler;
        this.mActivity = mActivity;
        this.mFragment = mFragment;
    }

    public String getTAG() {
        return TAG;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public Fragment getmFragment() {
        return mFragment;
    }

    public void setmFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    public int getmCardIndex() {
        return mCardIndex;
    }

    public void setmCardIndex(int mCardIndex) {
        this.mCardIndex = mCardIndex;
    }

    public int getmRealIndex() {
        return mRealIndex;
    }

    public void setmRealIndex(int mRealIndex) {
        this.mRealIndex = mRealIndex;
    }

    public abstract void initData(Object object);
}
