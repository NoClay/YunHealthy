package indi.noclay.cloudhealth.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.*;


/**
 * Created by clay on 2018/4/17.
 */

public class FoodKindTableHelper {
    public static final String TABLE_FOOD_KIND = "FoodKind";

    public static final String CREATE_FOOD_KIND = "" +
            "create table " + TABLE_FOOD_KIND + " (" +
            "id text primary key, " +
            "userId text, " +
            "foodKindName text, " +
            "isShow boolean)";

    public List<FoodKind> getFoodKindFromLocal(boolean isShow){
        LocalDataBase local = getDefaultInstance();
        String user = SharedPreferenceHelper.getLoginUserId();
        if (user == null || local.getReadableDatabase() == null){
            return null;
        }
        SQLiteDatabase database = local.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_FOOD_KIND +
                " where userId = '" + user + "' " +
                " and isShow = " + isShow, null);
        List<FoodKind> result = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
               FoodKind temp = new FoodKind();
               temp.setFoodKindName(cursor.getString(cursor.getColumnIndex("foodKindName")));
               temp.setShow(UtilClass.booleanValueOfInteger(cursor.getInt(cursor.getColumnIndex("isShow"))));
               temp.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
               result.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return result;
    }
}
