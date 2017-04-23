package wang.fly.com.yunhealth.Activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import wang.fly.com.yunhealth.Adapter.MedicineListAdapter;
import wang.fly.com.yunhealth.DataBasePackage.MedicineDetial;
import wang.fly.com.yunhealth.Fragments.DataMedicalFragment;
import wang.fly.com.yunhealth.R;

public class MedicineActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private int type;
    private CollapsingToolbarLayout mToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private RecyclerView mMedicineList;
    private List<MedicineDetial> medicineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        initView();
        medicineList = getMedicineList();
        MedicineListAdapter adapter = new MedicineListAdapter(medicineList, this, R.layout.item_medicine);
        mMedicineList.setLayoutManager(new LinearLayoutManager(this));
        mMedicineList.setHasFixedSize(true);
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
    }

    private void initActionBar() {
        type = getIntent().getIntExtra("type", DataMedicalFragment.NOW_MEDICINE);
        if (type == DataMedicalFragment.NOW_MEDICINE) {
            mToolbarLayout.setTitle("当前用药");
        } else {
            mToolbarLayout.setTitle("历史用药");
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
                break;
        }
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    public List<MedicineDetial> getMedicineList() {

        List<MedicineDetial> temp = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MedicineDetial te = new MedicineDetial();
            temp.add(te);
        }
        return temp;
    }
}
