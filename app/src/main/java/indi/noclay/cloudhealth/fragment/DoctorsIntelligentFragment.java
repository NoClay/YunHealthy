package indi.noclay.cloudhealth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import indi.noclay.cloudhealth.R;


/**
 * Created by noclay on 2017/5/10.
 */

public class DoctorsIntelligentFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctors_intelligent, container, false);
        return view;
    }
}
