package indi.noclay.cloudhealth.myview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import indi.noclay.cloudhealth.R;

/**
 * Created by clay on 2018/4/13.
 */

public class YunHealthyErrorView extends LinearLayout {

    private LottieAnimationView errorView;
    private TextView errorTips;
    public YunHealthyErrorView(Context context) {
        super(context);
        initAttrs(context,null);
    }

    public YunHealthyErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public YunHealthyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    public void initAttrs(Context context, AttributeSet attributeSet){
        LayoutInflater.from(context).inflate(R.layout.view_yun_healthy_error, this, true);
        
    }

}
