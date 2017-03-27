package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by no_clay on 2017/1/25.
 */

public class InputWeightDialog extends PopupWindow {
    View.OnClickListener mOnClickListener;
    EditText mHeightInput;
    EditText mWeightInput;
    float mHeight;
    float mWeight;


    public InputWeightDialog(Context context, View.OnClickListener listener) {
        super(context);
        this.mOnClickListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_height_and_weight, null);
        view.findViewById(R.id.commit_action).setOnClickListener(listener);
        mHeightInput = (EditText) view.findViewById(R.id.input_height);
        mWeightInput = (EditText) view.findViewById(R.id.input_weight);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_window_background));
//        this.getBackground().setAlpha(200);
//        ColorDrawable dw = new ColorDrawable(0x88000000);
        this.setBackgroundDrawable(null);
        this.setAnimationStyle(R.style.PopupAnimation);
        //设置wiindow软启动
    }

    /**
     * 检查身高与体重输入数据是否符合规定
     *
     * @return
     */
    public boolean checkData(){
        if (!mHeightInput.getText().toString().matches("^[0-9]+\\.{0,1}[0-9]{0,6}$")
                || !mWeightInput.getText().toString().matches("^[0-9]+\\.{0,1}[0-9]{0,6}$")){
            return false;
        }
        mHeight = Float.valueOf(mHeightInput.getText().toString());
        mWeight = Float.valueOf(mWeightInput.getText().toString());
        if (UtilClass.compareDouble(mHeight, 0) <= 0
                || UtilClass.compareDouble(mWeight, 0) <= 0){
            //有小于0的数
            return false;
        }else{
            return true;
        }
    }

    public void updateLocation(int x, int y){
        update(x, y,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
    }



    public float getInputHeight() {
        return mHeight;
    }

    public float getInputWeight() {
        return mWeight;
    }

    public void setInput(float height, float weight){
        mHeightInput.setText(height + "");
        mWeightInput.setText(weight + "");
    }
}
