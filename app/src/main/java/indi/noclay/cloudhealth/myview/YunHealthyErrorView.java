package indi.noclay.cloudhealth.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private LinearLayout errorLayout;
    private String errorHint;
    private int errorHintSize;
    private String errorAnimFileName;
    private int errorHintColor;
    private int errorAnimWidth;
    private int errorAnimHeight;
    private OnErrorRetryListener onErrorRetryListener;

    public OnErrorRetryListener getOnErrorRetryListener() {
        return onErrorRetryListener;
    }

    public void setOnErrorRetryListener(OnErrorRetryListener onErrorRetryListener) {
        this.onErrorRetryListener = onErrorRetryListener;
    }

    public YunHealthyErrorView(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public YunHealthyErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public YunHealthyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    public void initAttrs(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.view_yun_healthy_error, this, true);
        errorView = (LottieAnimationView) findViewById(R.id.errorAnimation);
        errorTips = (TextView) findViewById(R.id.errorTip);
        errorLayout = (LinearLayout) findViewById(R.id.errorLayer);
        if (attributeSet != null) {
            TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.YunHealthyErrorView);
            errorAnimFileName = array.getString(R.styleable.YunHealthyErrorView_errorAnimFile);
            errorHint = array.getString(R.styleable.YunHealthyErrorView_errorTip);
            errorAnimWidth = array.getLayoutDimension(R.styleable.YunHealthyErrorView_errorAnimWidth, LayoutParams.WRAP_CONTENT);
            errorAnimHeight = array.getLayoutDimension(R.styleable.YunHealthyErrorView_errorAnimHeight, LayoutParams.WRAP_CONTENT);
            errorHintColor = array.getColor(R.styleable.YunHealthyErrorView_errorTipColor, Color.BLACK);
            errorHintSize = array.getDimensionPixelSize(R.styleable.YunHealthyErrorView_errorTipSize, 24);
            array.recycle();
        }
        initView();
    }

    private void initView() {
        ViewGroup.LayoutParams lp = errorView.getLayoutParams();
        lp.width = errorAnimWidth;
        lp.height = errorAnimHeight;
        errorView.setLayoutParams(lp);
        errorView.setAnimation(errorAnimFileName);
        errorTips.setTextSize(errorHintSize);
        errorTips.setTextColor(errorHintColor);
        errorTips.setText(errorHint);
        errorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onErrorRetryListener != null) {
                    onErrorRetryListener.onErrorRetry();
                }
            }
        });
    }

    public interface OnErrorRetryListener {
        void onErrorRetry();
    }
}
