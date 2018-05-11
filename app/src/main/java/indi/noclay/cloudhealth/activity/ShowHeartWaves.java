package indi.noclay.cloudhealth.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.UtilClass;
import pers.noclay.ecgwaveview.ECGWaveView;

import static indi.noclay.cloudhealth.util.FileCacheUtil.getCacheDirName;
import static indi.noclay.cloudhealth.util.FileCacheUtil.getDownloadFilePath;


/**
 * Created by 82661 on 2016/11/9.
 */

public class ShowHeartWaves extends AppCompatActivity
        implements ECGWaveView.OnDataChangedListener, View.OnClickListener {

    private ECGWaveView heartWaves;
    private TextView showAverage;
    private TextView showMax;
    private ImageView back;
    private String fileName;
    private String filePath;
    private String fileUrl;

    private static final int MSG_FOR_HEART = 0;
    private static final String TAG = "ShowHeartWaves";
    private TextView infoTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_heart_waves);
        ActivityCollector.addActivity(this);
        UtilClass.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        handleArguments();
        initView();
    }

    private void handleArguments() {
        if (getIntent() == null) {
            return;
        }
        fileName = getIntent().getStringExtra(ConstantsConfig.PARAMS_FILE_NAME);
        fileUrl = getIntent().getStringExtra(ConstantsConfig.PARAMS_FILE_PATH);
        filePath = getDownloadFilePath(fileName);
        File file = new File(filePath);
        if (file.exists() && file.isFile()){
            initData();
        }else{
            //需要加载File
            file = new File(getCacheDirName() + fileName);
            if (file.exists() && file.isFile()){
                pers.noclay.utiltool.FileUtils.copyFile(file, filePath);
                initData();
            }else{
                BmobFile cache = new BmobFile(fileName, null, fileUrl);
                cache.download(new File(filePath), new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            initData();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ShowHeartWaves.this, "下载错误，请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    DataInputStream in = null;
                    try {
                        in = new DataInputStream(new FileInputStream(filePath));
                        byte[] bytes = new byte[2];
                        int count = 0;
                        while (in.read(bytes) != -1) {
                            int value = 0;
                            value += (bytes[0] & 0xFF) * 256;
                            value += (bytes[1] & 0xFF);
                            heartWaves.startRefresh();
                            heartWaves.drawNextPoint(value);
                            Thread.sleep(10);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void initView() {

        heartWaves = (ECGWaveView) findViewById(R.id.heartWaves);
//        showAverage = (TextView) findViewById(R.id.averageHeart);
        heartWaves.setOnDataChangedListener(this);
//        showMax = (TextView) findViewById(R.id.maxHeart);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        heartWaves.startRefresh();
        infoTitle = (TextView) findViewById(R.id.info_title);
        infoTitle.setText(fileName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (heartWaves != null) {
            heartWaves.startRefresh();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (heartWaves != null) {
            heartWaves.stopRefresh();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FOR_HEART: {
                    Log.d(TAG, "handleMessage: " + msg.arg1);
                    heartWaves.drawNextPoint(msg.arg1);
                    break;
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    @Override
    public void onMaxDataChanged(float max) {
//        showMax.setText("峰值：" + max);
    }

    @Override
    public void onAverageDataChanged(float average) {
//        showAverage.setText("平均值： " + average);
    }

    @Override
    public void onMinDataChanged(float min) {

    }
}
