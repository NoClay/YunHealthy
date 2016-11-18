package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.ResultMessage;

/**
 * Created by 82661 on 2016/11/6.
 */

public class ResultListViewAdapter extends ArrayAdapter<ResultMessage> {
    private int resource;
    public ResultListViewAdapter(Context context, int resource, List<ResultMessage> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultMessage resultMessage = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.labelView = (ImageView) view.findViewById(R.id.result_label_view);
            viewHolder.labelTitle = (TextView) view.findViewById(R.id.result_label_title);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(resultMessage.isDanger()){
            //代表不健康状态
            viewHolder.labelTitle.setTextColor(getContext().getResources().getColor(R.color.red));
            viewHolder.labelTitle.setText(resultMessage.getMessage() + "  " + resultMessage.getScore());

        }else{
            viewHolder.labelTitle.setTextColor(getContext().getResources().getColor(R.color.dimgray));
            viewHolder.labelTitle.setText(resultMessage.getMessage());
        }
        return view;
    }

    class ViewHolder{
        ImageView labelView;
        TextView labelTitle;
    }
}
