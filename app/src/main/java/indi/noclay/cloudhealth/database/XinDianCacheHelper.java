package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UploadBatchListener;
import indi.noclay.cloudhealth.util.FileCacheUtil;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import pers.noclay.utiltool.FileUtils;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;

/**
 * Created by NoClay on 2018/5/11.
 *
 * @Author NoClay
 * @Date 2018/5/11
 */
public class XinDianCacheHelper {
    private static final String TAG = "XinDianCacheHelper";
    public static final String TABLE_XINDIAN_CHACHE = "xinDianCache";
    public static final int NOT_UPLOAD = 1;
    public static final int HAS_UPLOAD = 2;

    public static final String CREATE_XINDIAN_CHCHE = "" +
            "create table " + TABLE_XINDIAN_CHACHE + " (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "filePath text unique," +
            "fileName text," +
            "status integer," +
            "fileLength text)";

    public static void addOneCache(File file) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        if (userId == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("filePath", file.getAbsolutePath());
        values.put("fileName", file.getName());
        values.put("fileLength", FileUtils.FormetFileSize(file.length()));
        values.put("userId", userId);
        values.put("status", NOT_UPLOAD);
        db.insert(TABLE_XINDIAN_CHACHE, null, values);
        values.clear();

        db.close();
        instance.close();
    }

    public static void deleteAll() {
        SQLiteDatabase db = getDefaultInstance().getWritableDatabase();
        db.delete(TABLE_XINDIAN_CHACHE, null, null);
        db.close();
    }

    public static void updateAll() {
        SQLiteDatabase db = getDefaultInstance().getWritableDatabase();
        db.execSQL("update " + TABLE_XINDIAN_CHACHE + " set status = " + HAS_UPLOAD + " " +
                " where userId = '" + SharedPreferenceHelper.getLoginUserId() + "' and status = " + NOT_UPLOAD);
        db.close();
    }

    public static List<MeasureXinDian> getCacheList() {
        List<MeasureXinDian> result = new ArrayList<>();
        SQLiteDatabase db = getDefaultInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_XINDIAN_CHACHE +
                        " where userId = '" + SharedPreferenceHelper.getLoginUserId() + "' and status = " + NOT_UPLOAD
                , null);
        if (cursor.moveToFirst()) {
            do {
                MeasureXinDian xinDianCacheFile = new MeasureXinDian();
                xinDianCacheFile.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                xinDianCacheFile.setOwner(new BmobPointer(SharedPreferenceHelper.getLoginUser()));
                xinDianCacheFile.setFileLength(cursor.getString(cursor.getColumnIndex("fileLength")));
                result.add(xinDianCacheFile);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static void upLoadCacheFile() {
        checkCacheBefore();
        //上传文件
        Log.d(TAG, "upLoadCacheFile: upload");
        final List<MeasureXinDian> files = XinDianCacheHelper.getCacheList();
        Log.d(TAG, "upLoadCacheFile: size = " + files.size());
        if (files.size() > 0) {
            final String[] filePaths = new String[files.size()];
            for (int i = 0; i < files.size(); i++) {
                filePaths[i] = FileCacheUtil.getCacheFilePath(
                        files.get(i).getOwner().getObjectId(),
                        files.get(i).getFileName()
                );
            }
            Log.d(TAG, "upLoadCacheFile: files = " + Arrays.toString(filePaths));
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    Log.d(TAG, "onSuccess: list = " + Arrays.toString(list.toArray()));
                    Log.d(TAG, "onSuccess: urls = " + Arrays.toString(list1.toArray()));
                    if (list1.size() == filePaths.length) {
                        //全部上传完成
                        Log.d(TAG, "onSuccess: 文件上传成功");
                        List<BmobObject> bmobObjects = new ArrayList<>();
                        for (int i = 0; i < filePaths.length; i++) {
                            files.get(i).setFileUrl(list1.get(i));
                            bmobObjects.add(files.get(i));
                        }
                        new BmobBatch().insertBatch(bmobObjects).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> list, BmobException e) {
                                Log.e(TAG, "done: ", e);
                                if (e == null) {
                                    Log.d(TAG, "done: 数据上传成功 size = " + list.size());
                                    updateAll();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                    //1、curIndex--表示当前第几个文件正在上传
                    //2、curPercent--表示当前上传文件的进度值（百分比）
                    //3、total--表示总的上传文件数
                    //4、totalPercent--表示总的上传进度（百分比）
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    private static void checkCacheBefore() {
        String nowCacheName = FileCacheUtil.getCacheFileName();
        File dir = new File(FileCacheUtil.getCacheDirName());
        if (dir.exists() && dir.isDirectory()) {
            //缓存目录存在
            File[] files = dir.listFiles();
            if (files != null && files.length > 0){
                for (File file : files) {
                    if (file.exists() && file.isFile() && file.getName().endsWith(".bin") && !file.getAbsolutePath().equals(nowCacheName)){
                        addOneCache(file);
                    }
                }
            }
        }
    }
}
