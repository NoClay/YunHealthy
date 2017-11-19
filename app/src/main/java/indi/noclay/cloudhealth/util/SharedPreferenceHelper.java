package indi.noclay.cloudhealth.util;

import android.content.SharedPreferences;


import cn.bmob.v3.datatype.BmobDate;
import indi.noclay.cloudhealth.database.SignUserData;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by noclay on 2017/4/16.
 */

public class SharedPreferenceHelper {

    public static final String LOGIN_STATE_PREF = "LoginState";

    public static SharedPreferences.Editor getEditor(String prefName){
        return HealthApplication.getContext().getSharedPreferences(prefName, MODE_PRIVATE)
                .edit();
    }
    public static void exitLogin() {
        SharedPreferences.Editor editor =
                HealthApplication.getContext().getSharedPreferences(LOGIN_STATE_PREF, MODE_PRIVATE)
                        .edit();
        editor.putBoolean("loginState", false);
        editor.apply();
    }


    public static String getDevice(){
        SharedPreferences sp = HealthApplication.getContext().getSharedPreferences(LOGIN_STATE_PREF, MODE_PRIVATE);
        return sp.getString("device", "暂无设备");
    }

    public static void updateDevice(String deviceMac){
        SharedPreferences.Editor editor = getEditor(LOGIN_STATE_PREF);
        editor.putString("device", deviceMac);
        editor.apply();
    }

    public static void editLoginState(SignUserData user, Boolean isLogin) {
        SharedPreferences.Editor editor =
                HealthApplication.getContext().getSharedPreferences("LoginState", MODE_PRIVATE)
                        .edit();
        if (isLogin != null) {
            if (isLogin) {
                editor.putBoolean("loginState", isLogin);
            } else {
                editor.putBoolean("loginState", isLogin);
                editor.apply();
                return;
            }
        }
        if (user.getObjectId() != null) {
            editor.putString("userId", user.getObjectId());
        }
        if (user.getUserName() != null) {
            editor.putString("userName", user.getUserName());
        }
        if (user.getPhoneNumber() != null) {
            editor.putString("phoneNumber", user.getPhoneNumber());
        }
        if (user.getMan() != null) {
            editor.putBoolean("isMan", user.getMan());
        }
        if (user.getHeight() != null) {
            editor.putInt("height", user.getHeight());
        }
        if (user.getWeight() != null) {
            editor.putFloat("weight", user.getWeight());
        }
        if (user.getIdNumber() != null) {
            editor.putString("idNumber", user.getIdNumber());
        }
        if (user.getBirthday() != null) {
            editor.putString("birthday", user.getBirthday().getDate());
        }
        if (user.getUserImage() != null) {
            editor.putString("userImage", user.getUserImage());
        }
        editor.commit();
    }

    public static String getLoginUserId(){
        SharedPreferences sp = HealthApplication.getContext()
                .getSharedPreferences("LoginState", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("loginState", false);
        if (!isLogin) {
            //没有登陆
            return null;
        }else{
            return sp.getString("userId", null);
        }
    }

    public static SignUserData getLoginUser() {
        SharedPreferences sp = HealthApplication.getContext()
                .getSharedPreferences("LoginState", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("loginState", false);
        if (!isLogin) {
            //没有登陆
            return null;
        } else {
            SignUserData user = new SignUserData();
            user.setUserName(sp.getString("userName", null));
            if (sp.getString("birthday", null) != null){
                user.setBirthday(new BmobDate(UtilClass.resolveBmobDate(
                        sp.getString("birthday", null), null)));
            }
            user.setHeight(sp.getInt("height", 0));
            user.setWeight(sp.getFloat("weight", 0.0f));
            user.setMan(sp.getBoolean("isMan", true));
            user.setPhoneNumber(sp.getString("phoneNumber", null));
            user.setIdNumber(sp.getString("idNumber", null));
            user.setUserImage(sp.getString("userImage", null));
            user.setObjectId(sp.getString("userId", null));
            return user;
        }
    }

    public static boolean isLogin() {
        SharedPreferences sp = HealthApplication.getContext()
                .getSharedPreferences("LoginState", MODE_PRIVATE);
        return sp.getBoolean("loginState", false);
    }
}
