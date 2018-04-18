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
import indi.noclay.cloudhealth.database.FoodKind;
import indi.noclay.cloudhealth.viewholder.BaseCard;
import indi.noclay.cloudhealth.viewholder.FoodMenuCard;

/**
 * Created by clay on 2018/4/18.
 */

public class RecyclerViewAdapterNormal extends RecyclerView.Adapter<BaseCard> {
    private static final String TAG = "RecyclerViewAdapterNorm";
    public List<Object> datas;
    public Handler mHandler;
    public static final int BASE_CARD = 1024;
    public static final int FOOD_KIND = BASE_CARD + 1;
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
            case FOOD_KIND:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_food_menu_checkable, parent, false);
                baseCard = new FoodMenuCard(itemView, mHandler, mActivity, mFragment);
                break;

            default:
        }
        return baseCard;
    }

    @Override
    public void onBindViewHolder(BaseCard holder, int position) {
        if (datas != null && position < datas.size()){
            holder.initData(datas.get(position));
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
                viewType = FOOD_KIND;
                break;
        }
        return viewType;
    }
}
