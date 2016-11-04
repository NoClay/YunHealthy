package wang.fly.com.yunhealth.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wang.fly.com.yunhealth.R;

/**
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataDataFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_data_fragment,container,false);
        return v;
    }
}
