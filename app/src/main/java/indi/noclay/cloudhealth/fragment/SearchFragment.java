package indi.noclay.cloudhealth.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.activity.SearchResultActivity;
import indi.noclay.cloudhealth.util.ConstantsConfig;

import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_MEDICINE_COMPANY_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_MEDICINE_INFO;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_NORMAL_ILLNESS;


/**
 * Created by NoClay on 2018/5/1.
 */

public class SearchFragment extends Fragment {

    View mView;
    private RelativeLayout searchBar;
    private EditText searchInput;
    private AppCompatRadioButton medicineInfoBt;
    private AppCompatRadioButton medicineCompanyInfoBt;
    private AppCompatRadioButton normalIllnessBt;
    private TextView actionSearch;
    private RadioGroup mRadioGroup;
    private int searchType;



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
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        onClickSearch();
                        break;
                    }
                }
                return false;
            }
        });
        medicineInfoBt = (AppCompatRadioButton) mView.findViewById(R.id.medicineInfoBt);
        medicineCompanyInfoBt = (AppCompatRadioButton) mView.findViewById(R.id.medicineCompanyInfoBt);
        normalIllnessBt = (AppCompatRadioButton) mView.findViewById(R.id.normalIllnessBt);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.medicineInfoBt:
                        searchType = TYPE_MEDICINE_INFO;
                        break;
                    case R.id.medicineCompanyInfoBt:
                        searchType = TYPE_MEDICINE_COMPANY_INFO;
                        break;
                    case R.id.normalIllnessBt:
                        searchType = TYPE_NORMAL_ILLNESS;
                        break;
                }
            }
        });
        actionSearch = (TextView) mView.findViewById(R.id.actionSearch);
        actionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearch();
            }
        });
    }

    private void onClickSearch() {
        if (TextUtils.isEmpty(searchInput.getText().toString())){
            Toast.makeText(getActivity(), R.string.hint_search_key_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsConfig.PARAMS_KEY_WORD, searchInput.getText().toString());
        bundle.putInt(ConstantsConfig.PARAMS_SEARCH_TYPE, searchType);
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(ConstantsConfig.PARAMS_BUNDLE, bundle);
        startActivity(intent);
    }
}
