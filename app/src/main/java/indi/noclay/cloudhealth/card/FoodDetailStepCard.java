package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.FoodDetailStep;
import indi.noclay.cloudhealth.util.ViewUtils;
import indi.noclay.cloudhealth.util.ViewUtils.*;

/**
 * Created by clay on 2018/4/20.
 */

public class FoodDetailStepCard extends BaseCard {
    private TextView mFoodStepText;
    private ImageView mFoodStepImage;
    private FoodDetailStep mFoodDetailStep;

    public FoodDetailStepCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof FoodDetailStep){
            mFoodDetailStep = (FoodDetailStep) object;
            mFoodStepText.setText(mFoodDetailStep.getStepText());
            if (!TextUtils.isEmpty(mFoodDetailStep.getStepImage())){
                ViewUtils.showView(mFoodStepImage);
                Glide.with(getContext()).load(mFoodDetailStep.getStepImage())
                        .crossFade().into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mFoodStepImage.setImageDrawable(resource);
                    }
                });
            }else{
                ViewUtils.hideView(mFoodStepImage);
            }

        }
    }

    @Override
    public void initView(View itemView) {
        mFoodStepText = (TextView) itemView.findViewById(R.id.foodStepText);
        mFoodStepImage = (ImageView) itemView.findViewById(R.id.foodStepImage);
    }
}
