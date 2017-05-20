package wang.fly.com.yunhealth.MyViewPackage.Dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.Adapter.AdapterChooseDose;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;

/**
 * Created by noclay on 2017/5/13.
 */

public class InputTimeAndDoseDialog extends PopupWindow {
    Context mContext;
    View mView;
    ViewHolder mHolder;
    View.OnClickListener mOnClickListener;
    List<Dose> datas = new ArrayList<>();

    public InputTimeAndDoseDialog(Context context,
                                  List<String> times,
                                  List<Float> doses,
                                  View.OnClickListener listener) {
        super(context);
        mContext = context;
        mOnClickListener = listener;
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_time_and_dose, null);
        setContentView(mView);
        mHolder = new ViewHolder(mView);
        initData(times, doses);
        initView();
    }

    public String getTimeByString(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < datas.size(); i++) {
            Dose temp = datas.get(i);
            if (temp.getChecked()){
                builder.append(temp.getTime() + "（" + + temp.getValue() +  "）,");
            }
        }
        return builder.toString();
    }

    private void initData(List<String> times,
                          List<Float> doses) {
        int j = 0;
        for (int i = 0; i < MyConstants.TIMES.length; i++) {
            if (j < times.size() && MyConstants.TIMES[i].equals(times.get(j))){
                Dose data = new Dose(MyConstants.TIMES[i]);
                data.setValue(doses.get(j));
                datas.add(data);
                j ++;
            }else{
                Dose data = new Dose(MyConstants.TIMES[i]);
                datas.add(data);
            }
        }
    }

    public List<String> getTime(){
        List<String> times = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getChecked()){
                times.add(new String(datas.get(i).getTime()));
            }
        }
        return times;
    }

    public List<Float> getDose(){
        List<Float> doses = new ArrayList<>();
        for (int i = 0; i < doses.size(); i++) {
            if (datas.get(i).getChecked()){
                doses.add(new Float(datas.get(i).getValue()));
            }
        }
        return doses;
    }


    private void initView() {
        mHolder.mCancelAction.setOnClickListener(mOnClickListener);
        mHolder.mSubmitAction.setOnClickListener(mOnClickListener);
        final AdapterChooseDose doses = new AdapterChooseDose(mContext, datas,
                R.layout.item_checkable_choose_time);
        mHolder.mListView.setAdapter(doses);
        mHolder.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dose data = datas.get(position);
                data.toggle();
                Log.d("time", "onItemClick: checked = " + data.getChecked());
                Log.d("time", "onItemClick: pos = " + position);
                doses.notifyDataSetChanged();
            }
        });
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x88000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.PopupAnimation);
    }

    static class ViewHolder {
        View view;
        ListView mListView;
        Button mSubmitAction;
        Button mCancelAction;

        ViewHolder(View view) {
            this.view = view;
            this.mListView = (ListView) view.findViewById(R.id.listView);
            this.mSubmitAction = (Button) view.findViewById(R.id.submitAction);
            this.mCancelAction = (Button) view.findViewById(R.id.cancelAction);
        }
    }

    public static class Dose{
        private String mTime;
        private Boolean mChecked;
        private Float mValue;

        public Dose() {
            this("");
        }

        public Dose(String time) {
            this(time, false, 0.25f);
        }

        public Dose(String time, Boolean checked, Float value) {
            mTime = time;
            mChecked = checked;
            mValue = value;
        }

        public String getTime() {
            return mTime;
        }

        public void setTime(String time) {
            mTime = time;
        }

        public Boolean getChecked() {
            return mChecked;
        }

        public void setChecked(Boolean checked) {
            mChecked = checked;
        }

        public Float getValue() {
            return mValue;
        }

        public void setValue(Float value) {
            mValue = value;
        }
        public void toggle(){
            mChecked = !mChecked;
        }
    }
}
