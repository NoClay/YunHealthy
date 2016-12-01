package wang.fly.com.yunhealth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import wang.fly.com.yunhealth.Activity.ChangeMyDataActivity;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.LoginAndSign.LoginActivity;
import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.MyViewPackage.CircleImageView;
import wang.fly.com.yunhealth.R;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.contextUri;
import static android.R.attr.editorExtras;
import static android.R.attr.path;
import static android.R.attr.phoneNumber;
import static android.R.attr.switchMinWidth;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION_CODES.M;

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
    private Context context;
    private static final String TAG = "MineFragment";

    static final int REQUEST_CHANGE_DATA = 0;

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
        userImage = (CircleImageView) v.findViewById(R.id.userImageShow);
        userName = (TextView) v.findViewById(R.id.userNameShow);
        userPhoneNumber = (TextView) v.findViewById(R.id.userPhoneNumberShow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.firstLayout:{
                //打开个人资料
                Intent intent = new Intent(getContext(), ChangeMyDataActivity.class);
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
        }
    }
    public void setUser(){
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences("LoginState", MODE_PRIVATE);
        String id = sharedPreferences.getString("userId", "");
        String phone = sharedPreferences.getString("phoneNumber", "");
        String name = sharedPreferences.getString("userName", "");
        String userImagePath = MainActivity.PATH_ADD + phone + "userImage.jpg";
        final File image = new File(userImagePath);
        if (!image.exists() || !image.isFile()){
            BmobQuery<SignUserData> query = new BmobQuery<>();
            query.getObject(id, new QueryListener<SignUserData>() {
                @Override
                public void done(final SignUserData signUserData, BmobException e) {
                    if (e == null && signUserData.getUserImage() != null){
                        BmobFile file = signUserData.getUserImage();
                        file.download(image, new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    LoginActivity.editLoginState(getContext(),
                                            signUserData,
                                            image.getPath(),
                                            null);
                                }
                            }

                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
                    }
                }
            });
        }
        userName.setText(name);
        userPhoneNumber.setText(phone);
        if (image.exists() && image.isFile()){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.
                        getContentResolver(), Uri.fromFile(image));
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            userImage.setImageDrawable(getResources()
                    .getDrawable(R.drawable.head_image_default));
        }
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
                setUser();
                break;
            }
        }
    }
}
