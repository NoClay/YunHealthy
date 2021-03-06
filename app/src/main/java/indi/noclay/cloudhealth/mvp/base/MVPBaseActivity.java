package indi.noclay.cloudhealth.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import indi.noclay.cloudhealth.mvp.base.BasePresenter;

/**
 * Created by no_clay on 2017/4/3.
 */

public abstract class MVPBaseActivity<V extends Object, T extends BasePresenter<V>> extends AppCompatActivity {
    protected T mPresenter;

    @Override
    @SuppressWarnings("忽略类型转换")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if (this instanceof MVPBaseActivity){
            mPresenter.attachView((V) this);
        }
    }

    protected abstract T createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
