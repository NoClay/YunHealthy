package indi.noclay.cloudhealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.card.BaseCard;
import indi.noclay.cloudhealth.card.FoodDetailStepCard;
import indi.noclay.cloudhealth.card.FoodMenuCard;
import indi.noclay.cloudhealth.card.FoodShowItemCard;

/**
 * Created by clay on 2018/4/18.
 */

public class RecyclerViewAdapterNormal extends RecyclerView.Adapter<BaseCard> {
    private static final String TAG = "RecyclerViewAdapterNorm";
    public List<Object> datas;
    public Handler mHandler;
    public static final int BASE_CARD = 1024;
    public static final int FOOD_KIND_CARD = BASE_CARD + 1;
    public static final int FOOD_SHOW_ITEM_CARD = BASE_CARD + 2;
    public static final int FOOD_DETAIL_STEP = BASE_CARD + 3;
    public Context mContext;
    public Activity mActivity;
    public Fragment mFragment;
    public OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(Object o, int position);
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
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

    @Override
    public BaseCard onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
        BaseCard baseCard = null;
        View itemView;
        switch (viewType) {
            case FOOD_KIND_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_food_menu_checkable, parent, false);
                baseCard = new FoodMenuCard(itemView, mHandler, mActivity, mFragment);
                break;
            case FOOD_SHOW_ITEM_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_food_show, parent, false);
                baseCard = new FoodShowItemCard(itemView, mHandler, mActivity, mFragment);
                break;
            case FOOD_DETAIL_STEP:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_food_detail_step, parent, false);
                baseCard = new FoodDetailStepCard(itemView, mHandler, mActivity, mFragment);
                break;
            default:
        }
        return baseCard;
    }

    @Override
    public void onBindViewHolder(BaseCard holder, int position) {
        if (datas != null && position < datas.size()){
            holder.initData(datas.get(position));
            final int pos = position;
            if (onItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(datas.get(pos), pos);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return simpleClassNameToInt(datas.get(position).getClass().getSimpleName());
    }

    public int simpleClassNameToInt(String className) {
        int viewType = BASE_CARD;
        switch (className) {
            case "FoodKind":
                viewType = FOOD_KIND_CARD;
                break;
            case "FoodShowItem":
                viewType = FOOD_SHOW_ITEM_CARD;
                break;
            case "FoodDetailStep":
                viewType = FOOD_DETAIL_STEP;
                break;
        }
        return viewType;
    }
}
