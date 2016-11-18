package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wang.fly.com.yunhealth.Adapter.MyAdapter;
import wang.fly.com.yunhealth.Adapter.RecycleAdapterForMeasure;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.ShowHeartWaves;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class MeasureFragment extends Fragment {
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private RecycleAdapterForMeasure myAdapter;
    String[] titleArrays = {"血氧\n34>=44", "心电\n442>=232", "体重体脂", "血糖",
            "体温", "粉尘", "脑电（待定）", "血压（待定）"};
    int[] idArrays = {R.drawable.bloodoxygen, R.drawable.heartwaves, R.drawable.weight, R.drawable.bloodsugar,
            R.drawable.temperature, R.drawable.dirty, R.drawable.headwaves, R.drawable.bloodpress};
    int[] colorArrays = {R.color.bg1, R.color.bg2, R.color.bg3, R.color.bg4, R.color.bg5, R.color.bg6,
            R.color.bg7, R.color.bg8};
    boolean[] stateArrays = {
            true,true,false,false,false,false,false,false
    };
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
                    for (int i = 0; i < titleArrays.length; i++) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("text", titleArrays[i]);
                        data.put("id", idArrays[i]);
                        data.put("color", colorArrays[i]);
                        data.put("isDanger", stateArrays[i]);
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
                        myAdapter = new RecycleAdapterForMeasure(maps,
                                R.layout.measure_recycle_item_layout, getContext());
                        recyclerView.setAdapter(myAdapter);
                        myAdapter.setOnItemClickListener(new RecycleAdapterForMeasure.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                switch (position){
                                    case 1:{
                                        //点击心电图的子项
                                        Toast.makeText(context, "打开心电图", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, ShowHeartWaves.class);
                                        context.startActivity(intent);
                                        break;
                                    }
                                    default: Toast.makeText(context, "暂无更多", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
    }
}