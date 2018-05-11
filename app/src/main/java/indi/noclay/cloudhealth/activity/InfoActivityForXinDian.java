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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.adapter.RecyclerViewAdapterNormal;
import indi.noclay.cloudhealth.database.MeasureXinDian;
import indi.noclay.cloudhealth.database.SignUserData;
import indi.noclay.cloudhealth.myview.DatePickerView;
import indi.noclay.cloudhealth.util.ConstantsConfig;

public class InfoActivityForXinDian extends AppCompatActivity
        implements DatePickerView.OnDateChangedListener, RecyclerViewAdapterNormal.OnItemClickListener {

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
        if (o instanceof MeasureXinDian) {
            Intent intent = new Intent(this, ShowHeartWaves.class);
            intent.putExtra(ConstantsConfig.PARAMS_FILE_NAME, ((MeasureXinDian) o).getFileName());
            intent.putExtra(ConstantsConfig.PARAMS_FILE_PATH, ((MeasureXinDian) o).getFileUrl());
            startActivity(intent);
        }
    }

    private class FileHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOAD_SUCCESS: {
                    mAdapterNormal.setDatas(mObjects);
                    mAdapterNormal.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    private Handler mHandler = new FileHandler();

    public Date getStartDate(int year, int month, int day) {
        String start = String.format("%4d-%2d-%2d 00:00:00", year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public Date getEndDate(int year, int month, int day) {
        String end = String.format("%4d-%2d-%2d 23:59:59", year, month, day);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public void getFileList(int year, int month, int day) {
        BmobQuery<MeasureXinDian> query = new BmobQuery<>();
        List<BmobQuery<MeasureXinDian>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<MeasureXinDian> q1 = new BmobQuery<>();
        q1.addWhereGreaterThanOrEqualTo("createDate", new BmobDate(getStartDate(year, month, day)));
        //小于23：59：59
        BmobQuery<MeasureXinDian> q2 = new BmobQuery<>();
        q2.addWhereLessThanOrEqualTo("createDate", new BmobDate(getEndDate(year, month, day)));
        BmobQuery<MeasureXinDian> q3 = new BmobQuery<>();
        and.add(q1);
        and.add(q2);
        SignUserData login = new SignUserData();
        login.setObjectId(ConstantsConfig.userId);
        q3.addWhereEqualTo("owner", new BmobPointer(login));
        and.add(q3);
//添加复合与查询
        query.and(and);
        query.order("createDate").setLimit(100)
                .findObjects(new FindListener<MeasureXinDian>() {
                    @Override
                    public void done(List<MeasureXinDian> list, BmobException e) {
                        if (e != null || list == null) {
                            mHandler.obtainMessage(MSG_LOAD_FAILED).sendToTarget();
                        } else {
                            mObjects.clear();
                            for (int i = 0; i < list.size(); i++) {
                                mObjects.add(list.get(i));
                            }
                            mHandler.obtainMessage(MSG_LOAD_SUCCESS).sendToTarget();
                        }
                    }
                });

    }
}
