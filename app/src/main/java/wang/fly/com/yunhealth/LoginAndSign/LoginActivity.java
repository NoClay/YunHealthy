package wang.fly.com.yunhealth.LoginAndSign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.R;

import static android.R.attr.name;
import static android.R.attr.phoneNumber;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String loginUserPhoneNumber;//用户输入的手机号
    private String loginUserPassWord;//用户自己输入的密码

    private AutoCompleteTextView user_name;
    private EditText user_password;
    private TextView signUser;
    private TextView forgetPassWord;
    private Button login;
    private CheckBox rememberLoginStateButton;
    long mExitTime;

    private static final int SIGN = 0;
    private static final int CHANGE_PASSWORD = 1;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
    }

    private void findView() {
        user_name = (AutoCompleteTextView) findViewById(R.id.userName);
        user_password = (EditText) findViewById(R.id.userPassWord);
        signUser = (TextView) findViewById(R.id.signUser);
        forgetPassWord = (TextView) findViewById(R.id.forgetPassWord);
        login = (Button) findViewById(R.id.login);
        rememberLoginStateButton = (CheckBox) findViewById(R.id.rememberLoginState);

        login.setOnClickListener(this);
        signUser.setOnClickListener(this);
        forgetPassWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgetPassWord: {
                Intent intent = new Intent(LoginActivity.this, ChangePassWord.class);
                startActivityForResult(intent, CHANGE_PASSWORD);
                break;
            }
            case R.id.signUser: {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivityForResult(intent, SIGN);
                break;
            }
            case R.id.login: {
                loginUserPhoneNumber = user_name.getText().toString();//用户自己输入的帐号
                loginUserPassWord = user_password.getText().toString(); //用户输入的密码
                if (TextUtils.isEmpty(loginUserPhoneNumber) || TextUtils.isEmpty(loginUserPassWord)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    BmobQuery<SignUserData> query = new BmobQuery<SignUserData>();
                    query.addWhereEqualTo("phoneNumber", loginUserPhoneNumber);
                    query.setLimit(1);
                    query.findObjects(new FindListener<SignUserData>() {
                        @Override
                        public void done(List<SignUserData> list, BmobException e) {
                            Log.e(TAG, "done: ", e);
                            if (e != null) {
                                toToast("服务器异常，请稍后登录");
                            } else if (list.isEmpty()) {
                                toToast("没有该用户");
                            } else {
                                SignUserData one = list.get(0);
                                if (!loginUserPassWord.equals(one.getPassWord())) {
                                    toToast("密码错误");
                                    user_password.setText("");
                                } else {
                                    //登陆成功
                                    toToast("登录成功");
                                    editLoginState(one);
//                                    Intent intent = new Intent("LOGIN_SUCCESS");
//                                    LocalBroadcastManager localBroadcastManager =
//                                            LocalBroadcastManager.getInstance(LoginActivity.this);
//                                    localBroadcastManager.sendBroadcast(intent);
                                    finish();
                                }
                            }

                        }
                    });
                }
                break;
            }
        }
    }

    private void editLoginState(SignUserData user) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", user.getObjectId());
        editor.putString("userName", user.getUserName());
        editor.putString("phoneNumber", user.getPhoneNumber());
        editor.putBoolean("isMan", user.getMan());
        editor.putInt("height", user.getHeight());
        editor.putInt("weight", user.getWeight());
        if (user.getUserImage() != null){
            editor.putString("userImage", user.getUserImage().getUrl());
        }else{
            editor.putString("userImage", "");
        }
        if (rememberLoginStateButton.isChecked()) {
            editor.putBoolean("loginRememberState", true);
        } else {
            editor.putBoolean("loginRememberState", false);
        }
        editor.commit();
    }

    public void toToast(String content) {
        Toast.makeText(LoginActivity.this,
                content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHANGE_PASSWORD: {
                if (resultCode == RESULT_OK) {//成功修改密码
                    user_name.setText(data.getStringExtra("phoneNumber"));
                }
                break;
            }
            case SIGN: {
                if (resultCode == RESULT_OK) {//成功注册
                    user_name.setText(data.getStringExtra("phoneNumber"));
                }
                break;
            }
        }
    }
}
