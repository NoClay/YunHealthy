package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by no_clay on 2017/1/25.
 */

public class InputWeightDialog extends PopupWindow {
    View.OnClickListener mOnClickListener;
    EditText height;
    EditText weight;
    float mHeight;
    float mWeight;

    public InputWeightDialog(Context context, View.OnClickListener listener) {
        super(context);
        this.mOnClickListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_height_and_weight, null);
        view.findViewById(R.id.commit_action).setOnClickListener(listener);
        height = (EditText) view.findViewById(R.id.input_height);
        weight = (EditText) view.findViewById(R.id.input_weight);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_window_background));
//        this.getBackground().setAlpha(200);
        ColorDrawable dw = new ColorDrawable(0x88000000);
//        this.setBackgroundDrawable(dw);
        this.setBackgroundDrawable(null);
        this.setAnimationStyle(R.style.PopupAnimation);
    }

    /**
     * 检查身高与体重输入数据是否符合规定
     *
     * @return
     */
    public boolean checkData(){
        if (!height.getText().toString().matches("^[0-9]+\\.{0,1}[0-9]{0,6}$")
                || !weight.getText().toString().matches("^[0-9]+\\.{0,1}[0-9]{0,6}$")){
            return false;
        }
        mHeight = Float.valueOf(height.getText().toString());
        mWeight = Float.valueOf(weight.getText().toString());
        if (UtilClass.compareDouble(mHeight, 0) <= 0
                || UtilClass.compareDouble(mWeight, 0) <= 0){
            //有小于0的数
            return false;
        }else{
            return true;
        }
    }


    public float getInputHeight() {
        return mHeight;
    }

    public float getInputWeight() {
        return mWeight;
    }
}
