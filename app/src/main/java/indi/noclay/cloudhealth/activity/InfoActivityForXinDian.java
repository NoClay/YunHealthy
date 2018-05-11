package indi.noclay.cloudhealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.carddata.FileCacheListItem;
import indi.noclay.cloudhealth.myview.DatePickerView;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.FileCacheUtil;

public class InfoActivityForXinDian extends AppCompatActivity
        implements DatePickerView.OnDateChangedListener, RecyclerViewAdapterNormal.OnItemClickListener{

    private TextView infoTitle;
    private ImageView back;
    private DatePickerView datePicker;
    private RecyclerView fileCacheList;
    private RecyclerViewAdapterNormal mAdapterNormal;
    private List<Object> mObjects = new ArrayList<>();
    public static final int MSG_DATA_CHANGE = 0;
    public static final int MSG_LOAD_START = 1;
    public static final int MSG_LOAD_SUCCESS = 2;
    public static final int MSG_LOAD_FAILED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_for_xindian);
        initView();
    }

    private void initView() {
        infoTitle = (TextView) findViewById(R.id.info_title);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        infoTitle.setText("心电图");
        datePicker = (DatePickerView) findViewById(R.id.date_picker);
        fileCacheList = (RecyclerView) findViewById(R.id.fileCacheList);
        fileCacheList.setLayoutManager(new LinearLayoutManager(this));
        fileCacheList.setHasFixedSize(false);
        mAdapterNormal = new RecyclerViewAdapterNormal();
        mAdapterNormal.setDatas(mObjects);
        mAdapterNormal.setmActivity(this);
        mAdapterNormal.setmContext(this);
        mAdapterNormal.setOnItemClickListener(this);
        fileCacheList.setAdapter(mAdapterNormal);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.setOnDateChangedListener(this);
        getFileList(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateChanged(int year, int month, int day) {
        getFileList(year, month, day);
    }


    @Override
    public void onItemClick(Object o, int position, int layoutPosition) {
        if (o instanceof FileCacheListItem){
            Intent intent = new Intent(this, ShowHeartWaves.class);
            intent.putExtra(ConstantsConfig.PARAMS_FILE_NAME, ((FileCacheListItem) o).getName());
            intent.putExtra(ConstantsConfig.PARAMS_FILE_PATH, ((FileCacheListItem) o).getFilePath());
            startActivity(intent);
        }
    }

    private class FileHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOAD_SUCCESS:{
                    mAdapterNormal.setDatas(mObjects);
                    mAdapterNormal.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
    private Handler mHandler = new FileHandler();

    public void getFileList(int year, int month, int day){

        File dir = new File(FileCacheUtil.getCacheDirName());
        if (dir.exists() && dir.isDirectory()){
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.exists() && file.isFile()
                        && file.getName().startsWith(year + "_" + month + "_" + day)
                        && file.getName().endsWith(".bin")){
                    FileCacheListItem item = new FileCacheListItem();
                    item.setName(file.getName());
                    item.setFilePath(file.getAbsolutePath());
                    item.setLength(file.length() + "");
                    mObjects.add(item);
                }
            }
            mHandler.obtainMessage(MSG_LOAD_SUCCESS).sendToTarget();
        }
    }
}
