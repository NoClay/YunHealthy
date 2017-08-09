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
import indi.noclay.cloudhealth.R;
import indi.noclay.cloudhealth.database.measuredata.MeasureData;
import indi.noclay.cloudhealth.database.measuredata.MeasureFenChen;
import indi.noclay.cloudhealth.database.measuredata.MeasureMaiBo;
import indi.noclay.cloudhealth.database.measuredata.MeasureNaoDian;
import indi.noclay.cloudhealth.database.measuredata.MeasureTiWen;
import indi.noclay.cloudhealth.database.measuredata.MeasureXinDian;
import indi.noclay.cloudhealth.database.measuredata.MeasureXueTang;
import indi.noclay.cloudhealth.database.measuredata.MeasureXueYa;
import indi.noclay.cloudhealth.database.measuredata.MeasureXueYang;
import indi.noclay.cloudhealth.fragment.DataMedicalFragment;
import indi.noclay.cloudhealth.util.MyConstants;
import indi.noclay.cloudhealth.util.UtilClass;


/**
 * Created by 82661 on 2016/11/17.
 */

public class MyDataBase extends SQLiteOpenHelper {
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
    private Context context;

    public MyDataBase(Context context,
                      String name,
                      SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
        Log.d(TAG, "MyDataBase: version = " + version);
        this.context = context;
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
        Log.d(TAG, "onUpgrade: old = " + oldVersion);
        Log.d(TAG, "onUpgrade: new = " + newVersion);
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

    public boolean isNeedEatMedicine(String time, String userId){
        Cursor cursor = getReadableDatabase().rawQuery("select * from MedicineDetail" +
                " where userId = '" + userId + "' " +
                " and times like '%" + time + "%' " +
                " and isOpen = " + CLOCK_OPEN +
                " and dayLength - dayCount > 0"  , null);
        return cursor.moveToFirst();
    }



    public List<MedicineDetail> getMedicineDetail(int type, String time, String userId){
        List<MedicineDetail> medicineDetails = new ArrayList<>();
        Cursor cursor;
        if (type == DataMedicalFragment.NOW_MEDICINE) {
            cursor = getReadableDatabase().rawQuery("select * from MedicineDetail" +
                    " where userId = '" + userId + "' " +
                    " and dayLength - dayCount > 0"  , null);
        } else if (type == DataMedicalFragment.LAST_MEDICINE){
            cursor = getReadableDatabase().rawQuery("select * from MedicineDetail" +
                    " where userId = '" + userId + "' " +
                    " and dayLength - dayCount <= 0"  , null);
        } else{
            cursor = getReadableDatabase().rawQuery("select * from MedicineDetail" +
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
        return medicineDetails;
    }

    public void updateMedicineDetail(String userId, MedicineDetail medicineDetail){
        SQLiteDatabase db = getWritableDatabase();
        deleteMedicineDetail(medicineDetail);
        insertMedicineDetail(userId, medicineDetail);
    }
    public void insertMedicineDetail(String userId,
                                     MedicineDetail medicine){
        if (medicine == null || userId == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
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
    }
    public void deleteMedicineDetail(MedicineDetail medicine){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from MedicineDetail " +
                "where id = '" + medicine.getObjectId() + "'");
        db.close();
    }

    /**
     * 向身高数据库中添加数据
     *
     * @param data
     * @param userId
     * @param date
     */
    public void insertHeightAndWeight(HeightAndWeight data,
                                      String userId,
                                      Date date) {
        if (data == null || userId == null) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put("height", data.getHeight());
        values.put("weight", data.getWeight());
        values.put("userId", userId);
        values.put("createTime", UtilClass.valueOfDate(date, "yyyy-MM-dd HH:MM:00"));
        db.insert("HeightWeightCache", null, values);
        db.close();
    }

    public void addOneMeasureData(
                                  MeasureData measureData,
                                  int type,
                                  Date date, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("name", MyConstants.LABEL_STRING[type]);
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
    }

    public HeightAndWeight checkTodayWeight(Date date, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
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
            db.close();
            return data;
        }
        db.close();
        return null;
    }

    public HeightAndWeight checkLastWeight(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
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
            db.close();
            return data;
        }
        db.close();
        return null;
    }


    /**
     * 查询一个数据，存在返回true，否则为false
     *
     * @param type
     * @param date
     * @return
     */
    public boolean checkOneMeasureDataCache(int type,
                                            Date date,
                                            String userId) {
        SQLiteDatabase database = this.getWritableDatabase();
        if (date == null || database == null || !database.isOpen() || userId == null) {
            return true;
        }
        Cursor cursor = database.rawQuery("select * from MeasureDataCache " +
                "where createTime = " + "'" + UtilClass.valueOfDate(date, null) + " ' and "
                + "type = " + type + " and userId = '" + userId + "'" +
                "", null);
        database.close();
        return cursor.moveToFirst();
    }

    public void initMenuData(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (userId == null) {
            return;
        }
        //检验单
        ContentValues values = new ContentValues();
        values.put("content", "血常规");
        values.put("image", R.drawable.jcd_ic_rb);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);
        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "血生化全套");
        values.put("image", R.drawable.jcd_ic_bb);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);
        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "他克莫司浓度");
        values.put("image", R.drawable.jcd_ic_fk);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);
        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "环孢素浓度");
        values.put("image", R.drawable.jcd_ic_csa);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "雷帕霉素浓度");
        values.put("image", R.drawable.jcd_ic_rapa);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "尿常规");
        values.put("image", R.drawable.jcd_ic_rt);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "群体反应抗体(PRA)");
        values.put("image", R.drawable.jcd_ic_grad1);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "超敏C反应蛋白");
        values.put("image", R.drawable.jcd_ic_acpd);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "凝血功能");
        values.put("image", R.drawable.jcd_ic_csa);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "血气分析");
        values.put("image", R.drawable.jcd_ic_bga);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肿瘤标志物");
        values.put("image", R.drawable.jcd_ic_tm);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肝炎系列");
        values.put("image", R.drawable.jcd_ic_hbs);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "血型");
        values.put("image", R.drawable.jcd_ic_bt);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "粪便常规");
        values.put("image", R.drawable.jcd_ic_fr);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "甲状旁腺激素");
        values.put("image", R.drawable.jcd_ic_pth);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "多瘤病毒");
        values.put("image", R.drawable.jcd_ic_bkv);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "乙肝病毒");
        values.put("image", R.drawable.jcd_ic_hbv_dna);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "霉芬酸");
        values.put("image", R.drawable.jcd_ic_mpa);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肾功能");
        values.put("image", R.drawable.jcd_ic_shengongneng);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肝功能");
        values.put("image", R.drawable.jcd_ic_gangongneng);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "血脂");
        values.put("image", R.drawable.jcd_ic_xuezhi);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "糖化血红蛋白");
        values.put("image", R.drawable.ft_ic_tanghuaxuehong);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "B2微球蛋白");
        values.put("image", R.drawable.ft_ic_weiqiudanbai);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "转铁蛋白+前白蛋白测定");
        values.put("image", R.drawable.ft_ic_zhuantiedaibai);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "铁三项");
        values.put("image", R.drawable.ft_ic_tiesanxiang);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "常规四项");
        values.put("image", R.drawable.ft_ic_changgui);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "尿蛋白、尿素、肌酐测定");
        values.put("image", R.drawable.ft_ic_niaodanbai);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "腹水常规检查");
        values.put("image", R.drawable.ft_ic_fushui);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "腹透液Cr、BuN、pro");
        values.put("image", R.drawable.ft_ic_futouye);
        values.put("type", 1);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        //检查单
        values.put("content", "心电图");
        values.put("image", R.drawable.jdc_icon_ecg);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "心超");
        values.put("image", R.drawable.icon_xinchao);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "颈部血管B超");
        values.put("image", R.drawable.jcd_ic_jingbuxueguanbchao);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "左上肢动脉超声");
        values.put("image", R.drawable.icon_zuoshangzhidongmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "左上肢静脉超声");
        values.put("image", R.drawable.icon_zuoshangzhijingmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "右上肢动脉超声");
        values.put("image", R.drawable.icon_youshangzhidongmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "右上肢静脉超声");
        values.put("image", R.drawable.icon_youshangzhijingmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "左下肢动脉超声");
        values.put("image", R.drawable.icon_zuoxiazhidongmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "左下肢静脉超声");
        values.put("image", R.drawable.icon_zuoxiazhijingmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "右下肢动脉超声");
        values.put("image", R.drawable.icon_youxiazhidongmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "右下肢静脉超声");
        values.put("image", R.drawable.icon_youxiazhijingmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "胸片");
        values.put("image", R.drawable.icon_xiongpian);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肺部CT");
        values.put("image", R.drawable.icon_feipian);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肝胆脾胰超声");
        values.put("image", R.drawable.icon_gandanpiyi);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "泌尿系B超");
        values.put("image", R.drawable.jcd_ic_miniaoxibchao);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "移植肾超声");
        values.put("image", R.drawable.jcd_ic_ultrasound);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "胸水超声");
        values.put("image", R.drawable.icon_xiongshuichaosheng);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "腹水超声");
        values.put("image", R.drawable.icon_fubuchaosheng);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "头部CT");
        values.put("image", R.drawable.icon_toulu);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "头颅MRI");
        values.put("image", R.drawable.icon_toulumir);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "颈部及腋下淋巴结超声");
        values.put("image", R.drawable.icon_jinbujiyexialinbajie);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "腹股沟淋巴结超声");
        values.put("image", R.drawable.icon_fugugoulinbajie);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肾动脉CT");
        values.put("image", R.drawable.icon_shendongmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "肾静脉CT");
        values.put("image", R.drawable.icon_shenjingmai);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "磁共振泌尿系水成像检查");
        values.put("image", R.drawable.icon_cigongzhenminiao);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        values.put("content", "CT尿路造影");
        values.put("image", R.drawable.icon_ctniaoluzaoying);
        values.put("type", 2);
        values.put("checked", 0);
        values.put("userId", userId);

        db.insert("report_menu", null, values);
        values.clear();
        db.close();
    }



    public int upLoadMeasureData(String objectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        //检查网络状态
        Log.d(TAG, "onUpgrade: old" + db.getVersion());

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
                switch (type) {
                    case MyConstants.MEASURE_TYPE_XUEYANG: {
                        MeasureXueYang measureXueYang = new MeasureXueYang();
                        measureData.copyTo(measureXueYang);
                        measureXueYang.setOwner(owner);
                        datas.add(measureXueYang);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_MAIBO: {
                        MeasureMaiBo measureMaiBo = new MeasureMaiBo();
                        measureData.copyTo(measureMaiBo);
                        measureMaiBo.setOwner(owner);
                        datas.add(measureMaiBo);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_XINDIAN: {
                        MeasureXinDian measureXinDian = new MeasureXinDian();
                        measureData.copyTo(measureXinDian);
                        measureXinDian.setOwner(owner);
                        datas.add(measureXinDian);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_TIWEN: {
                        MeasureTiWen measureTiWen = new MeasureTiWen();
                        measureData.copyTo(measureTiWen);
                        measureTiWen.setOwner(owner);
                        datas.add(measureTiWen);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_FENCHEN: {
                        MeasureFenChen measureFenChen = new MeasureFenChen();
                        measureData.copyTo(measureFenChen);
                        measureFenChen.setOwner(owner);
                        datas.add(measureFenChen);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_NAODIAN: {
                        MeasureNaoDian measureNaoDian = new MeasureNaoDian();
                        measureData.copyTo(measureNaoDian);
                        measureNaoDian.setOwner(owner);
                        datas.add(measureNaoDian);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_XUEYA: {
                        MeasureXueYa measureXueYa = new MeasureXueYa();
                        measureData.copyTo(measureXueYa);
                        measureXueYa.setOwner(owner);
                        datas.add(measureXueYa);
                        break;
                    }
                    case MyConstants.MEASURE_TYPE_XUETANG: {
                        MeasureXueTang measureXueTang = new MeasureXueTang();
                        measureData.copyTo(measureXueTang);
                        measureXueTang.setOwner(owner);
                        datas.add(measureXueTang);
                        break;
                    }
                }
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
        for (int i = 0; i < flags.length; i++) {
            if (flags[i]) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //有一批上传成功
            db.delete("MeasureDataCache", null, null);
            db.close();
            return datas.size();
        } else {
            db.close();
            return ERROR_LOAD;
        }
    }

    public boolean upLoadFiftyData(List<BmobObject> dataArray, int length) {
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
