package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.MeasureXinDian;

/**
 * Created by NoClay on 2018/5/9.
 */

public class FileCacheListCard extends BaseCard {
    private TextView fileNameShow;
    private TextView fileSizeShow;
    private MeasureXinDian mFileCacheListItem;

    public FileCacheListCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof MeasureXinDian){
            mFileCacheListItem = (MeasureXinDian) object;
            fileNameShow.setText(mFileCacheListItem.getFileName());
            fileSizeShow.setText(mFileCacheListItem.getFileLength());
        }
    }

    @Override
    public void initView(View itemView) {

        fileNameShow = (TextView) itemView.findViewById(R.id.fileNameShow);
        fileSizeShow = (TextView) itemView.findViewById(R.id.fileSizeShow);
    }
}
