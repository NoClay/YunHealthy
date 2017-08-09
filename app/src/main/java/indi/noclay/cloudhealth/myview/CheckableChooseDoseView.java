package indi.noclay.cloudhealth.myview;

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
import android.widget.Toast;

import indi.noclay.cloudhealth.R;


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
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

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


    public void clearCheck(boolean checked) {
        mCheckbox.setVisibility(GONE);
        mChooseDoseLayout.setVisibility(GONE);
        if (checked) {
            mChooseDoseLayout.setVisibility(VISIBLE);
            mCheckbox.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != isChecked){
            isChecked = checked;
            refreshDrawableState();
        }
        Log.d("time", "setChecked: " + isChecked);
    }

    @Override
    public boolean isChecked() {
        Log.d("time", "isChecked: " + isChecked);
        return isChecked;
    }

    @Override
    public void toggle() {
        Log.d("time", "toggle: ");
        setChecked(!isChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // 在原有状态中添加一个空间space用于保存checked状态
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            // 将checked状态合并到原有的状态数组中
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        Log.d("time", "onCreateDrawableState: isChecked " + isChecked);
        clearCheck(isChecked);
        return drawableState;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.incButton:{
                Toast.makeText(mContext, "点击了增加的按钮", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.decButton:{
                Toast.makeText(mContext, "点击了减少的按钮", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public void setTime(String time) {
        mTime.setText(time);
    }

    public void setFloat(Float value){
        mDose.setText(value.toString());
    }
}
