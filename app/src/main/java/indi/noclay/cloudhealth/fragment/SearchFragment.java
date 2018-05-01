package indi.noclay.cloudhealth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import indi.noclay.cloudhealth.R;


/**
 * Created by NoClay on 2018/5/1.
 */

public class SearchFragment extends Fragment {

    View mView;
    private RelativeLayout searchBar;
    private EditText searchInput;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        initView();
        return mView;
    }


    private void initView() {
        searchBar = (RelativeLayout) mView.findViewById(R.id.search_bar);
        searchInput = (EditText) mView.findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:{
                        Toast.makeText(getContext(), "搜索" + searchInput.getText().toString(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
