package wang.fly.com.yunhealth.Activity;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import wang.fly.com.yunhealth.MyViewPackage.HeartWavesView;
import wang.fly.com.yunhealth.R;

/**
 * Created by 82661 on 2016/11/9.
 */

public class ShowHeartWaves extends AppCompatActivity implements HeartWavesView.
        onDataChangedlistener, View.OnClickListener {

    private HeartWavesView heartWaves;
    private TextView showAverage;
    private TextView showMax;
    private ImageView back;

    private static final int MSG_FOR_HEART = 0;
    private static final String TAG = "ShowHeartWaves";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_heart_waves);
        ActivityCollector.addActivity(this);
        initView();
        initData();
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
                while (true){
                    Message me = new Message();
                    me.what = MSG_FOR_HEART;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    me.arg1 = new Random().nextInt(4000) - 2000;
                    handler.sendMessage(me);
                }
            }
        }.start();
    }

    private void initView() {
        heartWaves = (HeartWavesView) findViewById(R.id.heartWaves);
        showAverage = (TextView) findViewById(R.id.averageHeart);
        showMax = (TextView) findViewById(R.id.maxHeart);
        heartWaves.setOnDataChangedlistener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_FOR_HEART:{
                    Log.d(TAG, "handleMessage: " + msg.arg1);
                    heartWaves.drawNextPoint(msg.arg1);
                    break;
                }
            }
        }
    };

    @Override
    public void getMaxData(float max) {
        showMax.setText("峰值：" + max);
    }

    @Override
    public void getAverage(float average) {
        showAverage.setText("平均值： " + average);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
        }
    }
}
