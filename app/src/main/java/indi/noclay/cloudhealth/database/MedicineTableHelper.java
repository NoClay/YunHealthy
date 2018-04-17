package indi.noclay.cloudhealth.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;
import indi.noclay.cloudhealth.util.SharedPreferenceHelper;
import indi.noclay.cloudhealth.util.UtilClass;

import static indi.noclay.cloudhealth.database.LocalDataBase.getDefaultInstance;

/**
 * Created by clay on 2018/4/17.
 */

public class MedicineTableHelper {
    public static final int CLOCK_OPEN = 0;
    public static final int CLOCK_CLOSE = 1;
    /**
     * 某一天是否需要吃药
     * @param time
     * @return
     */
    public static boolean isNeedEatMedicine(String time){
        LocalDataBase instance = getDefaultInstance();
        String userId = SharedPreferenceHelper.getLoginUserId();
        if (userId == null){
            return false;
        }
        Cursor cursor = instance.getReadableDatabase().rawQuery("select * from MedicineDetail" +
                " where userId = '" + userId + "' " +
                " and times like '%" + time + "%' " +
                " and isOpen = " + CLOCK_OPEN +
                " and dayLength - dayCount > 0"  , null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        instance.close();
        return result;
    }



    public static List<MedicineDetail> getMedicineDetail(int type, String time){
        LocalDataBase instance = getDefaultInstance();
        String userId = SharedPreferenceHelper.getLoginUserId();
        if (userId == null){
            return new ArrayList<MedicineDetail>();
        }
        List<MedicineDetail> medicineDetails = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase database = instance.getReadableDatabase();
        if (type == DataMedicalFragment.NOW_MEDICINE) {
            cursor = database.rawQuery("select * from MedicineDetail" +
                    " where userId = '" + userId + "' " +
                    " and dayLength - dayCount > 0"  , null);
        } else if (type == DataMedicalFragment.LAST_MEDICINE){
            cursor = database.rawQuery("select * from MedicineDetail" +
                    " where userId = '" + userId + "' " +
                    " and dayLength - dayCount <= 0"  , null);
        } else{
            cursor = database.rawQuery("select * from MedicineDetail" +
                    " where userId = '" + userId + "' " +
                    " and times like '%" + time + "%' " +
                    " and isOpen = " + CLOCK_OPEN +
                    " and dayLength - dayCount > 0"  , null);
        }
        if (cursor.moveToFirst()){
            do {
                MedicineDetail temp = new MedicineDetail();
                temp.setObjectId(cursor.getString(cursor.getColumnIndex("userId")));
                temp.setMedicineName(cursor.getString(cursor.getColumnIndex("medicineName")));
                temp.setMedicinePicture(cursor.getString(cursor.getColumnIndex("medicinePicture")));
                temp.setUseType(cursor.getString(cursor.getColumnIndex("useType")));
                temp.setTag(cursor.getString(cursor.getColumnIndex("tag")));
                temp.setDoctor(cursor.getString(cursor.getColumnIndex("doctor")));
                temp.setDayLength(cursor.getInt(cursor.getColumnIndex("dayLength")));
                temp.setDayCount(cursor.getInt(cursor.getColumnIndex("dayCount")));
                temp.setTimes(UtilClass.asStringList(cursor.getString(cursor.getColumnIndex("times"))));
                temp.setDoses(UtilClass.asFloatList(cursor.getString(cursor.getColumnIndex("doses"))));
                temp.setStartTime(BmobDate.createBmobDate("yyyy-MM-dd HH:mm:ss", cursor.getString(
                        cursor.getColumnIndex("startTime"))));
                temp.setUnit(cursor.getString(cursor.getColumnIndex("unit")));
                temp.setIsOpen(cursor.getInt(cursor.getColumnIndex("isOpen")));
                medicineDetails.add(temp);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        instance.close();
        return medicineDetails;
    }

    public static void updateMedicineDetail(MedicineDetail medicineDetail){
        deleteMedicineDetail(medicineDetail);
        insertMedicineDetail(medicineDetail);
    }
    public static void insertMedicineDetail(MedicineDetail medicine){
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        if (medicine == null || userId == null) {
            return;
        }
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put("id", medicine.getObjectId());
        values.put("userId", userId);
        values.put("medicineName", medicine.getMedicineName());
        values.put("medicinePicture", medicine.getMedicinePicture());
        values.put("useType", medicine.getUseType());
        values.put("tag", medicine.getTag());
        values.put("doctor", medicine.getDoctor());
        values.put("dayLength", medicine.getDayLength());
        values.put("dayCount", medicine.getDayCount());
        values.put("times", Arrays.toString(medicine.getTimes().toArray()));
        values.put("doses", Arrays.toString(medicine.getDoses().toArray()));
        values.put("startTime", medicine.getStartTime().getDate());
        values.put("unit", medicine.getUnit());
        values.put("isOpen", medicine.getIsOpen());
        db.insert("MedicineDetail", null, values);
        db.close();
        instance.close();
    }
    public static void deleteMedicineDetail(MedicineDetail medicine){
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        if (medicine == null || userId == null) {
            return;
        }
        SQLiteDatabase db = instance.getWritableDatabase();
        db.execSQL("delete from MedicineDetail " +
                "where id = '" + medicine.getObjectId() + "'");
        db.close();
        instance.close();
    }
}
