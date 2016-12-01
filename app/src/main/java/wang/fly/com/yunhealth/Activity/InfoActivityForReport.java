package wang.fly.com.yunhealth.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import wang.fly.com.yunhealth.Adapter.RecycleAdapterForReportMenu;
import wang.fly.com.yunhealth.R;

public class InfoActivityForReport extends AppCompatActivity
        implements View.OnClickListener, RecycleAdapterForReportMenu.OnItemClickListener{

    private TextView title;
    private ImageView back;
    private RecyclerView menuView;
    private ImageView manage;
    private RecycleAdapterForReportMenu adapter;
    public final Context context = InfoActivityForReport.this;
    /*
    0 ---检验单
    1 ---检查单
    2 ---病理单
     */
    private int type;
    private static final String TAG = "MyDataBase";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_for_report);
        ActivityCollector.addActivity(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        type = getIntent().getIntExtra("type" , 0);

        title = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        menuView = (RecyclerView) findViewById(R.id.menuRecycleView);
        manage = (ImageView) findViewById(R.id.manage);

        String[] titles = {"检验单", "检查单"};
        if (type < titles.length){
            title.setText(titles[type]);
        }
        back.setOnClickListener(this);
        manage.setOnClickListener(this);
        adapter = new RecycleAdapterForReportMenu(
                        false,
                        R.layout.recycle_item_for_info_report,
                        getApplicationContext(),
                        type + 1);
        adapter.setOnItemClickListener(this);
        menuView.setAdapter(adapter);
        menuView.setLayoutManager(new LinearLayoutManager(context));
        menuView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                //退出按钮
                finish();
                break;
            }
            case R.id.manage:{
                //进入管理页面
                adapter.toggleManage();
//                Intent intent = new Intent(context, ReportMenuManage.class);
//                intent.putExtra("type", type);
//                startActivityForResult(intent, 0);
                break;
            }
        }
    }

    @Override
    public void onItemClick(View view, String title) {
        Log.d(TAG, "onItemClick: Id" + view.getId());
        Log.d(TAG, "onItemClick: title" + title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            Log.d(TAG, "onActivityResult: 返回");
        }
    }
}
