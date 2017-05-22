package wang.fly.com.yunhealth.MVP.Bases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by no_clay on 2017/4/3.
 */

public abstract class MVPBaseFragment<V, T extends BasePresenter<V>> extends Fragment {
    protected T mPresenter;


    public T getPresenter() {
        return mPresenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }


    protected abstract T createPresenter();


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
