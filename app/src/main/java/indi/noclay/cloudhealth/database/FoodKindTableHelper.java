package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.*;
import static indi.noclay.cloudhealth.util.UtilClass.integerValueOfBoolean;


/**
 * Created by clay on 2018/4/17.
 */

public class FoodKindTableHelper {
    public static final String TABLE_FOOD_KIND = "FoodKind";

    public static final String CREATE_FOOD_KIND = "" +
            "create table " + TABLE_FOOD_KIND + " (" +
            "id integer primary key autoincrement, " +
            "userId text, " +
            "foodKindName text unique, " +
            "isShow integer)";

    public static List<FoodKind> getFoodKindFromLocal(boolean isShow){
        LocalDataBase local = getDefaultInstance();
        String user = SharedPreferenceHelper.getLoginUserId();
        if (user == null || local.getReadableDatabase() == null){
            return null;
        }
        SQLiteDatabase database = local.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_FOOD_KIND +
                " where userId = '" + user + "' " +
                " and isShow = " + integerValueOfBoolean(isShow), null);
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

    public static List<FoodKind> getFoodKindFromLocal(){
        LocalDataBase local = getDefaultInstance();
        String user = SharedPreferenceHelper.getLoginUserId();
        if (user == null || local.getReadableDatabase() == null){
            return null;
        }
        SQLiteDatabase database = local.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_FOOD_KIND +
                " where userId = '" + user + "' ", null);
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


    public static void insertFoodKind(List<FoodKind> foodKinds){
        if (foodKinds == null || foodKinds.size() <= 0){
            return;
        }
        SQLiteDatabase database = getDefaultInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        String userId = SharedPreferenceHelper.getLoginUserId();
        for (FoodKind temp : foodKinds) {
            values.put("userId", userId);
            values.put("foodKindName", temp.getFoodKindName());
            values.put("isShow", integerValueOfBoolean(temp.isShow()));
            database.insert(TABLE_FOOD_KIND, null, values);
            values.clear();
        }
    }

    public static void updateFoodKind(FoodKind foodKind){
        if (foodKind == null){
            return;
        }
        SQLiteDatabase database = getDefaultInstance().getWritableDatabase();
        database.execSQL("update " + TABLE_FOOD_KIND
                + " set isShow = " + integerValueOfBoolean(foodKind.isShow())
                + " where foodKindName = '" + foodKind.getFoodKindName() + "' "
                + " and userId = '" + SharedPreferenceHelper.getLoginUserId() + "'"
        );
    }
}
