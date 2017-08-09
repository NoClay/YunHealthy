package indi.noclay.cloudhealth.mvp.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by no_clay on 2017/4/3.
 */

public abstract class BasePresenter<T> {
    protected Reference<T> mViewRef;
    public void attachView(T view){
        mViewRef = new WeakReference<T>(view);//弱引用关联
    }
    protected T getView(){
        return mViewRef.get();
    }
    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }
    //接触关联
    public void detachView(){
        if (mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }
}
