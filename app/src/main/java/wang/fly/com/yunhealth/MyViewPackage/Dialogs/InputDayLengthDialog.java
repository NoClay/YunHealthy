package wang.fly.com.yunhealth.MyViewPackage.Dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;

/**
 * Created by noclay on 2017/5/7.
 */

public class InputDayLengthDialog extends PopupWindow implements ListView.OnItemClickListener{
    Context mContext;
    View mView;
    ListView mListView;
    OnChooseChangedListener onChooseChangedListener;

    public interface OnChooseChangedListener{
        void onChooseChanged(int pos);
    }


    public InputDayLengthDialog(Context context, OnChooseChangedListener listener) {
        super(context);
        mContext = context;
        onChooseChangedListener = listener;
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_input_day_length, null);
        this.setContentView(mView);
        initView();
    }

    private void initView() {
        mListView = (ListView) mView.findViewById(R.id.listView);
        SimpleAdapter adapter = new SimpleAdapter(mContext, getData(), R.layout.item_choose_day_length
        , new String[]{"time"}, new int[]{R.id.time});
        mListView.setAdapter(adapter);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_window_background));
//        this.getBackground().setAlpha(200);
        ColorDrawable dw = new ColorDrawable(0x88000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.PopupAnimation);
        mListView.setOnItemClickListener(this);
    }

    public List<Map<String, String>> getData() {
        List<Map<String, String>> mData = new ArrayList<>();
        for (int i = 0; i < MyConstants.TIME_ITEM.length; i++) {
            Map<String, String> temp = new HashMap<>();
            temp.put("time", MyConstants.TIME_ITEM[i]);
            mData.add(temp);
        }
        return mData;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.dismiss();
        onChooseChangedListener.onChooseChanged(position);
    }
}
