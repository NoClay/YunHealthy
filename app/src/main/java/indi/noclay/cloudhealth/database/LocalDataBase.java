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

    public LocalDataBase(Context context,
                         String name,
                         SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    public static LocalDataBase getDefaultInstance() {
        return new LocalDataBase(HealthApplication.getContext(),
                ConstantsConfig.DEFAULT_LOCAL_DATABASE, null,
                ConstantsConfig.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReportMenuTableHelper.CREATE_REPORT_MENU);
        db.execSQL(MeasureTableHelper.CREATE_MEASURE_DATA_CACHE);
        db.execSQL(HeightAndWeightTableHelper.CREATE_HEIGHT_WEIGHT_CACHE);
        db.execSQL(MedicineTableHelper.CREATE_MEDICINE_DETAIL);
        db.execSQL(FoodKindTableHelper.CREATE_FOOD_KIND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(MeasureTableHelper.CREATE_MEASURE_DATA_CACHE);
            case 2:
                db.execSQL(HeightAndWeightTableHelper.CREATE_HEIGHT_WEIGHT_CACHE);
            case 3:
                db.execSQL(MedicineTableHelper.CREATE_MEDICINE_DETAIL);
            case 4:
                db.execSQL(FoodKindTableHelper.CREATE_FOOD_KIND);
            default:
        }
    }


}
