package wang.fly.com.yunhealth.MVP.Presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import java.io.File;

import wang.fly.com.yunhealth.MVP.Bases.BasePresenter;
import wang.fly.com.yunhealth.MVP.Views.ChangeMyDataActivityInterface;
import wang.fly.com.yunhealth.util.SharedPreferenceHelper;

import static wang.fly.com.yunhealth.util.MyConstants.PATH_ADD;

/**
 * Created by noclay on 2017/4/16.
 */

public class ChangeMyDataActivityPresenter
        extends BasePresenter<ChangeMyDataActivityInterface> {
    public static final int REQUEST_CODE_PICK_IMAGE = 0;
    public static final int REQUEST_CODE_CAPTURE_CAMERA = 1;
    public static final int REQUEST_RESIZE_REQUEST_CODE = 2;
    Handler mHandler;
    Context mContext;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    class MyHandler extends Handler{

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public ChangeMyDataActivityPresenter(Looper mainLooper) {
        mHandler = new MyHandler(mainLooper);
    }

    /**
     * 初始化所有的View
     */
    public void init() {
        getView().initView(SharedPreferenceHelper.getLoginUser());
    }

    public void exitLogin() {
    }

    public void birthdayEdit() {
    }

    public void saveData(){

    }

    public void changeImage(){
        getView().editImage();
    }

    public void maleEdit(){

    }

    public Uri resizeImage(Uri imageUri, Activity activity){
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
        }
        Uri userImageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image");
        intent.setDataAndType(imageUri, "image/*");
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
        activity.startActivityForResult(intent, REQUEST_RESIZE_REQUEST_CODE);
        return userImageUri;
    }


}
