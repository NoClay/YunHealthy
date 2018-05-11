package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.NewsData;

/**
 * Created by NoClay on 2018/5/11.
 *
 * @Author NoClay
 * @Date 2018/5/11
 */
public class NewsCard extends BaseCard {
    private TextView mDateShow;
    private TextView mTitleShow;
    private TextView mContentShow;
    private NewsData mNewsData;

    public NewsCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof NewsData){
            mNewsData = (NewsData) object;
            mDateShow.setText(mNewsData.getDate());
            mTitleShow.setText(mNewsData.getTitle());
            mContentShow.setText(mNewsData.getContent());
        }
    }

    @Override
    public void initView(View itemView) {
        mDateShow = (TextView) itemView.findViewById(R.id.dateShow);
        mTitleShow = (TextView) itemView.findViewById(R.id.titleShow);
        mContentShow = (TextView) itemView.findViewById(R.id.contentShow);
    }
}
