package wang.fly.com.yunhealth.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wang.fly.com.yunhealth.MyViewPackage.CircleShowData;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.DynamicData;

/**
 * Created by 82661 on 2016/11/10.
 */

public class RecycleAdapterForDynamic extends RecyclerView.Adapter<RecycleAdapterForDynamic.ViewHolder> {

    private List<DynamicData> datas;
    private int resource;

    public RecycleAdapterForDynamic(List<DynamicData> datas, int resource) {
        this.datas = datas;
        this.resource = resource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 绑定控件
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DynamicData data = datas.get(position);
        holder.stateShow.setLevel(data.getState());
        holder.bmiShow.setText(data.getQuality() + "");
        holder.weightShow.setText(data.getWeight() + "kg");
        Date date = new Date(System.currentTimeMillis());
        Date d = data.getDate();
        if(date.getYear() == d.getYear() && date.getMonth() == d.getMonth() && date.getDay() == d.getDay()){
            holder.dateShow.setText("今天");
        }else{
            holder.dateShow.setText(d.getYear() + "年" + d.getMonth() + "月" + d.getDay() + "日");
        }
        holder.timeShow.setText(d.getHours() + ":" + d.getMinutes());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
