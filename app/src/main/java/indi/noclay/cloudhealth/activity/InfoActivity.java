package indi.noclay.cloudhealth.activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.database.measuredata.MeasureFenChen;
import indi.noclay.cloudhealth.database.measuredata.MeasureMaiBo;
import indi.noclay.cloudhealth.database.measuredata.MeasureTiWen;
import indi.noclay.cloudhealth.database.measuredata.MeasureXinDian;
import indi.noclay.cloudhealth.database.measuredata.MeasureXueYang;
import indi.noclay.cloudhealth.myview.DatePickerView;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.UtilClass;
import pers.noclay.foldlineview.FoldLineAdapter;
import pers.noclay.foldlineview.FoldLineView;


/**
 * Created by 82661 on 2016/11/13.
 */

public class InfoActivity extends AppCompatActivity
        implements View.OnClickListener,
        FoldLineView.OnScrollChartListener,
        DatePickerView.OnDateChangedListener {
    private TextView infoText;
    private ImageView back;
    private FoldLineView foldLineView;
    private TextView averageText, maxText, minText;
    private TextView[] titleText;
    private Context context = this;
    private DatePickerView mDatePickerView;
    private TextView title;
    private List<MeasureData> mDatas;
    private List<MeasureData> temp;
    private AnimationDrawable loadDrawable;
    private LinearLayout loadLayout;
    private LinearLayout contentLayout;
    private LinearLayout nullDataLayout;
    private FoldLineAdapter<MeasureData> mAdapter;
    private float average, max, min;
    private int start;
    private int end;
    private int type;
    List<Integer> colors;
    public static final int MSG_DATA_CHANGE = 0;
    public static final int MSG_LOAD_START = 1;
    public static final int MSG_LOAD_SUCCESS = 2;
    public static final int MSG_LOAD_FAILED = 3;

    private static final String TAG = "InfoActivity";
    public static final String[] tableName = {
            "MeasureXueYang",
            "MeasureMaiBo",
            "MeasureXinDian",
            "MeasureTiWen",
            "MeasureFenChen",
            "MeasureXueTang",
            "MeasureNaoDian",
            "MeasureXueYa",
    };
    public static final String[] titleHint = {"\n平均值", "\n峰值", "\n低谷值"};
    public static final String[] unit = {"%", "次/分", "kg/m^2", "℃", "μg/m³", "mg/m^3", "mV", "mmHg"};
    public static final float [] highThreshold = {100, 255, 4096, 125, 800};
    public static final float [] lowThreshold = {0, 0, 0, -40, 0};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActivityCollector.addActivity(this);
        setupColor();
        initView();
        setInfo();
        showLoading();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        getDatas(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void setInfo() {
        infoText.setText(getIntent().getStringExtra("name"));
    }

    public void showLoading() {
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.VISIBLE);
        nullDataLayout.setVisibility(View.GONE);
    }

    public void showContent() {
        contentLayout.setVisibility(View.VISIBLE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.GONE);
    }

    public void showNullData() {
        title.setText("当天没有数据哦！");
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.VISIBLE);
    }

    public void showError() {
        title.setText("数据加载出错了哦，请稍后继续");
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.VISIBLE);
    }

    private void setupColor() {
        if (colors == null){
            colors = new ArrayList<>();
        }
        colors.add(getResources().getColor(R.color.bg1));
        colors.add(getResources().getColor(R.color.bg2));
        colors.add(getResources().getColor(R.color.bg3));
    }

    private void initView() {
        //初始化
        titleText = new TextView[3];
        type = getIntent().getIntExtra("position", 0);
        //绑定控件
        ImageView iv_loading = (ImageView) findViewById(R.id.iv_loading);
        loadDrawable = (AnimationDrawable) iv_loading.getDrawable();
        loadDrawable.start();
        loadLayout = (LinearLayout) findViewById(R.id.load_layout);
        contentLayout = (LinearLayout) findViewById(R.id.content);
        nullDataLayout = (LinearLayout) findViewById(R.id.null_data);
        infoText = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        titleText[0] = (TextView) findViewById(R.id.averageUnit);
        titleText[1] = (TextView) findViewById(R.id.highestUnit);
        titleText[2] = (TextView) findViewById(R.id.lowestUnit);
        averageText = (TextView) findViewById(R.id.chart_average_show);
        maxText = (TextView) findViewById(R.id.chart_highest_show);
        minText = (TextView) findViewById(R.id.chart_lowest_show);
        foldLineView = (FoldLineView) findViewById(R.id.surfaceView);
        foldLineView.setColors(colors);
        foldLineView.setOnScrollChartListener(this);
        foldLineView.setYRange(lowThreshold[type], highThreshold[type]);
        Log.d(TAG, "initView: range = " + lowThreshold[type] + "~" + highThreshold[type]);
        mDatePickerView = (DatePickerView) findViewById(R.id.date_picker);
        title = (TextView) findViewById(R.id.title);
        mDatePickerView.setOnDateChangedListener(this);
        back.setOnClickListener(this);
        //设置listener

        for (int i = 0; i < titleText.length; i++) {
            titleText[i].setText(unit[type] + titleHint[i]);
        }
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

    private class InfoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DATA_CHANGE: {
                    minText.setText(UtilClass.getTwoShortValue(min));
                    maxText.setText(UtilClass.getTwoShortValue(max));
                    averageText.setText(UtilClass.getTwoShortValue(average));

                    break;
                }
                case MSG_LOAD_START: {
                    break;
                }
                case MSG_LOAD_SUCCESS: {
                    if (msg.arg1 == 0) {
                        showNullData();
                    } else {
                        mDatas = temp;
                        if (mDatas != null && mDatas.size() != 0) {
                            mAdapter = new FoldLineAdapter<>(mDatas);
                            foldLineView.setAdapter(mAdapter);
                            foldLineView.startDrawing();
                            showContent();
                        } else {
                            showError();
                        }
                    }
                    break;
                }
                case MSG_LOAD_FAILED: {
                    showError();
                    break;
                }

            }
        }
    }

    Handler handler = new InfoHandler();


    public Date getStartDate(int year, int month, int day){
        String start = String.format("%4d-%2d-%2d 00:00:00", year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public Date getEndDate(int year, int month, int day){
        String end = String.format("%4d-%2d-%2d 23:59:59", year, month, day);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public void getDatas(int year, int month, int day){
        if (type < 0 || type >= tableName.length) {
            return;
        }
        //停止继续绘图
        foldLineView.stopDrawing();
        switch (type){
            case 0:{
                //血氧
                queryXueYang(year, month, day);
                break;
            }
            case 1:{
                //脉搏
                queryMaiBo(year, month, day);
                break;
            }
            case 2:{
                //心电
                queryXinDian(year, month, day);
                break;
            }
            case 3:{
                //体温
                queryTiWen(year, month, day);
                break;
            }
            case 4:{
                //粉尘浓度
                queryFenChen(year, month, day);
                break;
            }
            case 5:{
                //血糖
                break;
            }
        }
    }

    private void doOnGetDataSuccess(List list) {
        Message message = handler.obtainMessage(MSG_LOAD_SUCCESS);
        message.arg1 = list.size();
        temp = new ArrayList<MeasureData>();
        Log.d(TAG, "done: size " + list.size());
        for (int i = 0; i < list.size(); i++) {
            MeasureData item = (MeasureData) list.get(i);
            temp.add(item);
        }
        message.sendToTarget();
    }

    private void queryFenChen(int year, int month, int day) {
        BmobQuery<MeasureFenChen> query = new BmobQuery<>();
        List<BmobQuery<MeasureFenChen>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureFenChen> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("measureTime", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureFenChen> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("measureTime", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureFenChen> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("measureTime").setLimit(100)
                .findObjects(new FindListener<MeasureFenChen>() {
                    @Override
                    public void done(List<MeasureFenChen> list, BmobException e) {
                        if (e != null || list == null){
                            handler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        }else{
                            doOnGetDataSuccess(list);
                        }
                        Log.e(TAG, "done: ", e);
                        Log.d(TAG, "done: size = " + (list == null ? 0 : list.size()));
                    }
                });
    }

    private void queryTiWen(int year, int month, int day) {
        BmobQuery<MeasureTiWen> query = new BmobQuery<>();
        List<BmobQuery<MeasureTiWen>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureTiWen> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("measureTime", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureTiWen> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("measureTime", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureTiWen> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("measureTime").setLimit(100)
                .findObjects(new FindListener<MeasureTiWen>() {
                    @Override
                    public void done(List<MeasureTiWen> list, BmobException e) {
                        if (e != null || list == null){
                            handler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        }else{
                            doOnGetDataSuccess(list);
                        }
                        Log.e(TAG, "done: ", e);
                        Log.d(TAG, "done: size = " + (list == null ? 0 : list.size()));
                    }
                });
    }

    private void queryXinDian(int year, int month, int day) {
        BmobQuery<MeasureXinDian> query = new BmobQuery<>();
        List<BmobQuery<MeasureXinDian>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureXinDian> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("measureTime", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureXinDian> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("measureTime", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureXinDian> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("measureTime").setLimit(100)
                .findObjects(new FindListener<MeasureXinDian>() {
                    @Override
                    public void done(List<MeasureXinDian> list, BmobException e) {
                        if (e != null || list == null){
                            handler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        }else{
                            doOnGetDataSuccess(list);
                        }
                        Log.e(TAG, "done: ", e);
                        Log.d(TAG, "done: size = " + (list == null ? 0 : list.size()));
                    }
                });
    }

    private void queryMaiBo(int year, int month, int day) {
        BmobQuery<MeasureMaiBo> query = new BmobQuery<>();
        List<BmobQuery<MeasureMaiBo>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureMaiBo> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("measureTime", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureMaiBo> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("measureTime", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureMaiBo> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("measureTime").setLimit(100)
                .findObjects(new FindListener<MeasureMaiBo>() {
                    @Override
                    public void done(List<MeasureMaiBo> list, BmobException e) {
                        if (e != null || list == null){
                            handler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        }else{
                            doOnGetDataSuccess(list);
                        }
                        Log.e(TAG, "done: ", e);
                        Log.d(TAG, "done: size = " + (list == null ? 0 : list.size()));
                    }
                });
    }

    private void queryXueYang(int year, int month, int day) {
        BmobQuery<MeasureXueYang> query = new BmobQuery<>();
        List<BmobQuery<MeasureXueYang>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureXueYang> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("measureTime", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureXueYang> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("measureTime", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureXueYang> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("measureTime").setLimit(100)
                .findObjects(new FindListener<MeasureXueYang>() {
                    @Override
                    public void done(List<MeasureXueYang> list, BmobException e) {
                        if (e != null || list == null){
                            handler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        }else{
                            doOnGetDataSuccess(list);
                        }
                        Log.e(TAG, "done: ", e);
                        Log.d(TAG, "done: size = " + (list == null ? 0 : list.size()));
                    }
                });
    }

    @Override
    public void onDateChanged(int year, int month, int day) {
        Log.d(TAG, "onDateChanged: time = " + year + "-" + month + "-" + day);
        showLoading();
//        getDatas(year, month, day);
    }

    @Override
    public void onScroll(int i, int i1) {
        if (start != i || end != i1) {
            start = i;
            end = i1;
            Message message = Message.obtain();
            float[] temp = computeData(i, i1);
            max = temp[0];
            min = temp[1];
            average = temp[2];
            handler.sendMessage(message);
            Log.d(TAG, "onScroll: left = " + i + "... right = " + i1);
        }
    }

    private float[] computeData(int i, int i1) {
        float[] result = new float[3];
        if (i1 > i) {
            result[0] = mDatas.get(i).getAverageData();
            result[1] = mDatas.get(i).getAverageData();
            result[2] = 0;
            for (int j = i + 1; j < i1; j++) {
                float temp = mDatas.get(i).getAverageData();
                result[0] = result[0] < temp ? temp : result[0];
                result[1] = result[1] > temp ? temp : result[1];
                result[2] += temp;
            }
            result[2] /= (i1 - i + 1);
        }
        return result;
    }
}
