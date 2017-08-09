package indi.noclay.cloudhealth.mvp.view;

/**
 * Created by noclay on 2017/4/15.
 */

public interface LoginActivityInterface {
    void loadImage(String path);
    void forgetPassWord();
    void signUser();
    void loginSuccess();
    void loginFailed(String toast);
    void badNet();
}
