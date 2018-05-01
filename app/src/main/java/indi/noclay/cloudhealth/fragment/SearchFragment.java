package indi.noclay.cloudhealth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import indi.noclay.cloudhealth.R;


/**
 * Created by NoClay on 2018/5/1.
 */

public class SearchFragment extends Fragment {

    View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        initWindowInputMode();
        return mView;
    }



    private void initWindowInputMode() {
        this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getActivity().getWindow().setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}
