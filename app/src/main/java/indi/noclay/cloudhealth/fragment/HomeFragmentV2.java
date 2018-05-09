package indi.noclay.cloudhealth.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.activity.FoodMenuActivity;
import indi.noclay.cloudhealth.activity.FragmentContainerActivity;
import indi.noclay.cloudhealth.activity.NewDetailActivity;
import indi.noclay.cloudhealth.activity.NewsActivity;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.InternetUrlManager;

import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_FRAGMENT_TYPE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_TITLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_FRAGMENT_DYNAMIC;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_FRAGMENT_MEDICINE;

/**
 * Created by NoClay on 2018/5/4.
 */

public class HomeFragmentV2 extends Fragment implements View.OnClickListener {
    private Context context;
    private View homeView;
    private LinearLayout mHomePageLayout;
    private LinearLayout mHintPage;
    private ImageView mHomeHealthPlanIm;
    private TextView mHomeHealthPlanTv;
    private LinearLayout mFoodInput;
    private LinearLayout mNewsInput;
    private LinearLayout mMedicineInput;
    private LinearLayout mReportInput;
    private RelativeLayout mHealthyReport;
    private String[] images;
    private String[] titles;
    private String[] urls;
    private ViewFlipper viewFilpper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home_v2, container, false);
        context = getContext();
        initView(homeView);
        getViewFilpperData();
        return homeView;
    }

    private void getViewFilpperData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(InternetUrlManager.HEALTHY_LIFE).get();
                    Elements imagesElement = document.select("#focus_pic_list > li > a > img");
                    images = new String[imagesElement.size()];
                    for (int i = 0; i < imagesElement.size(); i++) {
                        images[i] = imagesElement.get(i).attr("src");
                    }
                    Elements texts = document.select("#focus_content_list > li");
                    titles = new String[images.length];
                    urls = new String[images.length];
                    for (int i = 0; i < texts.size(); i++) {
                        titles[i] = texts.get(i).select("h2 > a").text();
                        urls[i] = texts.get(i).select("h2 > a").attr("href");
                    }
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < images.length; i++) {
                                    View view = LayoutInflater.from(context).inflate(R.layout.view_filpper, null);
                                    ((TextView) view.findViewById(R.id.title)).setText(titles[i]);
                                    Glide.with(context).load(images[i])
                                            .into(((ImageView) view.findViewById(R.id.image)));
                                    final int index = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, NewDetailActivity.class);
                                            intent.putExtra(ConstantsConfig.PARAMS_IS_TOP, true);
                                            intent.putExtra(ConstantsConfig.PARAMS_URL, urls[index]);
                                            intent.putExtra(ConstantsConfig.PARAMS_TITLE, titles[index]);
                                            startActivity(intent);
                                        }
                                    });
                                    viewFilpper.addView(view);
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setEvent() {

    }

    private void initView(View view) {
        mHomePageLayout = (LinearLayout) view.findViewById(R.id.homePageLayout);
        mHintPage = (LinearLayout) view.findViewById(R.id.hint_page);
        mHomeHealthPlanIm = (ImageView) view.findViewById(R.id.home_health_plan_im);
        mHomeHealthPlanTv = (TextView) view.findViewById(R.id.home_health_plan_tv);
        mFoodInput = (LinearLayout) view.findViewById(R.id.foodInput);
        mNewsInput = (LinearLayout) view.findViewById(R.id.newsInput);
        mMedicineInput = (LinearLayout) view.findViewById(R.id.medicineInput);
        mReportInput = (LinearLayout) view.findViewById(R.id.reportInput);
        viewFilpper = (ViewFlipper) view.findViewById(R.id.viewFilpper);
        mHealthyReport = (RelativeLayout) view.findViewById(R.id.healthyReportBt);
        mHealthyReport.setOnClickListener(this);
        mFoodInput.setOnClickListener(this);
        mMedicineInput.setOnClickListener(this);
        mReportInput.setOnClickListener(this);
        mNewsInput.setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.foodInput: {
                intent = new Intent(getContext(), FoodMenuActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.newsInput: {
                intent = new Intent(getContext(), NewsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.medicineInput: {
                intent = new Intent(getContext(), FragmentContainerActivity.class);
                intent.putExtra(PARAMS_FRAGMENT_TYPE, TYPE_FRAGMENT_MEDICINE);
                intent.putExtra(PARAMS_TITLE, "用药");
                startActivity(intent);
                break;
            }
            case R.id.reportInput: {
                intent = new Intent(getContext(), FragmentContainerActivity.class);
                intent.putExtra(PARAMS_FRAGMENT_TYPE, TYPE_FRAGMENT_DYNAMIC);
                intent.putExtra(PARAMS_TITLE, "动态");
                startActivity(intent);
                break;
            }
            case R.id.healthyReportBt:{
                Toast.makeText(context, "健康报告计划开发中，敬请期待", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
