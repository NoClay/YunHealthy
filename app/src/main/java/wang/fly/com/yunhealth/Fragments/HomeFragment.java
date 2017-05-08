package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import wang.fly.com.yunhealth.Activity.MedicineActivity;
import wang.fly.com.yunhealth.Activity.NewsActivity;
import wang.fly.com.yunhealth.Adapter.MyAdapter;
import wang.fly.com.yunhealth.Adapter.ResultListViewAdapter;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.ResultDialog;
import wang.fly.com.yunhealth.MyViewPackage.ScanView;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.RecyclerUtils.MyRecyclerViewDivider;
import wang.fly.com.yunhealth.util.ResultMessage;

import static wang.fly.com.yunhealth.Fragments.DataMedicalFragment.NOW_MEDICINE;

/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private Context context;
    private MyAdapter myAdapter;
    private List<Map<String,Object>> datas;
    private ScanView scanView;
    private View homeView;
    private ResultDialog resultDialog;
    private ResultListViewAdapter resultListViewAdapter;
    private List<ResultMessage> resultMessageList;
    private boolean isShowResult = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home,container,false);
        context = getContext();
        findView(homeView);
        scanView.setOnClickListener(this);
        return homeView;
    }

    private void setEvent() {

    }

    public void getData(){
        Observable.create(new Observable.OnSubscribe<List<Map<String,Object>>>(){

            @Override
            public void call(Subscriber<? super List<Map<String, Object>>> subscriber) {
                try {
                    List<Map<String, Object>> datas1 = new ArrayList<>();
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
                        datas1.add(map);
                    }
                    subscriber.onNext(datas1);
                    subscriber.onCompleted();
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Map<String, Object>> maps) {
                        if(maps != null){
                            datas = maps;
                        }
                        myAdapter = new MyAdapter(datas,R.layout.item_home);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.addItemDecoration(new MyRecyclerViewDivider(context));
                        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                            @Override
                            public  void onItemClick(View view,int position) {
                                switch (position){
                                    case 0:
                                        Toast.makeText(context,"点击了"+position,Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Intent intent = new Intent(getContext(), NewsActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        Intent intent1 = new Intent(getContext(), MedicineActivity.class);
                                        intent1.putExtra("type", NOW_MEDICINE);
                                        startActivity(intent1);
                                        break;
                                    case 3:
                                        Toast.makeText(context,"点击了"+position,Toast.LENGTH_SHORT).show();
                                        break;

                                }
                            }
                        });
                    }
                });
    }

    private void findView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.home_recycle_menu);
        //layoutManager用来确定每一个item如何排列摆放，何时展示和隐藏
        gridLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        //如果确定每个子item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        getData();
        scanView = (ScanView) v.findViewById(R.id.scanButton);
        resultMessageList = new ArrayList<>();
        resultListViewAdapter = new ResultListViewAdapter(context,
                R.layout.item_result, resultMessageList);
        for (int i = 0; i < 10; i++) {
            ResultMessage resultMessage = new ResultMessage(i, "测试 " + i, true);
            resultMessageList.add(resultMessage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanButton:{
                //按下扫描按钮的时候，弹出结果框
                if (!isShowResult){
                    scanView.startScan();
                    resultDialog = new ResultDialog(context, this, resultListViewAdapter);
                    resultDialog.showAtLocation(homeView, Gravity.BOTTOM | Gravity.HORIZONTAL_GRAVITY_MASK,
                            0, 0);
                    isShowResult = true;
                }
                break;
            }
            case R.id.close_result_button:{
                isShowResult = false;
                scanView.stopScan();
                resultDialog.dismiss();
                break;
            }
            case R.id.lookAdvice:{
                Toast.makeText(context, "寻求建议", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
