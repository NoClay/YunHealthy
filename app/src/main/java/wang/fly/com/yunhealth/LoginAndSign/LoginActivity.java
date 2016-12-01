package wang.fly.com.yunhealth.LoginAndSign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
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

import java.io.File;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import wang.fly.com.yunhealth.Activity.ActivityCollector;
import wang.fly.com.yunhealth.DataBasePackage.SignUserData;
import wang.fly.com.yunhealth.MainActivity;
import wang.fly.com.yunhealth.R;
import wang.fly.com.yunhealth.util.UtilClass;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MSG_LOAD_START = 0;
    private static final int MSG_LOAD_PROGRESS = 1;
    private static final int MSG_LOAD_OVER = 2;
    private String loginUserPhoneNumber;//用户输入的手机号
    private String loginUserPassWord;//用户自己输入的密码

    private AutoCompleteTextView user_name;
    private EditText user_password;
    private TextView signUser;
    private TextView forgetPassWord;
    private Button login;
    private CheckBox rememberLoginStateButton;
    private ProgressDialog dialog;
    long mExitTime;
    private static final int SIGN = 0;
    private static final int CHANGE_PASSWORD = 1;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        findView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
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
                                final SignUserData one = list.get(0);
                                if (!loginUserPassWord.equals(one.getPassWord())) {
                                    toToast("密码错误");
                                    user_password.setText("");
                                } else {
                                    toToast("登录成功，正在更新信息");
                                    //用户登陆成功，下载头像
                                    final String path = MainActivity.PATH_ADD
                                            + one.getPhoneNumber()
                                            + "userImage.jpg";
                                    Log.d(TAG, "done: path" + path);
                                    File imagePic = new File(path);
                                    if (imagePic.exists() && imagePic.isFile()){
                                        editLoginState(LoginActivity.this,
                                                one, path, rememberLoginStateButton.isChecked());
                                        finish();
                                    }else if (UtilClass.isOpenNetWork(LoginActivity.this)){
                                        BmobFile bmobFile = one.getUserImage();
                                        if (bmobFile != null){
                                            bmobFile.download(imagePic, new DownloadFileListener() {
                                                @Override
                                                public void onStart() {
                                                    super.onStart();
                                                    Message message = Message.obtain();
                                                    message.what = MSG_LOAD_START;
                                                    handler.sendMessage(message);
                                                }

                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e == null){
                                                        editLoginState(LoginActivity.this,
                                                                one,
                                                                path,
                                                                rememberLoginStateButton.isChecked());
                                                    }else{
                                                        editLoginState(LoginActivity.this,
                                                                one,
                                                                null,
                                                                rememberLoginStateButton.isChecked());
                                                    }
                                                    Message message = Message.obtain();
                                                    message.what = MSG_LOAD_OVER;
                                                    handler.sendMessage(message);
                                                }
                                                @Override
                                                public void onProgress(Integer integer, long l) {
                                                    Message message = Message.obtain();
                                                    message.what = MSG_LOAD_PROGRESS;
                                                    message.arg1 = integer;
                                                    handler.sendMessage(message);
                                                    Log.d(TAG, "onProgress: progress" + integer + " / " + l);
                                                }
                                            });
                                        }
                                    }
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

    /**
     *用于修改本地的是用户登陆信息
     * @param context
     * @param user
     * @param path
     * @param isRemember
     */
    public static void editLoginState(Context context,
                                      SignUserData user,
                                      String path,
                                      Boolean isRemember) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
        if (path != null){
            editor.putString("userImage", path);
            Log.d(TAG, "editLoginState: path" + path);
        }else{
            editor.putString("userImage", "");
        }
        if (isRemember != null){
            editor.putBoolean("loginRememberState", isRemember);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOAD_START:{
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                    dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                    dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//                    dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                    dialog.setTitle("提示");
                    dialog.setMessage("正在更新数据");
                    dialog.setMax(100);
                    dialog.setProgress(0);
                    dialog.show();
                    break;
                }
                case MSG_LOAD_PROGRESS:{
                    if (msg.arg1 <= 100){
                        dialog.setProgress(msg.arg1);
                    }else{
                        dialog.dismiss();
                    }
                    break;
                }
                case MSG_LOAD_OVER:{
                    dialog.dismiss();
                    finish();
                    break;
                }
            }
        }
    };

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
