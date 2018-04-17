package indi.noclay.cloudhealth.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.MedicineListAdapter;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;
import indi.noclay.cloudhealth.myview.FullLinearLayoutManager;
import indi.noclay.cloudhealth.util.ConstantsConfig;

import static indi.noclay.cloudhealth.database.MedicineTableHelper.getMedicineDetail;


public class MedicineActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private int type;
    private CollapsingToolbarLayout mToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private RecyclerView mMedicineList;
    private List<MedicineDetail> medicines;
    private String time = null;
    private Context mContext = this;
    private LocalDataBase dbHelper;
    public static final int ADD_MEDICINE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        initView();
        medicines = getMedicines();
        MedicineListAdapter adapter = new MedicineListAdapter(medicines, this, R.layout.item_medicine);
        FullLinearLayoutManager fLayout = new FullLinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        fLayout.setSmoothScrollbarEnabled(true);
        mMedicineList.setLayoutManager(fLayout);
        mMedicineList.setHasFixedSize(true);
        mMedicineList.setNestedScrollingEnabled(false);
        mMedicineList.setAdapter(adapter);
    }


    private void initView() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        initActionBar();
        mMedicineList = (RecyclerView) findViewById(R.id.medicineList);
        dbHelper = new LocalDataBase(mContext,
                "LocalStore.db", null, ConstantsConfig.DATABASE_VERSION);
    }

    private void initActionBar() {
        type = getIntent().getIntExtra("type", DataMedicalFragment.NOW_MEDICINE);
        if (type == DataMedicalFragment.NOW_MEDICINE) {
            mToolbarLayout.setTitle("当前用药");
        } else if (type == DataMedicalFragment.LAST_MEDICINE){
            mToolbarLayout.setTitle("历史用药");
        } else{
            mToolbarLayout.setTitle("该吃药了");
            time = getIntent().getStringExtra("time");
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (type == DataMedicalFragment.NOW_MEDICINE) {
                    Intent intent = new Intent(mContext, AddMedicineActivity.class);
                    startActivityForResult(intent, ADD_MEDICINE);
                } else {
//                    mToolbarLayout.setTitle("历史用药");
                }
                break;
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public List<MedicineDetail> getMedicines() {
        return getMedicineDetail(type, time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_MEDICINE:{
                medicines.clear();
                medicines.addAll(getMedicines());
                break;
            }
        }
    }
}
