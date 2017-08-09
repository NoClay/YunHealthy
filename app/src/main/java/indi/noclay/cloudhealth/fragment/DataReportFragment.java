package indi.noclay.cloudhealth.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.activity.InfoActivityForReport;


/**
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataReportFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout[] layouts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data_report, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        layouts = new RelativeLayout[3];
        layouts[0] = (RelativeLayout) v.findViewById(R.id.firstLayout);
        layouts[1] = (RelativeLayout) v.findViewById(R.id.secondLayout);
        layouts[2] = (RelativeLayout) v.findViewById(R.id.thirdLayout);
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), InfoActivityForReport.class);
        switch (v.getId()) {
            case R.id.firstLayout:{
                intent.putExtra("type", 0);
                break;
            }
            case R.id.secondLayout:{
                intent.putExtra("type", 1);
                break;
            }
            case R.id.thirdLayout:{
                Toast.makeText(getContext(), "病历单为完善", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        startActivity(intent);

    }
}
