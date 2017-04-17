package wang.fly.com.yunhealth.MVP.Views;

import android.content.Context;

import java.util.Calendar;

import wang.fly.com.yunhealth.DataBasePackage.SignUserData;

/**
 * Created by noclay on 2017/4/16.
 */

public interface ChangeMyDataActivityInterface {
    void editMale(Context context, boolean male);
    void startSaveData();
    void saveSuccess();
    void saveFailed();
    void editImage();
    void editBirthday();
    void showMale(boolean isMan);
    void showBirthday(Calendar calendar);
    void showImage(String url);
    SignUserData getUser();
    void initView(SignUserData userData);
}
