package wang.fly.com.yunhealth.MVP.Views;

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
