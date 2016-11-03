package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Context context;
    private MyAdapter myAdapter;
    private List<Map<String,Object>> datas;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.homefragment_layout,container,false);
        context = getContext();
        datas = new ArrayList<>();
        findView(v);
        setEvent();
        return v;
    }

    private void setEvent() {

    }

    public void getData(){
        Observable.create(new Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String[] text = {getResources().getString(R.string.home_recipe),
                            getResources().getString(R.string.home_news),
                            getResources().getString(R.string.home_yao),
                            getResources().getString(R.string.home_report)};
                    int[] id = {R.drawable.share_invite_mcloud, R.drawable.share_invite_shortmessage
                            , R.drawable.share_invite_wechat, R.drawable.share_invite_wechatmoments};
                    for (int i = 0; i < 4; i++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("text", text[i]);
                        map.put("id", id[i]);
                        datas.add(map);
                    }
                    subscriber.onNext("OK");
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                myAdapter = new MyAdapter(datas);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
            }
        });
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.home_recycle);
        mLinearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //如果确定每个子item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        getData();
    }
}
