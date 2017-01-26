package wang.fly.com.yunhealth.DataBasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureData;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureFenChen;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureMaiBo;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureNaoDian;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureTiWen;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureXinDian;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureXueTang;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureXueYa;
import wang.fly.com.yunhealth.DataBasePackage.MeasureData.MeasureXueYang;
import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

/**
 * Created by 82661 on 2016/11/17.
 */

public class MyDataBase extends SQLiteOpenHelper {
    private static final String TAG = "MyDataBase";
    public static final int ERROR_LOAD = -1;
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
    private Context context;

    public MyDataBase(Context context,
                      String name,
                      SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REPORT_MENU);
        db.execSQL(CREATE_MEASURE_DATA_CACHE);
        db.execSQL(CREATE_HEIGHT_WEIGHT_CACHE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: old" + oldVersion);
        switch (oldVersion) {
            case 1:
                db.execSQL(CREATE_MEASURE_DATA_CACHE);
            case 2:
                db.execSQL(CREATE_HEIGHT_WEIGHT_CACHE);
            default:
        }
    }

    /**
     * 向身高数据库中添加数据
     * @param db
     * @param data
     * @param userId
     * @param date
     */
    public void insertHeightAndWeight(SQLiteDatabase db,
                                      HeightAndWeight data,
                                      String userId,
                                      Date date){
        if (data == null || userId == null){
            return;
        }
        ContentValues values = new ContentValues();
        values.clear();
        values.put("height", data.getHeight());
        values.put("weight", data.getWeight());
        values.put("userId", userId);
        values.put("createTime", UtilClass.valueOfDate(date, "yyyy-MM-dd 00:00:00"));
        db.insert("HeightWeightCache", null, values);
    }
    public void initMenuData(SQLiteDatabase db, String userId) {
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
    }

    public void addOneMeasureData(SQLiteDatabase db,
                                  MeasureData measureData,
                                  int type,
                                  Date date, String userId) {
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("name", MainActivity.LABEL_STRING[type]);
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
    }


    public boolean checkTodayWeight(SQLiteDatabase db, Date date, String userId){
        if (db == null || date == null || userId == null){
            return false;
        }
        Cursor cursor = db.rawQuery("select * from HeightWeightCache" +
                " where userId = '" + userId + "'" +
                " and createTime = '" + UtilClass.valueOfDate(date, "yyyy-MM-dd 00:00:00") + "'", null);
        return cursor.moveToFirst();
    }
    /**
     * 查询一个数据，存在返回true，否则为false
     *
     * @param database
     * @param type
     * @param date
     * @return
     */
    public boolean checkOneMeasureDataCache(SQLiteDatabase database,
                                            int type,
                                            Date date,
                                            String userId) {
        if (date == null || database == null || !database.isOpen() || userId == null) {
            return true;
        }
        Cursor cursor = database.rawQuery("select * from MeasureDataCache " +
                "where createTime = " + "'" + UtilClass.valueOfDate(date, null) + " ' and "
                + "type = " + type + " and userId = '" + userId + "'" +
                "", null);
        return cursor.moveToFirst();
    }


    public static final int UPLOAD_NO_DATABASE = -1;
    public static final int UPLOAD_SUCCESS = 0;

    public int upLoadMeasureData(SQLiteDatabase db, String objectId) {
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
                switch (type) {
                    case MainActivity.MEASURE_TYPE_XUEYANG: {
                        MeasureXueYang measureXueYang = new MeasureXueYang();
                        measureData.copyTo(measureXueYang);
                        measureXueYang.setOwner(owner);
                        datas.add(measureXueYang);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_MAIBO: {
                        MeasureMaiBo measureMaiBo = new MeasureMaiBo();
                        measureData.copyTo(measureMaiBo);
                        measureMaiBo.setOwner(owner);
                        datas.add(measureMaiBo);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_XINDIAN: {
                        MeasureXinDian measureXinDian = new MeasureXinDian();
                        measureData.copyTo(measureXinDian);
                        measureXinDian.setOwner(owner);
                        datas.add(measureXinDian);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_TIWEN: {
                        MeasureTiWen measureTiWen = new MeasureTiWen();
                        measureData.copyTo(measureTiWen);
                        measureTiWen.setOwner(owner);
                        datas.add(measureTiWen);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_FENCHEN: {
                        MeasureFenChen measureFenChen = new MeasureFenChen();
                        measureData.copyTo(measureFenChen);
                        measureFenChen.setOwner(owner);
                        datas.add(measureFenChen);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_NAODIAN: {
                        MeasureNaoDian measureNaoDian = new MeasureNaoDian();
                        measureData.copyTo(measureNaoDian);
                        measureNaoDian.setOwner(owner);
                        datas.add(measureNaoDian);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_XUEYA: {
                        MeasureXueYa measureXueYa = new MeasureXueYa();
                        measureData.copyTo(measureXueYa);
                        measureXueYa.setOwner(owner);
                        datas.add(measureXueYa);
                        break;
                    }
                    case MainActivity.MEASURE_TYPE_XUETANG: {
                        MeasureXueTang measureXueTang = new MeasureXueTang();
                        measureData.copyTo(measureXueTang);
                        measureXueTang.setOwner(owner);
                        datas.add(measureXueTang);
                        break;
                    }
                }
            } while (cursor.moveToNext());
        } else {
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
            return datas.size();
        } else {
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
