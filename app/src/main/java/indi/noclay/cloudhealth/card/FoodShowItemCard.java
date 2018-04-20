package indi.noclay.cloudhealth.card;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.carddata.FoodShowItem;
import indi.noclay.cloudhealth.util.ViewUtils;

/**
 * Created by clay on 2018/4/19.
 */

public class FoodShowItemCard extends BaseCard {

    private ImageView mFoodImage;
    private TextView mFoodName;
    private TextView mFoodCategory;
    private LinearLayout mFoodTagContainer;
    private FoodShowItem item;

    public FoodShowItemCard(View itemView, Handler mHandler, Activity mActivity, Fragment mFragment) {
        super(itemView, mHandler, mActivity, mFragment);
    }

    @Override
    public void initData(Object object) {
        if (object instanceof FoodShowItem){
            item = (FoodShowItem) object;
            Glide.with(getContext()).load(item.getFoodImageUrl())
                    .crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    mFoodImage.setImageDrawable(resource);
                }
            });
            mFoodName.setText(item.getFoodName());
            mFoodCategory.setText(item.getFoodCategory());
            if (TextUtils.isEmpty(item.getFoodTag())){
                ViewUtils.hideView(mFoodTagContainer);
            }else{
                String[] tags = item.getFoodTag().split(" ");
                for (int i = 0; i < tags.length; i++) {
                    mFoodTagContainer.addView(getTextView(tags[i]), i);
                }
            }
        }
    }

    private View getTextView(String tag) {
        View container = LayoutInflater.from(getContext())
                .inflate(R.layout.view_food_list_tag, null, false);
        TextView textView = (TextView) container.findViewById(R.id.foodImageTag);
        textView.setText(tag);
        return container;
    }

    @Override
    public void initView(View itemView) {
        mFoodImage = (ImageView) itemView.findViewById(R.id.foodImage);
        mFoodName = (TextView) itemView.findViewById(R.id.foodName);
        mFoodCategory = (TextView) itemView.findViewById(R.id.foodCategory);
        mFoodTagContainer = (LinearLayout) itemView.findViewById(R.id.foodTagContainer);
    }
}
