package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import indi.noclay.cloudhealth.util.ConstantsConfig;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;

/**
 * Created by clay on 2018/4/17.
 */

public class ReportMenuTableHelper {
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
            db.insert("report_menu", null, values);
            values.clear();
        }
        db.close();
        instance.close();
    }
}
