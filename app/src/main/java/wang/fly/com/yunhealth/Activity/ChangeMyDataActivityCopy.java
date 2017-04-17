package wang.fly.com.yunhealth.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MVP.Bases.MVPBaseActivity;
import wang.fly.com.yunhealth.MVP.Presenters.ChangeMyDataActivityPresenter;
import wang.fly.com.yunhealth.MVP.Views.ChangeMyDataActivityInterface;
import wang.fly.com.yunhealth.MyViewPackage.ChooseImageDialog;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

import static wang.fly.com.yunhealth.MVP.Presenters.ChangeMyDataActivityPresenter.REQUEST_CODE_PICK_IMAGE;

/**
 * Created by noclay on 2017/4/16.
 */

public class ChangeMyDataActivityCopy extends
        MVPBaseActivity<ChangeMyDataActivityInterface, ChangeMyDataActivityPresenter>
        implements ChangeMyDataActivityInterface, View.OnClickListener {
    private ImageView mBack;
    private TextView mSaveData;
    private CircleImageView mUserImageShow;
    private EditText mNameEdit;
    private EditText mIdCardEdit;
    private TextView mMaleEdit;
    private TextView mBirthdayEdit;
    private EditText mHeightEdit;
    private EditText mWeightEdit;
    private Button mExitButton;
    private LinearLayout mMainLayout;
    ChooseImageDialog chooseUserImageDialog;
    ProgressDialog progress;
    Calendar mCalendar;
    Uri userImageUri;
    Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_data);
        initView();
        mCalendar = Calendar.getInstance();
        mPresenter.setContext(mContext);
        mPresenter.init();
    }

    @Override
    protected ChangeMyDataActivityPresenter createPresenter() {
        return new ChangeMyDataActivityPresenter(getMainLooper());
    }

    @Override
    public void editMale(Context context, boolean male) {
//弹出窗口选择性别
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择性别");
        final boolean[] males = new boolean[1];
        builder.setSingleChoiceItems(new String[]{"男", "女"}, (male ? 0 : 1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                males[0] = (which == 0 ? true : false);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMale(males[0]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void startSaveData() {
        if (progress == null){
            progress = new ProgressDialog(mContext);
            progress.setTitle("正在保存数据中...");
            progress.show();
        }
    }

    @Override
    public void saveSuccess() {

    }

    @Override
    public void saveFailed() {

    }

    @Override
    public void editImage() {
        UtilClass.requestPermission(ChangeMyDataActivityCopy.this,
                android.Manifest.permission.CAMERA);
        chooseUserImageDialog = new ChooseImageDialog(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseUserImageDialog.dismiss();
                switch (v.getId()) {
                    case R.id.takePhotoBtn: {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent getImageByCamera = new
                                    Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(getImageByCamera,
                                    ChangeMyDataActivityPresenter.REQUEST_CODE_CAPTURE_CAMERA);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case R.id.pickPhotoBtn:
//                      Intent intent = new Intent(Intent.ACTION_PICK);//从相册中选取图片
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        //从相册/文件管理中选取图片
                        intent.setType("image/*");//相片类型
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                        break;
                    case R.id.cancelBtn: {
                        break;
                    }
                }
            }
        });
        chooseUserImageDialog.showAtLocation(findViewById(R.id.mainLayout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void editBirthday() {

    }

    @Override
    public void showMale(boolean isMan) {
        if (isMan) {
            mMaleEdit.setText("男");
        } else {
            mMaleEdit.setText("女");
        }
    }

    @Override
    public void showBirthday(Calendar calendar) {
        mBirthdayEdit.setText(calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void showImage(String url) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(200);
        Glide.with(mContext).load(url).animate(alphaAnimation)
                .placeholder(R.drawable.head_image_default)
                .error(R.drawable.head_image_default)
                .into(mUserImageShow);
    }

    @Override
    public SignUserData getUser() {
        return null;
    }

    /**
     * 初始化所有的布局
     *
     * @param userData
     */
    @Override
    public void initView(SignUserData userData) {
        if (userData == null) {
            return;
        }
        showImage(userData.getUserImage());
        mNameEdit.setText(userData.getUserName() + "");
        mIdCardEdit.setText(userData.getIdNumber() + "");
        showMale(userData.getMan());
        mCalendar.setTime(UtilClass.resolveBmobDate(userData.getBirthday().getDate(), null));
        showBirthday(mCalendar);
        mHeightEdit.setText(userData.getHeight() + "");
        mWeightEdit.setText(userData.getWeight() + "");
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSaveData = (TextView) findViewById(R.id.saveData);
        mSaveData.setOnClickListener(this);
        mUserImageShow = (CircleImageView) findViewById(R.id.userImageShow);
        mUserImageShow.setOnClickListener(this);
        mNameEdit = (EditText) findViewById(R.id.nameEdit);
        mIdCardEdit = (EditText) findViewById(R.id.idCardEdit);
        mMaleEdit = (TextView) findViewById(R.id.maleEdit);
        mMaleEdit.setOnClickListener(this);
        mBirthdayEdit = (TextView) findViewById(R.id.birthdayEdit);
        mBirthdayEdit.setOnClickListener(this);
        mHeightEdit = (EditText) findViewById(R.id.heightEdit);
        mWeightEdit = (EditText) findViewById(R.id.weightEdit);
        mExitButton = (Button) findViewById(R.id.exitButton);
        mExitButton.setOnClickListener(this);
        mMainLayout = (LinearLayout) findViewById(R.id.mainLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case ChangeMyDataActivityPresenter.REQUEST_CODE_CAPTURE_CAMERA:{
                    //直接拍照获取头像
                    imageUri = data.getData();
                    Log.d("test", "onActivityResult: 使用相机返回" + imageUri);
                    if (imageUri == null){
                        Bundle bundle = data.getExtras();
                        if (bundle != null){
                            Bitmap bitMap = (Bitmap) bundle.get("data"); //get bitmap
                            imageUri = Uri.parse(MediaStore.Images.Media.
                                    insertImage(getContentResolver(), bitMap, null, null));
                            Log.d("test", "onActivityResult: " + imageUri);
                        }
                    }
                    mPresenter.resizeImage(imageUri, ChangeMyDataActivityCopy.this);
                    break;
                }
                case ChangeMyDataActivityPresenter.REQUEST_CODE_PICK_IMAGE:{
                    //从文件中选择图片
                    imageUri = data.getData();
                    userImageUri = mPresenter.resizeImage(imageUri, this);
                    break;
                }
                case ChangeMyDataActivityPresenter.REQUEST_RESIZE_REQUEST_CODE:{
                    //重新截取后的头像
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.saveData:
                mPresenter.saveData();
                break;
            case R.id.userImageShow:
                mPresenter.changeImage();
                break;
            case R.id.maleEdit:
                mPresenter.maleEdit();
                break;
            case R.id.birthdayEdit:
                mPresenter.birthdayEdit();
                break;
            case R.id.exitButton:
                mPresenter.exitLogin();
                break;
        }
    }
}
