package wang.fly.com.yunhealth.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import wang.fly.com.yunhealth.MyViewPackage.CheckableChooseDoseView;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.InputTimeAndDoseDialog;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;

/**
 * Created by noclay on 2017/5/15.
 */

public class AdapterChooseDose extends ArrayAdapter {

    Context mContext;
    int mResource;
    List<InputTimeAndDoseDialog.Dose> mDatas;

    public AdapterChooseDose(Context context, List<InputTimeAndDoseDialog.Dose> datas, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return MyConstants.TIMES.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        InputTimeAndDoseDialog.Dose data = mDatas.get(position);
        holder.mListItem.setTime(MyConstants.TIMES[position]);
        holder.mListItem.setChecked(data.getChecked());
        holder.mListItem.setFloat(data.getValue());
//        holder.mListItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.mListItem.isChecked()){
//                    holder.mListItem.setChecked(false);
//                }else{
//                    holder.mListItem.setChecked(true);
//                }
//                holder.mListItem.toggle();
//            }
//        });
        return view;
    }



    static class ViewHolder {
        View view;
        CheckableChooseDoseView mListItem;

        ViewHolder(View view) {
            this.view = view;
            this.mListItem = (CheckableChooseDoseView) view.findViewById(R.id.list_item);
        }
    }
}
