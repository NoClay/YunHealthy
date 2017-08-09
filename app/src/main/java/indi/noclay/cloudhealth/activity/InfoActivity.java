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



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.myview.DatePickerView;
import indi.noclay.cloudhealth.myview.FoldLineView;
import indi.noclay.cloudhealth.util.MyConstants;
import indi.noclay.cloudhealth.util.UtilClass;


/**
 * Created by 82661 on 2016/11/13.
 */

public class InfoActivity extends AppCompatActivity
        implements View.OnClickListener,
        FoldLineView.onScrollChartListener,
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
    private float average, max, min;
    private int start;
    private int end;
    private int type;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActivityCollector.addActivity(this);
        initView();
        setInfo();
        showLoading();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        getDatas(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
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

    public void showLoading(){
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.VISIBLE);
        nullDataLayout.setVisibility(View.GONE);
    }

    public void showContent(){
        contentLayout.setVisibility(View.VISIBLE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.GONE);
    }

    public void showNullData(){
        title.setText("当天没有数据哦！");
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.VISIBLE);
    }

    public void showError(){
        title.setText("数据加载出错了哦，请稍后继续");
        contentLayout.setVisibility(View.GONE);
        loadLayout.setVisibility(View.GONE);
        nullDataLayout.setVisibility(View.VISIBLE);
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
        mDatePickerView = (DatePickerView) findViewById(R.id.date_picker);
        title = (TextView) findViewById(R.id.title);
        mDatePickerView.setOnDateChangedListener(this);
        foldLineView.setOnScrollChartListener(this);
        back.setOnClickListener(this);
        //设置listener
        String[] title = {"\n平均值", "\n峰值", "\n低谷值"};
        String[] unit = {"g/bl", "mV", "kg/m^2", "mg/dl", "℃", "mg/m^3", "mV", "mmHg"};
        for (int i = 0; i < titleText.length; i++) {
            titleText[i].setText(unit[type] + title[i]);
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



    @Override
    public void onScroll(float average, float max, float min, int start, int end) {
        Message message = Message.obtain();
        boolean flag = false;
        if (this.average != average) {
            this.average = average;
            flag = true;
        }
        if (this.max != max) {
            this.max = max;
            flag = true;
        }
        if (this.min != min) {
            this.min = min;
            flag = true;
        }
        if (this.start != start || this.end != end) {
            this.start = start;
            this.end = end;
            flag = true;
        }
        if (flag){
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DATA_CHANGE:{
                    minText.setText(UtilClass.getTwoShortValue(min));
                    maxText.setText(UtilClass.getTwoShortValue(max));
                    averageText.setText(UtilClass.getTwoShortValue(average));

                    break;
                }
                case MSG_LOAD_START:{
                    break;
                }
                case MSG_LOAD_SUCCESS:{
                    if (msg.arg1 == 0){
                        showNullData();
                    }else{
                        mDatas = temp;
                        foldLineView.setLines(mDatas);
                        if (foldLineView.startDrawing()){
                            showContent();
                        }else {
                            showError();
                        }
                    }
                    break;
                }
                case MSG_LOAD_FAILED:{
                    showError();
                    break;
                }
            }
        }
    };

    public void getDatas(int year, int month, int day){
        if (type < 0 || type >= tableName.length){
            return;
        }
        //停止继续绘图
        foldLineView.stopDrawing();
        BmobQuery query = new BmobQuery(tableName[type]);
        List<BmobQuery> and = new ArrayList<BmobQuery>();
//大于00：00：00
        BmobQuery q1 = new BmobQuery(tableName[type]);
        String start = String.format("%4d-%2d-%2d 00:00:00", year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date  = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q1.addWhereGreaterThanOrEqualTo("measureTime",new BmobDate(date));
        and.add(q1);
//小于23：59：59
        BmobQuery q2 = new BmobQuery(tableName[type]);
        String end = String.format("%4d-%2d-%2d 23:59:59", year, month, day);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1  = null;
        try {
            date1 = sdf1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q2.addWhereLessThanOrEqualTo("measureTime",new BmobDate(date1));
        and.add(q2);
        BmobQuery q3 = new BmobQuery(tableName[type]);
        SignUserData login = new SignUserData();
        login.setObjectId(MyConstants.userId);
        q3.addWhereEqualTo("owner", login);
        and.add(q3);
//添加复合与查询

        query.and(and);
        query.setLimit(100)
                .order("measureTime")
                .findObjectsByTable(new QueryListener<JSONArray>() {
                    @Override
                    public void done(JSONArray jsonArray, BmobException e) {
                        Message message = Message.obtain();
                        if (e != null){
                            Log.e(TAG, "done: ", e);
                            message.what = MSG_LOAD_FAILED;
                        }else{
                            int size = jsonArray.length();
                            message.what = MSG_LOAD_SUCCESS;
                            message.arg1 = size;
                            temp = new ArrayList<MeasureData>();
                            Log.d(TAG, "done: size " + size);
                            for (int i = 0; i < size; i++) {
                                try {
                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    MeasureData measure = new MeasureData();
                                    measure.setMaxData((float) obj.getDouble("maxData"));
                                    measure.setMinData((float) obj.getDouble("minData"));
                                    measure.setAverageData((float) obj.getDouble("averageData"));
                                    JSONObject date = obj.getJSONObject("measureTime");
                                    measure.setMeasureTime(
                                            new BmobDate(UtilClass.resolveBmobDate(
                                                    date.getString("iso"), null
                                            ))
                                    );
                                    temp.add(measure);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        handler.sendMessage(message);
                    }
                });
    }

    @Override
    public void onDateChanged(int year, int month, int day) {
        Log.d(TAG, "onDateChanged: time = " + year + "-" + month + "-" + day);
        showLoading();
        getDatas(year, month, day);
    }
}
