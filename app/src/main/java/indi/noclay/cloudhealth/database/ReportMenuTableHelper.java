package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.zxing.decoding.Intents;

import java.util.ArrayList;
import java.util.List;

import indi.noclay.cloudhealth.adapter.RecycleAdapterForReportMenu;
import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;

import static indi.noclay.cloudhealth.database.LocalDataBase.*;

/**
 * Created by clay on 2018/4/17.
 */

public class ReportMenuTableHelper {
    public static final String TABLE_REPORT_MENU = "report_menu";

    public static final String CREATE_REPORT_MENU = "" +
            "create table " + TABLE_REPORT_MENU + " (" +
            "id integer primary key autoincrement," +
            "userId text," +
            "content text unique," +
            "image integer," +
            "type integer," +
            "checked integer)";
    public static void initMenuData() {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        if (userId == null) {
            return;
        }
        ContentValues values = new ContentValues();
        for (int i = 0; i < ConstantsConfig.MENU_CONTENTS.length; i++) {
            values.put("content", ConstantsConfig.MENU_CONTENTS[i]);
            values.put("image", ConstantsConfig.MENU_ICONS[i]);
            values.put("type", ConstantsConfig.MENU_TYPES[i]);
            values.put("checked", 0);
            values.put("userId", userId);
            db.insert(TABLE_REPORT_MENU, null, values);
            values.clear();
        }
        db.close();
        instance.close();
    }

    public static void updateMenuData(int check, MenuInfo menuInfo){
        if (menuInfo == null){
            return;
        }
        SQLiteDatabase database = getDefaultInstance().getWritableDatabase();
        database.execSQL("update " + TABLE_REPORT_MENU +
                " set checked = " + check +
                " where content = '" + menuInfo.getTitle() + "'" +
                " and userId = '" + SharedPreferenceHelper.getLoginUserId() + "'");
        database.close();
    }

    public static List<MenuInfo> getMenuData(int type){
        List<MenuInfo> result = new ArrayList<>();
        SQLiteDatabase db = getDefaultInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_REPORT_MENU +
                " where type = " + type + " and userId = '" + SharedPreferenceHelper.getLoginUserId() + "'", null);
        if (cursor.moveToFirst()){
            do {
                MenuInfo m = new MenuInfo();
                m.setType(type);
                m.setTitle(cursor.getString(cursor.getColumnIndex("content")));
                m.setImage(cursor.getInt(cursor.getColumnIndex("image")));
                int flag = cursor.getInt(cursor.getColumnIndex("checked"));
                if (flag == 0){
                    m.setChecked(false);
                }else{
                    m.setChecked(true);
                }
                result.add(m);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
