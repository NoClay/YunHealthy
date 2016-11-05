package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wang.fly.com.yunhealth.Adapter.MyAdapter;
import wang.fly.com.yunhealth.R;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class MeasureFragment extends Fragment {
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private MyAdapter myAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.measurefragment_layout,container,false);
        context = getContext();
        findView(v);
        return v;
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.measure_recycleView);
        gridLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        Observable.create(new Observable.OnSubscribe<List<Map<String,Object>>>() {
            @Override
            public void call(Subscriber<? super List<Map<String, Object>>> subscriber) {
                try {
                    List<Map<String, Object>> datas = new ArrayList<>();
                    String[] str = {"心电", "血糖", "血压", "体重体脂", "血氧", "耳温", "血氧动态监测", "尿量"};
                    for (int i = 0; i < str.length; i++) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("text", str[i]);
                        data.put("id", R.drawable.share_invite_mcloud);
                        datas.add(data);
                    }
                    subscriber.onNext(datas);
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Map<String, Object>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Map<String, Object>> maps) {
                myAdapter = new MyAdapter(maps,R.layout.measure_recycle_item_layout);
                recyclerView.setAdapter(myAdapter);
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        switch (position){

                        }
                    }
                });
            }
        });
    }
}
