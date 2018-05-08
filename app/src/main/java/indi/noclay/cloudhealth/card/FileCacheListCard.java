package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.FileCacheListItem;

/**
 * Created by NoClay on 2018/5/9.
 */

public class FileCacheListCard extends BaseCard {
    private TextView fileNameShow;
    private TextView fileSizeShow;
    private FileCacheListItem mFileCacheListItem;

    public FileCacheListCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof FileCacheListItem){
            mFileCacheListItem = (FileCacheListItem) object;
            fileNameShow.setText(mFileCacheListItem.getName());
            fileSizeShow.setText(mFileCacheListItem.getLength());
        }
    }

    @Override
    public void initView(View itemView) {

        fileNameShow = (TextView) itemView.findViewById(R.id.fileNameShow);
        fileSizeShow = (TextView) itemView.findViewById(R.id.fileSizeShow);
    }
}
