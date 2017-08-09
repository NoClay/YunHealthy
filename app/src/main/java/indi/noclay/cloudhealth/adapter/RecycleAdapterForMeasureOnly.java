package indi.noclay.cloudhealth.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.myview.HeartWavesView;
import indi.noclay.cloudhealth.util.UtilClass;


/**
 * Created by 82661 on 2016/11/9.
 */

public class RecycleAdapterForMeasureOnly extends RecyclerView.Adapter<RecycleAdapterForMeasureOnly.ViewHolder> {

    private static final String TAG = "RecycleAdapterForMeasur";
    private int layout;
    private Context context;
    private static final int COLOR_NORMAL = Color.BLACK;
    private static final int COLOR_DANGER = Color.RED;
    boolean[] isOpen = new boolean[8];
    private List<MeasureData> measureDataList;
    public HeartWavesView heartWavesView;

    public RecycleAdapterForMeasureOnly(int layout, Context context, List<MeasureData> measureDataList) {
        this.layout = layout;
        this.context = context;
        this.measureDataList = measureDataList;
        isOpen[0] = true;
        isOpen[1] = true;
    }

    @Override
    public RecycleAdapterForMeasureOnly.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MeasureData temp = measureDataList.get(position);
        holder.title.setText(temp.getName());
        if (isOpen[position]) {
            holder.toggleButton.setRotation(90);
            holder.contentLayout.setVisibility(View.VISIBLE);
        } else {
            holder.toggleButton.setRotation(0);
            holder.contentLayout.setVisibility(View.GONE);
        }
        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 5 || position == 6 || position == 7){
                    Toast.makeText(context, "暂未开发此功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isOpen[position]) {
                    //打开的按钮动画
                    holder.toggleButton.setRotation(90);
                    isOpen[position] = false;
                    notifyItemChanged(position);
                } else {
                    holder.toggleButton.setRotation(0);
                    isOpen[position] = true;
                    notifyItemChanged(position);
                }
            }
        });
        //判定心电数据
        if (position != 2){
            holder.heartWavesView.setVisibility(View.GONE);
        }else{
            holder.heartWavesView.setVisibility(View.VISIBLE);
            //保持引用
            heartWavesView = holder.heartWavesView;
        }
        //设定其他基本数据
        holder.averageData.setText("平均值：" + UtilClass.getTwoShortValue(
                temp.getAverageData()
        ));
        if (temp.getAverageDanger()){
            holder.averageData.setTextColor(COLOR_DANGER);
        }else{
            holder.averageData.setTextColor(COLOR_NORMAL);
        }
        holder.maxData.setText("高峰值：" + UtilClass.getTwoShortValue(
                temp.getMaxData()
        ));
        if (temp.getMaxDanger()){
            holder.maxData.setTextColor(COLOR_DANGER);
        }else{
            holder.maxData.setTextColor(COLOR_NORMAL);
        }
        holder.minData.setText("低谷值：" + UtilClass.getTwoShortValue(
                temp.getMinData()
        ));
        if (temp.getMinDanger()){
            holder.minData.setTextColor(COLOR_DANGER);
        }else{
            holder.minData.setTextColor(COLOR_NORMAL);
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return measureDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout titleLayout;
        public LinearLayout contentLayout;
        public ImageView toggleButton;
        public TextView title;
        public HeartWavesView heartWavesView;
        public TextView averageData;
        public TextView maxData;
        public TextView minData;

        public ViewHolder(View itemView) {
            super(itemView);
            titleLayout = (RelativeLayout) itemView.findViewById(R.id.titleLayout);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout);
            toggleButton = (ImageView) itemView.findViewById(R.id.toggleButton);
            title = (TextView) itemView.findViewById(R.id.title);
            heartWavesView = (HeartWavesView) itemView.findViewById(R.id.heartWaves);
            averageData = (TextView) itemView.findViewById(R.id.averageData);
            maxData = (TextView) itemView.findViewById(R.id.maxData);
            minData = (TextView) itemView.findViewById(R.id.minData);
        }
    }
}
