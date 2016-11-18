package wang.fly.com.yunhealth.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.R;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by 兆鹏 on 2016/11/2.
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private TextView title;
    private ImageView back;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.minefragment_layout,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        title = (TextView) v.findViewById(R.id.info_title);
        back = (ImageView) v.findViewById(R.id.back);

        title.setText("我的");
        back.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
