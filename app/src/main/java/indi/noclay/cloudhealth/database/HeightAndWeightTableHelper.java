package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;

/**
 * Created by clay on 2018/4/17.
 */

public class HeightAndWeightTableHelper {
    public static final String TABLE_HEIGHT_AND_WEIGHT_CACHE = "HeightWeightCache";

    public static final String CREATE_HEIGHT_WEIGHT_CACHE = "" +
            "create table " + TABLE_HEIGHT_AND_WEIGHT_CACHE + " (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "height float," +
            "weight float," +
            "createTime text)";
    /**
     * 向身高数据库中添加数据
     * @param data
     * @param date
     */
    public static void insertHeightAndWeight(HeightAndWeight data, Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        if (data == null || userId == null) {
            return;
        }
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put("height", data.getHeight());
        values.put("weight", data.getWeight());
        values.put("userId", userId);
        values.put("createTime", UtilClass.valueOfDate(date, "yyyy-MM-dd HH:MM:00"));
        db.insert(TABLE_HEIGHT_AND_WEIGHT_CACHE, null, values);
        db.close();
        instance.close();
    }

    public static HeightAndWeight checkTodayWeight(Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        if (db == null || date == null || userId == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("select * from " + TABLE_HEIGHT_AND_WEIGHT_CACHE +
                " where userId = '" + userId + "'" +
                " and createTime like '"
                + UtilClass.valueOfDate(date, "yyyy-MM-dd 00:00:00").substring(0, 10) + "%'", null);
        if (cursor.moveToFirst()) {
            HeightAndWeight data = new HeightAndWeight();
            data.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
            data.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
            cursor.close();
            db.close();
            instance.close();
            return data;
        }
        cursor.close();
        db.close();
        instance.close();
        return null;
    }

    public static HeightAndWeight checkLastWeight() {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        if (db == null || userId == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("select * from " + TABLE_HEIGHT_AND_WEIGHT_CACHE +
                " where userId = '" + userId + "'" +
                " order by createTime desc ", null);

        if (cursor.moveToFirst()) {
            HeightAndWeight data = new HeightAndWeight();
            data.setHeight(cursor.getFloat(cursor.getColumnIndex("height")));
            data.setWeight(cursor.getFloat(cursor.getColumnIndex("weight")));
            cursor.close();
            db.close();
            instance.close();
            return data;
        }
        cursor.close();
        db.close();
        instance.close();
        return null;
    }
}
