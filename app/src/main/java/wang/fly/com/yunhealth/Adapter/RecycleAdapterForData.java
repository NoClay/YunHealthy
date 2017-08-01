package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wang.fly.com.yunhealth.MyViewPackage.CircleImageView;
import wang.fly.com.yunhealth.R;

/**
 * Created by 82661 on 2016/11/23.
 */

public class RecycleAdapterForData extends RecyclerView.Adapter<RecycleAdapterForData.ViewHolder>{

    private static final String TAG = "RecycleAdapterForMeasur";
    private List<Map<String,Object>> datas = new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;
    private int layout;
    private Context context;
    private static final int COLOR_NORMAL = Color.BLACK;
    private static final int COLOR_DANGER = R.color.danger;

    public RecycleAdapterForData(List<Map<String,Object>> datas, int layout, Context context){
        this.datas = datas;
        this.layout = layout;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public RecycleAdapterForData.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(datas.get(position).get("text").toString());
        holder.mImageView.setImageResource((Integer) datas.get(position).get("id"));
        holder.mImageView.setBackgroundColor(context.getResources().
                getColor((int)datas.get(position).get("color"), null));
        boolean flag = (boolean) datas.get(position).get("isDanger");
        if (flag){
            holder.mTextView.setTextColor(context.getResources().getColor(COLOR_DANGER, null));
        }else{
            holder.mTextView.setTextColor(COLOR_NORMAL);
        }
        holder.itemView.setTag(datas.get(position));
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public CircleImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.title_and_data);
            mImageView = (CircleImageView) itemView.findViewById(R.id.label_image);
        }
    }
}
