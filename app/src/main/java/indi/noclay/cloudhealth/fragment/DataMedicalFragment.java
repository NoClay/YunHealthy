package indi.noclay.cloudhealth.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.activity.MedicineActivity;


/*
 * Created by 兆鹏 on 2016/11/5.
 */
public class DataMedicalFragment extends Fragment implements View.OnClickListener {
    private View view;
    /**
     * 当前用药
     */
    private TextView mNowMedicine;
    /**
     * 历史用药
     */
    private TextView mLastMedicine;
    public static final int NOW_MEDICINE = 0;
    public static final int LAST_MEDICINE = 1;
    public static final int CLOCK_MEDICINE = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data_medicine, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mNowMedicine = (TextView) v.findViewById(R.id.nowMedicine);
        mLastMedicine = (TextView) v.findViewById(R.id.lastMedicine);
        mNowMedicine.setOnClickListener(this);
        mLastMedicine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), MedicineActivity.class);
        switch (v.getId()) {
            case R.id.nowMedicine:
                intent.putExtra("type", NOW_MEDICINE);
                break;
            case R.id.lastMedicine:
                intent.putExtra("type", LAST_MEDICINE);
                break;
        }
        startActivity(intent);
    }
}
