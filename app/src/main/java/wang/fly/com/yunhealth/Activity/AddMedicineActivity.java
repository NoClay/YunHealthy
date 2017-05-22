package wang.fly.com.yunhealth.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import wang.fly.com.yunhealth.DataBasePackage.MedicineDetail;
import wang.fly.com.yunhealth.MVP.Bases.MVPBaseActivity;
import wang.fly.com.yunhealth.MVP.Presenters.AddMedicinePresenter;
import wang.fly.com.yunhealth.MVP.Presenters.ChangeMyDataActivityPresenter;
import wang.fly.com.yunhealth.MVP.Views.AddMedicineActivityInterface;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.ChooseImageDialog;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.InputDayLengthDialog;
import wang.fly.com.yunhealth.MyViewPackage.Dialogs.InputTimeAndDoseDialog;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.SharedPreferenceHelper;
import wang.fly.com.yunhealth.util.UtilClass;

import static wang.fly.com.yunhealth.MVP.Presenters.ChangeMyDataActivityPresenter.REQUEST_CODE_PICK_IMAGE;

/**
 * Created by noclay on 2017/4/29.
 */

public class AddMedicineActivity extends
        MVPBaseActivity<AddMedicineActivityInterface, AddMedicinePresenter>
        implements AddMedicineActivityInterface, View.OnClickListener {
    private Spinner mChooseUseType;
    private Context mContext;
    private InputDayLengthDialog mInputDayLengthDialog;
    /**
     * 请输入药物名称
     */
    private EditText mMedicineName;
    private ImageView mMedicineImage;
    private Spinner mMedicineUseType;
    private EditText mMedicineTag;
    private EditText mMedicineContent;
    /**
     * 点击此处添加
     */
    private TextView mMedicineUseTime;
    /**
     * 添加完成
     */
    private TextView mCommitAction;
    private ChooseImageDialog chooseUserImageDialog;
    /**
     * 持续时间3天（点击修改)
     */
    private InputTimeAndDoseDialog mInputTimeAndDoseDialog;
    private TextView mInputDay;
    private MedicineDetail medicine;
    private Uri userImageUri;
    private ProgressDialog loadImage;
    private ProgressDialog saveData;
    private Spinner mMedicineUnit;
    private ScrollView mMainLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        initView();
        mPresenter.initView();
        UtilClass.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        UtilClass.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected AddMedicinePresenter createPresenter() {
        return new AddMedicinePresenter(getMainLooper());
    }

    private void initView() {
        mContext = this;
        mMedicineName = (EditText) findViewById(R.id.medicineName);
        mMedicineImage = (ImageView) findViewById(R.id.medicineImage);
        mMedicineImage.setOnClickListener(this);
        mMedicineUseType = (Spinner) findViewById(R.id.medicineUseType);
        mMedicineTag = (EditText) findViewById(R.id.medicineTag);
        mMedicineContent = (EditText) findViewById(R.id.medicineContent);
        mMedicineUseTime = (TextView) findViewById(R.id.medicineUseTime);
        mMedicineUseTime.setOnClickListener(this);
        mCommitAction = (TextView) findViewById(R.id.commit_action);
        mCommitAction.setOnClickListener(this);
        mInputDay = (TextView) findViewById(R.id.inputDay);
        mInputDay.setOnClickListener(this);
        mMedicineUnit = (Spinner) findViewById(R.id.medicineUnit);
        mMainLayout = (ScrollView) findViewById(R.id.mainLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("添加药物");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initUseType();
        initUnits();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }
        }
        return true;
    }

    private void initUnits() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext,
                R.array.units,
                R.layout.item_use_type_spinner
        );
        mMedicineUnit.setAdapter(adapter);
    }

    private void initUseType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext,
                R.array.useType,
                R.layout.item_use_type_spinner
        );
        adapter.setDropDownViewResource(R.layout.item_use_type_spinner);
        mMedicineUseType.setAdapter(adapter);
    }

    @Override
    public void startSaveData() {
        if (saveData != null) {
            saveData.dismiss();
            saveData = null;
        }
        saveData = new ProgressDialog(mContext);
        saveData.setTitle("正在保存数据到云端，请稍候...");
        saveData.show();
    }

    @Override
    public void saveSuccess() {
        if (saveData != null) {
            saveData.dismiss();
            finish();
            Toast.makeText(mContext, "数据保存成功", Toast.LENGTH_SHORT).show();
            saveData = null;
        }
    }


    @Override
    public void saveFailed() {
        if (saveData != null) {
            saveData.dismiss();
            Toast.makeText(mContext, "数据保存失败，请检查网络后再次上传", Toast.LENGTH_SHORT).show();
            saveData = null;
        }
    }

    @Override
    public void editImage() {
        UtilClass.requestPermission(AddMedicineActivity.this,
                Manifest.permission.CAMERA);
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
                            // 获取文件
                            File tempFile = new File(MyConstants.SRC_PATH_MEDICINE);
                            if (tempFile.exists() && tempFile.isFile()) {
                                tempFile.delete();
                            }
                            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
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
    public void showImage(String url) {
        Uri image = Uri.parse(url);
        Log.d("test", "showImage: url = " + url);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(200);
        medicine.setMedicinePicture(url);
        Glide.with(mContext)
                .load(image)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade(200)
                .placeholder(R.drawable.add_gray)
                .error(R.drawable.head_image_default)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mMedicineImage.setImageDrawable(resource);
                    }
                });
    }

    @Override
    public void initView(MedicineDetail medicineDetail) {
        if (medicineDetail == null) {
            medicine = new MedicineDetail();
            medicine.setStartTime(new BmobDate(new Date()));
            medicine.setDayLength(3);
            medicine.setDayCount(0);
        } else {
            medicine = medicineDetail;
        }
    }

    @Override
    public void toast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void startLoadImage() {
        if (loadImage != null) {
            loadImage.dismiss();
            loadImage = null;
        }
        loadImage = new ProgressDialog(mContext);
        loadImage.setTitle("正在上传图片，请稍候...");
        loadImage.show();
    }

    @Override
    public void loadSuccess() {
        if (loadImage != null) {
            loadImage.dismiss();
            Toast.makeText(mContext, "图片上传成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loadFailed() {
        if (loadImage != null) {
            loadImage.dismiss();
            Toast.makeText(mContext, "图片上传失败，请检查您的网络配置", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void inputDayLength() {
        if (mInputDayLengthDialog == null) {
            mInputDayLengthDialog = new InputDayLengthDialog(mContext,
                    new InputDayLengthDialog.OnChooseChangedListener() {
                        @Override
                        public void onChooseChanged(int pos) {
                            mInputDay.setText("持续时间" + MyConstants.TIME_ITEM[pos] + "(点击修改)");
                            medicine.setDayLength(MyConstants.TIME_VALUE[pos]);
                            Log.d("test", "onChooseChanged: value = " + MyConstants.TIME_VALUE[pos]);
                        }
                    });
            mInputDayLengthDialog.showAtLocation(findViewById(R.id.mainLayout),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else {
            mInputDayLengthDialog.dismiss();
            mInputDayLengthDialog = null;
        }
    }


    @Override
    public void inputTimeAndDose() {
        if (mInputTimeAndDoseDialog != null && mInputTimeAndDoseDialog.isShowing()) {
            mInputTimeAndDoseDialog.dismiss();
            mInputTimeAndDoseDialog = null;
        }

        mInputTimeAndDoseDialog = new InputTimeAndDoseDialog(mContext,
                medicine.getTimes(),
                medicine.getDoses(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancelAction: {
                        mInputTimeAndDoseDialog.dismiss();
                        break;
                    }
                    case R.id.submitAction: {
                        mInputTimeAndDoseDialog.dismiss();
                        medicine.setTimes(mInputTimeAndDoseDialog.getTime());
                        medicine.setDoses(mInputTimeAndDoseDialog.getDose());
                        mMedicineUseTime.setText(mInputTimeAndDoseDialog.getTimeByString());
                    }
                }
            }
        });
        mInputTimeAndDoseDialog.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mInputTimeAndDoseDialog.showAtLocation(findViewById(R.id.mainLayout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    @Override
    public MedicineDetail getMedicineDetail() {
        if (mMedicineName.getText().toString().length() == 0) {
            toast("药名不能为空");
            return null;
        }
        if (mMedicineTag.getText().toString().length() == 0) {
            toast("服药原因不能为空");
            return null;
        }
        if (medicine.getTimes().size() == 0) {
            toast("未设置服药时间");
            return null;
        }
        medicine.setMedicineName(mMedicineName.getText().toString());
        medicine.setOwner(SharedPreferenceHelper.getLoginUser());
        medicine.setDoctor(mMedicineContent.getText().toString());
        medicine.setUseType(mMedicineUseType.getSelectedItem().toString());
        medicine.setUnit(mMedicineUnit.getSelectedItem().toString());
        medicine.setTag(mMedicineTag.getText().toString());
        return medicine;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.medicineImage:
                mPresenter.changeImage();
                break;
            case R.id.medicineUseTime:
                //测是代码
                mPresenter.editTime();
                break;
            case R.id.commit_action:
                mPresenter.saveData();
                break;
            case R.id.inputDay:
                mPresenter.inputDay();
                break;
            case R.id.toolbar:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mInputTimeAndDoseDialog != null && mInputTimeAndDoseDialog.isShowing()) {
            mInputTimeAndDoseDialog.dismiss();
            mInputTimeAndDoseDialog = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ChangeMyDataActivityPresenter.REQUEST_CODE_CAPTURE_CAMERA: {
                    //直接拍照获取头像
                    if (data == null) {
                        imageUri = Uri.fromFile(new File(MyConstants.SRC_PATH_MEDICINE));
                    } else {
                        imageUri = data.getData();
                        Log.d("test", "onActivityResult: 使用相机返回" + imageUri);
                        if (imageUri == null) {
                            Bundle bundle = data.getExtras();
                            if (bundle != null) {
                                Bitmap bitMap = (Bitmap) bundle.get("data"); //get bitmap
                                imageUri = Uri.parse(MediaStore.Images.Media.
                                        insertImage(getContentResolver(), bitMap, null, null));
                                Log.d("test", "onActivityResult: " + imageUri);
                            }
                        }
                    }
                    userImageUri = mPresenter.resizeImage(imageUri, AddMedicineActivity.this);
                    break;
                }
                case ChangeMyDataActivityPresenter.REQUEST_CODE_PICK_IMAGE: {
                    //从文件中选择图片
                    imageUri = data.getData();
                    userImageUri = mPresenter.resizeImage(imageUri, this);
                    break;
                }
                case ChangeMyDataActivityPresenter.REQUEST_RESIZE_REQUEST_CODE: {
                    //重新截取后的头像
                    //剪切图片返回
                    if (userImageUri == null) {
                        Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                    } else {//截取图片完成
                        //上传图片
                        File file = new File(MyConstants.CROP_PATH_MEDICINE);
                        file.renameTo(new File(MyConstants.TEMP_MEDICINE));
                        mPresenter.uploadFile(new File(MyConstants.TEMP_MEDICINE));
                    }
                    break;
                }
            }
        }
    }
}
