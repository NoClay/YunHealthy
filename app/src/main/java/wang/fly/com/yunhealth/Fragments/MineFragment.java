package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import wang.fly.com.yunhealth.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 兆鹏 on 2016/11/2.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private TextView title;
    private ImageView back;
    private ImageView userImage;
    private TextView userName;
    private TextView userPhoneNumber;
    private RelativeLayout[] layouts;
    private Context context;
    private static final String TAG = "MineFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.minefragment_layout,container,false);
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
        layouts = new RelativeLayout[6];
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
        userImage = (ImageView) v.findViewById(R.id.userImageShow);
        userName = (TextView) v.findViewById(R.id.userNameShow);
        userPhoneNumber = (TextView) v.findViewById(R.id.userPhoneNumberShow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.firstLayout:{
                //打开个人资料
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在唤醒的时候加载数据
        SharedPreferences sharedPreferences = 
                getContext().getSharedPreferences("LoginState", MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("userName", "你还没有登录") );
        String phone = sharedPreferences.getString("phoneNumber", "你还没有登录");
        Log.d(TAG, "onResume: " + phone);
        userPhoneNumber.setText(sharedPreferences.getString("phoneNumber", "你还没有登录"));
        String url = sharedPreferences.getString("userImage", null);
        Glide.with(context).load(url)
                .placeholder(R.drawable.head_image_default)
                .crossFade().into(userImage);
    }
}
