package indi.noclay.cloudhealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.show.api.ShowApiRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.carddata.CompanyRetData;
import indi.noclay.cloudhealth.carddata.IllnessRetData;
import indi.noclay.cloudhealth.carddata.MedicineRetData;
import indi.noclay.cloudhealth.carddata.ShowApiRetBase;
import indi.noclay.cloudhealth.myview.AutoLoadMoreRecyclerView;
import indi.noclay.cloudhealth.myview.YunHealthyErrorView;
import indi.noclay.cloudhealth.util.YunHealthyLoading;

import static indi.noclay.cloudhealth.util.ConstantsConfig.API_MEDICINE_COMPANY_SEARCH;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_MEDICINE_SEARCH;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_NORMAL_ILLNESS_SEARCH;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_SHOW_APP_ID;
import static indi.noclay.cloudhealth.util.ConstantsConfig.API_SHOW_APP_SECRET;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_EMPTY;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_FAILED;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_NO_MORE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_SUCCESS;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_BUNDLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_ID;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_KEY_WORD;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_MEDICINE_DATA;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_SEARCH_TYPE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_TITLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.SEARCH_TYPE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_MEDICINE_COMPANY_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_MEDICINE_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_NORMAL_ILLNESS;
import static indi.noclay.cloudhealth.util.ViewUtils.hideView;
import static indi.noclay.cloudhealth.util.ViewUtils.showView;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener,
        AutoLoadMoreRecyclerView.LoadMoreListener,
        YunHealthyErrorView.OnErrorRetryListener,
        RecyclerViewAdapterNormal.OnItemClickListener{

    private int searchType;
    private String searchKey;
    private TextView infoTitle;
    private ImageView back;
    private AutoLoadMoreRecyclerView resultListView;
    private RecyclerViewAdapterNormal mAdapterNormal;
    private int mCurrentPage = 1;
    private int limit = 20;
    private List<Object> mObjects = new ArrayList<>();
    private static final String TAG = "SearchResultActivity";
    private TextView keyWord;
    private TextView allResultCount;
    private int allPages;
    private YunHealthyErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        handleArgument();
        YunHealthyLoading.show(this, resultListView);
    }

    private void handleArgument() {
        if (getIntent() == null) {
            return;
        }
        Bundle bundle = getIntent().getBundleExtra(PARAMS_BUNDLE);
        searchKey = bundle.getString(PARAMS_KEY_WORD);
        searchType = bundle.getInt(PARAMS_SEARCH_TYPE);
        if (infoTitle != null) {
            infoTitle.setText(SEARCH_TYPE[searchType]);
        }
        keyWord.setText(searchKey);
        fetchData();
    }

    private void fetchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res;
                ShowApiRequest request;
                Log.d(TAG, "run: key = " + searchKey);
                switch (searchType) {
                    case TYPE_MEDICINE_INFO: {
                        request = new ShowApiRequest(API_MEDICINE_SEARCH, API_SHOW_APP_ID, API_SHOW_APP_SECRET);
                        request.addTextPara("keyword", searchKey);
                        break;
                    }
                    case TYPE_MEDICINE_COMPANY_INFO: {
                        request = new ShowApiRequest(API_MEDICINE_COMPANY_SEARCH, API_SHOW_APP_ID, API_SHOW_APP_SECRET);
                        request.addTextPara("factoryName", searchKey);
                        break;
                    }
                    case TYPE_NORMAL_ILLNESS: {
                        request = new ShowApiRequest(API_NORMAL_ILLNESS_SEARCH, API_SHOW_APP_ID, API_SHOW_APP_SECRET);
                        request.addTextPara("key", searchKey);
                        break;
                    }
                    default:
                        request = new ShowApiRequest(API_MEDICINE_SEARCH, API_SHOW_APP_ID, API_SHOW_APP_SECRET);
                        break;
                }
                request.addTextPara("page", mCurrentPage + "");
                res = request.post();
                Log.d(TAG, "run: res = " + res);
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
                    Log.d(TAG, "run: body = " + resBody);
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
        ShowApiRetBase ret;
        switch (searchType) {
            case TYPE_MEDICINE_INFO: {
                ret = JSONObject.parseObject(resBody, MedicineRetData.class);
                break;
            }
            case TYPE_MEDICINE_COMPANY_INFO: {
                ret = JSONObject.parseObject(resBody, CompanyRetData.class);
                break;
            }
            case TYPE_NORMAL_ILLNESS: {
                ret = JSONObject.parseObject(resBody, IllnessRetData.class);
                break;
            }
            default:
                ret = JSONObject.parseObject(resBody, MedicineRetData.class);
                break;
        }
        if (ret == null || (ret.getRet_code() != null && ret.getRet_code() != 0)) {
            //接口级别失败
            mHandler.obtainMessage(MSG_LOAD_FAILED,
                    ret == null ? getString(R.string.hint_empity_error) : ret.getMsg())
                    .sendToTarget();
        } else {
            mCurrentPage++;
            List<Object> temps = ret.getDataItem();
            mObjects.addAll(temps);
            String allResults = ret.allResults;
            if (ret instanceof IllnessRetData) {
                allResults = ((IllnessRetData) ret).getPagebean().getAllNum().toString();
            }
            allPages = TextUtils.isEmpty(allResults) ? 0 : (Integer.valueOf(allResults) / limit);
            mHandler.obtainMessage(MSG_LOAD_SUCCESS, allResults).sendToTarget();
        }
    }


    private void initView() {
        infoTitle = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        resultListView = (AutoLoadMoreRecyclerView) findViewById(R.id.resultListView);
        mAdapterNormal = new RecyclerViewAdapterNormal();
        mAdapterNormal.setDatas(mObjects);
        mAdapterNormal.setmActivity(this);
        mAdapterNormal.setmContext(this);
        mAdapterNormal.setOnItemClickListener(this);
        resultListView.setLayoutManager(new LinearLayoutManager(this));
        resultListView.setHasFixedSize(true);
        resultListView.setAdapter(mAdapterNormal);
        keyWord = (TextView) findViewById(R.id.keyWord);
        allResultCount = (TextView) findViewById(R.id.allResultCount);
        allResultCount.setText("");
        errorView = (YunHealthyErrorView) findViewById(R.id.errorView);
        hideView(errorView);
        resultListView.setLoadMoreListener(this);
        back.setOnClickListener(this);
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

    private Handler mHandler = new SearchResultHandler();

    @Override
    public void onLoadMore() {
        if (mCurrentPage >= allPages) {
            mHandler.sendEmptyMessage(MSG_LOAD_NO_MORE);
            return;
        }
        resultListView.setLoadingMore(true);
        fetchData();
    }

    @Override
    public void onErrorRetry() {
//        fetchData();
    }


    @Override
    public void onItemClick(Object o, int position, int layoutPosition) {
        Intent intent = new Intent(this, SearchResultInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAMS_SEARCH_TYPE, searchType);
        if (o instanceof MedicineRetData.Drug){
            bundle.putString(PARAMS_ID, ((MedicineRetData.Drug) o).getId());
            bundle.putSerializable(PARAMS_MEDICINE_DATA, (Serializable) o);
            bundle.putString(PARAMS_TITLE, ((MedicineRetData.Drug) o).getDrugName());
        }else if (o instanceof IllnessRetData.Illness){
            bundle.putString(PARAMS_ID, ((IllnessRetData.Illness) o).getId());
            bundle.putString(PARAMS_TITLE, ((IllnessRetData.Illness) o).getName());
        }
        intent.putExtra(PARAMS_BUNDLE, bundle);
        startActivity(intent);
    }

    public class SearchResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            resultListView.setLoadingMore(false);
            YunHealthyLoading.dismiss();
            switch (msg.what) {
                case MSG_LOAD_EMPTY: {
                    showView(errorView);
                    hideView(resultListView);
                    errorView.setErrorHint((String) msg.obj);
                    break;
                }
                case MSG_LOAD_FAILED: {
                    showView(errorView);
                    hideView(resultListView);
                    errorView.setErrorHint((String) msg.obj);
                    break;
                }
                case MSG_LOAD_SUCCESS: {
                    showView(resultListView);
                    hideView(errorView);
                    allResultCount.setText((String) msg.obj);
                    resultListView.notifyMoreFinish(true);
                    break;
                }
                case MSG_LOAD_NO_MORE: {
                    resultListView.notifyMoreFinish(false);
                    break;
                }
            }
        }
    }
}
