package wang.fly.com.yunhealth.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import wang.fly.com.yunhealth.Adapter.RecycleAdapterForDynamic;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.DynamicData;

/**
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataDynamicFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private List<DynamicData> datas;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout downRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_dynamic_fragment,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        datas = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.dynamicDataShow);
        downRefresh = (SwipeRefreshLayout) v.findViewById(R.id.refreshLayout);
        downRefresh.setOnRefreshListener(this);
        RecycleAdapterForDynamic adapter = new RecycleAdapterForDynamic(datas,
                R.layout.dynamic_recycle_item);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(l);
        recyclerView.setHasFixedSize(true);
        initData();
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            DynamicData data = new DynamicData();
            Date date = new Date(System.currentTimeMillis());
            date.setMonth(new Random().nextInt(12));
            data.setDate(date);
            data.setWeight(40 + i * 10, 1.7f);
            datas.add(data);
        }
    }

    @Override
    public void onRefresh() {
        downRefresh.setRefreshing(false);
        //加载数据
    }
}
