package indi.noclay.cloudhealth.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;

/**
 * Created by clay on 2018/4/17.
 */

public class MeasureTableHelper {
    public static final int ERROR_LOAD = -1;
    public static final int UPLOAD_NO_DATABASE = -1;
    public static final int UPLOAD_SUCCESS = 0;

    private static final String TAG = "MeasureTableHelper";
    /**
     * 查询一个数据，存在返回true，否则为false
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
        Cursor cursor = instance.getReadableDatabase().rawQuery("select * from MeasureDataCache " +
                "where createTime = " + "'" + UtilClass.valueOfDate(date, null) + " ' and "
                + "type = " + type + " and userId = '" + userId + "'" +
                "", null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        instance.close();
        return result;
    }



    public static int upLoadMeasureData(String objectId) {
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        //检查网络状态
        if (db == null) {
            Log.d(TAG, "upLoadMeasureData: db is null");
            return ERROR_LOAD;
        }
        Cursor cursor = db.rawQuery("select * from MeasureDataCache where userId = '" + objectId + "'", null);
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
                measureData.setMaxData(cursor.getFloat(cursor.getColumnIndex("min")));
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
                datas.add(MeasureData.getBmobObject(type, measureData, owner));
            } while (cursor.moveToNext());
        } else {
            db.close();
            return 0;
        }
        Log.d(TAG, "upLoadMeasureData: datas.size" + datas.size());
        boolean[] flags = new boolean[datas.size() / 50 + 1];
        for (int i = 0; i < datas.size(); i += 50) {
            flags[i / 50] = upLoadFiftyData(datas, i);
            Log.d(TAG, "upLoadMeasureData: i = " + i);
            Log.d(TAG, "upLoadMeasureData: load = " + flags[i / 50]);
        }
        boolean flag = false;
        for (boolean flag1 : flags) {
            if (flag1) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //有一批上传成功
            db.delete("MeasureDataCache", null, null);
            db.close();
            instance.close();
            return datas.size();
        } else {
            db.close();
            instance.close();
            return ERROR_LOAD;
        }
    }

    public static boolean upLoadFiftyData(List<BmobObject> dataArray, int length) {
        if (dataArray == null) {
            return true;
        }
        List<BmobObject> item = new ArrayList<>();
        for (int i = length; i < length + 50 && i < dataArray.size(); i++) {
            item.add(dataArray.get(i));
        }
        final boolean[] flag = new boolean[1];
        //进行上传操作
        new BmobBatch().insertBatch(item);
        new BmobBatch().doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {
                    flag[0] = true;
                }
            }
        });
        return flag[0];
    }
}
