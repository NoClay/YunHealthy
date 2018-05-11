package indi.noclay.cloudhealth.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.carddata.FoodShowItem;
import indi.noclay.cloudhealth.database.NewsData;
import indi.noclay.cloudhealth.interfaces.CollectionFormat;
import indi.noclay.cloudhealth.myview.AutoLoadMoreRecyclerView;
import indi.noclay.cloudhealth.util.ConstantsConfig;

import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_EMPTY;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_FAILED;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_NO_MORE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.MSG_LOAD_SUCCESS;
import static indi.noclay.cloudhealth.util.SharedPreferenceHelper.getLoginUser;

public class CollectionActivity extends AppCompatActivity implements
        View.OnClickListener,
        AutoLoadMoreRecyclerView.LoadMoreListener,
        RecyclerViewAdapterNormal.OnItemLongClickListener,
        RecyclerViewAdapterNormal.OnItemClickListener{

    private TextView mInfoTitle;
    private ImageView mBack;
    private AutoLoadMoreRecyclerView mList;
    private List<Object> mObjects = new ArrayList<>();
    private boolean isNews;
    private int mCurrentPage = 0;
    private RecyclerViewAdapterNormal mAdapterNormal;
    private Context mContext;
    private static final String TAG = "CollectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        handleArguments();
        initView();
        getData();
        mContext = this;
    }

    private void getData() {
        if (isNews){
            BmobQuery<NewsData> query = new BmobQuery<>();
            query.addWhereEqualTo("owner", getLoginUser());
            query.setSkip(mCurrentPage * 10);
            query.setLimit(10);
            query.findObjects(new FindListener<NewsData>() {
                @Override
                public void done(List<NewsData> list, BmobException e) {
                    doGetData(list, e);
                }
            });
        }else{
            BmobQuery<FoodShowItem> query = new BmobQuery<>();
            query.addWhereEqualTo("owner", getLoginUser());
            query.setSkip(mCurrentPage * 10);
            query.setLimit(10);
            query.findObjects(new FindListener<FoodShowItem>() {
                @Override
                public void done(List<FoodShowItem> list, BmobException e) {
                    doGetData(list, e);
                }
            });
        }
    }

    private void doGetData(List list, BmobException e) {
        if (e == null){
            if (list == null || list.size() <= 0){
                mHandler.sendEmptyMessage(MSG_LOAD_EMPTY);
            }else{
                mObjects.addAll(list);
                mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
                if (list.size() < 10){
                    mHandler.sendEmptyMessage(MSG_LOAD_NO_MORE);
                }
            }
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
    }

    private Handler mHandler = new CollectionHandler();


    @Override
    public void onItemClick(Object o, int position, int layoutPosition) {
        if (isNews && o instanceof NewsData){
            Intent intent = new Intent(this, NewDetailActivity.class);
            //利用Bundle传输信息
            intent.putExtra(ConstantsConfig.PARAMS_URL, ((NewsData) o).getUrl());
            intent.putExtra(ConstantsConfig.PARAMS_IS_TOP, false);
            intent.putExtra(ConstantsConfig.PARAMS_TITLE, ((NewsData) o).getTitle());
            intent.putExtra(ConstantsConfig.PARAMS_OBJECT, ((NewsData) o));
            startActivity(intent);
        } else if (o instanceof FoodShowItem){
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra(ConstantsConfig.PARAMS_OBJECT, (Serializable) o);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(Object o, int position, int layoutPosition) {
        if (o instanceof CollectionFormat){
            final BmobObject object = (BmobObject) o;
            final int pos = layoutPosition;
            Dialog.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            Toast.makeText(mContext, "正在删除", Toast.LENGTH_SHORT).show();
                            object.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    final BmobException finalE = e;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (finalE == null){
                                                Log.e(TAG, "run: ", finalE);
                                                mList.notifyItemRemoved(pos);
                                                mList.notifyItemRangeRemoved(pos, mList.getChildCount() - pos);
                                                mObjects.remove(pos);
                                            }else{
                                                Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE: {
                            break;
                        }
                    }
                }
            };

            Dialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("是否删除？")
                    .setMessage(((CollectionFormat) o).getName())
                    .setPositiveButton("确定", listener)
                    .setNegativeButton("取消", listener)
                    .create();
            dialog.show();
        }
        return true;
    }

    private class CollectionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mList.setLoadingMore(false);
            switch (msg.what){
                case MSG_LOAD_SUCCESS:{
                    mList.notifyMoreFinish(true);
                    mCurrentPage ++;
                    break;
                }
                case MSG_LOAD_EMPTY:{
                    //暂无更多
                    mList.notifyMoreFinish(false);
                    break;
                }
                case MSG_LOAD_FAILED:{
                    Toast.makeText(CollectionActivity.this, "加载失败，请重试", Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_LOAD_NO_MORE:{
                    mList.notifyMoreFinish(false);
                    break;
                }
            }
        }
    }

    private void handleArguments() {
        if (getIntent() == null){
            finish();
            return;
        }
        isNews = getIntent().getBooleanExtra(ConstantsConfig.PARAMS_IS_NEWS, false);
    }

    private void initView() {
        mInfoTitle = (TextView) findViewById(R.id.info_title);
        mBack = (ImageView) findViewById(R.id.back);
        mList = (AutoLoadMoreRecyclerView) findViewById(R.id.list);
        mBack.setOnClickListener(this);
        if (isNews){
            mInfoTitle.setText("我的资讯");
        }else{
            mInfoTitle.setText("我的食谱");
        }
        mAdapterNormal = new RecyclerViewAdapterNormal(mObjects, mHandler, this, this, null);
        mAdapterNormal.setOnItemClickListener(this);
        mAdapterNormal.setOnItemLongClickListener(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setHasFixedSize(true);
        mList.setAdapter(mAdapterNormal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
        }
    }

    @Override
    public void onLoadMore() {
        mList.setLoadingMore(true);
        getData();
    }
}
