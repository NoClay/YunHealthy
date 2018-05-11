package indi.noclay.cloudhealth.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.HealthApplication;


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
        db.execSQL(XinDianCacheHelper.CREATE_XINDIAN_CHCHE);
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
            case 5:
                db.execSQL(XinDianCacheHelper.CREATE_XINDIAN_CHCHE);
            default:
        }
    }


}
