package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;
import static indi.noclay.cloudhealth.service.SynchronizeDataService.UP_LOAD_FAIL;
import static indi.noclay.cloudhealth.service.SynchronizeDataService.UP_LOAD_ING;
import static indi.noclay.cloudhealth.service.SynchronizeDataService.UP_LOAD_START;

/**
 * Created by clay on 2018/4/17.
 */

public class MeasureTableHelper {
    public static final String TABLE_MEASUREDATA_CACHE = "MeasureDataCache";

    public static final String CREATE_MEASURE_DATA_CACHE = "" +
            "create table " + TABLE_MEASUREDATA_CACHE + " (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "name text," +
            "type integer," +
            "average float," +
            "max float," +
            "min float," +
            "count integer," +
            "isAverageDanger boolean," +
            "isMaxDanger boolean," +
            "isMinDanger boolean," +
            "createTime text)";
    public static final int ERROR_LOAD = -1;
    public static final int UPLOAD_NO_DATABASE = -1;
    public static final int UPLOAD_SUCCESS = 0;

    private static final String TAG = "MeasureTableHelper";


    public static void addOneMeasureData(MeasureData measureData, int type, Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("name", ConstantsConfig.LABEL_STRING[type]);
        values.put("type", type);
        values.put("average", measureData.getAverageData());
        values.put("max", measureData.getMaxData());
        values.put("min", measureData.getMinData());
        values.put("count", measureData.getCount());
        values.put("isAverageDanger", measureData.getAverageDanger());
        values.put("isMaxDanger", measureData.getMaxDanger());
        values.put("isMinDanger", measureData.getMinDanger());
        values.put("createTime", UtilClass.valueOfDate(date, null));
        db.insert(TABLE_MEASUREDATA_CACHE, null, values);
        db.close();
        instance.close();
    }

    /**
     * 查询一个数据，存在返回true，否则为false
     *
     * @param type
     * @param date
     * @return
     */
    public static boolean checkOneMeasureDataCache(int type, Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        if (date == null || userId == null) {
            return true;
        }
        Cursor cursor = instance.getReadableDatabase().rawQuery("select * from " + TABLE_MEASUREDATA_CACHE +
                " where createTime = " + "'" + UtilClass.valueOfDate(date, null) + " ' and "
                + "type = " + type + " and userId = '" + userId + "'" +
                "", null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        instance.close();
        return result;
    }


    public static int upLoadMeasureData(String objectId, Handler handler) {
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        //检查网络状态
        if (db == null) {
            Log.d(TAG, "upLoadMeasureData: db is null");
            return ERROR_LOAD;
        }
        Cursor cursor = db.rawQuery("select * from " + TABLE_MEASUREDATA_CACHE +
                " where userId = '" + objectId + "'", null);
        List<BmobObject> datas = new ArrayList<>();
        SignUserData owner = new SignUserData();
        owner.setObjectId(objectId);
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                MeasureData measureData = new MeasureData();
                measureData.setAverageData(cursor.getFloat(cursor.getColumnIndex("average")));
                measureData.setCount(cursor.getInt(cursor.getColumnIndex("count")));
                measureData.setMaxData(cursor.getFloat(cursor.getColumnIndex("max")));
                measureData.setMinData(cursor.getFloat(cursor.getColumnIndex("min")));
                measureData.setAverageDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isAverageDanger"))));
                measureData.setMaxDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isMaxDanger"))));
                measureData.setMinDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isMinDanger"))));
                Date createTime = UtilClass.resolveBmobDate(
                        cursor.getString(cursor.getColumnIndex("createTime")),
                        null
                );
                measureData.setMeasureTime(new BmobDate(createTime));
                datas.add(MeasureData.getBmobObject(type, measureData, new BmobPointer(owner)));
            } while (cursor.moveToNext());
        } else {
            db.close();
            return 0;
        }
        Log.d(TAG, "upLoadMeasureData: datas.size" + datas.size());
        handler.obtainMessage(UP_LOAD_START, datas.size(), 50).sendToTarget();
        //开始上传
        for (int i = 0; i < datas.size(); i += 50) {
            upLoadFiftyData(datas, i, handler);
            Log.d(TAG, "upLoadMeasureData: i = " + i);
        }
        if (datas.size() > 0) {
            //有一批上传成功
            db.close();
            instance.close();
            return datas.size();
        } else {
            db.close();
            instance.close();
            return ERROR_LOAD;
        }
    }

    public static void deleteAll() {
//        LocalDataBase instance = getDefaultInstance();
//        SQLiteDatabase db = instance.getWritableDatabase();
//        db.delete("MeasureDataCache", null, null);
//        db.close();
//        instance.close();
    }

    public static void upLoadFiftyData(List<BmobObject> dataArray, final int beginIndex, final Handler handler) {
        if (dataArray == null) {
            return;
        }
        List<BmobObject> item = new ArrayList<>();
        for (int i = beginIndex; i < beginIndex + 50 && i < dataArray.size(); i++) {
            item.add(dataArray.get(i));
        }
        //进行上传操作
        new BmobBatch().insertBatch(item).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    int count = 0;
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            count++;
                            Log.d(TAG, "done: 成功");
                        } else {
                            Log.d(TAG,"第" + (i + beginIndex) + "个失败：" + ex.getErrorCode() + "," + ex.getMessage());
                        }
                    }
                    handler.obtainMessage(UP_LOAD_ING, count, beginIndex / 50).sendToTarget();
                } else {
                    handler.obtainMessage(UP_LOAD_FAIL, 0, beginIndex / 50).sendToTarget();
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public static void selectAllData(){
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_MEASUREDATA_CACHE, null);
        if (cursor.moveToFirst()){
            do {
                Log.d(TAG, "selectAllData: min = " + cursor.getFloat(cursor.getColumnIndex("min")));
            }while (cursor.moveToNext());
        }
        db.close();
        instance.close();
    }
}
