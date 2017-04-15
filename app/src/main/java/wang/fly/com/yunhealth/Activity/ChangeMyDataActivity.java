package wang.fly.com.yunhealth.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MVP.Presenters.LoginActivityPresenter;
import wang.fly.com.yunhealth.MyViewPackage.ChooseImageDialog;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/11/29.
 */

public class ChangeMyDataActivity extends AppCompatActivity
        implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener {

    private ImageView back;
    private TextView save;
    private EditText nameEdit, idCardEdit, heightEdit, weightEdit;
    private TextView maleEdit;
    private Button quit;
    private TextView birthdayEdit;
    private ChooseImageDialog chooseUserImageDialog;
    private CircleImageView userImageShow;
    private long mExitTime;//退出的时间
    private Uri userImageUri;
    private Context context = this;
    private Calendar birthdayDate;
    private boolean isMan = false;
    private static final String TAG = "ChangeMyDataActivity";
    private static final String PATH_ADD = "/CloudHealthy/userImage/";

    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private static final int REQUEST_RESIZE_REQUEST_CODE = 2;
    private static final int MESSAGE_FROM_USER_IMAGE = 0;
    private static final int MESSAGE_FROM_UP_USER_IAMGE = 2;
    private static final int MESSAGE_UP_USER_IMAGE_START = 3;
    private static final int MESSAGE_UPDATE_DATA = 4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_data);
        ActivityCollector.addActivity(this);
        initView();
        initData();
    }


    private void initData() {
        SharedPreferences shared = getSharedPreferences("LoginState", MODE_PRIVATE);
        Map<String, Object> data = (Map<String, Object>) shared.getAll();
        String name = (String) data.get("userName");
        nameEdit.setText(name);
        String card = (String) data.get("idNumber");
        if (card != null) {
            idCardEdit.setText(card);
        }
        String string = (String) data.get("birthday");
        if (string != null) {
            Log.d(TAG, "initData: birthday" + string);
            Date temp = UtilClass.resolveBmobDate("1995-06-02 00:00:00", null);
            if (temp != null){
                birthdayDate = Calendar.getInstance();
                birthdayDate.setTime(temp);
            }
            Log.d(TAG, "initData: birthday" + birthdayDate.get(Calendar.YEAR) + "-"
                    + (birthdayDate.get(Calendar.MONTH) + 1) + "-"
                    + birthdayDate.get(Calendar.DAY_OF_MONTH));
            Log.d(TAG, "initData: " + birthdayDate.toString());
        } else {
            birthdayDate = Calendar.getInstance();
            birthdayDate.setTimeInMillis(System.currentTimeMillis());
        }
        birthdayEdit.setText(birthdayDate.get(Calendar.YEAR) + "-"
                + (birthdayDate.get(Calendar.MONTH) + 1) + "-"
                + birthdayDate.get(Calendar.DAY_OF_MONTH));
        Integer height = (Integer) data.get("height");
        if (height != null) {
            heightEdit.setText(height.toString());
        }
        Float weight = (Float) data.get("weight");
        if (weight != null) {
            weightEdit.setText(UtilClass.getTwoShortValue(weight) + "");
        }
        isMan = (boolean) data.get("isMan");
        if (isMan) {
            maleEdit.setText("男" + "");
        } else {
            maleEdit.setText("女" + "");
        }
        String path = (String) data.get("userImage");
        AlphaAnimation alpha = new AlphaAnimation(0.1f, 1.0f);
        alpha.setDuration(100);
        Glide.with(context)
                .load(path)
                .animate(alpha)
                .placeholder(R.drawable.head_image_default)
                .error(R.drawable.head_image_default)
                .fitCenter()
                .into(userImageShow);
    }


    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        save = (TextView) findViewById(R.id.saveData);
        userImageShow = (CircleImageView) findViewById(R.id.userImageShow);
        birthdayEdit = (TextView) findViewById(R.id.birthdayEdit);
        heightEdit = (EditText) findViewById(R.id.heightEdit);
        weightEdit = (EditText) findViewById(R.id.weightEdit);
        idCardEdit = (EditText) findViewById(R.id.idCardEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        quit = (Button) findViewById(R.id.exitButton);
        maleEdit = (TextView) findViewById(R.id.maleEdit);

        quit.setOnClickListener(this);
        birthdayEdit.setOnClickListener(this);
        userImageShow.setOnClickListener(this);
        back.setOnClickListener(this);
        save.setOnClickListener(this);
        maleEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.maleEdit: {
                //弹出窗口选择性别
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请选择性别");
                final String[] sex = {"男", "女"};
                final boolean[] male = new boolean[1];
                //    设置一个单项选择下拉框
                int i = (isMan ? 0 : 1);
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认‘女‘ 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                builder.setSingleChoiceItems(sex, i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        male[0] = (which == 0 ? true : false);
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isMan = male[0];
                        if (isMan) {
                            maleEdit.setText("男");
                        } else {
                            maleEdit.setText("女");
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }
            case R.id.back: {
                finish();
                break;
            }
            case R.id.saveData: {
                //保存数据
                if (!UtilClass.isOpenNetWork(context)) {
                    UtilClass.toToast(context, "网络状况不佳");
                    return;
                }
                final SignUserData user = new SignUserData();
                user.setObjectId(getSharedPreferences("LoginState", MODE_PRIVATE).
                        getString("userId", null));
                if (nameEdit.getText().toString().isEmpty()
                        || nameEdit.getText().toString().length() > 20) {
                    UtilClass.toToast(context, "名字过长或为空");
                } else if (!maleEdit.getText().toString().equals("男")
                        && !maleEdit.getText().toString().equals("女")) {
                    UtilClass.toToast(context, "性别为“男”或“女”");
                } else if (!idCardEdit.getText().toString().isEmpty() &&
                        !(idCardEdit.getText().toString().length() == 15)
                        && !(idCardEdit.getText().toString().length() == 18)) {
                    UtilClass.toToast(context, "身份证号格式错误");
                } else {
                    user.setUserName(nameEdit.getText().toString());
                    user.setIdNumber(idCardEdit.getText().toString());
                    user.setMan(isMan);
                    if (birthdayDate != null) {
                        Date date = new Date(birthdayDate.getTimeInMillis());
                        user.setBirthday(new BmobDate(date));
                    }
                    if (!heightEdit.getText().toString().isEmpty()) {
                        user.setHeight(Integer.valueOf(heightEdit.getText().toString()));
                    }
                    if (!weightEdit.getText().toString().isEmpty()) {
                        user.setWeight(Float.valueOf(weightEdit.getText().toString()));
                    }
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Message message = Message.obtain();
                            message.what = MESSAGE_UPDATE_DATA;
                            message.arg1 = (e == null ? 0 : 1);
                            message.obj = user;
                            handler.sendMessage(message);
                        }
                    });
                }
                break;
            }
            case R.id.userImageShow: {
                UtilClass.requestPermission(this, android.Manifest.permission.CAMERA);
                chooseUserImage();
                break;
            }
            case R.id.birthdayEdit: {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.d(TAG, "onDateSet: " + i + "-" + i1 + "-" + i2);
                        birthdayDate.set(Calendar.YEAR, i);
                        birthdayDate.set(Calendar.MONTH, i1);
                        birthdayDate.set(Calendar.DAY_OF_MONTH, i2);
                        birthdayEdit.setText(birthdayDate.get(Calendar.YEAR) + "-"
                                + (birthdayDate.get(Calendar.MONTH) + 1) + "-"
                                + birthdayDate.get(Calendar.DAY_OF_MONTH));
                    }
                }, birthdayDate.get(Calendar.YEAR),
                        birthdayDate.get(Calendar.MONTH),
                        birthdayDate.get(Calendar.DAY_OF_MONTH)).show();
                break;
            }
            case R.id.exitButton: {
                SharedPreferences.Editor editor =
                        getSharedPreferences("LoginState", MODE_PRIVATE).edit();
                editor.putBoolean("loginRememberState", false);
                editor.commit();
                ActivityCollector.finishAll();
                break;
            }
        }

    }


    private void chooseUserImage() {//选择照片
        chooseUserImageDialog = new ChooseImageDialog(context, new View.OnClickListener() {
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
                                    REQUEST_CODE_CAPTURE_CAMEIA);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case R.id.pickPhotoBtn:
//                                Intent intent = new Intent(Intent.ACTION_PICK);//从相册中选取图片
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
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE_DATA: {
                    if (msg.arg1 == 0) {
                        SignUserData user = (SignUserData) msg.obj;
                        LoginActivityPresenter.editLoginState(user, true);
                        UtilClass.toToast(context, "资料已修改");
                        finish();
                    } else {
                        UtilClass.toToast(context, "资料修改失败，请稍后重试");
                    }
                    break;
                }
                case MESSAGE_FROM_UP_USER_IAMGE: {
                    String phoneNumber = getSharedPreferences("LoginState", MODE_PRIVATE).
                            getString("userName", null);
                    String path = Environment.getExternalStorageDirectory() +
                            PATH_ADD;
                    if (msg.arg1 == 1) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.
                                    getContentResolver(), Uri.fromFile(new File(path +
                                    phoneNumber + "userImage.jpg")));
                            //修改头像
                            userImageShow.setImageBitmap(bitmap);

                            Toast.makeText(context, "头像上传成功", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //头像上传失败
                        File oldFile = new File(path + phoneNumber + "userImage_copy.jpg");
                        oldFile.renameTo(new File(path + phoneNumber + "userImage.jpg"));
                        Toast.makeText(context, "头像上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri imageUri;
        if (resultCode == RESULT_CANCELED) {
        } else if (resultCode == RESULT_OK) {//选取成功后进行裁剪
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE: {
                    //从图库中选择图片作为头像
                    Log.d(TAG, "onActivityResult: " + data.getData());
                    if (Build.VERSION.SDK_INT
                            > android.os.Build.VERSION_CODES.KITKAT) {
                        //android版本高于4.4
                        imageUri = data.getData();
                        reSizeImage(imageUri);
                    } else {
                        Log.d(TAG, "onActivityResult: banben");
                        imageUri = data.getData();
                        imageUri = Uri.parse("file://" + UtilClass.getPath(context, imageUri));
                        Log.d(TAG, "onActivityResult: xiugaihoude uri" + imageUri);
                        reSizeImage(imageUri);
                    }
                    break;
                }
                case REQUEST_CODE_CAPTURE_CAMEIA: {
                    //使用相机获取头像
                    imageUri = data.getData();
//                    Log.d(TAG, "onActivityResult: " + imageUri);
                    if (imageUri == null) {
                        //use bundle to get data
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            Bitmap bitMap = (Bitmap) bundle.get("data"); //get bitmap
                            imageUri = Uri.parse(MediaStore.Images.Media.
                                    insertImage(getContentResolver(), bitMap, null, null));
//                            Log.d(TAG, "onActivityResult: bndle != null" + imageUri);
                            Log.d(TAG, "onActivityResult: " + imageUri);
                            reSizeImage(imageUri);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case REQUEST_RESIZE_REQUEST_CODE: {
                    //剪切图片返回
//                    Log.d(TAG, "剪切完毕：" + userImageUri);
                    if (userImageUri == null) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    } else {//截取图片完成
                        Log.d(TAG, "onActivityResult: 剪切完毕");
                        String phoneNumber = getSharedPreferences("LoginState", MODE_PRIVATE).
                                getString("userName", null);
                        //新旧文件的替换
                        String path = Environment.getExternalStorageDirectory() +
                                PATH_ADD;
                        File oldFile = new File(path + phoneNumber + "userImage.jpg");
                        oldFile.renameTo(new File(path + phoneNumber + "userImage_copy.jpg"));
                        File newFile = new File(path + "crop.jpg");
                        newFile.renameTo(new File(path + phoneNumber + "userImage.jpg"));

                        Message message = new Message();
                        message.what = MESSAGE_FROM_UP_USER_IAMGE;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                        final BmobFile bmobFile = new BmobFile(new File(path + phoneNumber + "userImage.jpg"));
                        bmobFile.upload(new UploadFileListener() {//尝试上传头像
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SignUserData user = new SignUserData();
                                    user.setObjectId(getSharedPreferences("LoginState", MODE_PRIVATE).
                                            getString("userId", null));
                                    Log.d(TAG, "done: id" + user.getObjectId());
                                    user.setUserImage(bmobFile);
                                    user.update(new UpdateListener() {
                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            Message message = new Message();
                                            message.what = MESSAGE_UP_USER_IMAGE_START;
                                            handler.sendMessage(message);
                                        }

                                        @Override
                                        public void done(BmobException e) {
                                            Message message = new Message();
                                            message.what = MESSAGE_FROM_UP_USER_IAMGE;
                                            message.arg1 = (e == null ? 1 : 0);
                                            handler.sendMessage(message);
                                        }

                                    });
                                } else {
                                    Message message = new Message();
                                    message.what = MESSAGE_FROM_UP_USER_IAMGE;
                                    message.arg1 = 0;
                                    handler.sendMessage(message);
                                }
                            }
                        });
                        //尝试上传更新头像
                    }
                    break;
                }
            }
        }
    }

    private void reSizeImage(Uri uri) {//重新剪裁图片的大小
//        Log.d(TAG, "尝试剪切的文件输出" + "uri = [" + uri + "]");
        File outputImage = new File(Environment.getExternalStorageDirectory()
                + PATH_ADD + "crop.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.getAbsoluteFile().delete();
            }
            outputImage.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "reSizeImage: ", e);
        }
        userImageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", 500);//输出X方向的像素
        intent.putExtra("outputY", 500);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);//设置为不返回数据
        /**
         * 此方法返回的图片只能是小图片（测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
//        intent.putExtra("return-data", true);
//        intent.putExtra("output", Uri.fromFile(new File("/mnt/sdcard/temp")));//保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, userImageUri);
//        Log.d(TAG, "reSizeImage() called with: " + "uri = [" + userImageUri + "]");
        startActivityForResult(intent, REQUEST_RESIZE_REQUEST_CODE);
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }
}
