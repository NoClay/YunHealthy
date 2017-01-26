package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import wang.fly.com.yunhealth.R;

/**
 * Created by no_clay on 2017/1/24.
 */

public class InputBlueMacDialog extends PopupWindow {
    Context mContext;
    View.OnClickListener mOnClickListener;
    EditText macAddress;
    View mView;

    public InputBlueMacDialog(Context context, View.OnClickListener mOnClickListener) {
        super(context);
        this.mContext = context;
        this.mOnClickListener = mOnClickListener;
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_input_blue_mac, null);
        this.setContentView(mView);
        initView();
    }

    private void initView() {
        mView.findViewById(R.id.connect_mac_device).setOnClickListener(this.mOnClickListener);
        mView.findViewById(R.id.cancel_action).setOnClickListener(this.mOnClickListener);
        macAddress = (EditText) mView.findViewById(R.id.input_mac);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.dialog_window_background));
//        this.getBackground().setAlpha(200);
        ColorDrawable dw = new ColorDrawable(0x88000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.PopupAnimation);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public String getMacAddress() {
        return macAddress.getText().toString();
    }

    public void setMacHint(String string) {
        macAddress.setHint(string);
    }
}
