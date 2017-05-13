package wang.fly.com.yunhealth.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wang.fly.com.yunhealth.DataBasePackage.HeightAndWeight;
import wang.fly.com.yunhealth.MyViewPackage.CircleShowData;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by no_clay on 2017/1/27.
 */

public class LoadItemAdapterForDynamic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private boolean mIsStagger;
    private List<HeightAndWeight> datas;
    private int resource;

    public LoadItemAdapterForDynamic(int resource, List<HeightAndWeight> datas) {
        this.resource = resource;
        this.datas = datas;
    }

    public void switchMode(boolean mIsStagger) {
        this.mIsStagger = mIsStagger;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HeightAndWeight data = datas.get(position);
        LoadItemAdapterForDynamic.ViewHolder holder = (ViewHolder) viewHolder;
        holder.stateShow.setLevel(data.getState());
        holder.bmiShow.setText(UtilClass.getTwoShortValue(data.getQuality()) + "");
        holder.weightShow.setText(data.getWeight() + "kg");
        Date date = new Date(System.currentTimeMillis());
        Date d = data.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        Log.d("test", "onBindViewHolder: date " + UtilClass.compareDate(date, d));
        if (UtilClass.compareDate(date, d) == 0) {
            holder.dateShow.setText("今天");
        } else if (UtilClass.compareDate(date, d) > 0) {
            holder.dateShow.setText(
                    calendar.get(Calendar.YEAR) + "年"
                            + (calendar.get(Calendar.MONTH) + 1) + "月"
                            + calendar.get(Calendar.DAY_OF_MONTH) + "日"
            );
        }
        holder.timeShow.setText(
                calendar.get(Calendar.HOUR_OF_DAY)
                + ":" + calendar.get(Calendar.MINUTE)
        );
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

//    public class StaggerViewHolder extends RecyclerView.ViewHolder {
//        public View mView;
//        public View iconView;
//        public TextView mContentView;
//
//        public StaggerViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//            iconView = itemView.findViewById(R.id.icon);
//            mContentView = (TextView) itemView.findViewById(R.id.content);
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateShow;
        public TextView timeShow;
        public TextView weightShow;
        public TextView bmiShow;
        public CircleShowData stateShow;

        public ViewHolder(View itemView) {
            super(itemView);
            dateShow = (TextView) itemView.findViewById(R.id.dateText);
            timeShow = (TextView) itemView.findViewById(R.id.timeText);
            weightShow = (TextView) itemView.findViewById(R.id.weightShow);
            bmiShow = (TextView) itemView.findViewById(R.id.BMIShow);
            stateShow = (CircleShowData) itemView.findViewById(R.id.stateShow);
        }
    }
}
