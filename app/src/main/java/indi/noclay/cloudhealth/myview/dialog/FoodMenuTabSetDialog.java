package indi.noclay.cloudhealth.myview.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.database.FoodKind;
import indi.noclay.cloudhealth.database.FoodKindTableHelper;
import indi.noclay.cloudhealth.database.MenuInfo;

/**
 * Created by clay on 2018/4/18.
 */

public class FoodMenuTabSetDialog extends ShowAtDialog implements View.OnClickListener{
    private RelativeLayout mHeaderLayer;
    private ImageView mTabSetBt;
    private RecyclerView mFoodKindList;
    private List<Object> mDatas;
    private RecyclerViewAdapterNormal adapterNormal;
    private static final String TAG = "FoodMenuTabSetDialog";

    public FoodMenuTabSetDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_DialogNoFullScreen);
    }

    public FoodMenuTabSetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FoodMenuTabSetDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void initData(){
        List<FoodKind> foodKinds = FoodKindTableHelper.getFoodKindFromLocal();
        if (foodKinds != null && foodKinds.size() > 0){
            mDatas = new ArrayList<>();
            for (int i = 0; i < foodKinds.size(); i++) {
                mDatas.add(foodKinds.get(i));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_food_menu_tab_setting);
        initView();
        setCanceledOnTouchOutside(false);
        initData();
        adapterNormal = new RecyclerViewAdapterNormal();
        adapterNormal.setDatas(mDatas);
        adapterNormal.setmActivity(getOwnerActivity());
        mFoodKindList.setAdapter(adapterNormal);
        mFoodKindList.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        adapterNormal.notifyDataSetChanged();
        Log.d(TAG, "onCreate: viewType = " + adapterNormal.getItemViewType(0));
        Log.d(TAG, "onCreate: itemCount = " + adapterNormal.getItemCount());

    }

    private void initView() {
        mHeaderLayer = (RelativeLayout) findViewById(R.id.headerLayer);
        mTabSetBt = (ImageView) findViewById(R.id.tabSetBt);
        mFoodKindList = (RecyclerView) findViewById(R.id.foodKindList);
        mTabSetBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tabSetBt:{
                this.dismiss();
                break;
            }
        }
    }
}
