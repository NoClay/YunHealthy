package indi.noclay.cloudhealth.viewholder;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.FoodKind;
import indi.noclay.cloudhealth.database.FoodKindTableHelper;


/**
 * Created by clay on 2018/4/18.
 */

public class FoodMenuCard extends BaseCard implements View.OnClickListener{

    private FoodKind mFoodKind;
    private TextView checkedTextView;
    public FoodMenuCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
        initView(itemView);
    }

    private void initView(View itemView) {
        checkedTextView = (TextView) itemView.findViewById(R.id.foodMenuName);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof FoodKind){
            mFoodKind = (FoodKind) object;
            setTextColor(checkedTextView, mFoodKind.isShow());
            checkedTextView.setText(mFoodKind.getFoodKindName());
            checkedTextView.setOnClickListener(this);
        }
    }

    private Context getContext(){
        if (mActivity != null){
            return mActivity;
        }
        if (mFragment != null){
            return mFragment.getContext();
        }
        return null;
    }

    private void setTextColor(TextView checkedTextView, boolean show) {
        Log.d(TAG, "setTextColor: context = " + getContext());
        if (getContext() == null){
            return;
        }
        if (show){
            checkedTextView.setTextColor(getContext().getResources().getColor(R.color.lightSeaGreen));
        }else{
            checkedTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.foodMenuName){
            Log.d(TAG, "onClick: ");
            toggle();
        }
    }

    private void toggle() {
        boolean newState = !mFoodKind.isShow();
        setTextColor(checkedTextView, newState);
        mFoodKind.setShow(newState);
        FoodKindTableHelper.updateFoodKind(mFoodKind);
    }
}
