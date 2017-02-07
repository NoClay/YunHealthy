package wang.fly.com.yunhealth.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.Adapter.LoadItemAdapterForDynamic;
import wang.fly.com.yunhealth.DataBasePackage.HeightAndWeight;
import wang.fly.com.yunhealth.DataBasePackage.MyDataBase;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.MyViewPackage.AutoLoadMoreRecyclerView;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataDynamicFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        AutoLoadMoreRecyclerView.LoadMoreListener{

    private List<HeightAndWeight> datas;
    private AutoLoadMoreRecyclerView recyclerView;
    private LoadItemAdapterForDynamic adapter;
    private SwipeRefreshLayout downRefresh;
    MyDataBase mMyDataBase;
    SQLiteDatabase mSQLiteDatabase;
    private int pageNum = 0;
    private int skip = 0;
    public static final int PAGE_SIZE = 10;
    private static final String TAG = "DataDynamicFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_dynamic_fragment,container,false);
        initView(v);
        mMyDataBase = new MyDataBase(getApplicationContext(),
                "LocalStore.db", null, MainActivity.DATABASE_VERSION);
        mSQLiteDatabase = mMyDataBase.getReadableDatabase();
        onRefresh();
        return v;
    }

    private void initView(View v) {
        datas = new ArrayList<>();
        recyclerView = (AutoLoadMoreRecyclerView) v.findViewById(R.id.dynamicDataShow);
        downRefresh = (SwipeRefreshLayout) v.findViewById(R.id.refreshLayout);
        downRefresh.setOnRefreshListener(this);
        adapter = new LoadItemAdapterForDynamic(
                R.layout.dynamic_recycle_item,
                datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(this);
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(l);
        recyclerView.setHasFixedSize(true);
    }


    @Override
    public void onRefresh() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                downRefresh.setRefreshing(false);
                //加载数据
                datas.clear();
                skip = 0;
                RefreshPage("HeightWeightCache");
                List<HeightAndWeight> result = getData(skip, "HeightWeightCache");
                if (result == null){
                    recyclerView.notifyMoreFinish(false);
                }else if (result.size() < PAGE_SIZE){
                    datas.addAll(result);
                    recyclerView.notifyMoreFinish(false);
                }else {
                    datas.addAll(result);
                    recyclerView.notifyMoreFinish(true);
                }
                recyclerView.setAutoLoadMoreEnable(true);
            }
        },1000);
    }

    @Override
    public void onLoadMore() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                downRefresh.setRefreshing(false);
                //下拉刷新的时候
                skip ++;
                List<HeightAndWeight> result = getData(skip, "HeightWeightCache");
                if (result == null){
                    recyclerView.notifyMoreFinish(false);
                }else if (result.size() < PAGE_SIZE){
                    datas.addAll(result);
                    recyclerView.notifyMoreFinish(false);
                }else {
                    datas.addAll(result);
                    recyclerView.notifyMoreFinish(true);
                }
            }
        }, 1000);
    }

    void RefreshPage(String tableName){
        String sql = "select count (*) from " + tableName;
        Cursor rec = mSQLiteDatabase.rawQuery(sql, null);
        rec.moveToFirst();
        long recSize = rec.getLong(0);
        rec.close();
        pageNum = (int) ((recSize / PAGE_SIZE) + 1);
    }

    List<HeightAndWeight> getData(int skip, String tableName){
        if (skip < 0){
            return null;
        }
        List<HeightAndWeight> result = new ArrayList<>();
        String sql= "select * from " + tableName +
                " where userId = '" + MainActivity.userId + "' " +
                " order by createTime desc " +
                " Limit "+String.valueOf(PAGE_SIZE)+ " Offset " +String.valueOf(skip * PAGE_SIZE);
        Cursor rec = mSQLiteDatabase.rawQuery(sql, null);
        if (rec.moveToFirst()){
            do {
                SignUserData login = new SignUserData();
                login.setObjectId(MainActivity.userId);
                HeightAndWeight data = new HeightAndWeight(
                        rec.getFloat(rec.getColumnIndex("height")),
                        rec.getFloat(rec.getColumnIndex("weight")),
                        login
                );
                data.setDate(UtilClass.resolveBmobDate(
                        rec.getString(rec.getColumnIndex("createTime")), null)
                );
                Log.d(TAG, "getData: createTime" + UtilClass.valueOfDate(data.getDate(), null));
                result.add(data);
            } while (rec.moveToNext());
        }
        return result;
    }
}
