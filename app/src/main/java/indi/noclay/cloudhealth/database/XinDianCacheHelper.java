package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
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

    public static final String CREATE_XINDIAN_CHCHE = "" +
            "create table " + TABLE_XINDIAN_CHACHE + " (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "filePath text unique," +
            "fileName text," +
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
        db.insert(TABLE_XINDIAN_CHACHE, null, values);
        values.clear();

        db.close();
        instance.close();
    }

    public static void deleteAll(){
        SQLiteDatabase db = getDefaultInstance().getWritableDatabase();
        db.delete(TABLE_XINDIAN_CHACHE, null, null);
        db.close();
    }

    public static List<MeasureXinDianCacheFile> getCacheList() {
        List<MeasureXinDianCacheFile> result = new ArrayList<>();
        SQLiteDatabase db = getDefaultInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_XINDIAN_CHACHE +
                " where userId = '" + SharedPreferenceHelper.getLoginUserId() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                MeasureXinDianCacheFile xinDianCacheFile = new MeasureXinDianCacheFile();
                xinDianCacheFile.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                xinDianCacheFile.setOwner(new BmobPointer(SharedPreferenceHelper.getLoginUser()));
                xinDianCacheFile.setFileLength(cursor.getString(cursor.getColumnIndex("fileLength")));
                result.add(xinDianCacheFile);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public static void upLoadCacheFile(){
        //上传文件
        final List<MeasureXinDianCacheFile> files = XinDianCacheHelper.getCacheList();
        if (files != null && files.size() > 0){
            final String[] filePaths = new String[files.size()];
            for (int i = 0; i < files.size(); i++) {
                filePaths[i] = FileCacheUtil.getCacheFilePath(
                        files.get(i).getOwner().getObjectId(),
                        files.get(i).getFileName()
                );
            }
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == filePaths.length){
                        //全部上传完成
                        List<BmobObject> bmobObjects = new ArrayList<>();
                        for (int i = 0; i < filePaths.length; i++) {
                            files.get(i).setFileUrl(list1.get(i));
                            bmobObjects.add(files.get(i));
                        }
                        new BmobBatch().insertBatch(bmobObjects).doBatch(new QueryListListener<BatchResult>() {
                            @Override
                            public void done(List<BatchResult> list, BmobException e) {
                               if (e == null){
                                   deleteAll();
                               }
                            }
                        });
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }
}
