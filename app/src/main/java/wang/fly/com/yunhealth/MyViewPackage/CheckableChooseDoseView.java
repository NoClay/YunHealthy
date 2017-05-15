package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wang.fly.com.yunhealth.R;

/**
 * Created by noclay on 2017/5/15.
 */

public class CheckableChooseDoseView extends RelativeLayout implements Checkable, View.OnClickListener {

    Context mContext;
    ImageView mCheckbox;
    TextView mTime;
    ImageView mIncButton;
    EditText mDose;
    ImageView mDecButton;
    LinearLayout mChooseDoseLayout;
    private boolean isChecked = false;

    public CheckableChooseDoseView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CheckableChooseDoseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public CheckableChooseDoseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_checkable_choose_time_and_dose,
                this, true);
        mCheckbox = (ImageView) findViewById(R.id.checkbox);
        mTime = (TextView) findViewById(R.id.time);
        mIncButton = (ImageView) findViewById(R.id.incButton);
        mDose = (EditText) findViewById(R.id.dose);
        mDecButton = (ImageView) findViewById(R.id.decButton);
        mChooseDoseLayout = (LinearLayout) findViewById(R.id.chooseDoseLayout);
        clearCheck(isChecked);
    }


    private void clearCheck(boolean checked) {
        mCheckbox.setVisibility(GONE);
        mChooseDoseLayout.setVisibility(GONE);
        if (checked) {
            mChooseDoseLayout.setVisibility(VISIBLE);
            mCheckbox.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        clearCheck(isChecked);
    }

    @Override
    public void onClick(View v) {

    }

    public void setTime(String time) {
        mTime.setText(time);
    }

    public void setFloat(Float value){
        mDose.setText(value.toString());
    }
}
