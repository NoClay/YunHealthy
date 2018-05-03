package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.TagData;


/**
 * Created by NoClay on 2018/5/3.
 */

public class SearchResultTagCard extends BaseCard {

    private TextView name;
    private TextView content;
    private TagData mTagData;

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getContent() {
        return content;
    }

    public void setContent(TextView content) {
        this.content = content;
    }

    public TagData getTagData() {
        return mTagData;
    }

    public void setTagData(TagData tagData) {
        mTagData = tagData;
    }

    public SearchResultTagCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof TagData){
            mTagData = (TagData) object;
            name.setText(mTagData.getName());
            content.setText(mTagData.getContent());
        }
    }

    @Override
    public void initView(View itemView) {

        name = (TextView) itemView.findViewById(R.id.name);
        content = (TextView) itemView.findViewById(R.id.content);
    }
}
