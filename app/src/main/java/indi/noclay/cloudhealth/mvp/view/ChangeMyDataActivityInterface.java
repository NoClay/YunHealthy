package indi.noclay.cloudhealth.mvp.view;

import android.content.Context;


import java.util.Calendar;

import indi.noclay.cloudhealth.database.SignUserData;

/**
 * Created by noclay on 2017/4/16.
 */

public interface ChangeMyDataActivityInterface {
    void editMale(Context context, boolean male);
    void startSaveData();
    void saveSuccess(SignUserData userData);
    void saveFailed();
    void editImage();
    void showImage(String url);
    void initView(SignUserData userData);
    void toast(String content);
    void startLoadImage();
    void loadSuccess();
    void loadFailed();
    void editBirthday();
    void showMale(boolean isMan);
    void showBirthday(Calendar calendar);
    SignUserData getUser();
    void exitLogin();
}
