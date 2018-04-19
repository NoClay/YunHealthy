package indi.noclay.cloudhealth.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.database.FoodShowItem;
import indi.noclay.cloudhealth.util.InternetUrlManager;
import indi.noclay.cloudhealth.util.YunHealthyLoading;

/**
 * Created by clay on 2018/4/17.
 */

public class FoodListFragment extends Fragment implements RecyclerViewAdapterNormal.OnItemClickListener{

    View mView;
    RecyclerViewAdapterNormal adapterNormal;
    RecyclerView mFoodList;
    String mTitle;
    List<Object> mDatas = new ArrayList<>();

    public static final int LOAD_SUCCESS = 1;
    public static final int LOAD_EMPITY = 0;
    public static final int LOAD_FAILED = -1;

    private String mNextPage = null;
    private boolean mHasNext = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    private void handleArguments() {
        if (getArguments() != null){
            mTitle = getArguments().getString("title");
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_food_list, container, false);
        initView(mView);
        getData();
        return mView;
    }

    private void getData() {
//        YunHealthyLoading.show(getContext(), mView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url;
                    if (mHasNext && TextUtils.isEmpty(mNextPage)){
                        url = mNextPage;
                    }else{
                        url = InternetUrlManager.getHealthyFoodListURL(mTitle);
                    }
                    Document document = Jsoup.connect(url).get();
                    Elements elements = document.select("#container > div");
                    for (int i = 0; i < elements.size(); i++) {
                        FoodShowItem temp = new FoodShowItem();
                        temp.setFoodImageUrl(elements.get(i).select("a > img").get(0).attr("src"));
                        temp.setFoodDetailUrl(elements.get(i).select("div > a.cp_name").attr("href"));
                        temp.setFoodName(elements.get(i).select("div > a.cp_name").text());
                        temp.setFoodCategory(elements.get(i).select("div > p.cp_cate").text());
                        temp.setFoodTag(elements.get(i).select("div > div.cp_tag > a").text());
                        mDatas.add(temp);
                    }
                    mNextPage = document.select("#main > div.pagediv.mb35 > div > div > div > div > span:nth-child(5) > a").attr("href");
                    if (TextUtils.isEmpty(mNextPage)){
                        mHasNext = false;
                    }
                    mHandler.sendEmptyMessage(LOAD_SUCCESS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler mHandler = new FoodListHandler();

    private class FoodListHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOAD_SUCCESS:{
                    adapterNormal.setDatas(mDatas);
                    adapterNormal.notifyDataSetChanged();
//                    YunHealthyLoading.dismiss();
                    break;
                }
                case LOAD_FAILED:{
//                    YunHealthyLoading.dismiss();
                    break;
                }
            }
        }
    }
    private void initView(View mView) {
        mFoodList = (RecyclerView) mView.findViewById(R.id.foodList);
        adapterNormal = new RecyclerViewAdapterNormal();
        adapterNormal.setmActivity(getActivity());
        adapterNormal.setDatas(mDatas);
        mFoodList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFoodList.setAdapter(adapterNormal);
        adapterNormal.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(Object o, int position) {
        Toast.makeText(getContext(), "position = " + position + " && " + mTitle, Toast.LENGTH_SHORT).show();
    }
}
