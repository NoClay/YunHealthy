package indi.noclay.cloudhealth.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.show.api.ShowApiRequest;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.carddata.IllnessInfoRetData;
import indi.noclay.cloudhealth.carddata.MedicineRetData;
import indi.noclay.cloudhealth.carddata.TagData;
import indi.noclay.cloudhealth.myview.YunHealthyErrorView;
import indi.noclay.cloudhealth.util.YunHealthyLoading;

import static indi.noclay.cloudhealth.util.ConstantsConfig.API_ILLNESS_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_SHOW_APP_ID;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_SHOW_APP_SECRET;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_EMPTY;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_FAILED;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_SUCCESS;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_BUNDLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_ID;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_MEDICINE_DATA;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_SEARCH_TYPE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_TITLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_MEDICINE_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_NORMAL_ILLNESS;
import static indi.noclay.cloudhealth.util.ViewUtils.hideView;
import static indi.noclay.cloudhealth.util.ViewUtils.showView;

public class SearchResultInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView infoTitle;
    private ImageView back;
    private ImageView img;
    private RecyclerView tagListView;
    private int searchType;
    private String id;
    private String title;
    private MedicineRetData.Drug mDrug;
    private List<Object> mObjects = new ArrayList<>();
    RecyclerViewAdapterNormal adapterNormal;
    private LinearLayout contentLayer;
    private YunHealthyErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_info);
        initView();
        handleArgument();
    }

    private void handleArgument() {
        if (getIntent() == null) {
            return;
        }
        Bundle bundle = getIntent().getBundleExtra(PARAMS_BUNDLE);
        if (bundle == null) {
            return;
        }
        searchType = bundle.getInt(PARAMS_SEARCH_TYPE);
        id = bundle.getString(PARAMS_ID);
        title = bundle.getString(PARAMS_TITLE);
        infoTitle.setText(title);
        if (searchType == TYPE_MEDICINE_INFO) {
            mDrug = (MedicineRetData.Drug) bundle.getSerializable(PARAMS_MEDICINE_DATA);
            handleMedicineData();
        } else if (searchType == TYPE_NORMAL_ILLNESS) {
            hideView(img);
            YunHealthyLoading.show(this, contentLayer);
            getDataFromNet();
        }
    }

    private void getDataFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowApiRequest request = new ShowApiRequest(API_ILLNESS_INFO, API_SHOW_APP_ID, API_SHOW_APP_SECRET);
                request.addTextPara("id", id);
                String res;
                res = request.post();
                if (!TextUtils.isEmpty(res)) {
                    JSONObject result = JSONObject.parseObject(res);
                    String errorTips = result.getString("showapi_res_error");
                    Integer errorCode = result.getInteger("showapi_res_code");
                    if (errorCode != null && errorCode != 0) {
                        //系统级失败
                        mHandler.obtainMessage(MSG_LOAD_FAILED, errorTips).sendToTarget();
                        return;
                    }
                    String resBody = result.getString("showapi_res_body");
                    handleResultBody(resBody);
                    return;
                }
                //失败
                mHandler.obtainMessage(MSG_LOAD_EMPTY, getString(R.string.hint_nothing_but_error)).sendToTarget();
            }
        }).start();
    }


    private void handleResultBody(String resBody) {
        if (TextUtils.isEmpty(resBody)) {
            mHandler.obtainMessage(MSG_LOAD_EMPTY, getString(R.string.hint_empity_error)).sendToTarget();
            return;
        }
        IllnessInfoRetData ret = JSONObject.parseObject(resBody, IllnessInfoRetData.class);
        if (ret == null || (ret.getRet_code() != null && ret.getRet_code() != 0) || ret.getItem() == null) {
            //接口级别失败
            mHandler.obtainMessage(MSG_LOAD_FAILED,
                    ret == null ? getString(R.string.hint_empity_error) : ret.getMsg())
                    .sendToTarget();
        } else {
            mObjects.add(new TagData("疾病名称", ret.getItem().getName()));
            mObjects.add(new TagData("描述", ret.getItem().getSummary()));
            mObjects.add(new TagData("科目名称", ret.getItem().getTypeName()));
            mObjects.add(new TagData("子科室名称", ret.getItem().getSubTypeName()));
            if (ret.getItem().getTagList() != null) {
                mObjects.addAll(ret.getItem().getTagList());
            }
            mHandler.obtainMessage(MSG_LOAD_SUCCESS).sendToTarget();
        }
    }

    /**
     * 渲染
     */
    private void handleMedicineData() {
        Glide.with(this).load(mDrug.getImg()).error(R.drawable.icon_load_failed).into(img);
        mObjects.add(new TagData("药品名称", mDrug.getDrugName()));
        mObjects.add(new TagData("规格型号", mDrug.getGgxh()));
        mObjects.add(new TagData("禁忌", mDrug.getJj()));
        mObjects.add(new TagData("生产企业", mDrug.getManu()));
        mObjects.add(new TagData("参考价格", mDrug.getPrice()));
        mObjects.add(new TagData("批准文号", mDrug.getPzwh()));
        mObjects.add(new TagData("适应症", mDrug.getSyz()));
        mObjects.add(new TagData("不良反应", mDrug.getBlfy()));
        mObjects.add(new TagData("药品类别", mDrug.getType()));
        mObjects.add(new TagData("性状", mDrug.getXz()));
        mObjects.add(new TagData("用法用量", mDrug.getYfyl()));
        mObjects.add(new TagData("药物相互作用", mDrug.getYwxhzy()));
        mObjects.add(new TagData("有效期", mDrug.getYxq()));
        mObjects.add(new TagData("贮藏", mDrug.getZc()));
        mObjects.add(new TagData("执行标准", mDrug.getZxbz()));
        mObjects.add(new TagData("注意事项", mDrug.getZysx()));
        mObjects.add(new TagData("主治疾病", mDrug.getZzjb()));
        mObjects.add(new TagData("主要成分", mDrug.getZycf()));
        adapterNormal.setDatas(mObjects);
        adapterNormal.notifyDataSetChanged();
    }

    private Handler mHandler = new SearchResultInfoActivityHandler();

    private class SearchResultInfoActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            YunHealthyLoading.dismiss();
            switch (msg.what) {
                case MSG_LOAD_EMPTY: {
                    hideView(contentLayer);
                    showView(errorView);
                    errorView.setErrorHint((String) msg.obj);
                    break;
                }
                case MSG_LOAD_FAILED: {
                    hideView(contentLayer);
                    showView(errorView);
                    errorView.setErrorHint((String) msg.obj);
                    break;
                }
                case MSG_LOAD_SUCCESS: {
                    showView(contentLayer);
                    hideView(errorView);
                    adapterNormal.setDatas(mObjects);
                    adapterNormal.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private void initView() {
        infoTitle = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        img = (ImageView) findViewById(R.id.img);
        tagListView = (RecyclerView) findViewById(R.id.tagListView);
        adapterNormal = new RecyclerViewAdapterNormal();
        adapterNormal.setmActivity(this);
        adapterNormal.setDatas(mObjects);
        adapterNormal.setmContext(this);
        tagListView.setAdapter(adapterNormal);
        tagListView.setHasFixedSize(true);
        tagListView.setNestedScrollingEnabled(false);
        tagListView.setFocusable(false);
        tagListView.setLayoutManager(new LinearLayoutManager(this));
        contentLayer = (LinearLayout) findViewById(R.id.contentLayer);
        errorView = (YunHealthyErrorView) findViewById(R.id.errorView);
        hideView(errorView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
        }
    }
}
