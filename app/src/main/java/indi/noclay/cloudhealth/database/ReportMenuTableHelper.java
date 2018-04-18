package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

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
}
