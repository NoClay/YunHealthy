package wang.fly.com.yunhealth.util;

import android.content.SharedPreferences;

import cn.bmob.v3.datatype.BmobDate;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by noclay on 2017/4/16.
 */

public class SharedPreferenceHelper {
    public static void editLoginState(SignUserData user, Boolean isLogin){
        SharedPreferences.Editor editor =
                MyApplication.getContext().getSharedPreferences("LoginState", MODE_PRIVATE)
                        .edit();
        if (isLogin != null){
            if (isLogin){
                editor.putBoolean("loginState", isLogin);
            }else{
                editor.putBoolean("loginState", isLogin);
                editor.commit();
                return;
            }
        }
        if (user.getObjectId() != null){
            editor.putString("userId", user.getObjectId());
        }
        if (user.getUserName() != null){
            editor.putString("userName", user.getUserName());
        }
        if (user.getPhoneNumber() != null){
            editor.putString("phoneNumber", user.getPhoneNumber());
        }
        if (user.getMan() != null){
            editor.putBoolean("isMan", user.getMan());
        }
        if (user.getHeight() != null){
            editor.putInt("height", user.getHeight());
        }
        if (user.getWeight() != null){
            editor.putFloat("weight", user.getWeight());
        }
        if (user.getIdNumber() != null){
            editor.putString("idNumber", user.getIdNumber());
        }
        if (user.getBirthday() != null){
            editor.putString("birthday", user.getBirthday().getDate());
        }
        if (user.getUserImage() != null){
            editor.putString("userImage", user.getUserImage());
        }
        editor.commit();
    }

    public static SignUserData getLoginUser(){
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences("LoginState", MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("loginState", false);
        if (!isLogin){
            //没有登陆
            return null;
        }else{
            SignUserData user = new SignUserData();
            user.setUserName(sp.getString("userName", null));
            user.setBirthday(new BmobDate(UtilClass.resolveBmobDate(
                    sp.getString("birthday", null), null)));
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

    public static boolean isLogin(){
        SharedPreferences sp = MyApplication.getContext()
                .getSharedPreferences("LoginState", MODE_PRIVATE);
        return sp.getBoolean("loginState", false);
    }
}
