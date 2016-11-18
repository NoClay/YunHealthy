package wang.fly.com.yunhealth.MyViewPackage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.x;
import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by 82661 on 2016/10/17.
 */

public class ResultBox extends ViewGroup {

    private boolean isShowResult = false;
    private Scroller mScroller;
    private int screenWidth;
    private VelocityTracker mVelocityTracker;
    private static final String TAG = "HorizontalScrollViewEx";


    public ResultBox(Context context) {
        super(context);
        init();
    }

    public ResultBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }





    /**
     * 重写onMeasure方法，对容器内的测量进行修正
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * super.onMeasure方法内部调用了setMeasuredDimension(getDefaultSize(
         * getSuggestedMinimumWidth(), widthMeasureSpec),
         * getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
         */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = 0;
        int measuredHeight = 0;
        final int childCount = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpaceMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpaceMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.d(TAG, "onMeasure: child" + childCount);
        if(childCount == 0){
            //no child -- no width, no height
            setMeasuredDimension(0, 0);
        }else{
            //width is wrap but height not
            final View childView = getChildAt(0);
            measuredWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(measuredWidth, heightSpaceSize);
            screenWidth = childView.getMeasuredWidth();
            Log.d(TAG, "onMeasure: measure");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++){
            final View childView = getChildAt(i);
            if(childView.getVisibility() != View.GONE){
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth,
                        childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    /**
     * 两个startScrollBy方法，一个加上了时间间隔，一个并没有
     * 之前整错了，用错了方法
     * @param dx
     */
    private void smoothScrollBy(int dx) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }


    public void toggleResutl(){
        Log.d(TAG, "toggleResutl: isShowResult" + isShowResult);
        if(isShowResult){
            smoothScrollBy(-screenWidth);
            isShowResult =  false;
        }else{
            smoothScrollBy(screenWidth);
            isShowResult = true;
        }
    }

}
