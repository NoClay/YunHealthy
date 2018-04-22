package indi.noclay.cloudhealth.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.MainActivityCopy;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.activity.FoodMenuActivity;
import indi.noclay.cloudhealth.activity.MedicineActivity;
import indi.noclay.cloudhealth.activity.NewsActivity;
import indi.noclay.cloudhealth.adapter.ResultListViewAdapter;
import indi.noclay.cloudhealth.myview.ScanView;
import indi.noclay.cloudhealth.myview.dialog.ResultDialog;
import indi.noclay.cloudhealth.util.ResultMessage;

import static indi.noclay.cloudhealth.fragment.DataMedicalFragment.NOW_MEDICINE;


/*
 * Created by 兆鹏 on 2016/11/2.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ScanView scanView;
    private View homeView;
    private ResultDialog resultDialog;
    private ResultListViewAdapter resultListViewAdapter;
    private List<ResultMessage> resultMessageList;
    private boolean isShowResult = false;
    private LinearLayout mHomePageLayout;
    private LinearLayout mHintPage;
    private ImageView mHomeHealthPlanIm;
    private TextView mHomeHealthPlanTv;
    private LinearLayout mFoodInput;
    private LinearLayout mNewsInput;
    private LinearLayout mMedicineInput;
    private LinearLayout mReportInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        initView(homeView);
        return homeView;
    }

    private void setEvent() {

    }

    private void initView(View view) {
        mHomePageLayout = (LinearLayout) view.findViewById(R.id.homePageLayout);
        scanView = (ScanView) view.findViewById(R.id.scanButton);
        mHintPage = (LinearLayout) view.findViewById(R.id.hint_page);
        mHomeHealthPlanIm = (ImageView) view.findViewById(R.id.home_health_plan_im);
        mHomeHealthPlanTv = (TextView) view.findViewById(R.id.home_health_plan_tv);
        mFoodInput = (LinearLayout) view.findViewById(R.id.foodInput);
        mNewsInput = (LinearLayout) view.findViewById(R.id.newsInput);
        mMedicineInput = (LinearLayout) view.findViewById(R.id.medicineInput);
        mReportInput = (LinearLayout) view.findViewById(R.id.reportInput);
        resultMessageList = new ArrayList<>();
        resultListViewAdapter = new ResultListViewAdapter(context,
                R.layout.item_result, resultMessageList);
        for (int i = 0; i < 10; i++) {
            ResultMessage resultMessage = new ResultMessage(i, "测试 " + i, true);
            resultMessageList.add(resultMessage);
        }
        scanView.setOnClickListener(this);
        mFoodInput.setOnClickListener(this);
        mMedicineInput.setOnClickListener(this);
        mReportInput.setOnClickListener(this);
        mNewsInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.scanButton: {
                //按下扫描按钮的时候，弹出结果框
                if (!isShowResult) {
                    scanView.startScan();
                    resultDialog = new ResultDialog(context, this, resultListViewAdapter);
                    resultDialog.showAtLocation(homeView, Gravity.BOTTOM | Gravity.HORIZONTAL_GRAVITY_MASK,
                            0, 0);
                    isShowResult = true;
                }
                break;
            }
            case R.id.close_result_button: {
                isShowResult = false;
                scanView.stopScan();
                resultDialog.dismiss();
                break;
            }
            case R.id.lookAdvice: {
                Toast.makeText(context, "寻求建议", Toast.LENGTH_SHORT).show();
                break;
            }

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
                intent = new Intent(getContext(), MedicineActivity.class);
                intent.putExtra("type", NOW_MEDICINE);
                startActivity(intent);
                break;
            }
            case R.id.reportInput: {
                if (getActivity() instanceof MainActivityCopy){
                    ((MainActivityCopy) getActivity()).setCurrentPage(MainActivityCopy.PAGE_DATA, 2);
                }
                break;
            }
        }

    }


}
