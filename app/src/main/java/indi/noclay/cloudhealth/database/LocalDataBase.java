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
    public static final int ERROR_LOAD = -1;
    public static final int UPLOAD_NO_DATABASE = -1;
    public static final int UPLOAD_SUCCESS = 0;
    public static final int CLOCK_OPEN = 0;
    public static final int CLOCK_CLOSE = 1;
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
        db.insert("HeightWeightCache", null, values);
        db.close();
        instance.close();
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

    public static HeightAndWeight checkTodayWeight(Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        if (db == null || date == null || userId == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("select * from HeightWeightCache" +
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
        Cursor cursor = db.rawQuery("select * from HeightWeightCache" +
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


    /**
     * 查询一个数据，存在返回true，否则为false
     * @param type
     * @param date
     * @return
     */
    public static boolean checkOneMeasureDataCache(int type, Date date) {
        String userId = SharedPreferenceHelper.getLoginUserId();
        LocalDataBase instance = getDefaultInstance();
        if (date == null || userId == null) {
            return true;
        }
        Cursor cursor = instance.getReadableDatabase().rawQuery("select * from MeasureDataCache " +
                "where createTime = " + "'" + UtilClass.valueOfDate(date, null) + " ' and "
                + "type = " + type + " and userId = '" + userId + "'" +
                "", null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        instance.close();
        return result;
    }

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



    public static int upLoadMeasureData(String objectId) {
        LocalDataBase instance = getDefaultInstance();
        SQLiteDatabase db = instance.getWritableDatabase();
        //检查网络状态
        if (db == null) {
            Log.d(TAG, "upLoadMeasureData: db is null");
            return ERROR_LOAD;
        }
        Cursor cursor = db.rawQuery("select * from MeasureDataCache where userId = '" + objectId + "'", null);
        List<BmobObject> datas = new ArrayList<>();
        SignUserData owner = new SignUserData();
        owner.setObjectId(objectId);
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                MeasureData measureData = new MeasureData();
                measureData.setAverageData(cursor.getFloat(cursor.getColumnIndex("average")));
                measureData.setCount(cursor.getInt(cursor.getColumnIndex("count")));
                measureData.setMaxData(cursor.getFloat(cursor.getColumnIndex("max")));
                measureData.setMaxData(cursor.getFloat(cursor.getColumnIndex("min")));
                measureData.setAverageDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isAverageDanger"))));
                measureData.setMaxDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isMaxDanger"))));
                measureData.setMinDanger(UtilClass.booleanValueOfInteger(
                        cursor.getInt(cursor.getColumnIndex("isMinDanger"))));
                Date createTime = UtilClass.resolveBmobDate(
                        cursor.getString(cursor.getColumnIndex("createTime")),
                        null
                );
                measureData.setMeasureTime(new BmobDate(createTime));
                datas.add(MeasureData.getBmobObject(type, measureData, owner));
            } while (cursor.moveToNext());
        } else {
            db.close();
            return 0;
        }
        Log.d(TAG, "upLoadMeasureData: datas.size" + datas.size());
        boolean[] flags = new boolean[datas.size() / 50 + 1];
        for (int i = 0; i < datas.size(); i += 50) {
            flags[i / 50] = upLoadFiftyData(datas, i);
            Log.d(TAG, "upLoadMeasureData: i = " + i);
            Log.d(TAG, "upLoadMeasureData: load = " + flags[i / 50]);
        }
        boolean flag = false;
        for (boolean flag1 : flags) {
            if (flag1) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //有一批上传成功
            db.delete("MeasureDataCache", null, null);
            db.close();
            instance.close();
            return datas.size();
        } else {
            db.close();
            instance.close();
            return ERROR_LOAD;
        }
    }

    public static boolean upLoadFiftyData(List<BmobObject> dataArray, int length) {
        if (dataArray == null) {
            return true;
        }
        List<BmobObject> item = new ArrayList<>();
        for (int i = length; i < length + 50 && i < dataArray.size(); i++) {
            item.add(dataArray.get(i));
        }
        final boolean[] flag = new boolean[1];
        //进行上传操作
        new BmobBatch().insertBatch(item);
        new BmobBatch().doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {
                    flag[0] = true;
                }
            }
        });
        return flag[0];
    }
}
