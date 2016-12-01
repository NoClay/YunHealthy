package wang.fly.com.yunhealth.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wang.fly.com.yunhealth.MyViewPackage.FoldLineView;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.DataInfo;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/11/13.
 */

public class InfoActivity extends AppCompatActivity
        implements View.OnClickListener,
        FoldLineView.onScrollChartListener {

    private TextView infoText;
    private ImageView back;
    private FoldLineView foldLineView;
    private TextView[] tabText;
    private TextView averageText, maxText, minText;
    private TextView[] titleText;
    private TextView timeText;
    private Context context = this;
    private float average, max, min;
    private int start;
    private int end;
    private int type;
    public static final int MSG_DATA_CHANGE = 0;
    private static final String TAG = "InfoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActivityCollector.addActivity(this);
        initView();
        setInfo();
        List<DataInfo> mLineList = getData();
        foldLineView.setmLines(mLineList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private List<DataInfo> getData() {
        List<DataInfo> mLineList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            DataInfo dataInfo = new DataInfo(new Random().nextInt(300), "11." + i);
            mLineList.add(dataInfo);
        }
        return mLineList;
    }

    private void setInfo() {
        infoText.setText(getIntent().getStringExtra("name"));
    }

    private void initView() {
        //初始化
        tabText = new TextView[4];
        titleText = new TextView[3];
        type = getIntent().getIntExtra("position", 0);
        //绑定控件
        infoText = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        tabText[0] = (TextView) findViewById(R.id.oneDayChecked);
        tabText[1] = (TextView) findViewById(R.id.sevenDayChecked);
        tabText[2] = (TextView) findViewById(R.id.fourteenDayChecked);
        tabText[3] = (TextView) findViewById(R.id.thirtyDayChecked);
        titleText[0] = (TextView) findViewById(R.id.averageUnit);
        titleText[1] = (TextView) findViewById(R.id.highestUnit);
        titleText[2] = (TextView) findViewById(R.id.lowestUnit);
        averageText = (TextView) findViewById(R.id.chart_average_show);
        maxText = (TextView) findViewById(R.id.chart_highest_show);
        minText = (TextView) findViewById(R.id.chart_lowest_show);
        timeText = (TextView) findViewById(R.id.chart_time_show);
        foldLineView = (FoldLineView) findViewById(R.id.surfaceView);
        //设置listener
        for (int i = 0; i < tabText.length; i++) {
            tabText[i].setOnClickListener(this);
        }
        back.setOnClickListener(this);
        foldLineView.setOnScrollChartListener(this);
        //初始化View
        setTabCheck(2);
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
            case R.id.oneDayChecked: {
                setTabCheck(0);
                break;
            }
            case R.id.sevenDayChecked: {
                setTabCheck(1);
                break;
            }
            case R.id.fourteenDayChecked: {
                setTabCheck(2);
                break;
            }
            case R.id.thirtyDayChecked: {
                setTabCheck(3);
                break;
            }
        }
    }

    public void setTabCheck(int tabCheck) {
        //清除选中状态
        for (int i = 0; i < tabText.length; i++) {
            tabText[i].setTextColor(context.getResources().getColor(R.color.titleTextColor));
            tabText[i].setBackgroundColor(Color.WHITE);
        }
        tabText[tabCheck].setTextColor(Color.WHITE);
        tabText[tabCheck].setBackgroundColor(context.getResources().getColor(R.color.lightseagreen));
    }


    @Override
    public void onScroll(float average, float max, float min, int start, int end) {
        Message message = Message.obtain();
        boolean flag = false;
        if (this.average != average) {
            this.average = average;
            flag = true;
            Log.d(TAG, "average: average" + this.average);
        }
        if (this.max != max) {
            this.max = max;
            flag = true;
            Log.d(TAG, "max: max" + this.max);
        }
        if (this.min != min) {
            this.min = min;
            flag = true;
            Log.d(TAG, "min: min " + this.min);
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
                    timeText.setText("测量时间：" + start + "~" + end);
                    minText.setText(UtilClass.getTwoShortValue(min));
                    maxText.setText(UtilClass.getTwoShortValue(max));
                    averageText.setText(UtilClass.getTwoShortValue(average));

                    break;
                }
            }
        }
    };

}
