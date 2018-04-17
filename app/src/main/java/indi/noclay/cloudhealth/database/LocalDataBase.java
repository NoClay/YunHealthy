package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.HealthApplication;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;


/**
 * Created by 82661 on 2016/11/17.
 */

public class LocalDataBase extends SQLiteOpenHelper {
    private static final String TAG = "MyDataBase";


    public static final String CREATE_REPORT_MENU = "" +
            "create table report_menu (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "content text unique," +
            "image integer," +
            "type integer," +
            "checked integer)";
    public static final String CREATE_MEASURE_DATA_CACHE = "" +
            "create table MeasureDataCache (" +
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
    public static final String CREATE_HEIGHT_WEIGHT_CACHE = "" +
            "create table HeightWeightCache (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "height float," +
            "weight float," +
            "createTime text)";

    public static final String CREATE_MEDICINE_DETAIL = "" +
            "create table MedicineDetail (" +
            "id text primary key, " +
            "userId text," +
            "medicineName text," +
            "medicinePicture text," +
            "useType text," +
            "tag text," +
            "doctor text," +
            "dayLength integer," +
            "dayCount integer," +
            "times text, " +
            "doses text, " +
            "startTime text, " +
            "unit text, " +
            "isOpen integer )";

    public LocalDataBase(Context context,
                         String name,
                         SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    public static LocalDataBase getDefaultInstance(){
        return new LocalDataBase(HealthApplication.getContext(),
                ConstantsConfig.DEFAULT_LOCAL_DATABASE, null,
                ConstantsConfig.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REPORT_MENU);
        db.execSQL(CREATE_MEASURE_DATA_CACHE);
        db.execSQL(CREATE_HEIGHT_WEIGHT_CACHE);
        db.execSQL(CREATE_MEDICINE_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(CREATE_MEASURE_DATA_CACHE);
            case 2:
                db.execSQL(CREATE_HEIGHT_WEIGHT_CACHE);
            case 3:
                db.execSQL(CREATE_MEDICINE_DETAIL);
            default:
        }
    }



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
        db.insert("MeasureDataCache", null, values);
        db.close();
        instance.close();
    }

}
