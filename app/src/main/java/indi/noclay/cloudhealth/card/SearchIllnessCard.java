package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.IllnessRetData;

/**
 * Created by NoClay on 2018/5/2.
 */

public class SearchIllnessCard extends BaseCard {
    private TextView name;
    private TextView kind;
    private TextView summary;
    private IllnessRetData.Illness mIllness;

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getKind() {
        return kind;
    }

    public void setKind(TextView kind) {
        this.kind = kind;
    }

    public TextView getSummary() {
        return summary;
    }

    public void setSummary(TextView summary) {
        this.summary = summary;
    }

    public IllnessRetData.Illness getIllness() {
        return mIllness;
    }

    public void setIllness(IllnessRetData.Illness illness) {
        mIllness = illness;
    }

    public SearchIllnessCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof IllnessRetData.Illness){
            mIllness = (IllnessRetData.Illness) object;
            name.setText(mIllness.getName());
            kind.setText(mIllness.getTypeName());
            summary.setText(mIllness.getSummary());
        }
    }

    @Override
    public void initView(View itemView) {

        name = (TextView) itemView.findViewById(R.id.name);
        kind = (TextView) itemView.findViewById(R.id.kind);
        summary = (TextView) itemView.findViewById(R.id.summary);
    }
}
