package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by no_clay on 2017/1/29.
 */

public class DatePickerView extends RelativeLayout implements View.OnClickListener{
    private View mainView;
    ImageView back1;
    ImageView back2;
    ImageView next1;
    ImageView next2;
    TextView timeShow;
    Calendar calendar;
    int year, month, day;
    OnDateChangedListener mOnDateChangedListener;

    public OnDateChangedListener getOnDateChangedListener() {
        return mOnDateChangedListener;
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mOnDateChangedListener = onDateChangedListener;
    }

    public interface OnDateChangedListener{
        public void onDateChanged(int year, int month, int day);
    }

    public DatePickerView(Context context) {
        super(context);
        init();
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mainView = LayoutInflater.from(getContext()).inflate(R.layout.view_date_picker, this, true);
        back1 = (ImageView) mainView.findViewById(R.id.back1);
        back2 = (ImageView) mainView.findViewById(R.id.back2);
        next1 = (ImageView) mainView.findViewById(R.id.next1);
        next2 = (ImageView) mainView.findViewById(R.id.next2);
        timeShow = (TextView) mainView.findViewById(R.id.chart_time_show);

        initTime();
        setTimeText(year, month, day);

        back1.setOnClickListener(this);
        back2.setOnClickListener(this);
        next1.setOnClickListener(this);
        next2.setOnClickListener(this);
    }

    public void initTime(){
        if (calendar == null){
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setTimeText(int year, int month, int day){
        timeShow.setText(year + " 年 " + month + " 月 " + day + " 日 ");
    }

    public Date getPickDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back1:{
//                前一天
                if (day > 1){
                    day --;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }else if (month > 1){
                    month --;
                    day = UtilClass.getDayOfMonthPast(year, month);
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }else if (year > 1){
                    year --;
                    month = 12;
                    day = UtilClass.getDayOfMonthPast(year, month);
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }
                setTimeText(year, month, day);
                break;
            }
            case R.id.back2:{
                int temp;
                if (month > 1){
                    month --;
                }else if (year > 1){
                    year --;
                    month = 12;
                }
                temp = UtilClass.getDayOfMonthPast(year, month);
                day = day > temp ? temp : day;
                mOnDateChangedListener.onDateChanged(year, month, day);
                setTimeText(year, month, day);
                break;
            }
            case R.id.next1:{
                if (day < UtilClass.getDayOfMonthPast(year, month)
                        && UtilClass.checkDatePast(year, month, day + 1)){
                    day ++;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }else if (month < 12 && UtilClass.checkDatePast(year, month + 1, 1)){
                    month ++;
                    day = 1;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }else if (UtilClass.checkDatePast(year + 1, 1, 1)){
                    month = day = 1;
                    year ++;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }
                setTimeText(year, month, day);
                break;
            }
            case R.id.next2:{
                if (month < 12 && UtilClass.checkDatePast(year, month + 1, 1)){
                    month ++;
                    day = 1;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }else if (UtilClass.checkDatePast(year + 1, 1, 1)){
                    month = day = 1;
                    year ++;
                    mOnDateChangedListener.onDateChanged(year, month, day);
                }
                setTimeText(year, month, day);
                break;
            }
        }
    }
}
