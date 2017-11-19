package indi.noclay.cloudhealth.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import indi.noclay.cloudhealth.database.LocalDataBase;
import indi.noclay.cloudhealth.database.MedicineDetail;
import indi.noclay.cloudhealth.mvp.base.BasePresenter;
import indi.noclay.cloudhealth.mvp.view.AddMedicineActivityInterface;
import indi.noclay.cloudhealth.util.ConstantsConfig;


/**
 * Created by noclay on 2017/5/7.
 */

public class AddMedicinePresenter extends BasePresenter<AddMedicineActivityInterface> {
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
                        getView().loadSuccess();
                        getView().showImage(image.toString());
                    }else{
                        getView().loadFailed();
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
                    //此处进行本地的缓存
                    getView().saveSuccess();
                    break;
                }
            }
        }
    }

    public AddMedicinePresenter(Looper mainLoop) {
        mHandler = new MyHandler(mainLoop);
    }


    public void saveData(){
        final MedicineDetail medicineDetail = getView().getMedicineDetail();
        medicineDetail.setIsOpen(LocalDataBase.CLOCK_OPEN);
        mHandler.sendEmptyMessage(SAVE_START);
        medicineDetail.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    mHandler.sendEmptyMessage(SAVE_SUCCESS);
                    LocalDataBase.insertMedicineDetail(medicineDetail);
                }else{
                    mHandler.sendEmptyMessage(SAVE_FAILED);
                }
            }
        });
    }

    public void editTime(){
        getView().inputTimeAndDose();
    }


    public void changeImage(){
        getView().editImage();
    }

    public Uri resizeImage(Uri imageUri, Activity activity){
//        Log.d(TAG, "尝试剪切的文件输出" + "uri = [" + uri + "]");
        File outputImage = new File(ConstantsConfig.CROP_PATH_MEDICINE);
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
        intent.putExtra("aspectX", 3);//输出是X方向的比例
        intent.putExtra("aspectY", 2);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
//        intent.putExtra("outputX", 500);//输出X方向的像素
//        intent.putExtra("outputY", 500);
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
        getView().startLoadImage();
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

    public void inputDay(){
        getView().inputDayLength();
    }

    public void initView(){
        getView().initView(null);
    }
}
