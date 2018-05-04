package indi.noclay.cloudhealth.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.fragment.DataDynamicFragment;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;

import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_FRAGMENT_TYPE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.PARAMS_TITLE;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_FRAGMENT_DYNAMIC;
import static indi.noclay.cloudhealth.util.ConstantsConfig.TYPE_FRAGMENT_MEDICINE;

public class FragmentContainerActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView infoTitle;
    private ImageView back;
    private FrameLayout container;
    private int fragmentType;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        initView();
        handleArguments();
    }

    private void handleArguments() {
        if (getIntent() == null){
            finish();
        }
        fragmentType = getIntent().getIntExtra(PARAMS_FRAGMENT_TYPE, TYPE_FRAGMENT_DYNAMIC);
        title = getIntent().getStringExtra(PARAMS_TITLE);
        infoTitle.setText(title);
        FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
        switch (fragmentType){
            case TYPE_FRAGMENT_DYNAMIC:{
                transition.replace(R.id.container, new DataDynamicFragment());
                break;
            }
            case TYPE_FRAGMENT_MEDICINE:{
                transition.replace(R.id.container, new DataMedicalFragment());
                break;
            }
        }
        transition.commit();
    }

    private void initView() {
        infoTitle = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        container = (FrameLayout) findViewById(R.id.container);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:finish();break;
        }
    }
}
