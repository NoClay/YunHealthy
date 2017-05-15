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
import wang.fly.com.yunhealth.MyViewPackage.CheckableChooseDoseView;
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

    public InputTimeAndDoseDialog(Context context, View.OnClickListener listener) {
        super(context);
        mContext = context;
        mOnClickListener = listener;
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_time_and_dose, null);
        setContentView(mView);
        mHolder = new ViewHolder(mView);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < MyConstants.TIMES.length; i++) {
            Dose data = new Dose(MyConstants.TIMES[i]);
            datas.add(data);
        }
    }

    private void initView() {
        mHolder.mCancelAction.setOnClickListener(mOnClickListener);
        mHolder.mSubmitAction.setOnClickListener(mOnClickListener);
        mHolder.mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final AdapterChooseDose doses = new AdapterChooseDose(mContext, datas, R.layout.item_checkable_choose_time);
        mHolder.mListView.setAdapter(doses);
        mHolder.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean checked = mHolder.mListView.isItemChecked(position);

                CheckableChooseDoseView view1 = (CheckableChooseDoseView) parent.getChildAt(position);
                datas.get(position).setChecked(false);
                view1.setChecked(checked);
                Log.d("time", "onItemClick: pos = " + position);
                Log.d("time", "onItemClick: from list " + checked);
                Log.d("time", "onItemClick: from data " + datas.get(position).getChecked());
                Log.d("time", "onItemClick: from view " + view1.isChecked());
                view1.toggle();

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
            this(time, false, 0.0f);
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
    }
}
