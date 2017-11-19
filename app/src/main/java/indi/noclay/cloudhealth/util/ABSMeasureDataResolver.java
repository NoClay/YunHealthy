package indi.noclay.cloudhealth.util;

/**
 * Created by NoClay on 2017/11/19.
 */

public abstract class ABSMeasureDataResolver {
    public OnResolveListener mOnResolveListener;
    public abstract void resolveData(byte[] datas);
    public interface OnResolveListener{
        public void onResolve(String result, int type);
    }

    public OnResolveListener getOnResolveListener() {
        return mOnResolveListener;
    }

    public void setOnResolveListener(OnResolveListener onResolveListener) {
        mOnResolveListener = onResolveListener;
    }
}
