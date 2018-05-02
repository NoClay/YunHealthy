package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.CompanyRetData;

import static indi.noclay.cloudhealth.util.ViewUtils.hideView;
import static indi.noclay.cloudhealth.util.ViewUtils.showView;

/**
 * Created by NoClay on 2018/5/2.
 */

public class SearchCompanyCard extends BaseCard {
    private TextView name;
    private TextView phone;
    private TextView address;
    private CompanyRetData.DrugFactory mFactory;

    public SearchCompanyCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof CompanyRetData.DrugFactory){
            mFactory = (CompanyRetData.DrugFactory) object;
            name.setText(mFactory.getFactoryName());
            if (TextUtils.isEmpty(mFactory.getLinkPhone())){
                hideView(phone);
            }else{
                showView(phone);
            }
            if (TextUtils.isEmpty(mFactory.getAddr())){
                hideView(address);
            }else {
                showView(address);
            }
            phone.setText(mFactory.getLinkPhone());
            address.setText(mFactory.getAddr());
        }
    }

    @Override
    public void initView(View itemView) {
        name = (TextView) itemView.findViewById(R.id.name);
        phone = (TextView) itemView.findViewById(R.id.phone);
        address = (TextView) itemView.findViewById(R.id.address);
    }
}
