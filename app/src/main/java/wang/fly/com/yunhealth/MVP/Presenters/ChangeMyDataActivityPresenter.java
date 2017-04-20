package wang.fly.com.yunhealth.MVP.Presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MVP.Bases.BasePresenter;
import wang.fly.com.yunhealth.MVP.Views.ChangeMyDataActivityInterface;
import wang.fly.com.yunhealth.util.MyConstants;
import wang.fly.com.yunhealth.util.SharedPreferenceHelper;

/**
 * Created by noclay on 2017/4/16.
 */

public class ChangeMyDataActivityPresenter
        extends BasePresenter<ChangeMyDataActivityInterface> {
    public static final int REQUEST_CODE_PICK_IMAGE = 0;
    public static final int REQUEST_CODE_CAPTURE_CAMERA = 1;
    public static final int REQUEST_RESIZE_REQUEST_CODE = 2;
    public static final int UPLOAD_START = 0;
    public static final int UPLOAD_END = 1;
    public static final int SAVE_START = 2;
    public static final int SAVE_SUCCESS = 3;
    public static final int SAVE_FAILED = 4;

    Handler mHandler;
    Context mContext;
    SignUserData mUserData;

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
            switch (msg.what){
                case UPLOAD_START:{
                    break;
                }
                case UPLOAD_END:{
                    if (msg.arg1 == 0){
                        //上传成功
                        Uri image = Uri.parse((String) msg.obj);
                        getView().showImage(image.toString());
                    }else{
                        getView().toast("头像截取失败！");
                    }
                    break;
                }
                case SAVE_START:{
                    //开始保存数据的修改
                    getView().startSaveData();
                    break;
                }
                case SAVE_FAILED:{
                    getView().saveFailed();
                    break;
                }
                case SAVE_SUCCESS:{
                    getView().saveSuccess(mUserData);
                    break;
                }
            }
        }
    }

    public ChangeMyDataActivityPresenter(Looper mainLooper) {
        mHandler = new MyHandler(mainLooper);
    }

    /**
     * 初始化所有的View
     */
    public void init() {
        mUserData = SharedPreferenceHelper.getLoginUser();
        getView().initView(mUserData);
    }

    public void exitLogin() {
        SharedPreferenceHelper.exitLogin();
        getView().exitLogin();
    }

    public void birthdayEdit() {
        getView().editBirthday();
    }

    public void saveData(){
        mHandler.sendEmptyMessage(SAVE_START);
        final SignUserData user = getView().getUser();
        if (user != null){
            user.setObjectId(mUserData.getObjectId());
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        mUserData = user;
                        mHandler.sendEmptyMessage(SAVE_SUCCESS);
                    }else{
                        mHandler.sendEmptyMessage(SAVE_FAILED);
                    }
                }
            });
        }
    }

    public void changeImage(){
        getView().editImage();
    }

    public void maleEdit(){
        getView().editMale(mContext, mUserData.getMan());
    }

    public Uri resizeImage(Uri imageUri, Activity activity){
//        Log.d(TAG, "尝试剪切的文件输出" + "uri = [" + uri + "]");
        File outputImage = new File(MyConstants.PATH_ADD + "crop.jpg");
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


    public void uploadFile(File temp){
        if (temp.exists() && temp.isFile()){
            Log.d("test", "uploadFile: path = " + temp.getAbsolutePath());
            Log.d("test", "uploadFile: size = " + temp.length());
        }
        final BmobFile image = new BmobFile(temp);
        image.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                Message message = Message.obtain();
                message.what = UPLOAD_END;
                message.arg1 = (e == null ? 0 : 1);
                message.obj = image.getFileUrl();
                mHandler.sendMessage(message);
            }
        });
    }


}
