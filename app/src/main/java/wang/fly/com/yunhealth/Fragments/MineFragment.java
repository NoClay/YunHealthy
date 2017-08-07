package wang.fly.com.yunhealth.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.activity.CaptureActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import wang.fly.com.yunhealth.Activity.ChangeMyDataActivityCopy;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.SharedPreferenceHelper;
import wang.fly.com.yunhealth.util.UtilClass;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 兆鹏 on 2016/11/2.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private TextView title;
    private ImageView back;
    private CircleImageView userImage;
    private TextView userName;
    private TextView userPhoneNumber;
    private RelativeLayout[] layouts;
    private TextView myDevice;
    private Context context;
    private static final String TAG = "MineFragment";

    static final int REQUEST_CHANGE_DATA = 0;
    static final int REQUEST_OPEN_ACTIVITY = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mine,container,false);
        preInitView(v);
        initView(v);
        afterInitView(v);
        return v;
    }

    private void afterInitView(View v) {
        title.setText("我的");
        back.setVisibility(View.GONE);
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setOnClickListener(this);
        }
    }

    private void preInitView(View v) {
        layouts = new RelativeLayout[7];
        context = getContext();
    }

    private void initView(View v) {
        title = (TextView) v.findViewById(R.id.info_title);
        back = (ImageView) v.findViewById(R.id.back);
        layouts[0] = (RelativeLayout) v.findViewById(R.id.firstLayout);
        layouts[1] = (RelativeLayout) v.findViewById(R.id.secondLayout);
        layouts[2] = (RelativeLayout) v.findViewById(R.id.thirdLayout);
        layouts[3] = (RelativeLayout) v.findViewById(R.id.fourthLayout);
        layouts[4] = (RelativeLayout) v.findViewById(R.id.fifthLayout);
        layouts[5] = (RelativeLayout) v.findViewById(R.id.sixthLayout);
        layouts[6] = (RelativeLayout) v.findViewById(R.id.seventhLayout);
        userImage = (CircleImageView) v.findViewById(R.id.userImageShow);
        userName = (TextView) v.findViewById(R.id.userNameShow);
        userPhoneNumber = (TextView) v.findViewById(R.id.userPhoneNumberShow);
        myDevice = (TextView) v.findViewById(R.id.myDevice);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.firstLayout:{
                //打开个人资料
                Intent intent = new Intent(getContext(), ChangeMyDataActivityCopy.class);
                startActivityForResult(intent, REQUEST_CHANGE_DATA);
                break;
            }
            case R.id.secondLayout:{
                //管理健康方案
                break;
            }
            case R.id.thirdLayout:{
                //联系人
                break;
            }
            case R.id.fourthLayout:{
                //云健康账户
                Toast.makeText(context, "账户功能尚未投入使用", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.fifthLayout:{
                //设置
                break;
            }
            case R.id.sixthLayout:{
                //关于云健康
                break;
            }
            case R.id.seventhLayout:{
                UtilClass.requestPermission(getActivity(), Manifest.permission.CAMERA);
                if (UtilClass.hasPermission(getActivity(), Manifest.permission.CAMERA)) {
                    Intent intent = new Intent(context, CaptureActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, REQUEST_OPEN_ACTIVITY);
                }
                //我的设备
                break;
            }
        }
    }
    public void setUser(){
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences("LoginState", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phoneNumber", "");
        String name = sharedPreferences.getString("userName", "");
        String userImagePath = sharedPreferences.getString("userImage", null);
        String deviceMac = sharedPreferences.getString("device", "暂无设备");
        myDevice.setText(deviceMac);
        AlphaAnimation alpha = new AlphaAnimation(0.1f, 1.0f);
        alpha.setDuration(100);
        Glide.with(context)
                .load(userImagePath)
                .animate(alpha)
                .placeholder(R.drawable.head_image_default)
                .error(R.drawable.head_image_default)
                .fitCenter()
                .into(userImage);
        userName.setText(name);
        userPhoneNumber.setText(phone);
    }


    @Override
    public void onResume() {
        super.onResume();
        //在唤醒的时候加载数据
        setUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CHANGE_DATA:{
                if (resultCode == RESULT_OK){
                    setUser();
                }
                break;
            }
            case REQUEST_OPEN_ACTIVITY: {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String macAddress = bundle.getString("result");
                    if (UtilClass.isMacAddress(macAddress)) {
                        SharedPreferenceHelper.undateDevice(macAddress);
                    } else {
                        UtilClass.toToast(getContext(), "设备地址不合法");
                    }
                }
            }
        }
    }
}
