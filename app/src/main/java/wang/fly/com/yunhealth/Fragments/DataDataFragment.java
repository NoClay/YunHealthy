package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import wang.fly.com.yunhealth.Adapter.RecycleAdapterForData;
import wang.fly.com.yunhealth.Activity.InfoActivity;
import wang.fly.com.yunhealth.R;

/**
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataDataFragment extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private RecycleAdapterForData myAdapter;
    String[] titleArrays = {"血氧", "脉搏", "心电", "体温",
            "粉尘浓度", "血糖（待定）", "脑电（待定）", "血压（待定）"};
    int[] idArrays = {
            R.drawable.bloodoxygen,
            R.drawable.file_icon_ecg,
            R.drawable.home_page_ic_ecg,
            R.drawable.home_icon_temperature,
            R.drawable.dirty,
            R.drawable.ft_ic_tanghuaxuehong,
            R.drawable.icon_toulu,
            R.drawable.home_icon_pressure
    };
    int[] colorArrays = {R.color.white, R.color.bg2, R.color.bg3, R.color.bg4, R.color.bg5, R.color.bg6,
            R.color.bg7, R.color.bg8};
    boolean[] stateArrays = {
            false,false,false,false,false,false,false,false
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data_data,container,false);
        this.context = getContext();
        initView(v);
        return v;
    }

    private void initView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.data_data_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
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
                        myAdapter = new RecycleAdapterForData(maps,
                                R.layout.item_measure, getContext());
                        recyclerView.setAdapter(myAdapter);
                        myAdapter.setOnItemClickListener(new RecycleAdapterForData.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                switch (position){
                                    case 6:{
                                        Toast.makeText(context, "暂无更多", Toast.LENGTH_SHORT).show();
                                    }
                                    case 7:{
                                        Toast.makeText(context, "暂无更多", Toast.LENGTH_SHORT).show();
                                    }
                                    default:{
                                        Intent intent = new Intent(context, InfoActivity.class);
                                        intent.putExtra("name", titleArrays[position]);
                                        intent.putExtra("position", position);
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                });
    }
}
