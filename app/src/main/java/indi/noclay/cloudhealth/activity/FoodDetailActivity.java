package indi.noclay.cloudhealth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.carddata.FoodDetailStep;
import indi.noclay.cloudhealth.myview.FullLinearLayoutManager;

import static indi.noclay.cloudhealth.util.ViewUtils.hideView;

public class FoodDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mInfoTitle;
    private ImageView mBack;
    private LinearLayout mContainer;
    private String tip;
    private String url;
    private String name;
    private String image;
    private String mainFoodDosing;
    private String otherFoodDosing;
    private String foodStepTitle;
    public static final int LOAD_SUCCESS = 1;
    public static final int LOAD_EMPTY = 0;
    public static final int LOAD_FAILED = -1;
    private static final String TAG = "FoodDetailActivity";
    private ImageView mFoodImage;
    private TextView mFoodStepTitle;
    private RecyclerView mFoodSteps;
    private Context mContext;
    private List<Object> mSteps = new ArrayList<>();
    private RecyclerViewAdapterNormal adapterNormal;
    private LinearLayout mMainDosingLayer;
    private TextView mMainFoodDosing;
    private LinearLayout mOtherDosingLayer;
    private TextView mOtherFoodDosing;
    public static final int STATUS_INIT = 0;
    public static final int STATUS_MAIN_DOSING = 1;
    public static final int STATUS_OTHER_DOSING = 2;
    private LinearLayout mTipsLayer;
    private TextView mTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        initView();
        handleArgument(getIntent());
        getData();
    }

    private void handleArgument(Intent intent) {
        if (intent != null) {
            url = intent.getStringExtra("url");
            image = intent.getStringExtra("image");
            name = intent.getStringExtra("name");
            mInfoTitle.setText(name);
            Glide.with(mContext).load(image).crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    mFoodImage.setImageDrawable(resource);
                }
            });
        }
    }

    private void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(url).get();
                    if (document == null) {
                        mHandler.sendEmptyMessage(LOAD_FAILED);
                        return;
                    }
                    Elements elements = document.select("#main > div.releft > div.recinfo > div.retew.r3.pb25.mb20 > table > tbody > tr");
                    int status = -1;
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; elements != null && i < elements.size(); i++) {
                        Element e = elements.get(i);
                        if (e.text().contains("难度")) {
                            status = STATUS_INIT;
                            builder = new StringBuilder();
                            continue;
                        } else if (e.text().contains("主料")) {
                            status = STATUS_MAIN_DOSING;
                            builder = new StringBuilder();
                            continue;
                        } else if (e.text().contains("辅料")) {
                            status = STATUS_OTHER_DOSING;
                            builder = new StringBuilder();
                            continue;
                        }
                        if (status == STATUS_MAIN_DOSING) {
                            Elements temp = e.select("td");
                            for (int j = 0; temp != null && j < temp.size(); j++) {
                                builder.append(temp.get(j).text())
                                        .append(" ");
                            }
                            mainFoodDosing = builder.toString();
                        } else if (status == STATUS_OTHER_DOSING) {
                            Elements temp = e.select("td");
                            for (int j = 0; temp != null && j < temp.size(); j++) {
                                builder.append(temp.get(j).text())
                                        .append(" ");
                            }
                            otherFoodDosing = builder.toString();
                        }
                    }
                    tip = document.select("#main > div.releft > div.recinfo > div.retew.r3.pb25.mb20 > div.xtieshi > p").text();
                    foodStepTitle = document.select("#main > div.releft > div.recinfo > div.retew.r3.pb25.mb20 > div.step.clearfix > h2").text();
                    elements = document.select("#main > div.releft > div.recinfo > div.retew.r3.pb25.mb20 > div.step.clearfix > div");
                    for (int i = 0; elements != null && i < elements.size(); i++) {
                        FoodDetailStep t = new FoodDetailStep();
                        t.setStepImage(elements.get(i).select("div > a > img").attr("original"));
                        t.setStepText(elements.get(i).select("div > p").text());
                        Log.d(TAG, "run: image = " + t.getStepImage());
                        mSteps.add(t);
                    }
                    mHandler.sendEmptyMessage(LOAD_SUCCESS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler mHandler = new FoodDetailHandler();

    public class FoodDetailHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_SUCCESS: {
                    if (!TextUtils.isEmpty(mainFoodDosing)) {
                        mMainFoodDosing.setText(mainFoodDosing);
                    } else {
                        hideView(mMainDosingLayer);
                    }

                    if (!TextUtils.isEmpty(otherFoodDosing)) {
                        mOtherFoodDosing.setText(otherFoodDosing);
                    } else {
                        hideView(mOtherDosingLayer);
                    }

                    if (!TextUtils.isEmpty(tip)){
                        mTips.setText(tip);
                    }else{
                        hideView(mTipsLayer);
                    }
                    mFoodStepTitle.setText(foodStepTitle);
                    adapterNormal.setDatas(mSteps);
                    Log.d(TAG, "handleMessage: itemCount = " + adapterNormal.getItemCount());
                    adapterNormal.notifyDataSetChanged();
                    break;
                }
                case LOAD_EMPTY: {
                    break;
                }
                case LOAD_FAILED: {
                    break;
                }
            }
        }
    }

    private void initView() {
        mInfoTitle = (TextView) findViewById(R.id.info_title);
        mBack = (ImageView) findViewById(R.id.back);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mFoodImage = (ImageView) findViewById(R.id.foodImage);
        mFoodStepTitle = (TextView) findViewById(R.id.foodStepTitle);
        mFoodSteps = (RecyclerView) findViewById(R.id.foodSteps);
        mContext = this;
        mMainDosingLayer = (LinearLayout) findViewById(R.id.mainDosingLayer);
        mMainFoodDosing = (TextView) findViewById(R.id.mainFoodDosing);
        mOtherDosingLayer = (LinearLayout) findViewById(R.id.otherDosingLayer);
        mOtherFoodDosing = (TextView) findViewById(R.id.otherFoodDosing);
        adapterNormal = new RecyclerViewAdapterNormal();
        adapterNormal.setDatas(mSteps);
        adapterNormal.setmActivity(this);
        FullLinearLayoutManager fLayout = new FullLinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        fLayout.setSmoothScrollbarEnabled(true);
        mFoodSteps.setLayoutManager(fLayout);
        mFoodSteps.setHasFixedSize(true);
        mFoodSteps.setNestedScrollingEnabled(false);
        mFoodSteps.setAdapter(adapterNormal);
        mTipsLayer = (LinearLayout) findViewById(R.id.tipsLayer);
        mTips = (TextView) findViewById(R.id.tips);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
