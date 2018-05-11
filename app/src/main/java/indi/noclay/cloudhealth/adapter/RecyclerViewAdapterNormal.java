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
import indi.noclay.cloudhealth.card.FileCacheListCard;
import indi.noclay.cloudhealth.card.FoodDetailStepCard;
import indi.noclay.cloudhealth.card.FoodMenuCard;
import indi.noclay.cloudhealth.card.FoodShowItemCard;
import indi.noclay.cloudhealth.card.NewsCard;
import indi.noclay.cloudhealth.card.SearchCompanyCard;
import indi.noclay.cloudhealth.card.SearchIllnessCard;
import indi.noclay.cloudhealth.card.SearchMedicineCard;
import indi.noclay.cloudhealth.card.SearchResultTagCard;

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
    public static final int SEARCH_MEDICINE_CARD = BASE_CARD + 4;
    public static final int SEARCH_COMPANY_CARD = BASE_CARD + 5;
    public static final int SEARCH_ILLNESS_CARD = BASE_CARD + 6;
    public static final int SEARCH_RESULT_TAG_CARD = BASE_CARD + 7;
    public static final int FILE_CACHE_LIST_CARD = BASE_CARD + 8;
    public static final int NEWS_CARD = BASE_CARD + 9;
    public Context mContext;
    public Activity mActivity;
    public Fragment mFragment;
    public OnItemClickListener onItemClickListener;
    public OnItemLongClickListener onItemLongClickListener;

    public RecyclerViewAdapterNormal(List<Object> datas, Handler handler, Context context, Activity activity, Fragment fragment) {
        this.datas = datas;
        mHandler = handler;
        mContext = context;
        mActivity = activity;
        mFragment = fragment;
    }

    public RecyclerViewAdapterNormal() {
        //需要自己设置，兼容之前的用法
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(Object o, int position, int layoutPosition);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(Object o, int position, int layoutPosition);
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
            case SEARCH_MEDICINE_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_medicine_search_result, parent, false);
                baseCard = new SearchMedicineCard(itemView, mHandler, mActivity, mFragment);
                break;
            case SEARCH_COMPANY_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_company_search_result, parent, false);
                baseCard = new SearchCompanyCard(itemView, mHandler, mActivity, mFragment);
                break;
            case SEARCH_ILLNESS_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_normal_illness_search_result, parent, false);
                baseCard = new SearchIllnessCard(itemView, mHandler, mActivity, mFragment);
                break;
            case SEARCH_RESULT_TAG_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_tag, parent, false);
                baseCard = new SearchResultTagCard(itemView, mHandler, mActivity, mFragment);
                break;
            case FILE_CACHE_LIST_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_file_cache_list, parent, false);
                baseCard = new FileCacheListCard(itemView, mHandler, mActivity, mFragment);
                break;
            case NEWS_CARD:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                baseCard = new NewsCard(itemView, mHandler, mActivity, mFragment);
            default:
        }
        return baseCard;
    }

    @Override
    public void onBindViewHolder(final BaseCard holder, int position) {
        if (datas != null && position < datas.size()){
            holder.initData(datas.get(position));
            final int pos = position;
            if (onItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(datas.get(pos), pos, holder.getLayoutPosition());
                    }
                });
            }
            if (onItemLongClickListener != null){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onItemLongClickListener.onItemLongClick(datas.get(pos), pos, holder.getLayoutPosition());
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
            case "Drug":
                viewType = SEARCH_MEDICINE_CARD;
                break;
            case "DrugFactory":
                viewType = SEARCH_COMPANY_CARD;
                break;
            case "Illness":
                viewType = SEARCH_ILLNESS_CARD;
                break;
            case "TagData":
                viewType = SEARCH_RESULT_TAG_CARD;
                break;
            case "FileCacheListItem":
                viewType = FILE_CACHE_LIST_CARD;
                break;
            case "NewsData":
                viewType = NEWS_CARD;
                break;
        }
        return viewType;
    }
}
