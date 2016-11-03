package wang.fly.com.yunhealth.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wang.fly.com.yunhealth.R;


/*
 * Created by 兆鹏 on 2016/11/3.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Map<String,Object>> datas = new ArrayList<>();

    public MyAdapter(List<Map<String,Object>> datas){
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycle_item_layout,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(datas.get(position).get("text").toString());
        holder.mImageView.setImageResource((Integer) datas.get(position).get("id"));
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.home_recycle_item_tv);
            mImageView = (ImageView) itemView.findViewById(R.id.home_recycle_item_im);
        }
    }
}
